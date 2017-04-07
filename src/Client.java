import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Client implements Initializable{
    public TextArea tArea;
    public TextField tField;
    public ImageView imageView;
    public VBox boxWithHyperlinks;

    private String message;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    public Client() {
        try {

            output = new ObjectOutputStream(Login.socket.getOutputStream());
            System.out.println(Login.userName);
            output.writeObject(Login.userName);
            output.flush();
            input = new ObjectInputStream(Login.socket.getInputStream());
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

