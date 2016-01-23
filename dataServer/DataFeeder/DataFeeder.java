import java.net.*;

public class DataFeeder {

	private final int port = 37825;

	public static void main(String args[]) {
		new DataFeeder();
	}

	public DataFeeder() {
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