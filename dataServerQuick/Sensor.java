import java.util.*;

public class Sensor {

	public static final double border = 0.15d;
	public static final double mean = 0.45;

	public int id;
	public int location;
	public String title;
	public double[][] dist;
	public double[][] bounds;
	public ArrayList<Datum> data;

	public Sensor (int id, int location, String title) {
		this.id = id;
		this.location = location;
		this.title = title;
		dist = new double[24*7][32];
		bounds = new double[24*7][2];
		int bin = (int)Math.floor(mean*32);
		for(int i = 0; i < 24*7; i++) {
			dist[i][bin] = 1d;
			bounds[i][0] = bin/32d + (1d/32d) * border;
			bounds[i][1] = bin/32d + (1d/32d) * (1 - border);
		}
		data = new ArrayList<Datum>();
	}

	public void addDatum(Datum datum) {
		data.add(datum);
	}

}