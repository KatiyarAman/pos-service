package com.ris.inventory.pos.util.enumeration;

public enum TransactionStatus {
    INITIATED("INITIATED"),
    REFUNDED("REFUNDED"),
    IN_PROGRESS("IN PROCESS"),
    ORDER_CHANGED("ORDER_CHANGED"),
    SUCCESS("SUCCESS"),
    FAIL("FAIL"),
    CANCELLED("CANCELLED"),
    ERROR("Error");

    private String status;

    TransactionStatus(String status) {
        this.status = status;
    }

    public static TransactionStatus from(String status) {
        if (status.equalsIgnoreCase("initiated"))
            return TransactionStatus.INITIATED;
        else if (status.equalsIgnoreCase("refunded"))
            return TransactionStatus.REFUNDED;
        else if (status.equalsIgnoreCase("in_progress") || status.equalsIgnoreCase("in progress"))
            return TransactionStatus.IN_PROGRESS;
        else if (status.equalsIgnoreCase("success"))
            return TransactionStatus.SUCCESS;
        else if (status.equalsIgnoreCase("order_changed") || status.equalsIgnoreCase("order changed"))
            return TransactionStatus.ORDER_CHANGED;
        else if (status.equalsIgnoreCase("cancelled"))
            return TransactionStatus.CANCELLED;
        else if (status.equalsIgnoreCase("fail"))
            return TransactionStatus.FAIL;
        else
            return TransactionStatus.ERROR;
    }

    public String getStatus() {
        return status;
    }
}
