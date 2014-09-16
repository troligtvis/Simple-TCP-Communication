
import java.net.*;
import java.io.*;

public class TCPServer {
	
	public static final int PORT = 3490;
	
	public static void main(String[] args) throws IOException {
		
		String quitCommand = "/quit";
		String whoCommand = "/who";
		String nickCommand = "/nick";
		String helpCommand = "/help";
		String unknownCommand = "unknown command";
		
		ServerSocket servSock = null;
		
		try {
			// Create a server socket, and bind it to a local port
			servSock = new ServerSocket(PORT);
			// Listen to incoming connections, create a separate
			// client handler thread for each connection
			while(true) {
				System.out.println("Server is listening...");
				Socket sock = servSock.accept();
				System.out.println("Server contacted from " + 
												sock.getInetAddress());
				 // Create a new clienthandler
				ClientHandler cl = new ClientHandler(sock);
				cl.start();
			}
		}
		// Close the  server socket...
		finally {
			try {
				if(servSock != null) servSock.close();
			} catch(Exception e) {}
		}
	}
}

/** The class that handles a client in a separate thread
 */
class ClientHandler extends Thread {
	
	private Socket sock = null;
	private PrintWriter sout = null; 
	
	ClientHandler(Socket sock) {
		this.sock = sock;
	}
	
	// The thread activity, send a single message and then exit.
	public void run() {
		
		try {
			// Get a stream for sending messages to client 
			// true = auto flush
			sout = new PrintWriter(sock.getOutputStream(), true);
			// Send a message
			sout.println("Hello from the servers client handler.");				
		}
		catch(IOException ie) {
			System.err.println(ie.toString());
		}
		finally {
			try {
				if(sock != null) sock.close();
			} catch(Exception e) {}
		}
	}
}