public class Datum implements Comparable<Datum> {

	public long time;
	public double value;

	public Datum(long time, double value) {
		this.time = time;
		this.value = value;
	}

	@Override
	public int compareTo(Datum d) {
		if (this.time < d.time) return -1;
		else if (this.time > d.time) return 1;
		return 0;
	}

}