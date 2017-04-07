import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static Map<String, ObjectOutputStream> clients =
            Collections.synchronizedMap
            (new HashMap<String, ObjectOutputStream>());

    private Server() {
    }

    public static boolean isNameValid(String nameCheck){
        if(clients.size()>0){
            if(clients.containsKey(nameCheck)){
                return false;
            }
            return true;
        }
        return true;
    }

    private void start() {
        ServerSocket serverSocket = null;
        Socket socket;
        try {
            serverSocket = new ServerSocket(6969);
            System.out.println("Server Started");
            while(true) {
                socket = serverSocket.accept();
                System.out.println("Yes! " + socket.getPort());
                ServerReceiver thread = new ServerReceiver(socket);
                thread.start();
            }
        } catch(IOException e) {
            System.out.println("Connection Failed");
        } finally {
            SocketUtil.close(serverSocket);
        }
    }

    private void broadCast(String msg) {

        for (String s : clients.keySet()) {
            try {
                ObjectOutputStream out = clients.get(s);
                out.writeObject(msg);
                out.flush();
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }

    class ServerReceiver extends Thread {
        Socket socket;
        ObjectInputStream input;
        ObjectOutputStream output;

        ServerReceiver(Socket socket) {
            this.socket = socket;

            try {
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
            } catch(IOException e) {
                System.out.println("Stream Failed");
            }
        }

        @Override
        public void run() {
            String name = "";
            try {
                name = (String) input.readObject();
                if(clients.containsKey(name)){
                    name+=clients.size();
                }
                broadCast(name + " : Connected");
                clients.put(name, output);

                while (input != null) {
                    broadCast(name + " : " + input.readObject());
                }
            } catch(IOException e) {
                System.out.println("Read Failed");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                broadCast(name + " : Disconnected");
                clients.remove(name);
            }
        }
    }
}