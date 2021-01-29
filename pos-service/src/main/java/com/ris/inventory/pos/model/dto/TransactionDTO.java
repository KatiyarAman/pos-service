package com.ris.inventory.pos.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ris.inventory.pos.domain.Transaction;
import com.ris.inventory.pos.model.response.TransactionResponse;
import com.ris.inventory.pos.util.enumeration.PaymentMethod;
import com.ris.inventory.pos.util.enumeration.TransactionStatus;
import com.ris.inventory.pos.util.enumeration.TransactionType;
import io.swagger.annotations.ApiModel;

import java.util.Date;

@ApiModel(value = "Transaction", description = "Response for : Inner object of Invoice")
public class TransactionDTO implements TransactionResponse {

    @JsonProperty("invoiceId")
    private String transactionId;

    private float orderCost;

    private float discount;

    private float deliveryAmount;

    private float taxes;

    @JsonProperty("transactionCharge")
    private float paymentGatewayCharge;

    private float payableAmount;

    @JsonProperty("paymentMethod")
    private PaymentMethod method;

    @JsonProperty("paymentStatus")
    private TransactionStatus status;

    @JsonProperty("paymentType")
    private TransactionType type;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JsonProperty("paymentDate")
    private Date dateCreated;

    private LocationDTO location;

    private UserDTO salesPerson;

    public TransactionDTO() {
    }

    public TransactionDTO(Transaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.orderCost = transaction.getOrderCost();
        this.discount = transaction.getDiscount();
        this.taxes = transaction.getTaxes();
        this.paymentGatewayCharge = transaction.getPaymentGatewayCharge();
        this.payableAmount = transaction.getPayableAmount();
        this.method = transaction.getMethod();
        this.status = transaction.getStatus();
        this.type = transaction.getType();
        this.dateCreated = transaction.getDateCreated();
        this.deliveryAmount = transaction.getDeliveryAmount();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public float getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(float orderCost) {
        this.orderCost = orderCost;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(float deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public float getTaxes() {
        return taxes;
    }

    public void setTaxes(float taxes) {
        this.taxes = taxes;
    }

    public float getPaymentGatewayCharge() {
        return paymentGatewayCharge;
    }

    public void setPaymentGatewayCharge(float paymentGatewayCharge) {
        this.paymentGatewayCharge = paymentGatewayCharge;
    }

    public float getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(float payableAmount) {
        this.payableAmount = payableAmount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public UserDTO getSalesPerson() {
        return salesPerson;
    }

    public void setSalesPerson(UserDTO salesPerson) {
        this.salesPerson = salesPerson;
    }
}
