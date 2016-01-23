import java.net.*;
import java.util.*;
import java.io.*;
import org.json.simple.*;

public class DataSimulator {

	private final String address = "192.168.1.100";
	private final int port = 37825;
	private final SocketAddress socketAddress = new InetSocketAddress(address,port);
	private final int timeout = 250;

	private final int[] sensors = {0,1,2,3,4,5,6,7,8,9};
	private final long interval = 30*60*1000;	//Every 30 min

	private Random r;

	public static void main (String args[]) {
		new DataSimulator();
	}

	public DataSimulator() {
		long delay = interval/TimeUtils.speed/sensors.length;
		r = new Random();
		while(true) {
			for (int sensor : sensors) {
				try {
					Socket socket = new Socket();
					socket.connect(socketAddress,timeout);
					PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

					double value = 0.5d + 0.3d * r.nextDouble();

					JSONObject obj = new JSONObject();
					obj.put("function","pushData");
					obj.put("sensor",sensor);
					obj.put("value",value);
					obj.put("offset",interval/2);

					pw.println(obj.toString());
					pw.close();
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