package com.ris.inventory.pos.util.enumeration;

public enum TransactionType {
    REFUND("REFUND"),
    EXCHANGE("EXCHANGE"), //dont use it for exchange logic and currently this is not used in the system
    SALE("SALE"),
    ERROR("Error");

    private String type;

    TransactionType(String type) {
        this.type = type;
    }

    public static TransactionType from(String type) {
        if (type.equalsIgnoreCase("refund"))
            return TransactionType.REFUND;
        else if (type.equalsIgnoreCase("exchange"))
            return TransactionType.EXCHANGE;
        else if (type.equalsIgnoreCase("sale"))
            return TransactionType.SALE;
        else
            return TransactionType.ERROR;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return type;
    }
}
