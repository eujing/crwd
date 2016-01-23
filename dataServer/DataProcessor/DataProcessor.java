import com.almworks.sqlite4java.*;
import java.util.logging.*;
import java.util.*;
import java.io.*;

public class DataProcessor {

	private int next;
	private int hist = 4*60;
	private double memoryFactor = 0.2;
	private int interval = 60;

	private Calendar c = Calendar.getInstance();

	public static void main(String args[]) {
		new DataProcessor();
	}

	public DataProcessor() {
		Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF); 
		int time = DataTime.getTime(new Date().getTime());
		next = ((time + interval - 1) / interval) * interval;
		while(true) {
			time = DataTime.getTime(new Date().getTime());
			if (time >= next) {
				update(next);
				next += interval;
			}
			else {
				try {
					Thread.sleep(100);
				} catch(Exception e) {}
			}
		}
	}

	private void update(int time) {
		try {
			SQLiteConnection db = new SQLiteConnection(new File("./../db.sqlite3"));
			db.open();

			ArrayList<Data> data = getExpiredData(db,time - hist);
			HashMap<Integer, Double[][]> dist = decodeDistributions(getDistributions(db));
			
			System.out.println(data.size() + " entries removed");

			for(Data d : data) {
				modifyDistribution(dist.get(d.sensor), d);
			}

			writeDistributions(db, dist);

			db.dispose();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void modifyDistribution(Double[][] values, Data data) {
		Date date = new Date((long)data.time*60*1000);
		c.setTime(date);
		int frame = (c.get(Calendar.DAY_OF_WEEK)-1) * 24 + c.get(Calendar.HOUR_OF_DAY);
		int bin = (int)Math.floor(data.value*32);
		values[frame][bin] += memoryFactor;
		double sum = 0;
		for(int i = 0; i < 32; i++) sum += values[frame][i];
		for(int i = 0; i < 32; i++) values[frame][i] /= sum;
	}

	public void writeDistributions(SQLiteConnection db, HashMap<Integer, Double[][]> dist) throws SQLiteException {
		StringBuilder sb = new StringBuilder();
		Iterator it = dist.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String line = "UPDATE sensors SET dist = \"" + getDistString((Double[][])pair.getValue()) + "\" WHERE id = " + pair.getKey() + ";";
			sb.append(line);
		}
		db.exec(sb.toString());
	}

	public HashMap<Integer, Double[][]> decodeDistributions(HashMap<Integer, String> stringMap) {
		HashMap<Integer, Double[][]> map = new HashMap<>();
		Iterator it = stringMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Double[][] values= new Double[24*7][32];
			String s = (String)pair.getValue();
			for(int i = 0; i < 24*7; i++) {
				for(int j = 0; j < 32; j++) {
					char cc = s.charAt(2*(i*32 + j));
					char c = s.charAt(2*(i*32 + j) + 1);
					values[i][j] = (32*getValueFromString(cc) + getValueFromString(c)) / 1024d;
				}
			}
			map.put((Integer)pair.getKey(),values);
		}
		return map;
	}

	public ArrayList<Data> getExpiredData(SQLiteConnection db, int time) throws SQLiteException {
		ArrayList<Data> data = new ArrayList<>();
		SQLiteStatement st = db.prepare("SELECT sensor, time, value FROM data WHERE time < " + time);
		while(st.step()) {
			data.add(new Data(st.columnInt(0), st.columnInt(1), st.columnDouble(2)));
		}
		db.exec("DELETE FROM data WHERE time < " + time);
		return data;
	}

	public HashMap<Integer, String> getDistributions(SQLiteConnection db) throws SQLiteException {
		HashMap<Integer, String> map = new HashMap<>();
		SQLiteStatement st = db.prepare("SELECT id, dist FROM sensors");
		while(st.step()) {
			map.put(st.columnInt(0),st.columnString(1));
		}
		return map;
	}

	public int getValueFromString(char c) {
		if (c < 'A') return c - '0';
		else return c - 'A' + 10;
	}

	public char getCharFromValue(int value) {
		if (value < 10) return (char)('0' + value);
		else return (char)('A' + value - 10);
	}

	public String getDistString(Double[][] dist) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 24*7; i++) {
			for(int j = 0; j < 32; j++) {
				int v = (int)Math.floor(dist[i][j]*1024);
				sb.append("" + getCharFromValue(v/32) + getCharFromValue(v%32));
			}
		}
		return sb.toString();
	}

	private class Data {

		public Data(int sensor, int time, double value) {
			this.sensor = sensor;
			this.time = time;
			this.value = value;
		}

		public int sensor;
		public int time;
		public double value;

		@Override
		public String toString() {
			return sensor + " " + time + " " + value;
		}
	}

}