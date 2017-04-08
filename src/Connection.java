import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable
{
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        out=new ObjectOutputStream(socket.getOutputStream());
        in=new ObjectInputStream(socket.getInputStream());
    }

    public void send(Message message) throws IOException {
            out.writeObject(message);
            out.flush();
    }

    public Message receive() throws IOException, ClassNotFoundException {
            return (Message) in.readObject();

    }

    public SocketAddress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }

    public Boolean getBoolean() throws IOException, ClassNotFoundException{
        return (Boolean) in.readObject();
    }

    public void sendBoolean(Boolean b) throws IOException{
        out.writeObject(b);
        out.flush();
    }

    @Override
    public void close() throws IOException
    {
        in.close();
        out.close();
        socket.close();
    }
}
