package com.ris.inventory.pos.model.dto;

import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.util.enumeration.OrderStatus;
import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Invoice", description = "Response for : Invoice/Payment details of order")
public class InvoiceDTO {

    private String orderId;

    private OrderStatus orderStatus;

    private List<TransactionDTO> transactions = new ArrayList<>();

    private List<ProductDTO> products = new ArrayList<>();

    public InvoiceDTO() {
    }

    public InvoiceDTO(Order order) {
        this.orderId = order.getOrderId();
        this.orderStatus = order.getOrderStatus();
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

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public void setTransaction(TransactionDTO transaction) {
        if (transaction != null)
            this.transactions.add(transaction);
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public void setProduct(ProductDTO product) {
        if (product != null)
            this.products.add(product);
    }
}
