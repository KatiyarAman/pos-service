package com.ris.inventory.pos.model.co;

import com.ris.inventory.pos.util.enumeration.Period;
import com.ris.inventory.pos.util.enumeration.TransactionType;

import java.util.Date;

public class FilterCO {

    private Date start;

    private Date end;

    private TransactionType transactionType;

    private Period period;

    private String locationId;

    private String firstName;

    private String lastName;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
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

    public boolean isValidTransactionAndDateFilter() {
        return (this.start != null && this.end != null && this.transactionType != null);
    }

    public boolean isValidTransactionFilter() {
        return (this.transactionType != null);
    }

    public boolean isValidDateFilter() {
        return (this.start != null && this.end != null);
    }

    public boolean isValidPeriodFilter() {
        return (this.period != null);
    }

    public boolean isValidLocationFilter() {
        return (this.locationId != null && !this.locationId.isEmpty());
    }

    public boolean isValidNameFilter() {
        return (this.firstName != null);
    }
}
