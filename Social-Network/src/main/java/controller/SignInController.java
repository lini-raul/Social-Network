package controller;

import domain.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import service.Network;
import validators.RepositoryException;
import validators.ValidationException;

import java.io.IOException;

public class SignInController {
    private Network service;
    Stage dialogStage;
    Stage loginStage;
    @FXML
    TextField textFieldName;
    @FXML
    TextField textFieldUsername;
    @FXML
    TextField textFieldPassword;
    @FXML
    Button buttonSignIn;

    public void setService(Network service, Stage stage,Stage loginStage){
        this.service = service;
        this.dialogStage=stage;
        this.loginStage = loginStage;
    }

    public void handleSignIn(){
        String name = textFieldName.getText();
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        try{
            service.addUser(username,name,password);
            Stage stage = new Stage();
            stage.setTitle("Social Network");
            FXMLLoader userUiLoader = new FXMLLoader();
            userUiLoader.setLocation(getClass().getResource("/userUI/userView_2.fxml"));

            AnchorPane userUiLayout  = userUiLoader.load();

            UserController userController = userUiLoader.getController();
            User user = service.findUser(username);
            userController.setService(service,stage,user);

            stage.setScene(new Scene(userUiLayout));
            stage.setWidth(700);
            stage.show();
            dialogStage.close();
            loginStage.close();
        }
         catch (RepositoryException e) {
             MessageAlert messageAlert = null;
             messageAlert.showErrorMessage(this.dialogStage,e.getMessage());

         } catch (ValidationException e) {
            MessageAlert messageAlert = null;
            messageAlert.showErrorMessage(this.dialogStage,e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
