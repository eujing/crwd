import java.net.*;
import java.util.*;
import java.io.*;
import org.json.*;

public class DataServer {

	private final int port = 37825;
	private final long REFRESH_TIME = 30*60*1000; //30 minutes
	private final long EXPIRATION_TIME = 24*60*60*1000; //24 hours

	private long next;

	ArrayList<Location> locations;
	ArrayList<Sensor> sensors;

	public static void main(String args[]) {
		new DataServer();
	}

	public DataServer() {
		locations = new ArrayList<Location>();
		sensors = new ArrayList<Sensor>();
		Loader.load(locations,sensors);

		next = TimeUtils.getTime(new Date().getTime()) + REFRESH_TIME;
		
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(true) {
				Socket clientSocket = serverSocket.accept();

				long time = TimeUtils.getTime(new Date().getTime());
				if (time > next) {
					refreshData(time);
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
						else break;
					} while(true);

					sc.close();
					pw.close();
				} catch(Exception e) {
					e.printStackTrace();
				}

				clientSocket.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void pushData(JSONObject obj) throws JSONException {
		long time = TimeUtils.getTime(new Date().getTime()) - obj.getLong("offset");
		double value = obj.getDouble("value");
		int sensor = obj.getInt("sensor");

		Datum datum = new Datum(time,value);
		sensors.get(sensor).addDatum(datum);
	}

	public void refreshData(long time) {
		for (Sensor sensor : sensors) {
			ArrayList<Datum> expiredData = new ArrayList<Datum>();
			ArrayList<Datum> freshData = new ArrayList<Datum>();
			for (Datum datum : sensor.data) {
				//Update distributions
			}
		}
	}

}