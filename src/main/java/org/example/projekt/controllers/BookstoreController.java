package org.example.projekt.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.projekt.util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookstoreController {

    @FXML
    private ListView<String> recommendedBooksListView;
    @FXML
    private ListView<String> searchResultsListView;
    @FXML
    private TextField searchField;

    private ObservableList<String> booksList;

    @FXML
    public void initialize() {
        loadRecommendedBooks();
        recommendedBooksListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                searchResultsListView.getSelectionModel().clearSelection();
            }
        });
        searchResultsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                recommendedBooksListView.getSelectionModel().clearSelection();
            }
        });
    }

    private void loadRecommendedBooks() {
        ObservableList<String> books = FXCollections.observableArrayList();
        String query = "SELECT ks.tytul, COUNT(sz.ID_szczegolu) as liczba_zamowien " +
                "FROM ksiazki ks " +
                "JOIN szczegoly_zamowienia sz ON ks.ID_ksiazki = sz.ID_ksiazki " +
                "GROUP BY ks.tytul " +
                "ORDER BY liczba_zamowien DESC " +
                "LIMIT 5";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                books.add(resultSet.getString("tytul"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        recommendedBooksListView.setItems(books);
    }

    @FXML
    public void searchBooks() {
        String searchTerm = searchField.getText();
        ObservableList<String> books = FXCollections.observableArrayList();
        String query = "SELECT tytul FROM ksiazki WHERE tytul LIKE ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                books.add(resultSet.getString("tytul"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchResultsListView.setItems(books);
    }

    public void orderSelectedBook() {
        String selectedBookTitle = recommendedBooksListView.getSelectionModel().getSelectedItem();
        if (selectedBookTitle == null) {
            selectedBookTitle = searchResultsListView.getSelectionModel().getSelectedItem();
        }
        if (selectedBookTitle == null) {
            showAlert("Select a book to order.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projekt/views/order.fxml"));
            Parent root = loader.load();

            OrderAddressController controller = loader.getController();
            controller.initData(selectedBookTitle);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Order Address");
            stage.show();
        } catch (IOException e) {
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

    public ObservableList<String> getBooksList() {
        return booksList;
    }

    public void logout() {
        try {
            Stage stage = (Stage) recommendedBooksListView.getScene().getWindow();
            stage.close(); // Zamknij bieżące okno
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projekt/views/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
