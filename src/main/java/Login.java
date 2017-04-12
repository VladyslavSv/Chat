import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mmari on 05.04.2017.
 */
public class Login implements Initializable{

    public JFXButton bConnect;
    static String userName = "User";
    public Label error;
    public JFXTextField tfName;

    public void onButtonConnect() {
            try {
            //получаем сообщение из текстового поля
            String textFromArea=tfName.getText();
                //проверяем не пустое ли поле
                if (textFromArea.equals(""))
                    return;
            userName = textFromArea;
            //отправляем на проверку серверу
            Message message=new Message(MessageType.SEND_USER_NAME,userName);
            HelperForClient.getConnection().send(message);
                //если сервер одобрил переходим в chat room
                if (HelperForClient.getConnection().getBoolean()) {

                    Stage stage = (Stage) bConnect.getScene().getWindow();
                    URL url = new File("src\\main\\java\\scenes\\sample.fxml").toURL();
                    Parent root = FXMLLoader.load(url);

                    stage.setScene(new Scene(root));
                    stage.setTitle("Chat Room");
                    stage.show();
                }
                else {
                    tfName.setText("");
                    bConnect.setDisable(true);
                    error.setVisible(true);
                }
            }
            catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }

    }
    //127.0.0.1
    //195.138.81.175
    @Override
    public void initialize(URL location, ResourceBundle resources){
        try {
            HelperForClient.setConnection(new Connection(new Socket("127.0.0.1", 6969)));
        }
        catch (IOException e){
            System.out.println("Error with opening connection");
            e.printStackTrace();
        }
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        if(tfName.getText().length()>=4 && tfName.getText().length()<14) bConnect.setDisable(false);
        else bConnect.setDisable(true);

        if(!bConnect.isDisabled() && keyEvent.getCode().equals(KeyCode.ENTER)){
            onButtonConnect();
        }
    }

    public void onConnectPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER))
            onButtonConnect();
    }
}