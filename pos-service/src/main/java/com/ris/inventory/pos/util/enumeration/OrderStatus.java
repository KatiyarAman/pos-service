package com.ris.inventory.pos.util.enumeration;

public enum OrderStatus {
    INITIALIZED("INITIALIZED"),
    CREATED("CREATED"),
    RECEIVED("RECEIVED"),// Online or shipment case status
    ON_THE_WAY("ON THE WAY"), // Online or shipment case status
    DELIVERED("DELIVERED"),// Online or shipment case status
    REFUNDED("REFUNDED"),
    REFUND_IN_PROGRESS("REFUND_IN_PROGRESS"),
    EXCHANGE_IN_PROGRESS("EXCHANGE_IN_PROGRESS"),
    EXCHANGED("EXCHANGED"),
    PARTIAL_EXCHANGED("PARTIAL EXCHANGED"),
    PARTIAL_REFUNDED("PARTIAL REFUNDED"),
    CANCELLED("CANCELLED"),
    ERROR("Error");

    private String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public static OrderStatus from(String status) {
        if (status.equalsIgnoreCase("initialized"))
            return OrderStatus.INITIALIZED;
        else if (status.equalsIgnoreCase("created"))
            return OrderStatus.CREATED;
        else if (status.equalsIgnoreCase("received"))
            return OrderStatus.RECEIVED;
        else if (status.equalsIgnoreCase("on the way"))
            return OrderStatus.ON_THE_WAY;
        else if (status.equalsIgnoreCase("delivered"))
            return OrderStatus.DELIVERED;
        else if (status.equalsIgnoreCase("refunded"))
            return OrderStatus.REFUNDED;
        else if (status.equalsIgnoreCase("refund_in_progress") || status.equalsIgnoreCase("refund in progress"))
            return OrderStatus.REFUND_IN_PROGRESS;
        else if (status.equalsIgnoreCase("exchange_in_progress") || status.equalsIgnoreCase("exchange in progress"))
            return OrderStatus.EXCHANGE_IN_PROGRESS;
        else if (status.equalsIgnoreCase("exchanged"))
            return OrderStatus.EXCHANGED;
        else if (status.equalsIgnoreCase("partial refunded"))
            return OrderStatus.PARTIAL_REFUNDED;
        else if (status.equalsIgnoreCase("partial exchanged"))
            return OrderStatus.PARTIAL_EXCHANGED;
        else if (status.equalsIgnoreCase("cancelled"))
            return OrderStatus.CANCELLED;
        else
            return OrderStatus.ERROR;
    }

    public String getStatus() {
        return status;
    }
}
