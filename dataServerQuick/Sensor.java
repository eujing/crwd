import java.util.*;

public class Sensor {

	public static final double border = 0.15d;

	public int id;
	public String title;
	public double[][] dist;
	public double[][] bounds;
	public ArrayList<Datum> data;

	public Sensor (int id, String title) {
		this.id = id;
		this.title = title;
		dist = new double[24*7][32];
		bounds = new double[24*7][2];
		for(int i = 0; i < 24*7; i++) {
			dist[i][0] = 1d;
			bounds[i][0] = (1d/32d) * border;
			bounds[i][1] = (1d/32d) * (1 - border);
		}
		data = new ArrayList<Datum>();
	}

	public void addDatum(Datum datum) {
		data.add(datum);
	}

}