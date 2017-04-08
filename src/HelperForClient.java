import javafx.stage.Stage;

/**
 * Created by ASUS on 08.04.2017.
 */
public class HelperForClient {
    private static Stage stage;
    private static Connection connection;
    public static String userName;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        HelperForClient.stage = stage;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        HelperForClient.connection = connection;
    }
}
