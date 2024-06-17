package org.example.projekt.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.projekt.util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    public void login(ActionEvent event) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (isValidCredentials(login, password)) {
            loadBookstore();
        } else {
            showAlert("Invalid login or password");
        }
    }

    private boolean isValidCredentials(String login, String password) {
        String query = "SELECT * FROM klienci WHERE login = ? AND haslo = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadBookstore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projekt/views/bookstore.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Bookstore");
            stage.setScene(new Scene(root));
            stage.show();
            Stage currentStage = (Stage) loginField.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void openRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projekt/views/register.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
