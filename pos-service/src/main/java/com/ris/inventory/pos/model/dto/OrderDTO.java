package com.ris.inventory.pos.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ris.inventory.pos.util.enumeration.TransactionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Order (In Response)", description = "Response for : Order details for Exchange Order, Refund Order etc")
public class OrderDTO {

    private String orderId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String transactionId;

    private String customerId;

    @JsonProperty("customerMobile")
    private String mobile;

    private float deliveryAmount;

    private float totalDiscount;

    private float totalTax;

    private float orderCost;

    private float payableAmount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "Bill type when exchange request is sent")
    private TransactionType billType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "Exchange bill when exchange request is sent")
    private OrderDTO exchangeBill;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "Replacement bill when exchange request is sent")
    private OrderDTO replacementBill;

    public OrderDTO() {
    }

    public OrderDTO(String orderId, float totalDiscount, float totalTax, float orderCost, float payableAmount, float deliveryAmount) {
        this.orderId = orderId;
        this.totalDiscount = totalDiscount;
        this.totalTax = totalTax;
        this.orderCost = orderCost;
        this.payableAmount = payableAmount;
        this.deliveryAmount = deliveryAmount;
    }

    public OrderDTO(String orderId, String customerId, String customerMobile, float totalDiscount, float totalTax, float orderCost,
                    float payableAmount, float deliveryAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.mobile = customerMobile;
        this.totalDiscount = totalDiscount;
        this.totalTax = totalTax;
        this.orderCost = orderCost;
        this.payableAmount = payableAmount;
        this.deliveryAmount = deliveryAmount;
    }

    public OrderDTO(String orderId, float totalDiscount, float totalTaxes, float totalProductCost, float payableAmount) {
        this.orderId = orderId;
        this.totalDiscount = totalDiscount;
        this.totalTax = totalTaxes;
        this.orderCost = totalProductCost;
        this.payableAmount = payableAmount;
    }

    public OrderDTO(String orderId, float orderCost, float payableAmount) {
        this.orderId = orderId;
        this.orderCost = orderCost;
        this.payableAmount = payableAmount;
    }

    public OrderDTO(String orderId) {
        this.orderId = orderId;
    }

    public static OrderDTO calculateExchangeBill(String orderId, String customerId, String mobile, OrderDTO exchange, OrderDTO replacement) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.totalDiscount = replacement.getTotalDiscount() - exchange.getTotalDiscount();
        orderDTO.totalTax = replacement.getTotalTax() - exchange.getTotalTax();
        orderDTO.orderCost = replacement.getOrderCost() - exchange.getOrderCost();
        orderDTO.payableAmount = replacement.getPayableAmount() - exchange.getPayableAmount();

        orderDTO.setOrderId(orderId);
        orderDTO.setCustomerId(customerId);
        orderDTO.setMobile(mobile);

        exchange.setOrderId(orderId);
        exchange.setCustomerId(customerId);
        exchange.setMobile(mobile);

        replacement.setOrderId(orderId);
        replacement.setCustomerId(customerId);
        replacement.setMobile(mobile);

        orderDTO.setExchangeBill(exchange);
        orderDTO.setReplacementBill(replacement);
        orderDTO.setDeliveryAmount(replacement.getDeliveryAmount());
        orderDTO.setBillType(orderDTO.getPayableAmount() > 0 ? TransactionType.SALE : TransactionType.REFUND);
        return orderDTO;
    }

    public TransactionType getBillType() {
        return billType;
    }

    public void setBillType(TransactionType billType) {
        this.billType = billType;
    }

    public OrderDTO getExchangeBill() {
        return exchangeBill;
    }

    public void setExchangeBill(OrderDTO exchangeBill) {
        this.exchangeBill = exchangeBill;
    }

    public OrderDTO getReplacementBill() {
        return replacementBill;
    }

    public void setReplacementBill(OrderDTO replacementBill) {
        this.replacementBill = replacementBill;
    }

    public float getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(float deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public float getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(float totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public float getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(float totalTax) {
        this.totalTax = totalTax;
    }

    public float getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(float orderCost) {
        this.orderCost = orderCost;
    }

    public float getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(float payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderDTO abs() {
        this.totalDiscount = Math.abs(this.totalDiscount);
        this.totalTax = Math.abs(this.totalTax);
        this.orderCost = Math.abs(this.orderCost);
        this.payableAmount = Math.abs(this.payableAmount);
        return this;
    }
}
