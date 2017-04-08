import java.io.Serializable;

/**
 * Created by ASUS on 08.04.2017.
 */
public class Message implements Serializable{
    private MessageType messageType;
    private String data;

    public Message(MessageType messageType, String data) {
        this.messageType = messageType;
        this.data = data;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
