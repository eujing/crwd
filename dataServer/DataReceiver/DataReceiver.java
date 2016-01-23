import com.almworks.sqlite4java.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.util.logging.*;

public class DataReceiver {

	private final int port = 40274;

	public static void main(String args[]) {
		new DataReceiver();
	}

	public DataReceiver() {
		Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF); 
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(true) {
				Socket clientSocket = serverSocket.accept();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				try {
					int sensor = Integer.parseInt(br.readLine());
					double value = Double.parseDouble(br.readLine());
					int delta = Integer.parseInt(br.readLine());
					int time = DataTime.getTime(new Date().getTime() - delta * 60000l);

					SQLiteConnection db = new SQLiteConnection(new File("./../db.sqlite3"));
					db.open();
					db.exec("INSERT INTO data VALUES (" + sensor + "," + time + "," + value + ")");
					db.dispose();

					System.out.println("Data from : " + sensor);
					System.out.println("Time      : " + time);
					System.out.println("Value     : " + value);
					System.out.println();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				clientSocket.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}