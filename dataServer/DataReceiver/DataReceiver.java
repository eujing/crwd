import java.net.*;
import java.util.*;
import java.io.*;

public class DataReceiver {

	private final int port = 40274;

	public static void main(String args[]) {
		new DataReceiver();
	}

	public DataReceiver() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(true) {
				Socket clientSocket = serverSocket.accept();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				try {
					int sensor = Integer.parseInt(br.readLine());
					double value = Double.parseDouble(br.readLine());
					int delta = Integer.parseInt(br.readLine());

					System.out.println(sensor + " " + value + " " + delta);
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