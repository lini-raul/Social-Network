package controller;
import domain.friendship.Friendship;
import domain.friendship.FriendshipDto;
import domain.friendship.Status;
import domain.message.Message;
import domain.user.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.Network;
import utils.observer.Observer;
import validators.RepositoryException;
import validators.ValidationException;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class UserController implements Observer {
    private Network service;
    private Stage dialogStage;
    private User user;

    private ObservableList<FriendshipDto> friendships = FXCollections.observableArrayList();
    private ObservableList<User> nonFriendUsers = FXCollections.observableArrayList();

    private ObservableList<FriendshipDto> sentRequests = FXCollections.observableArrayList();

    @FXML
    Label labelUsername;
    @FXML
    TextField textFieldSearchFriend;
    @FXML
    TextField textFieldSearchUser;
    @FXML
    Button buttonRemoveFriend;
    @FXML
    Button buttonAddFriend;
    @FXML
    Button buttonFriendRequests;


    @FXML
    TableView<FriendshipDto> tableViewFriends;
    @FXML
    TableColumn<FriendshipDto,String> tableColumnFriends;
    @FXML
    TableColumn<FriendshipDto,String> tableColumnFriendsFrom;

    @FXML
    TableView<User> tableViewUsers;
    @FXML
    TableColumn<User,String> tableColumnUsername;
    @FXML
    TableColumn<User,String> tableColumnName;


    @FXML
    TableView<FriendshipDto> tableViewSentRequests;
    @FXML
    TableColumn<User,String> tableColumnUsernameRequests;
    @FXML
    TableColumn<User,String> tableColumnStatusRequests;
    @FXML
    Button buttonCancelRequest;


    @FXML
    TextField textFieldSearchFriend_2;
    @FXML
    TableView<FriendshipDto> tableViewFriendsMessages;
    @FXML
    TableColumn<FriendshipDto,String> tableColumnFriendsMessages;
    @FXML
    VBox VBoxMessages;
    @FXML
    TextField textFieldMessages;
    @FXML
    Button buttonSendMessage;




    public void setService(Network service, Stage stage,User user){
        this.service = service;
        service.addObserver(this);
        this.dialogStage = stage;
        this.user = user;
        initializeUserFriendsController();
        initializeUsersController();
        initializeUserFriendRequestsController();
        initializeUserFriendsMessagesController();
    }


    @Override
    public void update() {
        initializeUserFriendsController();
        initializeUsersController();
        initializeUserFriendRequestsController();
        initializeUserFriendsMessagesController();

    }

    private void initializeUserFriendsController() {
        labelUsername.setText(user.getUsername());

        List<FriendshipDto> f = new ArrayList<>();
        service.getFriendshipsOfUser(user).forEach(f::add);
        friendships = FXCollections.observableArrayList(f);

        tableViewFriends.setItems(friendships);

        tableColumnFriends.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnFriendsFrom.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));
    }

    private void initializeUserFriendsMessagesController() {
        List<FriendshipDto> f = new ArrayList<>();
        service.getFriendshipsOfUser(user).forEach(f::add);
        friendships = FXCollections.observableArrayList(f);

        tableViewFriendsMessages.setItems(friendships);

        tableColumnFriendsMessages.setCellValueFactory(new PropertyValueFactory<>("username"));

        tableViewFriendsMessages.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                //Check whether item is selected and set value of selected item to Label
                if(tableViewFriendsMessages.getSelectionModel().getSelectedItem() != null)
                {
                    loadMessagesOnScreen();
                }
            }
        });
    }


    private void loadMessagesOnScreen() {
        VBoxMessages.getChildren().clear();

        FriendshipDto selectedItem = tableViewFriendsMessages.getSelectionModel().getSelectedItem();
        List<Message> messages = null;
        try {
            messages = service.getMessagesOfUsers(this.user,service.findUser(selectedItem.getUsername()));
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        for(Message m : messages){
            HBox messageBox = new HBox();
            messageBox.setPadding(new Insets(5, 5, 5, 10));
            Text text = new Text(m.getMessage());
            messageBox.getChildren().add(text);
            if(this.user.getUsername().equals(m.getSender()))
                messageBox.setAlignment(Pos.CENTER_RIGHT);
            else
                messageBox.setAlignment(Pos.CENTER_LEFT);

            VBoxMessages.getChildren().add(messageBox);
        }

    }

    private void initializeUsersController(){
        List<User> u = new ArrayList<>();
        service.getNonFriendsOfUser(user).forEach(u::add);
        nonFriendUsers = FXCollections.observableArrayList(u);

        tableViewUsers.setItems(nonFriendUsers);
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

    }

    public void initializeUserFriendRequestsController(){
        List<FriendshipDto> f = new ArrayList<>();
        service.getRequestsSentOfUser(user).forEach(f::add);
        sentRequests = FXCollections.observableArrayList(f);

        tableViewSentRequests.setItems(sentRequests);
        tableColumnUsernameRequests.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnStatusRequests.setCellValueFactory(new PropertyValueFactory<>("status"));


    }

    public void handleRemoveFriend(){
        FriendshipDto selectedItems = tableViewFriends.getSelectionModel().getSelectedItem();
        try {
            service.deleteFriendship(user.getUsername(),selectedItems.getUsername());
        } catch (RepositoryException e) {
            MessageAlert messageAlert = null;
            messageAlert.showErrorMessage(this.dialogStage, e.getMessage());
        }

    }

    public void handleSearchFriend(){
        Predicate<FriendshipDto> p1 = n->n.getUsername().contains(textFieldSearchFriend.getText());
        List<FriendshipDto> f = new ArrayList<>();
        service.getFriendshipsOfUser(user).stream().filter(p1).forEach(f::add);

        friendships = FXCollections.observableArrayList(f);

        tableViewFriends.setItems(friendships);

        tableColumnFriends.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnFriendsFrom.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));
    }
    public void handleSearchUser(){
        Predicate<User> p1 = n->n.getUsername().contains(textFieldSearchUser.getText());
        List<User> u = new ArrayList<>();
        service.getNonFriendsOfUser(user).stream().filter(p1).forEach(u::add);

        nonFriendUsers = FXCollections.observableArrayList(u);
        tableViewUsers.setItems(nonFriendUsers);
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    }
    public void handleAddFriend(){
        User selectedItems = tableViewUsers.getSelectionModel().getSelectedItem();
        try {
            service.addFriendship(user.getUsername(),selectedItems.getUsername(), Status.SEND);
        } catch (ValidationException e) {
            MessageAlert messageAlert = null;
            messageAlert.showErrorMessage(this.dialogStage, e.getMessage());
        } catch (RepositoryException e) {
            MessageAlert messageAlert = null;
            messageAlert.showErrorMessage(this.dialogStage, e.getMessage());
        }
    }

    public void handleFriendRequest(){
        try{
            Stage stage = new Stage();
            stage.setTitle("Friend requests");
            FXMLLoader friendRequestsLoader = new FXMLLoader();
            friendRequestsLoader.setLocation(getClass().getResource("/userUI/friendRequestsView.fxml"));

            AnchorPane friendRequestsLayout  = friendRequestsLoader.load();

            FriendRequestsController friendRequestsController = friendRequestsLoader.getController();
            friendRequestsController.setService(service,stage,user);

            stage.setScene(new Scene(friendRequestsLayout));
            stage.setWidth(400);
            stage.show();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void handleCancelFriendRequest(){
        FriendshipDto selectedItems = tableViewSentRequests.getSelectionModel().getSelectedItem();
        try{
            service.deleteFriendship(user.getUsername(), selectedItems.getUsername());

        } catch (RepositoryException e) {
            MessageAlert messageAlert = null;
            messageAlert.showErrorMessage(this.dialogStage, e.getMessage());
        }
    }


    public void handleSearchFriend_2(){
        Predicate<FriendshipDto> p1 = n->n.getUsername().contains(textFieldSearchFriend_2.getText());
        List<FriendshipDto> f = new ArrayList<>();
        service.getFriendshipsOfUser(user).stream().filter(p1).forEach(f::add);

        friendships = FXCollections.observableArrayList(f);

        tableViewFriendsMessages.setItems(friendships);

        tableColumnFriendsMessages.setCellValueFactory(new PropertyValueFactory<>("username"));
    }

    public void handleSendMessage() {
        String msg = textFieldMessages.getText();
        if(!msg.trim().isEmpty())
        {
            FriendshipDto selectedItem = tableViewFriendsMessages.getSelectionModel().getSelectedItem();
            User receiver;
            try {
                receiver =  service.findUser(selectedItem.getUsername());
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
            Message message = new Message(this.user.getUsername(),receiver.getUsername(),msg, LocalDateTime.now());
            try {
                service.addMessage(message);
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
            loadMessagesOnScreen();
            textFieldMessages.clear();
        }
    }
}
