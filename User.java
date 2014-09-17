
public class User {

	private int id;
	private String nick;
	
	public User(int id, String nick){
		this.id=id;
		this.nick=nick;
	}
	
	public int getId(){
		return id;
	}
	
	public String getNick(){
		return nick;
	}
	
	public void setNick(String nick){
		this.nick=nick;
	}
}
