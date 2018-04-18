import java.io.*;
import java.net.*;
import java.util.*;
import java.io.Serializable;


public class Server
{

	public static void main(String[] args)
							throws IOException
	{ //server set up start
		ServerSocket serverSocket = null;
		final int PORT = 1234;
		Socket client;
		ClientHandler handler;
		System.out.println("Opening connection...\n");

		//User set up
		int clientTotal = 0;
		user[] Users = new user[50];
		Scanner input;
		try
		{ // socket attempt open
			serverSocket = new ServerSocket(PORT);
		}
		catch (IOException ioEx)
		{
			System.out.println("\nUnable to set up port!");
			System.exit(1);
		}

		System.out.println("\nServer running...\n");

		do
		{

			//Wait for client to join.
			client = serverSocket.accept();
			input = new Scanner(client.getInputStream());
			System.out.println("\nNew client accepted to the server.\n");
			System.out.println("\nClients:" + clientTotal);
			handler = new ClientHandler(client, clientTotal);

			//clientTotal = clientTotal + 1;
			clientTotal++;
			handler.start();
		}while (true);
	}
}

class ClientHandler extends Thread
{
	private Socket client;
	private Scanner input;
	private PrintWriter output;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private static int numOfUsers;
	private static user[] Users = new user[20];
	private int currentUserNum;

	public ClientHandler(Socket socket, int clientTotal) throws IOException
	{//setting up stream for client 
		client = socket;

		input = new Scanner(client.getInputStream());
		output = new PrintWriter(client.getOutputStream(),true);
		in = new ObjectInputStream(client.getInputStream());
		out = new ObjectOutputStream(client.getOutputStream());

		updateUsers(clientTotal);
		currentUserNum = clientTotal;
	}

	public void run()
	{//Main 

		//ease of mind things are actully joining (:
		System.out.println("\nClient:" + Users[numOfUsers].Client +" Has joined the server");
		for (int i = 0; i <= numOfUsers; i++)
		{
			System.out.println("Client " + i + ": " + Users[i].Client);
		}
		String sendRead = input.nextLine();

	while (true)
		{
			try{
				///// for  sending  emails /////	
				if (sendRead.equals("send")){
					System.out.println("Sending");
					email E = (email) in.readObject();
					System.out.println("email accepted into the server");
					boolean hasSent = false;
					for (int i = 0; i <= numOfUsers; i++){
						if (Users[i].Client.equals(E.getto())){
							doSend(Users[i], Users[currentUserNum].Client, in, E);
							hasSent = true;
						}
					}
					if (!hasSent){
						System.out.println("Could not Find user you are looking for : ");
						//String message = input.nextLine();
					}
				}

				///// for  reading emails ///////
				else{
					doRead(Users[currentUserNum], out);
				}
				sendRead = "";
				System.out.println("waiting for next input from users");
				sendRead = input.nextLine();
				////// catch any exceptions//////
				}
				catch(IOException IOex){
					System.out.println("It was an IO exception");
				}
				catch(ClassNotFoundException n){
					System.out.println("");
				}
		}
	}
	private synchronized void updateUsers(int clientCount){
		Users[clientCount] = new user();
		Users[clientCount].Client = input.nextLine();
		numOfUsers = clientCount;
	}
	private synchronized user[] syncUsers(){
		return Users;
	}


	private void doSend(user recipient, String senderName, ObjectInputStream in, email E)
	{
		if (recipient.messagesInBox == recipient.MAX_MESSAGES)
			System.out.println("\nMessage box full!");
		else{
			recipient.inbox[recipient.messagesInBox] = E;
			System.out.println("\nmessage was sent");
			recipient.messagesInBox++;
		}
	}

	private void doRead(user Reader, ObjectOutputStream out)
	{
		System.out.println("\nSending "	+ Reader.messagesInBox + " message(s).\n");
		//output.println(Reader.messagesInBox);
		try
		{
			out.reset();
			out.writeObject(Reader);
			System.out.println("\n it was output ");
		}
		catch(IOException uhEx)
		{
			System.out.println("\nHost ID not found!\n");
		}
	}
}
class InvalidClientException extends Exception
{
	public InvalidClientException()
	{
		super("Invalid client name!");
	}

	public InvalidClientException(String message)
	{
		super(message);
	}
}
class InvalidRequestException extends Exception
{
	public InvalidRequestException()
	{
		super("Invalid request!");
	}

	public InvalidRequestException(String message)
	{
		super(message);
	}
}
