import java.net.*;
import java.util.*;
import java.io.*;
import org.json.*;

public class DataSimulator {

	private final String address = "127.0.0.1";
	private final int port = 37825;
	private final SocketAddress socketAddress = new InetSocketAddress(address,port);
	private final int timeout = 250;

	private final int[] sensors = {0,1,3,4,5,6,7,8,9};
	private final long interval = 30*60*1000;	//Every 30 min

	private Calendar c = Calendar.getInstance();

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

					long time = TimeUtils.getTime(new Date().getTime());
					double value = getValue(time);

					JSONObject obj = new JSONObject();
					obj.put("function","pushData");
					obj.put("sensor",sensor);
					obj.put("value",value);
					obj.put("offset",interval/2);

					System.out.println("Sensor : " + sensor + " Value : " + value);

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

	public double getValue(long time) {
		c.setTime(new Date(time));
		int hour = c.get(Calendar.HOUR_OF_DAY);
		double phase = 2*Math.PI*(hour + 3)/24d;
		double value = 0.45d - 0.25d*Math.sin(phase) + 0.15d*r.nextGaussian();
		if (value < 0.01d) value = 0.01d;
		if (value > 0.99d) value = 0.99d;
		return value;
	}

}