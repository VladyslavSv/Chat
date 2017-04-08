import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mmari on 05.04.2017.
 */
public class Login implements Initializable{
    public Button bConnect;
    public TextField tfName;
    public static String userName = "User";

    public void onButtonConnect() {
            try {
            //получаем сообщение из текстового поля
            String textFromAread=tfName.getText();
                //проверяем не пустое ли поле
                if (textFromAread.equals(""))
                    return;
            userName = textFromAread;
            //отправляем на проверку серверу
            Message message=new Message(MessageType.SEND_USER_NAME,userName);
            HelperForClient.getConnection().send(message);
                //если сервер одобрил переходим в chat room
                if (HelperForClient.getConnection().getBoolean()==true) {

                    Stage stage = (Stage) bConnect.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("scenes/sample.fxml"));

                    stage.setScene(new Scene(root));
                    stage.setTitle("Chat Room");
                    stage.show();
                }
                else {
                    alert();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch(ClassNotFoundException e) {
                e.printStackTrace();
            }

    }

    private void alert(){
        //уведомить что такой пользователь уже существует
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("This name is already taken!!!");
        alert.showAndWait();
    }
    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == (KeyCode.ENTER) && !tfName.getText().equals("")){
                onButtonConnect();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        try {
            HelperForClient.setConnection(new Connection(new Socket("195.138.81.175", 6969)));
        }
        catch (IOException e){
            System.out.println("Error with opening connection");
            e.printStackTrace();
        }
    }
}