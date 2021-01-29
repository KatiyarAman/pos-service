package com.ris.inventory.pos.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import java.util.Date;

@ApiModel(value = "User", description = "Response for : Registered User details")
public class UserDTO {

    private String userId;

    private String mobileNumber;

    private String username; //email

    private String firstName;

    private String lastName;

    @JsonProperty("joiningDate")
    private Date dateCreated;

    @JsonIgnore
    private String transactionId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String authority;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocationDTO location;

    private boolean isVerifiedEmail;

    private boolean isVerifiedMobile;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isVerifiedEmail() {
        return isVerifiedEmail;
    }

    public void setVerifiedEmail(boolean verifiedEmail) {
        isVerifiedEmail = verifiedEmail;
    }

    public boolean isVerifiedMobile() {
        return isVerifiedMobile;
    }

    public void setVerifiedMobile(boolean verifiedMobile) {
        isVerifiedMobile = verifiedMobile;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
