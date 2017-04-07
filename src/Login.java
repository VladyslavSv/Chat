import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by mmari on 05.04.2017.
 */
public class Login{
    public Button bConnect;
    public TextField tfName;
    public static String userName = "User";

    public void onButtonConnect() throws IOException {
        if(tfName.getText().equals(""))
            return;
        userName = tfName.getText();
        Stage stage = (Stage) bConnect.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("\\sample\\sample.fxml"));

        stage.setScene(new Scene(root));
        stage.setTitle("Chat Room");
        stage.show();
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == (KeyCode.ENTER) && !tfName.getText().equals("")){
            try {
                onButtonConnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}