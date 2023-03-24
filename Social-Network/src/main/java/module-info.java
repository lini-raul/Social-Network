module Main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    /*opens com.example.lab6 to javafx.fxml;
    exports com.example.lab6;*/

    opens Main to javafx.fxml;
    exports Main;

    opens controller to javafx.fxml;
    exports controller;

    opens domain.user to javafx.fxml;
    exports domain.user;

    opens domain.friendship to javafx.fxml;
    exports domain.friendship;

}