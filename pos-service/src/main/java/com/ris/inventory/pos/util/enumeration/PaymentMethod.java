package com.ris.inventory.pos.util.enumeration;

public enum PaymentMethod {
    WAITING("WAITING"),
    CASH("CASH"),
    CANCELLED("CANCELLED"),
    CHEQUE("CHEQUE"),
    CREDIT_DEBIT_CARD("CREDIT DEBIT CARD"),
    NET_BANKING("NET BANKING"),
    SPLIT_PAYMENT("SPLIT PAYMENT"),
    ORDER_CHANGED("ORDER CHANGED"),
    ERROR("Error");

    private String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public static PaymentMethod from(String method) {
        if (method.equalsIgnoreCase("cash"))
            return PaymentMethod.CASH;
        else if (method.equalsIgnoreCase("cheque"))
            return PaymentMethod.CHEQUE;
        else if (method.equalsIgnoreCase("waiting"))
            return PaymentMethod.WAITING;
        else if (method.equalsIgnoreCase("cancelled"))
            return PaymentMethod.CANCELLED;
        else if (method.equalsIgnoreCase("credit debit card") || method.equalsIgnoreCase("credit_debit_card"))
            return PaymentMethod.CREDIT_DEBIT_CARD;
        else if (method.equalsIgnoreCase("order_changed") || method.equalsIgnoreCase("order changed"))
            return PaymentMethod.ORDER_CHANGED;
        else if (method.equalsIgnoreCase("SPLIT_PAYMENT") || method.equalsIgnoreCase("SPLIT PAYMENT"))
            return PaymentMethod.SPLIT_PAYMENT;
        else if (method.equalsIgnoreCase("net_banking") || method.equalsIgnoreCase("net banking"))
            return PaymentMethod.NET_BANKING;
        else
            return PaymentMethod.ERROR;
    }

    public String getMethod() {
        return method;
    }
}
