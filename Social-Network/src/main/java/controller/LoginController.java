package controller;

import domain.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import validators.RepositoryException;
import service.Network;

import javax.security.auth.login.AppConfigurationEntry;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {
    private Network service;
    Stage dialogStage;

    @FXML
    TextField textFieldUsername;
    @FXML
    TextField textFieldPassword;
    @FXML
    Button buttonLogin;
    @FXML
    Button buttonSignIn;
    @FXML
    Button buttonAdmin;

    public void setService(Network service,Stage stage){
        this.service = service;
        this.dialogStage=stage;
    }

    @FXML
    public void handleLogin(){
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        try {
            User user = service.findUser(username);
            if(user.getPassword().equals(password)){
                Stage stage = new Stage();
                stage.setTitle("Social Network");
                FXMLLoader userUiLoader = new FXMLLoader();
                userUiLoader.setLocation(getClass().getResource("/userUI/userView_2.fxml"));

                AnchorPane userUiLayout  = userUiLoader.load();

                UserController userController = userUiLoader.getController();
                userController.setService(service,stage,user);

                stage.setScene(new Scene(userUiLayout));
                stage.setWidth(700);
                stage.show();
                dialogStage.close();
            }
            else{
                MessageAlert messageAlert = null;
                messageAlert.showErrorMessage(this.dialogStage,"Invalid password!");
            }

        } catch (RepositoryException e) {
            MessageAlert messageAlert = null;
            messageAlert.showErrorMessage(this.dialogStage,"Username does not exist!");
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleSignIn(){
        try{
            Stage stage = new Stage();
            stage.setTitle("Sign In");
            FXMLLoader signInLoader = new FXMLLoader();
            signInLoader.setLocation(getClass().getResource("/login/signInView.fxml"));

            AnchorPane signInLayout  = signInLoader.load();

            SignInController signInController = signInLoader.getController();
            signInController.setService(service,stage,dialogStage);

            stage.setScene(new Scene(signInLayout));
            stage.setWidth(400);
            stage.show();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }

    }

}
