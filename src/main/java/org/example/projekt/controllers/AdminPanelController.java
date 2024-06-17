package org.example.projekt.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import org.example.projekt.modules.Book;
import org.example.projekt.util.DBUtil;
import javafx.scene.control.ListCell;

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
        String title = titleField.getText();
        String author = authorField.getText();
        String publisher = publisherField.getText();
        double price = Double.parseDouble(priceField.getText());

        String query = "INSERT INTO ksiazki (tytul, autor, wydawnictwo, cena) VALUES (?,?,?,?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, publisher);
            preparedStatement.setDouble(4, price);
            preparedStatement.executeUpdate();
            loadBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void modifyBook(ActionEvent event) {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook!= null) {
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void deleteBook(ActionEvent event) {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook!= null) {
            String query = "DELETE FROM ksiazki WHERE ID_ksiazki =?";
            try (Connection connection = DBUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, selectedBook.getId());
                preparedStatement.executeUpdate();
                loadBooks();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}