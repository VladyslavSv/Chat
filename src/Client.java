import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Client extends Application implements Initializable{
    public TextArea tArea;
    public TextArea tField;
    public ImageView imageView;
    public VBox boxWithHyperlinks;

    private String message;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    public Client() {
        try {
            Socket socket = new Socket("195.138.81.175", 6969);
            output = new ObjectOutputStream(socket.getOutputStream());
            System.out.println(Login.userName);
            output.writeObject(Login.userName);
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            chat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boxWithHyperlinks.getChildren().add(new Text("              Files"));
        boxWithHyperlinks.getChildren().add(new Text("--------------------------------------------"));
        imageView.setImage(new Image("file:somePicture.png"));
        tArea.setEditable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.getIcons().add(new Image("file:chat-icon.png"));
        Parent root = FXMLLoader.load(getClass().getResource("sample/login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaxHeight(900);
        primaryStage.show();
    }

    public void onButtonClick() {
        try {
            String message = tField.getText();
            output.writeObject(message);
            output.flush();
            tField.setText("");
            tField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == (KeyCode.ENTER)){
            onButtonClick();
        }
    }

    public void onPicturePressed(){
        System.out.println("Press");

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            boxWithHyperlinks.getChildren().add(new Hyperlink(selectedFile.getName()));
        }
    }

    private void chat(){
        new Thread(() -> {
            while(true) {
                try {
                    message = (String) input.readObject();
                    tArea.appendText(message + "\n");
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

