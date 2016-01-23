import java.io.*;
import java.util.*;
import org.apache.commons.csv.*;
import java.nio.charset.StandardCharsets;

public class Loader {
	public static String L_PATH = "./locations.csv";
	public static String S_PATH = "./sensors.csv";

	public static void load(ArrayList<Location> locations, ArrayList<Sensor> sensors) {
		try {
			CSVParser parser;
			List<CSVRecord> list;
			parser = CSVParser.parse(new File(L_PATH),StandardCharsets.UTF_8,CSVFormat.DEFAULT);
			list = parser.getRecords();
			for ( int i = 1; i < list.size(); i++ ) {
				String title = list.get(i).get(1);
				String address = list.get(i).get(2);
				double latitude = Double.parseDouble(list.get(i).get(3));
				double longitude = Double.parseDouble(list.get(i).get(4));
				locations.add(new Location(title,address,latitude,longitude));
			}
			parser = CSVParser.parse(new File(S_PATH),StandardCharsets.UTF_8,CSVFormat.DEFAULT);
			list = parser.getRecords();
			for ( int i = 1; i < list.size(); i++ ) {
				int location = Integer.parseInt(list.get(i).get(1));
				String title = list.get(i).get(2);
				Sensor sensor = new Sensor(Integer.parseInt(list.get(i).get(0)),title);
				sensors.add(sensor);
				locations.get(location).addSensor(sensor);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}