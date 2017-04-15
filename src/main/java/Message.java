import java.io.Serializable;

/**
 * Created by ASUS on 08.04.2017.
 */
public class Message implements Serializable{
    private MessageType messageType;
    private String data;
    private String receiver;
    private String sender;

    Message(MessageType messageType, String data, String receiver, String sender) {
        this.messageType = messageType;
        this.data = data;
        this.receiver = receiver;
        this.sender = sender;
    }
    Message(MessageType messageType, String data) {
        this.messageType = messageType;
        this.data = data;
    }

    String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
