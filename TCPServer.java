import java.net.*;
import java.io.*;

public class TCPServer {
	
	public static final int PORT = 3490;
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket servSock = null;
		
		try {
			// Create a server socket, and bind it to a local port
			servSock = new ServerSocket(PORT);
			
			// Listen to incoming connections and process requests
			while(true) {
				System.out.println("Server is listening...");
				Socket sock = servSock.accept();
				System.out.println("Server contacted from " + 
												sock.getInetAddress());
				// Process the client request
				PrintWriter sout = null;
				try {
					
					// Get a stream for sending messages to client 
					// true = auto flush
					sout = new PrintWriter(sock.getOutputStream(), true);
					// Send a message
					sout.println("Hello from server.");				
				}
				catch(IOException ie) {
					System.err.println(ie.toString());
				}
				finally {
					// Close the socket
					try {
						
						if(sock != null) sock.close();
					} catch(Exception e) {}
				}
			}	// while loop: back to listening (accept)...

		}
		finally {
			// Close the Server Socket
			try {
				if(servSock != null) servSock.close();
			} catch(Exception e) {}
		}
	}
}