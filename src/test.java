// Model solution to Task 2 of Lab 22.

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.io.*;

public class test extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }


    FileChooser fileChooser;


    public void start(Stage primaryStage)
    {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Save Personnel File");

        Button saveButton = new Button("Save file");
        BorderPane pane = new BorderPane();
        pane.setCenter(saveButton);
        saveButton.setOnAction(event -> saveFile(primaryStage));

        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }


    public void saveFile(Stage stage)
    {

        File file = fileChooser.showSaveDialog(stage);
        if (file == null)
            System.exit(0);

        try {
            ObjectOutputStream outStream =
              new ObjectOutputStream(new FileOutputStream(file));
            for (int i = 0; i < 10; i++)
                outStream.writeObject("staff[i]");
            outStream.close();
            System.exit(0);
        }
        catch (IOException ioEx) {
            ioEx.printStackTrace();
            System.exit(1);
        }
    }
}