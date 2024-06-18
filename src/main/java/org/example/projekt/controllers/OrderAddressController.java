package org.example.projekt.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.example.projekt.modules.Order;
import org.example.projekt.modules.OrderDetail;
import org.example.projekt.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class OrderAddressController {

    @FXML
    private TextField cityField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField buildingNumberField;

    private LoginController loginController;
    private String selectedBookTitle;

    public void initData(String selectedBookTitle) {
        this.selectedBookTitle = selectedBookTitle;
    }

    @FXML
    public void initialize() {
        loginController = new LoginController();
    }

    @FXML
    public void placeOrder() {
        String city = cityField.getText();
        String postalCode = postalCodeField.getText();
        String buildingNumber = buildingNumberField.getText();


        // Pobierz ID adresu z bazy danych lub stwórz nowy rekord w tabeli adres
        int addressId = getAddressId(city, postalCode, buildingNumber);

        if (addressId != -1) {
            int clientId = loginController.getCurrentUserId();
            // Utwórz nowe zamówienie i szczegóły zamówienia
            Order order = new Order(0, clientId, LocalDate.now(), addressId);
            int orderId = saveOrder(order);

            if (orderId != -1) {
                // Pobierz ID książki na podstawie tytułu
                int bookId = getBookIdByTitle(selectedBookTitle);

                if (bookId != -1) {
                    OrderDetail orderDetail = new OrderDetail(0, orderId, bookId);
                    if (saveOrderDetail(orderDetail)) {
                        showAlert("Order placed successfully!");
                    } else {
                        showAlert("Failed to save order details.");
                    }
                } else {
                    showAlert("Book not found.");
                }
            } else {
                showAlert("Failed to save order.");
            }
        } else {
            showAlert("Failed to save address.");
        }
    }

    private int getAddressId(String city, String postalCode, String buildingNumber) {
        String query = "SELECT ID_adresu FROM adres WHERE miejscowosc = ? AND kod_pocztowy = ? AND numer_budynku = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, postalCode);
            preparedStatement.setString(3, buildingNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("ID_adresu");
            } else {
                // Jeśli adres nie istnieje, dodaj nowy i zwróć jego ID
                return insertAddress(city, postalCode, buildingNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int insertAddress(String city, String postalCode, String buildingNumber) {
        String query = "INSERT INTO adres (miejscowosc, kod_pocztowy, numer_budynku) VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, postalCode);
            preparedStatement.setString(3, buildingNumber);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Creating address failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new RuntimeException("Creating address failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int saveOrder(Order order) {
        String query = "INSERT INTO zamowienia (ID_klienta, data, ID_adresu) VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, order.getClientId());
            preparedStatement.setDate(2, java.sql.Date.valueOf(order.getDate()));
            preparedStatement.setInt(3, order.getAddressId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new RuntimeException("Creating order failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private boolean saveOrderDetail(OrderDetail orderDetail) {
        String query = "INSERT INTO szczegoly_zamowienia (ID_zamowienia, ID_ksiazki) VALUES (?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, orderDetail.getOrderId());
            preparedStatement.setInt(2, orderDetail.getBookId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getBookIdByTitle(String bookTitle) {
        String query = "SELECT ID_ksiazki FROM ksiazki WHERE tytul = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, bookTitle);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("ID_ksiazki");
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

