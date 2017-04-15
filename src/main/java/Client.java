import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Client implements Initializable{
    public TextArea tArea;
    public TextField tField;
    public VBox vBox;
    public TabPane tabPane;
    public Tab tabMain;

    private Message message;
    private Connection connection;
    private ClientExecutor clientExecutor;
    private ArrayList<String> allOnline = new ArrayList<>();
    private Map<String, Tab> openTabs = new HashMap<>();
    private String myName;
    private ContextMenu contextMenu;

    public Client() {
        myName = Login.userName;
        HelperForClient.getStage().setOnCloseRequest(e->onCloseProgram());
        connection = HelperForClient.getConnection();

        clientExecutor=new ClientExecutor();
        clientExecutor.start();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contextMenu = new ContextMenu();
        MenuItem item = new MenuItem("Close");//В контекстом меню один пункт
        item.setOnAction(event -> {//при нажатии на close
            openTabs.remove(tabPane.getSelectionModel().getSelectedItem().getText());//из мапы удаляется запись
            tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());//удаляется не та вкладка по которой кликнули, а активная(ПРОБЛЕМА)
        });
        contextMenu.getItems().add(item);//В контекст добавляю пункт
        tArea.setEditable(false);
        try {
            HelperForClient.getConnection().send(new Message(MessageType.REQUEST_FOR_ONLINE,null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onButtonClick() {
        try {
            if(tField.getText().length()>0) {
                if(tabPane.getSelectionModel().getSelectedIndex() !=0){//Если мы не во вкладке main(0), то будем отправлять личное сообщение
                    Tab t = tabPane.getSelectionModel().getSelectedItem();//Получаем текущую вкладку
                    String receiverTabName = t.getText();//Название вкладки - имя получателя
                    message = new Message(MessageType.SEND_PERSONAL_MESSAGE,myName + ": " + tField.getText(),receiverTabName,myName);//создаем сообщение
                    connection.send(message);//отправляем сообщение серверу
                    selfMessage(t);//отправляем сообщение себе
                    tField.setText("");//TextField одно на всё приложение.Поле tField очистится во всех вкладках
                    // Решение: либо создавать TextField для каждой вкладки либо запоминать текст и вставлять обратно.
                    tField.requestFocus();
                    return;
                }
                message = new Message(MessageType.SEND_TEXT_MESSAGE, myName + ": " + tField.getText());
                connection.send(message);
                tField.setText("");
                tField.requestFocus();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == (KeyCode.ENTER)){
            onButtonClick();
        }
    }
    private void onCloseProgram(){
        try {
            message = new Message(MessageType.EXIT, null);
            connection.send(message);
            clientExecutor.stop();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private void openTab(String tabName){
        if (tabName.equals(myName))//вкладку с собой открыть нельзя
            return;

        if(openTabs.containsKey(tabName)){//если вкладка не открыта
            tabPane.getSelectionModel().select(openTabs.get(tabName));//переходим в другую вкладку
        }else {
            Tab myNewTab = new Tab(tabName);//создаю вкладку
            myNewTab.setContextMenu(contextMenu);//устанавливаю контекстное меню
            TextArea t = new TextArea();
            t.setPrefSize(462.0,272.0);
            t.setEditable(false);
            myNewTab.setContent(new AnchorPane(t));//устанавливаю AnchorPane c TextArea внутри(child)
            tabPane.getTabs().add(myNewTab);//добавляю в Панель Вкладок
            tabPane.getSelectionModel().select(myNewTab);//Выбираю созданную
            openTabs.put(tabName, myNewTab);//В мапу записываю
            myNewTab.setOnClosed(e -> openTabs.remove(tabName));//при закрытии вкладки удаляется запись из открытых вкладок
        }
    }



    private void selfMessage(Tab tab){
        AnchorPane a = (AnchorPane) tab.getContent();//Во вкладке есть anchorPane
        TextArea ta = (TextArea) a.getChildren().get(0);//В anchorPane есть TextArea
        ta.appendText(myName + ": " + tField.getText() + "\n");//Вставляем текст в личный textArea.TextArea создаётся для каждой вкладки
    }
    private void selfMessage(Message m){
        Tab t = openTabs.get(m.getSender());//Если уже открыто, если нет - откроем
        AnchorPane a = (AnchorPane) t.getContent();
        TextArea tAre = (TextArea) a.getChildren().get(0);//По идее должны получить TextArea local к Табу
        tAre.appendText(m.getData());
    }

    private class ClientExecutor extends Thread {
        public void run() {
            while (true) {
                try {
                    message = connection.receive();
                    switch (message.getMessageType()) {
                        case SEND_PERSONAL_MESSAGE://Получаем личное сообщение от сервера
                            if(openTabs.containsKey(message.getSender())) {//Если вкладка с личкой открыта
                                selfMessage(message);//переписываем сообщение себе в чат
                            }else{//вкладка с личкой закрыта
                                Platform.runLater(() -> {
                                    openTab(message.getSender());//Открываем вкладку отправителя у себя
                                    selfMessage(message);////переписываем сообщение себе в чат
                                });
                            }
                            break;
                        case BROAD_CAST:
                            tArea.appendText(message.getData() + "\n");
                            break;
                        case ADD_TO_ONLINE:
                            allOnline.add(message.getData());
                            Platform.runLater(() -> {
                                vBox.getChildren().clear();
                                for(String s : allOnline) {
                                    Label l = new Label(s);
                                    l.setOnMouseClicked(event -> {
                                        if (event.getButton().equals(MouseButton.PRIMARY)) {
                                            if (event.getClickCount() == 2) {
                                                int sz = l.getText().length();
                                                openTab(l.getText().substring(0,sz-1));//Без переноса на новую строку таб нужен
                                            }
                                        }
                                    });
                                    l.setId(s);
                                    vBox.getChildren().add(l);
                                }
                            });
                            break;
                        case REMOVE_FROM_ONLINE:
                            Platform.runLater(() -> {
                                vBox.getChildren().remove(
                                        vBox.getChildren().get(
                                                allOnline.indexOf(
                                                        message.getData())));
                                allOnline.remove(message.getData());
                            });
                            break;
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}