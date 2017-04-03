import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ASUS on 02.04.2017.
 */
public class Server {
    private ServerSocket server;
    private ArrayList<Socket> sockets;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Server() {
        Socket connection = null;
        try {
            server = new ServerSocket(6789);
            while(true) {
                connection = server.accept();
                sockets.add(connection);
                System.out.println("HostName: " + connection.getInetAddress().getHostName());
            }
        } catch(IOException e) {
            System.out.println("Conncetion Failed");
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            output.close();
            input.close();
            for(int i = 0;i<sockets.size();i++){
                if(sockets.get(i) != null){
                    sockets.get(i).close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
