package com.ris.inventory.pos.domain;

import com.ris.inventory.pos.model.co.AddressCO;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "address")
public class Address implements Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    private String addressId;

    @Column(nullable = false)
    private String line1;

    private String line2;

    private String zipCode;

    private String city;

    private String state;

    private String country;

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

    public Address() {
        this.addressId = generateAddressId();
    }

    public Address(AddressCO addressCO) {
        this.addressId = generateAddressId();
        this.line1 = addressCO.getLine1();
        this.line2 = addressCO.getLine2();
        this.zipCode = addressCO.getZipCode();
        this.city = addressCO.getCity();
        this.state = addressCO.getState();
        this.country = addressCO.getCountry();
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public void update(AddressCO addressCO) {
        this.line1 = addressCO.getLine1();
        this.line2 = addressCO.getLine2();
        this.zipCode = addressCO.getZipCode();
        this.city = addressCO.getCity();
        this.state = addressCO.getState();
        this.country = addressCO.getCountry();
    }

    public String generateAddressId() {
        return "ADD-" + new Date().getTime();
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
