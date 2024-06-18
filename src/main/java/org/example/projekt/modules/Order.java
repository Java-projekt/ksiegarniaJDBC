package org.example.projekt.modules;

import java.time.LocalDate;

public class Order {
    private int id;
    private int clientId;
    private LocalDate date;
    private int addressId;

    public Order(int id, int clientId, LocalDate date, int addressId) {
        this.id = id;
        this.clientId = clientId;
        this.date = date;
        this.addressId = addressId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
}
