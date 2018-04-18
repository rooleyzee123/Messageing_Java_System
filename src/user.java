import java.io.Serializable;
public class user implements Serializable{
	//Each user of the messageing system with a max inbox size
	public String Client;
	public int MAX_MESSAGES = 10;
	public int messagesInBox = 0;
	public email[] inbox = new email[MAX_MESSAGES];
}