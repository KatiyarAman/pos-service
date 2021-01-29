package com.ris.inventory.pos.util.enumeration;

public enum DeliveryStatus {
    INITIALIZED("INITIALIZED"),
    CREATED("CREATED"),
    RECEIVED("RECEIVED"),// Online or shipment case status
    ON_THE_WAY("ON THE WAY"), // Online or shipment case status
    DELIVERED("DELIVERED"),// Online or shipment case status
    CANCELLED("CANCELLED"),
    ERROR("Error");

    private String status;

    DeliveryStatus(String status) {
        this.status = status;
    }

    public static DeliveryStatus from(String status) {
        if (status.equalsIgnoreCase("initialized"))
            return DeliveryStatus.INITIALIZED;
        else if (status.equalsIgnoreCase("created"))
            return DeliveryStatus.CREATED;
        else if (status.equalsIgnoreCase("received"))
            return DeliveryStatus.RECEIVED;
        else if (status.equalsIgnoreCase("on the way") || status.equalsIgnoreCase("on_the_way"))
            return DeliveryStatus.ON_THE_WAY;
        else if (status.equalsIgnoreCase("delivered"))
            return DeliveryStatus.DELIVERED;
        else if (status.equalsIgnoreCase("cancelled"))
            return DeliveryStatus.CANCELLED;
        else
            return DeliveryStatus.ERROR;
    }

    public String getStatus() {
        return status;
    }
}
