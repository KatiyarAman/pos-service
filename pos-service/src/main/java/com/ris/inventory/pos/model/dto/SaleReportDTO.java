package com.ris.inventory.pos.model.dto;

import com.ris.inventory.pos.model.response.SalespersonResponse;

import java.util.ArrayList;
import java.util.List;

public class SaleReportDTO implements SalespersonResponse {

    private String userId;

    private String mobileNumber;

    private String username; //email

    private String firstName;

    private String lastName;

    private LocationDTO location;

    private double totalSale;

    private int transactionCount;

    private List<TransactionDTO> transactions = new ArrayList<>();

    public SaleReportDTO(UserDTO user) {
        this.userId = user.getUserId();
        this.mobileNumber = user.getMobileNumber();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.location = user.getLocation();
    }

    public SaleReportDTO() {
    }

    public double getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(double totalSale) {
        this.totalSale = totalSale;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public double calculateSale() {
        return this.transactions.stream().mapToDouble(TransactionDTO::getPayableAmount).sum();
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }
}
