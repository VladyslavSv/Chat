import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

/**
 * Created by ASUS on 07.04.2017.
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL url = new File("src\\main\\java\\scenes\\login.fxml").toURL();
        Parent root = FXMLLoader.load(url);

        HelperForClient.setStage(primaryStage);

        primaryStage.getIcons().add(new Image("file:chat-icon.png"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}