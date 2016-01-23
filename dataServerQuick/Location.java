import java.util.*;

public class Location {

	public String title;
	public String address;
	public double latitude;
	public double longitude;
	public ArrayList<Sensor> sensors;

	public Location(String title, String address, double latitude, double longitude) {
		this.title = title;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		sensors = new ArrayList<Sensor>();
	}

	public void addSensor(Sensor sensor){
		sensors.add(sensor);
	}

}