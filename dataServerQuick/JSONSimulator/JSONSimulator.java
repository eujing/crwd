import java.net.*;
import java.util.*;
import java.io.*;
import org.json.*;

public class JSONSimulator {

	private final String address = "192.168.1.101";
	private final int port = 37825;

	public static void main(String args[]) {
		new JSONSimulator();
	}

	public JSONSimulator() {
		try {
			Socket socket = new Socket(address,port);
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

			JSONObject obj = new JSONObject();
			obj.put("function","listSensors");
			obj.put("location",3);
			pw.println(obj.toString());

			Scanner sc = new Scanner(socket.getInputStream());
			JSONObject reply = new JSONObject(sc.nextLine());
			System.out.println(reply);

			sc.close();
			pw.close();
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}