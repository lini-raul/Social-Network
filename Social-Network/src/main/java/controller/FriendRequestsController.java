package controller;

import domain.friendship.Friendship;
import domain.friendship.FriendshipDto;
import domain.friendship.Status;
import domain.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import service.Network;
import utils.observer.Observer;
import validators.RepositoryException;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsController implements Observer {
    private Network service;
    private Stage dialogStage;
    private User user;

    private ObservableList<User> friendships = FXCollections.observableArrayList();

    @FXML
    TableView<User> tableViewFriendRequests;
    @FXML
    TableColumn<User,String> tableColumnUsername;
    @FXML
    TableColumn<User,String> tableColumnName;
    @FXML
    Button buttonAcceptRequest;
    @FXML
    Button buttonDeclineRequest;


    public void setService(Network service, Stage stage,User user){
        this.service = service;
        service.addObserver(this);
        this.dialogStage = stage;
        this.user = user;
        initializeFriendRequests();
    }

    public void initializeFriendRequests(){
        List<User> f = new ArrayList<>();

        service.getFriendRequestsOfUser(user).forEach(f::add);
        friendships = FXCollections.observableArrayList(f);
        tableViewFriendRequests.setItems(friendships);
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    public void handleAcceptRequest(){
        User selectedItems = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        try {
            Friendship friendship = service.findFriendship(user,selectedItems);
            service.updateFriendshipStatus(friendship, Status.ACCEPTED);
        } catch (RepositoryException e) {
            MessageAlert messageAlert = null;
            messageAlert.showErrorMessage(this.dialogStage, e.getMessage());
        }
    }

    public void handleDeclineRequest(){
        User selectedItems = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        try {
            service.deleteFriendship(user.getUsername(),selectedItems.getUsername());
        } catch (RepositoryException e) {
            MessageAlert messageAlert = null;
            messageAlert.showErrorMessage(this.dialogStage, e.getMessage());
        }
    }

    @Override
    public void update() {
        initializeFriendRequests();
    }
}
