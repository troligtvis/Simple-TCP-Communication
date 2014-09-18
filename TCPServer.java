
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TCPServer {
	
	public static final int PORT = 3490;
	static protected ArrayList<ClientHandler> clientList;
	static protected Iterator clientIter;
	static protected List<User> users;
	
	public static void main(String[] args) throws IOException {
		

		
		clientList = new ArrayList<ClientHandler>();
		users=new ArrayList<User>();
		
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
				User user=new User(i, "random"+i);
				users.add(user);
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
	
	private String quitCommand = "/quit";
	private String whoCommand = "/who";
	private String nickCommand = "/nick";
	private String helpCommand = "/help";
	private String unknownCommand = "unknown command";
	
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
	
	private String currentTime(){
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	return sdf.format(cal.getTime()).toString();
	}
	
	
	// The thread activity, send a single message and then exit.
	public void run() {
		
		System.out.println("Client handler " + id + " started."); 
		if (in != null && out != null) {
			try {
				putMessage("Hello! This is Java BroadcastEchoServer. Enter /quit to exit.");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}       
			try {
				for (;;) {
					String str = in.readLine();
					if (str == null) {
						break; 
					}else{
						//putMessage("Echo: " + str); 
						System.out.println("Received (" + id + "): " + str);
						String[] inputCommand=new String[2];
						List<User> users=TCPServer.users;
						
						//Finds the senders ID so you can get its name etc
						int arrayId=0;
						for(int i=0;i<users.size();i++){
							if(users.get(i).getId()==id){
								arrayId=i;
							}
						}
						
						//Checks the first character
						if(!str.isEmpty() && str.charAt(0) == '/'){
							inputCommand=str.split(" ");
							//Split the str from maincommand and subcommand
							if(inputCommand[0].equalsIgnoreCase(nickCommand) && !inputCommand[1].isEmpty()){
								
								Iterator clientIter = TCPServer.clientList.iterator();
								while  (clientIter.hasNext()) {
									ClientHandler t = (ClientHandler) clientIter.next();
									t.putMessage("["+currentTime()+"] Changed nick from "+users.get(arrayId).getNick()+" to "+inputCommand[1]);
								}
								users.get(arrayId).setNick(inputCommand[1]); //Changes the name in arraylist object with same id
								
							}else if(str.trim().equalsIgnoreCase(whoCommand)){
								String online="";
								for(int i=0;i<users.size();i++){
									System.out.println(users.get(i).getNick());
									online+=users.get(i).getNick()+"\n";
								}
								putMessage("Users online:\n"+online);
							}else if(str.trim().equalsIgnoreCase(helpCommand)){
								putMessage("Help:\n\n/nick [name] - To change your nick ex. '/nick Jones'\n/who - To show every online user\n/quit - Exit");
							}else if(str.trim().equalsIgnoreCase(quitCommand)){
								Iterator clientIter = TCPServer.clientList.iterator();
								while  (clientIter.hasNext()) {
									ClientHandler t = (ClientHandler) clientIter.next();
									if (t != this) {
										t.putMessage("["+currentTime()+"] " + users.get(arrayId).getNick() + ": Has left the building"); 
									} 
								}
								users.remove(arrayId);
								break;
							}else{
								putMessage("Echo: "+unknownCommand);
							}
						}else {
							Iterator clientIter = TCPServer.clientList.iterator();
							while  (clientIter.hasNext()) {
								ClientHandler t = (ClientHandler) clientIter.next();
								if (t != this) {
									t.putMessage("["+currentTime()+"] " + users.get(arrayId).getNick() + ": " + str); 
								}
							}
						}
					}
				}
				
				//Removes the client from the arraylist and socket
				TCPServer.clientList.remove(this);
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