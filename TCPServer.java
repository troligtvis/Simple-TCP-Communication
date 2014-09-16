
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;

public class TCPServer {
	
	public static final int PORT = 3490;
	static protected ArrayList<ClientHandler> clientList;
	static protected Iterator clientIter;
	
	public static void main(String[] args) throws IOException {
		
		String quitCommand = "/quit";
		String whoCommand = "/who";
		String nickCommand = "/nick";
		String helpCommand = "/help";
		String unknownCommand = "unknown command";
		
		clientList = new ArrayList<ClientHandler>();
		
		ServerSocket servSock = null;
		
		int i = 1;
		
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
				System.out.println("Spawning client thread " + i);
				ClientHandler cl = new ClientHandler(sock, i);
				clientList.add(cl);
				cl.start();
				i++;
				
				
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
	protected BufferedReader in; 
	protected PrintWriter out;
	protected DataOutputStream outToClient;
	protected int id;
	
	public ClientHandler(Socket sock, int id) {
		this.sock = sock;
		this.id = id;
		try {
			if (sock != null) {
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));             
				out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);             
			 	}
		} catch (Exception e) {
			System.out.println("Error: " + e); 
		}
	
	}
	
	public synchronized void putMessage(String msg) throws IOException {
		if (out != null) {
			out.println(msg); 
			//out.flush();
			
			//outToClient = new DataOutputStream(sock.getOutputStream());
			//outToClient.writeBytes(msg);
		 }
	}
	
	
	// The thread activity, send a single message and then exit.
	public void run() {
		
		//System.out.println("Client handler " + id + " started."); 
		if (in != null && out != null) {
			try {
				putMessage("Hello! This is Java BroadcastEchoServer. Enter BYE to exit.");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}       
			try {
				for (;;) {
					String str = in.readLine(); 
					if (str == null) {
						break; 
					} else {
						putMessage("Echo: " + str); 
						System.out.println("Received (" + id + "): " + str);

						if (str.trim().equals("BYE")) {
							break; 
						} else {
							Iterator clientIter = TCPServer.clientList.iterator();
							while  (clientIter.hasNext()) {
								ClientHandler t = (ClientHandler) clientIter.next();
								//if (t != this) {
									t.putMessage("Broadcast(" + id + "): " + str); 
								//}
							}
						}
					}
				}
				sock.close(); 
				//BroadcastEchoServer.activeThreads.removeElement(this); 
			} catch (IOException e) {} 
		}
		System.out.println("Client thread " + id + " stopped."); 
		/*
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
		*/
	}
}