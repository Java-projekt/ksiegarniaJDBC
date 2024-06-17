package org.example.projekt.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.projekt.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField numberField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void register() {
        String imie = nameField.getText();
        String nazwisko = lastNameField.getText();
        String email = emailField.getText();
        String numer_tel = numberField.getText();
        String login = usernameField.getText();
        String haslo = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!haslo.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Registration Error!", "Passwords do not match.");
            return;
        }

        if (login.isEmpty() || haslo.isEmpty()) {
            showAlert(AlertType.ERROR, "Registration Error!", "Please fill in all fields.");
            return;
        }

        try {
            // Replace with your actual database connection logic
            Connection connection = DBUtil.getConnection();
            String sql = "INSERT INTO klienci (imie, nazwisko, email, numer_tel, login, haslo) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, imie);
            statement.setString(2, nazwisko);
            statement.setString(3, email);
            statement.setString(4, numer_tel);
            statement.setString(5, login);
            statement.setString(6, haslo);
            statement.executeUpdate();
            showAlert(AlertType.INFORMATION, "Registration Successful!", "User registered successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Registration Error!", "An error occurred during registration.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
