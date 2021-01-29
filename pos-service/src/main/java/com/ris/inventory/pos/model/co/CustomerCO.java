package com.ris.inventory.pos.model.co;

import com.ris.inventory.pos.util.enumeration.CustomerType;
import io.swagger.annotations.ApiModel;
import org.springframework.lang.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(value = "Customer", description = "Request for : Add new customer details for order")
public class CustomerCO {

    @NotBlank
    @NotNull
    private String firstName;

    private String lastName;

    @NotBlank
    @NotNull
    private String mobile;

    @NotBlank
    @NotNull
    private String email;

    @NotNull
    private CustomerType customerType;

    @NonNull
    @Valid
    private AddressCO address;

    public AddressCO getAddress() {
        return address;
    }

    public void setAddress(AddressCO address) {
        this.address = address;
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
}
