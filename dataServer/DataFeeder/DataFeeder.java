import java.net.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

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
				InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream(), "UTF-8");
				JSONParser parser = new JSONParser();
				JSONObject object = (JSONObject)parser.parse(inputStreamReader);
				
				System.out.println(object);
				
				System.out.println("ok");
				clientSocket.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}