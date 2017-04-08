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

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Client implements Initializable{
    public TextArea tArea;
    public TextField tField;
    public ImageView imageView;
    public TextArea tOnline;

    private Message message;
    private Connection connection;
    private ClientExecutor clientExecutor;
    private ArrayList<String> allOnline = new ArrayList<>();

    public Client() {
            HelperForClient.getStage().setOnCloseRequest(e->onCloseProgram());
            connection = HelperForClient.getConnection();

            clientExecutor=new ClientExecutor();
            clientExecutor.start();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.setImage(new Image("file:somePicture.png"));
        tArea.setEditable(false);
        tOnline.setEditable(false);
        try {
            HelperForClient.getConnection().send(new Message(MessageType.REQUEST_FOR_ONLINE,null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onButtonClick() {
        try {
            message = new Message(MessageType.SEND_TEXT_MESSAGE,Login.userName+": "+tField.getText());
            connection.send(message);///////////////////////////
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
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            //boxWithHyperlinks.getChildren().add(new Hyperlink(selectedFile.getName()));
        }
    }

    public void onCloseProgram(){
        try {
            message = new Message(MessageType.EXIT, null);
            connection.send(message);
            clientExecutor.stop();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    private class ClientExecutor extends Thread {
        public void run() {
                while (true) {
                    try {
                        message = connection.receive();
                        switch (message.getMessageType()) {
                            case BROAD_CAST:
                                tArea.appendText(message.getData() + "\n");
                                break;
                            case ADD_TO_ONLINE:
                                allOnline.add(message.getData());
                                tOnline.appendText(message.getData());
                                break;
                            case REMOVE_FROM_ONLINE:
                                allOnline.remove(message.getData());
                                tOnline.clear();
                                for(String s : allOnline){
                                    tOnline.appendText(s);
                                }
                                break;
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

}

