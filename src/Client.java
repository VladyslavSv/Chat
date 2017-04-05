import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Client {
    private String str;
    private String ip;
    private String username;
    private String message;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private TextField userText;

    public Client(String ip) {
        this.ip = ip;
        userText.setEditable(false); //сначала запрещаем редактирование чата, после соединения с сервером разрешаем
        userText.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    sendMessage(event.toString());
                    userText.setText("");
                }
            }
        );
    }

    private void sendMessage(String actionCommand) {
        try {
            output.writeObject(username + "" + actionCommand);
            output.flush();
            showMessage(username + "" + actionCommand + "\n");
        } catch (IOException e) {
            //выводим в свой чат chatWindow.append("ERROR WITH SENDING MESSAGE\n");

        }
    }
    //
    public void startRunning(){
        try {
            connect();
            setupStreams();
            whileChatting();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            closeResources();
        }
    }

    private void closeResources() {
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ableToType(final boolean b) {
        Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //запрещаем edit текстового поля userText.setEditable(b);

                }
            }
        );
    }

    private void whileChatting() {
        ableToType(true);
        do{
            try {
                message = (String) input.readObject();
                showMessage(message + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }while (!message.equals("SERVER -  END"));
    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("Set up completed!\n");
    }

    private void connect() throws IOException {
        //showMessage("Connecting to the server...\n");
        connection = new Socket(ip,6789);
        //showMessage("Connected to the server - " + connection.getInetAddress().getHostName() + "\n");
    }


    private void showMessage(final String s) {
        Platform.runLater(new Runnable() {
                              @Override
                              public void run() {
                                  //выводим в свой чат chatWindow.append(s);
                              }
                          }
        );
    }
}

