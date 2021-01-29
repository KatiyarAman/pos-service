package com.ris.inventory.pos.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ris.inventory.pos.util.enumeration.CustomerType;
import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApiModel(value = "Customer ", description = "Response for : Customer details for order")
public class CustomerDTO {

    private String customerId;

    private String firstName;

    private String lastName;

    private String mobile;

    private String email;

    private String fullName;

    private CustomerType customerType;

    @JsonProperty("registrationDate")
    private Date dateCreated;

    private List<AddressDTO> address = new ArrayList<>();

    public List<AddressDTO> getAddress() {
        return address;
    }

    public void setAddress(List<AddressDTO> address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
