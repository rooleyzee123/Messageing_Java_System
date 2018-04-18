import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.awt.Desktop;
import javax.imageio.*;
import javafx.scene.image.*;
import javafx.scene.media.*;

public class Client extends Application {
	static Scanner keyboard;
	private static InetAddress host;
	private static String name;
	private static Scanner networkInput, userEntry;
	private static PrintWriter networkOutput;
	static ObjectOutputStream outObj;
	static ObjectInputStream inObj;
	public File file;
	user U = new user();
	static byte[] attachmentObj = null;

	public static void main(String[] args) {
		launch(args);
	}

	Socket socket;
	Scanner input;
	PrintWriter output;
	TextField hostInput, lineToSend;

	public void start(Stage stage) {
		BorderPane rootPane;
		FlowPane hostPane, entryPane;
		Label hostPrompt, messagePrompt;
		Button closeConnection, refresh, inbox, sendM;
		Scene scene;

		rootPane = new BorderPane();
		hostPane = new FlowPane();

		hostPrompt = new Label("Enter UserName :");
		hostInput = new TextField();

		hostInput.setPrefColumnCount(25);
		hostPane.getChildren().add(hostPrompt);
		hostPane.getChildren().add(hostInput);
		rootPane.setTop(hostPane);
		hostPane.setAlignment(Pos.TOP_LEFT);
		hostInput.setOnAction(event -> {
			try {
				name = hostInput.getText();
				acceptHost(name);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		entryPane = new FlowPane();
		messagePrompt = new Label("Enter text:");

		inbox = new Button("Inbox");
		sendM = new Button("Send Message");

		inbox.setOnAction(event -> Inbox());
		sendM.setOnAction(event -> SendMessage());

		entryPane.getChildren().add(inbox);
		entryPane.getChildren().add(sendM);

		rootPane.setLeft(entryPane);

		closeConnection = new Button("Close connection");
		closeConnection.setOnAction(event -> closeDown());
		rootPane.setBottom(closeConnection);
		BorderPane.setAlignment(closeConnection, Pos.BOTTOM_CENTER);
		scene = new Scene(rootPane, 400, 200);

		stage.setScene(scene);
		stage.setTitle("Email Homepage");
		stage.show();
	}

	public void Inbox() {

		Stage stage = new Stage();
		BorderPane rootPane;
		FlowPane hostPane, entryPane;
		int pos;
		Label heading;
		Button read, attachment;
		Scene scene;
		ListView<String> received = new ListView<String>();

		rootPane = new BorderPane();
		hostPane = new FlowPane();
		heading = new Label("Inbox");
		hostPane.getChildren().add(heading);

		received.setMaxHeight(300);
		received.setMaxWidth(300);
		received.setEditable(false);
		rootPane.setTop(hostPane);
		rootPane.setLeft(received);
		BorderPane.setAlignment(received, Pos.CENTER_LEFT);
		received.getSelectionModel().selectedItemProperty();

		read = new Button("read");
		read.setOnAction(event -> theMessage(received.getSelectionModel().getSelectedIndex()));
		rootPane.setBottom(read);
		BorderPane.setAlignment(read, Pos.BOTTOM_CENTER);

		scene = new Scene(rootPane, 500, 350);

		stage.setScene(scene);
		stage.setTitle("Inbox");
		stage.show();
		//Fill stage with content

		networkOutput.println("read");

		try {

			System.out.println("I got this far1");
			U = (user) inObj.readObject();
			System.out.println("Recieved: " + U.messagesInBox);
			if (U.messagesInBox == 0) {
				received.getItems().add("Mailbox empty");
			} else {
				for (int i = 0; i < U.messagesInBox; i++) {
					received.getItems().add(U.inbox[i].getFrom() + ": " + U.inbox[i].getsubject());
				}
			}
			//U = null;
		} catch (IOException ioex) {
			System.out.println("IoEx I got no clue someone help    MOST LIKELY ORDER OF DATA BEING SENT IS WRONG");
		} catch (ClassNotFoundException cnfe) {
			System.out.println("CNFEx");
		}
	}

	public void theMessage(int ID) {
		int pos = (ID);
		Stage stage = new Stage();
		BorderPane rootPane;
		FlowPane hostPane, entryPane;
		Label M;
		Button OAttachment;
		Scene scene;

		rootPane = new BorderPane();
		hostPane = new FlowPane();

		M = new Label(U.inbox[pos].getBody());
		OAttachment = new Button("Save Attachment");
		OAttachment.setOnAction(event -> attachmentwindow(ID, stage));

		hostPane.getChildren().add(OAttachment);
		hostPane.getChildren().add(M);
		BorderPane.setAlignment(M, Pos.BOTTOM_CENTER);
		rootPane.setTop(hostPane);
		hostPane.setAlignment(Pos.TOP_LEFT);

		scene = new Scene(rootPane, 500, 350);

		stage.setScene(scene);
		stage.setTitle("Message");
		stage.show();
		//Fill stage with content

	}

	public void SendMessage() {
		Stage stage = new Stage();
		BorderPane rootPane;
		FlowPane hostPane;
		TextField TargetMessage, SubjectMessage, attachID;
		TextArea MessageToSend;
		Scene scene;
		Button send, attachment;

		FileChooser fileChooser;

		fileChooser = new FileChooser();
		fileChooser.setTitle("Choose attachment");

		MessageToSend = new TextArea();
		TargetMessage = new TextField();
		SubjectMessage = new TextField();
		attachID = new TextField();
		rootPane = new BorderPane();
		hostPane = new FlowPane();

		attachment = new Button("Attachment");
		attachment.setOnAction(event -> attachFile(fileChooser, stage));
		send = new Button("Send Message");
		send.setOnAction(event -> doSend(MessageToSend.getText(), TargetMessage.getText(), SubjectMessage.getText(),
				attachID.getText()));

		MessageToSend.setPrefColumnCount(25);
		MessageToSend.setEditable(true);
		MessageToSend.setPromptText("Enter your email Content here");
		MessageToSend.setWrapText(true);
		TargetMessage.setPrefColumnCount(25);
		TargetMessage.setEditable(true);
		TargetMessage.setPromptText("Enter Who this is for");
		SubjectMessage.setPrefColumnCount(25);
		SubjectMessage.setEditable(true);
		SubjectMessage.setPromptText("Subject of message ?");
		attachID.setPrefColumnCount(25);
		attachID.setEditable(true);
		attachID.setPromptText("Attachment Type");

		hostPane.getChildren().add(TargetMessage);
		hostPane.getChildren().add(SubjectMessage);
		hostPane.getChildren().add(MessageToSend);
		hostPane.getChildren().add(attachID);
		hostPane.getChildren().add(send);
		hostPane.getChildren().add(attachment);

		rootPane.setCenter(hostPane);
		scene = new Scene(rootPane, 300, 350);

		stage.setScene(scene);
		stage.setTitle("Send Message");
		stage.show();
		//Fill stage with content
	}

	public void acceptHost(String name) throws IOException {
		try {
			host = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			System.out.println("Host ID not found!");
			System.exit(1);
		}
		System.out.println("the users name is " + name);
		final int PORT = 1234;
		socket = new Socket(host, PORT); //connection to server
		networkInput = new Scanner(socket.getInputStream());
		networkOutput = new PrintWriter(socket.getOutputStream(), true);
		outObj = new ObjectOutputStream(socket.getOutputStream());
		inObj = new ObjectInputStream(socket.getInputStream());
		keyboard = new Scanner(System.in);
		networkOutput.println(name);
	}

	private static void doSend(String message, String destination, String Subject, String attachtype) {
		networkOutput.println("send");

		// Make Email object and populate with data
		email E = new email();
		E.setto(destination);
		E.setBody(message);
		E.setFrom(name);
		E.setsubject(Subject);
		E.setattachType(attachtype);
		E.setAttachment(attachmentObj);

		try {
			outObj.writeObject(E);
			outObj.flush();
		} catch (IOException uhEx) {
			System.out.println("\nHost ID not found!\n");
		}
	}

	public void acceptMessageAndResponse() {
		String text, response, host;

		text = lineToSend.getText();
		response = "";
		output.println(text);
		response = input.nextLine();

		lineToSend.setText("");
	}

	public void closeDown() {
		networkOutput.println(name);
		networkOutput.println("quit");
	}

	public void attachmentwindow(int pos, Stage stage) {
		FileChooser fileChooser;
		String type = U.inbox[pos].getattachType();

		File file = null;
		fileChooser = new FileChooser();
		fileChooser.setTitle("Where do you want the file to save");
		file = fileChooser.showSaveDialog(stage);
		try {
			FileOutputStream stream = new FileOutputStream(file);
			try {
				stream.write(U.inbox[pos].getAttachment());
			} finally {
				stream.close();
			}
		} catch (IOException uhEx) {
			System.out.println("\nFile not found\n");
		}
		System.out.println("\nI GOT TO HERE");
		///// Data check for which to display to show
		if (type.equals("IMAGE")) {
			stage = new Stage();
			Image image;
			ImageView imageView;
			BorderPane pane;
			Scene scene;
			image = new Image(new ByteArrayInputStream(U.inbox[pos].getAttachment()));
			imageView = new ImageView(image);
			imageView.setFitWidth(500);
			imageView.setFitHeight(350);
			imageView.setPreserveRatio(true);
			pane = new BorderPane();
			pane.setCenter(imageView);

			// Add the layout pane to a scene
			scene = new Scene(pane);

			// Add the scene to the stage, set the title
			// and show the stage
			stage.setScene(scene);
			stage.setTitle("Image Demo");
			stage.show();
		}
		////////////////// VIDEO AND AUDIO USE ALMOST THE SAME
		Media video, sound;

		MediaView viewer;
		BorderPane pane;
		FlowPane Fpane;
		Scene scene;
		Button startBtn, stopBtn, pauseBtn;
		TextArea content = new TextArea();

		startBtn = new Button("Start");
		stopBtn = new Button("Stop");
		pauseBtn = new Button("Pause");
		Fpane = new FlowPane();
		pane = new BorderPane();
		scene = new Scene(pane);

		if (type.equals("VIDEO")) {
			MediaPlayer playerV;
			video = new Media(file.toURI().toString());
			playerV = new MediaPlayer(video);
			playerV.setAutoPlay(true);

			viewer = new MediaView(playerV);
			viewer.setFitWidth(700);
			viewer.setFitHeight(300);
			viewer.setPreserveRatio(true);
			startBtn.setOnAction(event -> playerV.play());
			stopBtn.setOnAction(event -> playerV.stop());
			pauseBtn.setOnAction(event -> playerV.pause());

			Fpane = new FlowPane();
			pane = new BorderPane(viewer);
		}
		if (type.equals("AUDIO")) {
			MediaPlayer playerA;
			sound = new Media(file.toURI().toString());
			playerA = new MediaPlayer(sound);
			playerA.setAutoPlay(true);

			startBtn.setOnAction(event -> playerA.play());
			stopBtn.setOnAction(event -> playerA.stop());
			pauseBtn.setOnAction(event -> playerA.pause());

			Fpane = new FlowPane();
			pane = new BorderPane();
			pane.setBottom(Fpane);

			// Add the scene to the stage, set the title
			// and show the stage
			stage.setScene(scene);
			stage.setTitle("Audio");
			stage.show();
		}
		if (type.equals("TEXT")) {
			

			try {
				BufferedReader in = new BufferedReader(new FileReader(file));

				String line;
				while ((line = in.readLine()) != null) {
					content.appendText(line);
				}
				in.close();
			} catch (IOException IO) {
				System.out.println("\nFailed the read\n");
			}

			Fpane.getChildren().add(content);
			pane = new BorderPane();
		}

		pane.setBottom(Fpane);
		Fpane.getChildren().add(startBtn);
		Fpane.getChildren().add(stopBtn);
		Fpane.getChildren().add(pauseBtn);

		// Add the layout pane to a scene
		scene = new Scene(pane);

		// Add the scene to the stage, set the title
		// and show the stage
		stage.setScene(scene);
		stage.setTitle("Attachment");
		stage.show();

	}

	public void attachFile(FileChooser fileChooser, Stage stage) {
		file = fileChooser.showOpenDialog(stage);
		attachmentObj = new byte[(int) file.length()];

		try {

			FileInputStream fis = new FileInputStream(file);
			fis.read(attachmentObj); //read file into bytes[]
			fis.close();
		} catch (IOException uhEx) {
			System.out.println("\nfile not saved\n");
		}
	}
}
