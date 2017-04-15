import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static ConfigurableApplicationContext context=new ClassPathXmlApplicationContext("spring.xml");
    private  Map<String, ObjectOutputStream> clients;

    private Server() {
        clients = Collections.synchronizedMap
                (new HashMap<String, ObjectOutputStream>());
        start();
    }

    public static void main(String[] args) {
        Server server=(Server)context.getBean("server");
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

    private void broadCast(Message message) {

        for (String s : clients.keySet()) {
            try {
                ObjectOutputStream out = clients.get(s);
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
    }

    private class ServerReceiver extends Thread {
        private Socket socket;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private Message message;
        private String userName;

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
            try {
                boolean forTheCycle=true;
                //получаем , проверяем и добавляем нового пользователся
                while (true){
                    if(getNameAndAddToList()){
                        break;
                    }
                }
                //главный обработчик сообщений
                while (forTheCycle) {
                    //получаем сообщение
                    message=(Message)input.readObject();
                    //обрабатываем его
                    switch (message.getMessageType()){
                        //если пользователь отослал сообщение в чат
                        //оповестим всех об этом
                        case SEND_TEXT_MESSAGE:
                            broadCast(new Message(MessageType.BROAD_CAST,message.getData()));
                            break;
                        case REQUEST_FOR_ONLINE:
                            for(String s : clients.keySet()) {
                                output.writeObject(new Message(MessageType.ADD_TO_ONLINE, s+"\n"));
                                output.flush();
                            }
                            break;
                        case SEND_PERSONAL_MESSAGE:
                            String receiver = message.getReceiver();//получатель
                            String sender = message.getSender();//отправитель
                            String mes = message.getData();//собщение
                            for(String s : clients.keySet()){//ищем в клиентах получателя
                                if(receiver.equals(s)){//если нашли
                                    ObjectOutputStream sOut = clients.get(s);//получаем его ObjectOutputStream
                                    sOut.writeObject(new Message(MessageType.SEND_PERSONAL_MESSAGE,mes+"\n",receiver,sender));//отправляем сообщение
                                    sOut.flush();
                                }
                            }
                            break;
                        //если пользователь захотел выйти
                        case EXIT:
                            forTheCycle=false;
                            break;

                    }
                }
            } catch(IOException e) {
                System.out.println("Read Failed");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                close();
            }
        }

        private boolean getNameAndAddToList() throws IOException,ClassNotFoundException{
            //получаем от клиента сообщение
            message = (Message) input.readObject();
                // получаем из сообщения имя
                userName=message.getData();
                //если такой пользователь уже авторизирован то уведомим об этом
                if (clients.containsKey(userName)) {
                    output.writeObject(Boolean.FALSE);
                    return false;
                }
                else {
                    output.writeObject(Boolean.TRUE);
                    //оповестить всех о том что этот пользователь присоединилмя к чату
                    broadCast(new Message(MessageType.BROAD_CAST,"@ "+userName + " Connected to chat"));
                    System.out.println(userName + " : Connected");
                    //добавить пользователя в список всех пользователей
                    broadCast(new Message(MessageType.ADD_TO_ONLINE,userName+"\n"));
                    clients.put(userName, output);

                    output.flush();
                    return true;
                }
        }

        private void close(){
            try {
                broadCast(new Message(MessageType.BROAD_CAST,userName + " : Disconnected"));
                System.out.println(userName + " : Disconnected");
                //удаляем пользователя из чата
                clients.remove(userName);
                broadCast(new Message(MessageType.REMOVE_FROM_ONLINE,userName+"\n"));
                output.flush();
                input.close();
                output.close();
                SocketUtil.close(socket);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}