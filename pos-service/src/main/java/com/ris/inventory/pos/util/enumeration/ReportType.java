package com.ris.inventory.pos.util.enumeration;

public enum ReportType {

    PAYMENT_REPORT("Payment Report"),
    CUSTOMER_REPORT("Customer Report"),
    SALES_PERSON_REPORT("Sales Person Report"),
    TRANSACTION_REPORT("Transaction Report"),
    ORDER_REPORT("Order Report");

    private String type;

    ReportType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
