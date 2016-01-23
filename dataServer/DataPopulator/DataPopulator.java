import com.almworks.sqlite4java.*;
import java.util.*;
import java.io.*;

public class DataPopulator {

	public static void main(String args[]) {
		new DataPopulator();
	}

	public DataPopulator() {
		try {
			new File("./../db.sqlite3").delete();
			SQLiteConnection db = new SQLiteConnection(new File("./../db.sqlite3"));
			db.open();

			db.exec("CREATE TABLE locations (id INT PRIMARY KEY NOT NULL, title TEXT NOT NULL, address TEXT NOT NULL, lat REAL NOT NULL, long REAL NOT NULL)");
			db.exec("CREATE TABLE sensors (id INT PRIMARY KEY NOT NULL, location INT NOT NULL, title TEXT NOT NULL, history TEXT NOT NULL, FOREIGN KEY(location) REFERENCES locations(id))");
			db.exec("CREATE TABLE data (sensor INT, time INT, value REAL, PRIMARY KEY(sensor,time))");
			db.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}