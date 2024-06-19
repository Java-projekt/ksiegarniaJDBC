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
import org.example.projekt.modules.Client;
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

    private static int currentUserId;


    public void login(ActionEvent event) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (isValidCredentials(login, password)) {
            // Nie rób nic więcej, bo obsługa przekierowania jest już w isValidCredentials
        } else {
            showAlert("Invalid login or password");
        }
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    private boolean isValidCredentials(String login, String password) {
        String query = "SELECT * FROM klienci WHERE login = ? AND haslo = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currentUserId = resultSet.getInt("ID_klienta");
                int userId = resultSet.getInt("ID_klienta");
                if (userId == 1) {
                    // Przekierowanie do panelu administratora
                    loadAdminPanel();
                } else {
                    // Przekierowanie do księgarni dla zwykłego użytkownika
                    loadBookstore();
                }
                Stage currentStage = (Stage) loginField.getScene().getWindow();
                currentStage.close();  // Zamknięcie okna logowania po udanym logowaniu
                return true;
            } else {
                showAlert("Invalid login or password");
                return false;
            }
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
            Scene scene = stage.getScene();
            scene.getStylesheets().add(getClass().getResource("/org/example/projekt/styles/bookstore.css").toExternalForm());
            stage.show();
            Stage currentStage = (Stage) loginField.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAdminPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projekt/views/admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Admin panel");
            stage.setScene(new Scene(root));
            Scene scene = stage.getScene();
            scene.getStylesheets().add(getClass().getResource("/org/example/projekt/styles/admin.css").toExternalForm());
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

    public Client getLoggedInClient(String login, String password) {
        String query = "SELECT * FROM klienci WHERE login = ? AND haslo = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("ID_klienta");
                String firstName = resultSet.getString("imie");
                String lastName = resultSet.getString("nazwisko");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("numer_telefonu");
                boolean isAdmin = resultSet.getBoolean("admin");
                return new Client(id, firstName, lastName, email, phoneNumber, login, password, isAdmin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void openRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projekt/views/register.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            Scene scene = stage.getScene();
            scene.getStylesheets().add(getClass().getResource("/org/example/projekt/styles/login.css").toExternalForm());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
