package com.ris.inventory.pos.domain;

import com.ris.inventory.pos.domain.converter.PaymentMethodConverter;
import com.ris.inventory.pos.model.co.PaymentCO;
import com.ris.inventory.pos.util.enumeration.PaymentMethod;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payment")
public class Payment implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Convert(converter = PaymentMethodConverter.class)
    private PaymentMethod method;

    private float amount;

    private String userId;

    private String location;

    @ManyToOne
    @JoinColumn(name = "transactionId")
    private Transaction transaction;

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

    public Payment() {
    }

    public Payment(PaymentCO payment, String userId, String location, Transaction transaction) {
        this.method = payment.getMethod();
        this.amount = payment.getAmount();
        this.userId = userId;
        this.location = location;
        this.transaction = transaction;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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
