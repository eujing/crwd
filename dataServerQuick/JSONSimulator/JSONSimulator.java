import java.net.*;
import java.util.*;
import java.io.*;
import org.json.*;

public class JSONSimulator {

	private final String address = "127.0.0.1";
	private final int port = 37825;

	public static void main(String args[]) {
		new JSONSimulator();
	}

	public JSONSimulator() {
		try {
			Socket socket = new Socket(address,port);
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

			JSONObject obj = new JSONObject();
			obj.put("function","getLocation");
			obj.put("location",3);
			obj.put("tFront",3d);
			obj.put("tBack",3d);
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