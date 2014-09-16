import java.net.*;
import java.io.*;

public class TCPClient {
	
	public static final int SERVER_PORT = 3490;
	
	public static void main(String[] args) throws IOException {
		
		if(args.length != 1) {
			System.out.println("Usage: java TCPClient <server name>");
			System.exit(0);
		}
		
		// Connect to server
		InetAddress serverAddr = InetAddress.getByName(args[0]);
		Socket sock = new Socket(serverAddr, SERVER_PORT);
		BufferedReader inFromUser = null;
		BufferedReader inFromServer = null;
		DataOutputStream outToServer = null;
		try {
			
			
			
			// Get a stream for reading messages from server
			for(;;){
			inFromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
			outToServer = new DataOutputStream(sock.getOutputStream());
			String message = inFromUser.readLine();
			outToServer.writeBytes(message);
			//System.out.println("Message from server " + serverAddr.getHostName());
			String messageFromServer = inFromServer.readLine();
			System.out.println(messageFromServer);
			
			//System.out.println(message);
			}
		}
		// Close socket...
		finally {
			try {
				if(sock != null) sock.close();
			}catch(Exception e) {}
		}
	}
}