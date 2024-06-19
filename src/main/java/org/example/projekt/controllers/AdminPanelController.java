package org.example.projekt.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.example.projekt.modules.Book;
import org.example.projekt.util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminPanelController {

    @FXML
    private ListView<Book> bookListView;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publisherField;
    @FXML
    private TextField priceField;
    @FXML
    private Button addButton;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadBooks();
        bookListView.setCellFactory(param -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });

        // Add listener for selection changes
        bookListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleBookSelection(newValue));
    }

    private void loadBooks() {
        bookList.clear();
        String query = "SELECT * FROM ksiazki";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("ID_ksiazki"),
                        resultSet.getString("tytul"),
                        resultSet.getString("autor"),
                        resultSet.getString("wydawnictwo"),
                        resultSet.getDouble("cena")
                );
                bookList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bookListView.setItems(bookList);
    }

    @FXML
    public void addBook(ActionEvent event) {
        if (areFieldsFilled()) {
            String title = titleField.getText();
            String author = authorField.getText();
            String publisher = publisherField.getText();
            double price = Double.parseDouble(priceField.getText());

            // Check if the book already exists
            if (isBookAlreadyExists(title, author, publisher, price)) {
                showAlert("This book already exists in the database.");
            } else {
                String query = "INSERT INTO ksiazki (tytul, autor, wydawnictwo, cena) VALUES (?,?,?,?)";
                try (Connection connection = DBUtil.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, title);
                    preparedStatement.setString(2, author);
                    preparedStatement.setString(3, publisher);
                    preparedStatement.setDouble(4, price);
                    preparedStatement.executeUpdate();
                    loadBooks();
                    clearTextFields(); // Clear text fields after adding a book
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("All fields must be completed to add a book.");
        }
    }

    private boolean isBookAlreadyExists(String title, String author, String publisher, double price) {
        String query = "SELECT * FROM ksiazki WHERE tytul = ? AND autor = ? AND wydawnictwo = ? AND cena = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, publisher);
            preparedStatement.setDouble(4, price);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // true if there is any matching book
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @FXML
    public void modifyBook(ActionEvent event) {
        if (areFieldsFilled()) {
            Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                String title = titleField.getText();
                String author = authorField.getText();
                String publisher = publisherField.getText();
                double price = Double.parseDouble(priceField.getText());

                String query = "UPDATE ksiazki SET tytul =?, autor =?, wydawnictwo =?, cena =? WHERE ID_ksiazki =?";
                try (Connection connection = DBUtil.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, title);
                    preparedStatement.setString(2, author);
                    preparedStatement.setString(3, publisher);
                    preparedStatement.setDouble(4, price);
                    preparedStatement.setInt(5, selectedBook.getId());
                    preparedStatement.executeUpdate();
                    loadBooks();
                    clearTextFields(); // Clear text fields after modifying a book
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("All fields must be completed to modify the book.");
        }
    }

    @FXML
    public void deleteBook(ActionEvent event) {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            String query = "DELETE FROM ksiazki WHERE ID_ksiazki =?";
            try (Connection connection = DBUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, selectedBook.getId());
                preparedStatement.executeUpdate();
                loadBooks();
                clearTextFields(); // Clear text fields after deleting a book
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleBookSelection(Book selectedBook) {
        if (selectedBook != null) {
            titleField.setText(selectedBook.getTitle());
            authorField.setText(selectedBook.getAuthor());
            publisherField.setText(selectedBook.getPublisher());
            priceField.setText(Double.toString(selectedBook.getPrice()));
        }
    }

    private void clearTextFields() {
        titleField.clear();
        authorField.clear();
        publisherField.clear();
        priceField.clear();
    }

    private boolean areFieldsFilled() {
        return !titleField.getText().isEmpty() &&
                !authorField.getText().isEmpty() &&
                !publisherField.getText().isEmpty() &&
                !priceField.getText().isEmpty();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Missing data");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void logout() {
        try {
            Stage stage = (Stage) bookListView.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projekt/views/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            Scene loginScene = loginStage.getScene();
            loginScene.getStylesheets().add(getClass().getResource("/org/example/projekt/styles/login.css").toExternalForm());
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
