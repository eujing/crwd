import java.net.*;

public class DataSimulator {

	private final String address = "192.168.1.100";
	private final int port = 40274;

	public static void main (String args[]) {
		new DataSimulator();
	}

	public DataSimulator() {
		while(true) {
			try {
				Socket socket = new Socket(address, port);
				socket.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch(Exception e) {}
		}
	}

}