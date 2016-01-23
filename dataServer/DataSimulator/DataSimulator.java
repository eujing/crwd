import java.net.*;
import java.util.*;
import java.io.*;

public class DataSimulator {

	private final String address = "192.168.1.100";
	private final int port = 40274;
	private final SocketAddress socketAddress = new InetSocketAddress(address,port);
	private final int timeout = 300;

	private final int[] sensors = {0,1,2,3,4,5,6,7,8,9};
	private final int speed = 240;
	private final int interval = 15;	//Every 15 min
	private int delay;

	private Random r;

	public static void main (String args[]) {
		new DataSimulator();
	}

	public DataSimulator() {
		delay = (int)((interval*60000f)/speed/sensors.length);
		r = new Random();
		while(true) {
			for (int sensor : sensors) {
				try {
					Socket socket = new Socket();
					socket.connect(socketAddress,timeout);
					PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

					double value = 0.5d + 0.3d * r.nextDouble();

					pw.println(sensor);
					pw.println(value);
					pw.println(interval * 60 / 2);

					socket.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(delay);
				} catch(Exception e) {}
			}
		}
	}

}