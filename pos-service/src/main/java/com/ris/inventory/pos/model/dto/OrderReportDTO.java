package com.ris.inventory.pos.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.model.response.OrderResponse;
import com.ris.inventory.pos.util.enumeration.OrderStatus;

import java.util.Date;
import java.util.List;

public class OrderReportDTO implements OrderResponse {

    private String orderId;

    private OrderStatus orderStatus;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("products")
    private List<ProductDTO> productList;

    private String location;

    @JsonProperty("date")
    private Date dateCreated;

    private List<TransactionDTO> transactions;

    public OrderReportDTO() {
    }

    public OrderReportDTO(Order order) {
        this.orderId = order.getOrderId();
        this.orderStatus = order.getOrderStatus();
        this.location = order.getLocation();
        this.dateCreated = order.getDateCreated();
    }

    public List<ProductDTO> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductDTO> productList) {
        this.productList = productList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }
}
