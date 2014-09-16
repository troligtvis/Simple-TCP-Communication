import java.net.*;
import java.io.*;

public class TCPClient {
	
	public static final int SERVER_PORT = 3490;
	
	public static void main(String[] args) throws IOException {
		
		if(args.length != 1) {
			System.out.println("Usage: java TCPClient <server name>");
			System.exit(0);
		}
		
		Socket sock = null;
		BufferedReader sin = null;
		
		try {
			// Connect to server
			InetAddress serverAddr = InetAddress.getByName(args[0]);
			sock = new Socket(serverAddr, SERVER_PORT);
			
			// Get a stream for reading messages from server
			sin = 
				new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			String message = sin.readLine();
			System.out.println("Message from server " + serverAddr.getHostName());
			System.out.println(message);
		}
		// Close socket...
		finally {
			try {
				if(sock != null) sock.close();
			}catch(Exception e) {}
		}
	}
}