import java.net.*;
import java.util.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

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
					InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream(), "UTF-8");
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject)parser.parse(inputStreamReader);

					String function = (String)obj.get("function");
					if (function.equals("pushData")) {
						pushData(obj);
					}
					//Handle other functions
				} catch(Exception e) {
					e.printStackTrace();
				}

				clientSocket.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void pushData(JSONObject obj) {
		long time = TimeUtils.getTime(new Date().getTime()) - (long)obj.get("offset");
		double value = (double)obj.get("value");
		int sensor = (int)((long)obj.get("sensor"));

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