package com.ris.inventory.pos.domain;


import com.ris.inventory.pos.domain.converter.PaymentMethodConverter;
import com.ris.inventory.pos.domain.converter.TransactionStatusConverter;
import com.ris.inventory.pos.domain.converter.TransactionTypeConverter;
import com.ris.inventory.pos.util.enumeration.PaymentMethod;
import com.ris.inventory.pos.util.enumeration.TransactionStatus;
import com.ris.inventory.pos.util.enumeration.TransactionType;
import org.hibernate.Interceptor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transaction")
public class Transaction implements Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String transactionId;

    private float orderCost;

    private float discount;

    private float taxes;

    private float paymentGatewayCharge;

    private float deliveryAmount;

    private float payableAmount;

    @Convert(converter = PaymentMethodConverter.class)
    private PaymentMethod method;

    @Convert(converter = TransactionStatusConverter.class)
    private TransactionStatus status;

    @Convert(converter = TransactionTypeConverter.class)
    private TransactionType type;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Order order;

    private String userId;

    private String location;

    private boolean isExchange = false;

    @Column(name = "isDeleted")
    private boolean isDeleted = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = true)
    @org.hibernate.annotations.UpdateTimestamp
    private Date lastModified;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date dateDeleted;

    public Transaction() {
        this.transactionId = generateOrderId();
    }

    public Transaction(float orderCost, float discount, float taxes, float payableAmount, float deliveryAmount, TransactionType type, boolean isExchange,
                       PaymentMethod method, Interceptor interceptor) {
        this.transactionId = generateOrderId();
        this.orderCost = orderCost;
        this.discount = discount;
        this.taxes = taxes;
        this.payableAmount = payableAmount;
        this.deliveryAmount = deliveryAmount;
        this.status = TransactionStatus.INITIATED;
        this.type = type;
        this.method = method;
        AuditInterceptor auditInterceptor = ((AuditInterceptor) interceptor);
        this.userId = auditInterceptor.getUserId();
        this.location = auditInterceptor.getLocation();
        this.isExchange = isExchange;
    }

    public Transaction(float orderCost, float payableAmount, TransactionType type, boolean isExchange, PaymentMethod method, Interceptor interceptor) {
        this.transactionId = generateOrderId();
        this.orderCost = orderCost;
        this.payableAmount = payableAmount;
        this.status = TransactionStatus.INITIATED;
        this.type = type;
        this.method = method;
        AuditInterceptor auditInterceptor = ((AuditInterceptor) interceptor);
        this.userId = auditInterceptor.getUserId();
        this.location = auditInterceptor.getLocation();
        this.isExchange = isExchange;
    }

    public float getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(float deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    private String generateOrderId() {
        return "TXN-" + new Date().getTime();
    }


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isExchange() {
        return isExchange;
    }

    public void setExchange(boolean exchange) {
        isExchange = exchange;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this.dateDeleted = dateDeleted;
    }
}
