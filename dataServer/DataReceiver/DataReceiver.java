import java.net.*;

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
				System.out.println("ok");
				clientSocket.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}