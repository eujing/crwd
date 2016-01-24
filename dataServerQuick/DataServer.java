import umontreal.iro.lecuyer.functionfit.SmoothingCubicSpline;
import java.net.*;
import java.util.*;
import java.io.*;
import org.json.*;
import java.text.*;

public class DataServer {

	private static final int port = 37825;
	private static final long REFRESH_TIME = 6*60*60*1000; //6 hours
	private static final long EXPIRATION_TIME = 24*60*60*1000; //24 hours

	private static final double retention = 0.9d;
	private static final double bounds = 0.03d;

	private static final double RHO = 2.5E-19;
	private static final long DELTA = 30*60*1000; //30 minutes

	private static final DecimalFormat df = new DecimalFormat("0.00000");

	private long next;

	private Calendar c = Calendar.getInstance();

	ArrayList<Location> locations;
	ArrayList<Sensor> sensors;

	public static void main(String args[]) {
		new DataServer();
	}

	public DataServer() {
		System.out.println(RHO);
		locations = new ArrayList<Location>();
		sensors = new ArrayList<Sensor>();
		Loader.load(locations,sensors);

		next = TimeUtils.getTime(new Date().getTime()) + REFRESH_TIME;
		
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("SOCKET ESTABLISHED");

				long time = TimeUtils.getTime(new Date().getTime());
				if (time > next) {
					removeExpiredData(time - EXPIRATION_TIME);
					next += REFRESH_TIME;
				}

				try {
					Scanner sc = new Scanner(clientSocket.getInputStream());
					PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
					do {
						if (!sc.hasNextLine()) break;
						JSONObject obj = new JSONObject(sc.nextLine());
						String function = obj.getString("function");

						System.out.println(function);

						if (function.equals("pushData")) {
							pushData(obj);
							break;
						}
						else if (function.equals("listLocations")) {
							JSONArray list = new JSONArray();
							for(int i = 0; i < locations.size(); i++) {
								Location location = locations.get(i);
								JSONObject loc = new JSONObject();
								loc.put("id",i);
								loc.put("title",location.title);
								loc.put("address",location.address);
								loc.put("latitude",location.latitude);
								loc.put("longitude",location.longitude);
								list.put(loc);
							}
							JSONObject reply = new JSONObject();
							reply.put("locations",list);
							pw.println(reply.toString());
						}
						else if (function.equals("listSensors")) {
							int id = obj.getInt("location");
							Location location = locations.get(id);
							JSONArray list = new JSONArray();
							for(int i = 0; i < location.sensors.size(); i++) {
								Sensor sensor = location.sensors.get(i);
								JSONObject sen = new JSONObject();
								sen.put("id",sensor.id);
								sen.put("location",id);
								sen.put("title",sensor.title);
								list.put(sen);
							}
							JSONObject reply = new JSONObject();
							reply.put("sensors",list);
							pw.println(reply.toString());
						}
						else if (function.equals("getLocation")) {
							int id = obj.getInt("location");
							double tFront = obj.getDouble("tFront");
							double tBack = obj.getDouble("tBack");
							pw.println(getLocationJSON(id,tFront,tBack));
						}
						else if (function.equals("getSensor")) {
							int id = obj.getInt("sensor");
							double tFront = obj.getDouble("tFront");
							double tBack = obj.getDouble("tBack");
							pw.println(getSensorJSON(id,tFront,tBack));
						}
						else break;
					} while(true);

					sc.close();
					pw.close();
				} catch(Exception e) {
					e.printStackTrace();
				}

				clientSocket.close();
				System.out.println("SOCKET CLOSED");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public JSONObject getLocationJSON(int index, double tFront, double tBack) throws JSONException {
		long time = TimeUtils.getTime(new Date().getTime());
		long tMax = time + (long)(tFront*60*60*1000);
		long tMin = time - (long)(tBack*60*60*1000);
		
		Location location = locations.get(index);
		JSONObject locationData = new JSONObject();
		locationData.put("minTime",tMin);
		locationData.put("maxTime",tMax);
		locationData.put("rangeData", getRangeArray(location.bounds,tFront,tBack,time));
		
		int n = location.sensors.size();
		ArrayList<Datum> dataPoints = new ArrayList<>();
		SmoothingCubicSpline[] splines = new SmoothingCubicSpline[n];
		int nNotNull = 0;
		for(int i = 0; i < n; i++) {
			Sensor sensor = location.sensors.get(i);
			dataPoints = getDataPoints(sensor.data,tBack,time);
			splines[i] = getSpline(dataPoints,tMin);
			if(splines[i] != null) nNotNull++;
		}

		if (nNotNull != 0) {
			SmoothingCubicSpline[] splinesNotNull = new SmoothingCubicSpline[nNotNull];
			int cursor = 0;
			for(int i = 0; i < n; i++) {
				if (splines[i] != null) {
					splinesNotNull[cursor] = splines[i];
					cursor++;
				}
			}
			ArrayList<Datum> interpolatedPoints = getInterpolatedPoints(splinesNotNull,tMin,time);
			locationData.put("trendData", getTrendArray(interpolatedPoints));
		} else {
			locationData.put("trendData", new JSONArray());
		}
		return locationData;
	}


	public JSONObject getSensorJSON(int index, double tFront, double tBack) throws JSONException {
		long time = TimeUtils.getTime(new Date().getTime());
		long tMax = time + (long)(tFront*60*60*1000);
		long tMin = time - (long)(tBack*60*60*1000);

		Sensor sensor = sensors.get(index);
		JSONObject sensorData = new JSONObject();
		sensorData.put("minTime",tMin);
		sensorData.put("maxTime",tMax);
		sensorData.put("rangeData", getRangeArray(sensor.bounds,tFront,tBack,time));
		
		ArrayList<Datum> dataPoints = getDataPoints(sensor.data,tBack,time);
		SmoothingCubicSpline spline = getSpline(dataPoints,tMin);

		if (spline != null) {
			ArrayList<Datum> interpolatedPoints = getInterpolatedPoints(new SmoothingCubicSpline[]{spline},tMin,time);
			sensorData.put("trendData", getTrendArray(interpolatedPoints));
		} else {
			sensorData.put("trendData", new JSONArray());
		}
		return sensorData;
	}

	public ArrayList<Datum> getInterpolatedPoints(SmoothingCubicSpline[] splines, long tMin, long tMax) {
		ArrayList<Datum> data = new ArrayList<>();
		int n = splines.length;
		long time = tMin;
		do {
			double value = 0;
			for(int i = 0; i < n; i++) {
				value += splines[i].evaluate((double)(time - tMin));
			}
			data.add(new Datum(time,value/n));
			time += DELTA;
		} while(time < tMax);
		return data;
	}

	public SmoothingCubicSpline getSpline(ArrayList<Datum> data, long epoch) {
		int n = data.size();
		double[] x = new double[n];
		double[] y = new double[n];
		for(int i = 0; i < n; i++) {
			x[i] = (double)(data.get(i).time - epoch);
			y[i] = data.get(i).value;
		}
		try {
			return new SmoothingCubicSpline(x,y,RHO);
		} catch (Exception e) {}
		return null;
	}

	public JSONArray getRangeArray(double[][] rangeData, double tFront, double tBack, long time) throws JSONException {
		c.setTime(new Date(time));
		int frame = (c.get(Calendar.DAY_OF_WEEK)-1) * 24 + c.get(Calendar.HOUR_OF_DAY);
		int frameFront = (int)Math.ceil(tFront+1);
		int frameBack = -(int)Math.ceil(tBack+1);

		JSONArray rangeArray = new JSONArray();
		for(int i = frameBack; i <= frameFront; i++) {
			JSONObject range = new JSONObject();
			int f = Math.floorMod(frame+i,24*7);
			range.put("time",time + 30*60*1000 + (long)i*60*60*1000);
			range.put("min",rangeData[f][0]);
			range.put("max",rangeData[f][1]);
			rangeArray.put(range);
		}
		return rangeArray;
	}

	public JSONArray getTrendArray(ArrayList<Datum> data) throws JSONException {
		JSONArray trendArray = new JSONArray();
		for(Datum datum : data) {
			JSONObject trend = new JSONObject();
			trend.put("time",datum.time);
			trend.put("value",datum.value);
			trendArray.put(trend);
		}
		return trendArray;
	}

	public ArrayList<Datum> getDataPoints(ArrayList<Datum> data, double tBack, long time) {
		long cutoff = time - (long)(tBack+1)*60*60*1000;
		ArrayList<Datum> list = new ArrayList<>();
		for(Datum datum : data) {
			if(datum.time > cutoff) {
				list.add(datum);
			}
		}
		Collections.sort(list);
		return list;
	}

	public void pushData(JSONObject obj) throws JSONException {
		long time = TimeUtils.getTime(new Date().getTime()) - obj.getLong("offset");
		double value = obj.getDouble("value");
		Sensor sensor = sensors.get(obj.getInt("sensor"));

		Datum datum = new Datum(time,value);
		sensor.addDatum(datum);

		c.setTime(new Date(time));
		int frame = (c.get(Calendar.DAY_OF_WEEK)-1) * 24 + c.get(Calendar.HOUR_OF_DAY);
		adjustDistribution(sensor.dist[frame],value);
		sensor.bounds[frame] = findBounds(sensor.dist[frame]);

		double[] distCombined = new double[32];
		Location location = locations.get(sensor.location);
		for(int i = 0; i < 32; i++) {
			double total = 0;
			for(Sensor s : location.sensors) total += s.dist[frame][i];
			distCombined[i] = total / location.sensors.size();
		}
		location.bounds[frame] = findBounds(distCombined);
	}

	public static void adjustDistribution(double[] dist, double value) {
		int bin = (int)Math.floor(value*32);
		dist[bin] += (1 - retention);
		double total = 0;
		for(int i = 0; i < 32; i++) total += dist[i];
		for(int i = 0; i < 32; i++) dist[i] /= total;
	}

	public static double[] findBounds(double[] dist) {
		double min = 0d;
		double max = 1d;

		double tally = 0;
		for(int i = 0; i < 32; i++) {
			if (tally + dist[i] > bounds) {
				min = i/32d + (bounds - tally)/dist[i]/32d;
				break;
			}
			else {
				tally += dist[i];
			}
		}
		tally = 0;
		for(int i = 31; i >= 0; i--) {
			if (tally + dist[i] > bounds) {
				max = (i+1)/32d - (bounds-tally)/dist[i]/32d;
				break;
			}
			else {
				tally += dist[i];
			}
		}
		return new double[]{min,max};
	}

	public void removeExpiredData(long time) {
		for (Sensor sensor : sensors) {
			ArrayList<Datum> freshData = new ArrayList<Datum>();
			for (Datum datum : sensor.data) {
				if (datum.time > time) freshData.add(datum);
			}
			sensor.data = freshData;
		}
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("./dist.txt")));
			for(int i = 0; i < locations.size(); i++) {
				Location location = locations.get(i);
				for(int j = 0; j < 24*7; j++) {
					pw.println(df.format(location.bounds[j][0]) + " " + df.format(location.bounds[j][1]));
				}
			}
			pw.println();
			for(int i = 0; i < sensors.size(); i++) {
				Sensor sensor = sensors.get(i);
				for(int j = 0; j < 24*7; j++) {
					pw.println(df.format(sensor.bounds[j][0]) + " " + df.format(sensor.bounds[j][1]));
				}
			}
			for(int i = 0; i < sensors.size(); i++) {
				double[][] dist = sensors.get(i).dist;
				for(int j = 0; j < 24*7; j++) {
					for(int k = 0; k < 32; k++) {
						pw.print(df.format(dist[j][k]) + " ");
					}
					pw.println();
				}
				pw.println();
			}
			pw.close();
		} catch(Exception e){}
	}

}