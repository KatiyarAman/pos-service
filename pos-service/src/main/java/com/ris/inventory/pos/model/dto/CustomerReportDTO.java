package com.ris.inventory.pos.model.dto;

import com.ris.inventory.pos.domain.Customer;
import com.ris.inventory.pos.model.response.CustomerResponse;
import com.ris.inventory.pos.util.enumeration.CustomerType;

import java.util.ArrayList;
import java.util.List;

public class CustomerReportDTO implements CustomerResponse {

    private String customerId;

    private String firstName;

    private String lastName;

    private String fullName;

    private String mobile;

    private String email;

    private CustomerType customerType;

    private int visitCount;

    private List<OrderReportDTO> orders = new ArrayList<>();

    public CustomerReportDTO(Customer customer) {
        this.customerId = customer.getCustomerId();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.fullName = customer.getFirstName() + " " + customer.getLastName();
        this.mobile = customer.getMobile();
        this.email = customer.getEmail();
        this.customerType = customer.getCustomerType();
    }

    public CustomerReportDTO() {
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

    public List<OrderReportDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderReportDTO> orders) {
        this.orders = orders;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
}
