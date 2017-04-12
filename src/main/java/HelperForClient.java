import javafx.stage.Stage;

/**
 * Created by ASUS on 08.04.2017.
 */
public class HelperForClient {
    private static Stage stage;
    private static Connection connection;
    public static String userName;

    static Stage getStage() {
        return stage;
    }

    static void setStage(Stage stage) {
        HelperForClient.stage = stage;
    }

    static Connection getConnection() {
        return connection;
    }

    static void setConnection(Connection connection) {
        HelperForClient.connection = connection;
    }
}
