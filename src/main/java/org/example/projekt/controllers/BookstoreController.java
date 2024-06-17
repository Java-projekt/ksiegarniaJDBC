package org.example.projekt.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.projekt.util.DBUtil;

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

    @FXML
    public void initialize() {
        loadRecommendedBooks();
    }

    private void loadRecommendedBooks() {
        ObservableList<String> books = FXCollections.observableArrayList();
        String query = "SELECT tytul FROM ksiazki LIMIT 5";
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
}
