package com.ris.inventory.pos.domain;

import com.ris.inventory.pos.domain.converter.CarrierConverter;
import com.ris.inventory.pos.domain.converter.DeliveryStatusConverter;
import com.ris.inventory.pos.util.enumeration.CarrierType;
import com.ris.inventory.pos.util.enumeration.DeliveryStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "delivery")
public class Delivery implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String deliveryId;

    private float amount;

    @Convert(converter = DeliveryStatusConverter.class)
    private DeliveryStatus status;

    @Convert(converter = CarrierConverter.class)
    private CarrierType carrier;

    @OneToOne(cascade = CascadeType.MERGE)
    private Order order;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedDeliveryDate;

    @Column(name = "isDeleted")
    private boolean isDeleted;

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

    public Delivery() {
        this.deliveryId = generateDeliveryId();
        this.isDeleted = false;
    }

    public Delivery(float amount, CarrierType carrier) {
        this.deliveryId = generateDeliveryId();
        this.amount = amount;
        this.status = DeliveryStatus.INITIALIZED;
        this.carrier = carrier;
        this.isDeleted = false;
    }

    private String generateDeliveryId() {
        return "D-" + new Date().getTime();
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public CarrierType getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierType carrier) {
        this.carrier = carrier;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
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
