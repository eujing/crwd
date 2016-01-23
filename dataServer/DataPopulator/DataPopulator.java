import com.almworks.sqlite4java.*;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.*;
import java.util.*;
import java.io.*;

public class DataPopulator {

	public static String dist;

	static {
		String segment = "VV";
		for(int i = 1; i < 32; i++) {
			segment = segment + "00";
		}
		dist = "";
		for(int i = 0; i < 24; i++) {
			dist = dist + segment;
		}
	}

	public static void main(String args[]) {
		new DataPopulator();
	}

	public DataPopulator() {
		try {
			new File("./../db.sqlite3").delete();
			SQLiteConnection db = new SQLiteConnection(new File("./../db.sqlite3"));
			db.open();

			db.exec("CREATE TABLE locations (id INT PRIMARY KEY NOT NULL, title TEXT NOT NULL, address TEXT NOT NULL, lat REAL NOT NULL, long REAL NOT NULL)");
			db.exec("CREATE TABLE sensors (id INT PRIMARY KEY NOT NULL, location INT NOT NULL, title TEXT NOT NULL, dist TEXT NOT NULL, FOREIGN KEY(location) REFERENCES locations(id))");
			db.exec("CREATE TABLE data (sensor INT, time INT, value REAL, PRIMARY KEY(sensor,time))");
			
			CSVParser parser;
			List<CSVRecord> list;

			parser = CSVParser.parse(new File("./locations.csv"),StandardCharsets.UTF_8,CSVFormat.DEFAULT);
			list = parser.getRecords();
			for ( int i = 1; i < list.size(); i++ ) {
				db.exec("INSERT INTO locations VALUES(" + list.get(i).get(0) + ",\"" + list.get(i).get(1) + "\",\"" + list.get(i).get(2) + "\"," + list.get(i).get(3) + "," + list.get(i).get(4) + ")");
			}

			parser = CSVParser.parse(new File("./sensors.csv"),StandardCharsets.UTF_8,CSVFormat.DEFAULT);
			list = parser.getRecords();
			for ( int i = 1; i < list.size(); i++ ) {
				db.exec("INSERT INTO sensors VALUES(" + list.get(i).get(0) + "," + list.get(i).get(1) + ",\"" + list.get(i).get(2) + "\",\"" + dist + "\")");
			}

			db.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}