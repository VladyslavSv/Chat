import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Client implements Initializable{
    public TextArea tArea;
    public TextField tField;
    private String message;
    private ObjectInputStream input;
    private ObjectOutputStream output;
//195.138.81.175
    public Client() {
        try {
            Socket socket = new Socket("127.0.0.1", 6789);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject("User");
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            chat();
        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tArea.setEditable(false);
    }
}

