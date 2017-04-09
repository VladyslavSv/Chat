/**
 * Created by mmari on 05.04.2017.
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class SocketUtil {

    static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void close(ServerSocket serverSoc) {
        if (serverSoc != null) {
            try {
                serverSoc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}