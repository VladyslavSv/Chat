import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Client implements Initializable{
    public TextArea tArea;
    public TextField tField;
    public VBox vBox;

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
        tArea.setEditable(false);
        try {
            HelperForClient.getConnection().send(new Message(MessageType.REQUEST_FOR_ONLINE,null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onButtonClick() {
        try {
            if(tField.getText().length()>0) {
                message = new Message(MessageType.SEND_TEXT_MESSAGE, Login.userName + ": " + tField.getText());
                connection.send(message);///////////////////////////
                tField.setText("");
                tField.requestFocus();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == (KeyCode.ENTER)){
            onButtonClick();
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
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        vBox.getChildren().clear();
                                        for(String s : allOnline) {
                                            vBox.getChildren().add(new Label(s));
                                        }
                                    }
                                });
                                break;
                            case REMOVE_FROM_ONLINE:
                                allOnline.remove(message.getData());
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        vBox.getChildren().clear();
                                        for(String s : allOnline)
                                            vBox.getChildren().add(new Label(s));
                                    }
                                });
                                break;
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

}

