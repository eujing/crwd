import java.util.*;

public class Location {

	public static final double mean = 0.45;

	public String title;
	public String address;
	public double latitude;
	public double longitude;
	public ArrayList<Sensor> sensors;

	public double[][] bounds;

	public Location(String title, String address, double latitude, double longitude) {
		this.title = title;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		sensors = new ArrayList<Sensor>();
		bounds = new double[24*7][2];
		int bin = (int)Math.floor(mean*32);
		for(int i = 0; i < 24*7; i++) {
			bounds[i][0] = bin/32d + (1d/32d) * Sensor.border;
			bounds[i][1] = bin/32d + (1d/32d) * (1 - Sensor.border);
		}
	}

	public void addSensor(Sensor sensor){
		sensors.add(sensor);
	}

}