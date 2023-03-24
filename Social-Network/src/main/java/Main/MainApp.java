package Main;

import controller.LoginController;
import domain.friendship.Friendship;
import domain.friendship.Status;
import domain.message.Message;
import domain.user.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import repository.InMemoryRepository;
import repository.Repository;
import repository.data_base.FriendshipDBRepository;
import repository.data_base.MessageDBRepository;
import repository.data_base.UserDBRepository;
import service.Network;
import validators.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class MainApp extends Application {

    service.Network srv;
    @Override
    public void start(Stage primaryStage) throws IOException {

        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        /*Repository<User,String> userRepo = new InMemoryRepository<>();
        Repository<Friendship, Set<User>> friendshipRepo = new InMemoryRepository<>();*/
        String url = "jdbc:postgresql://localhost:5432/Social_Network";
        UserDBRepository userRepo = new UserDBRepository(url,"postgres","postgres");
        FriendshipDBRepository friendshipRepo = new FriendshipDBRepository(url,"postgres","postgres");
        MessageDBRepository messageRepo = new MessageDBRepository(url,"postgres", "postgres");
        srv = new service.Network(userRepo,friendshipRepo,messageRepo,userValidator,friendshipValidator);





        /*try {
            srv.addUser("a","a","1234");
            srv.addUser("bogdan","b","1234");
            srv.addUser("costin","c","1234");
            srv.addUser("denis","d","1234");
            srv.addUser("evelin","e","1234");
            srv.addUser("florin","f","1234");
            srv.addUser("gigi","g","1234");
            srv.addUser("horea","h","1234");
            srv.addUser("j","j","1234");
            srv.addUser("k","k","1234");
            srv.addUser("l","l","1234");
            srv.addUser("m","m","1234");
            srv.addUser("n","n","1234");
            srv.addUser("o","o","1234");
            srv.addFriendship("a","bogdan", Status.ACCEPTED);
            srv.addFriendship("a","costin", Status.ACCEPTED);
            srv.addFriendship("a","denis", Status.ACCEPTED);
            srv.addFriendship("a","evelin", Status.ACCEPTED);
            srv.addFriendship("a","florin", Status.ACCEPTED);
            srv.addFriendship("a","gigi", Status.ACCEPTED);
            srv.addFriendship("a","horea", Status.ACCEPTED);
            srv.addFriendship("a","j", Status.SEND);
            srv.addFriendship("a","k", Status.SEND);
            srv.addFriendship("a","l", Status.SEND);
            srv.addFriendship("a","m", Status.ACCEPTED);
            srv.addFriendship("a","n", Status.ACCEPTED);
            srv.addFriendship("a","o", Status.ACCEPTED);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }*/



        initView(primaryStage);
        primaryStage.show();

    }

    private void initView(Stage primaryStage) throws IOException{
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/login/loginView.fxml"));

        AnchorPane mainLayout = loginLoader.load();
        LoginController loginController = loginLoader.getController();
        loginController.setService(srv,primaryStage);

        Scene scene = new Scene(mainLayout, 550, 430);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);

    }

    public static void main(String[] args) {
        launch();
    }
}
