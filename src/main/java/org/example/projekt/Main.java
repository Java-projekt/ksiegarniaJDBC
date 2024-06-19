package org.example.projekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/org/example/projekt/views/login.fxml"));
        Scene scene = new Scene(root);
        String css = getClass().getResource("/org/example/projekt/styles/login.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setTitle("Bookstore Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
