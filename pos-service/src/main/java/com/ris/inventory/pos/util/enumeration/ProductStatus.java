package com.ris.inventory.pos.util.enumeration;

public enum ProductStatus {

    PURCHASED("PURCHASED"),
    REPLACEMENT("REPLACEMENT"),
    REFUNDED("REFUNDED"),
    PARTIAL_REFUNDED("PARTIAL REFUNDED"),
    EXCHANGED("EXCHANGED"),
    PARTIAL_EXCHANGED("PARTIAL EXCHANGED"),
    CANCELLED("CANCELLED"),
    ERROR("Error");

    private String status;

    ProductStatus(String status) {
        this.status = status;
    }

    public static ProductStatus from(String status) {
        if (status.equalsIgnoreCase("replacement"))
            return ProductStatus.REPLACEMENT;
        else if (status.equalsIgnoreCase("purchased"))
            return ProductStatus.PURCHASED;
        else if (status.equalsIgnoreCase("cancelled"))
            return ProductStatus.CANCELLED;
        else if (status.equalsIgnoreCase("refunded"))
            return ProductStatus.REFUNDED;
        else if (status.equalsIgnoreCase("partial refunded") || status.equalsIgnoreCase("partial_refunded"))
            return ProductStatus.PARTIAL_REFUNDED;
        else if (status.equalsIgnoreCase("exchanged"))
            return ProductStatus.EXCHANGED;
        else if (status.equalsIgnoreCase("partial_exchanged") || status.equalsIgnoreCase("partial exchanged"))
            return ProductStatus.PARTIAL_EXCHANGED;
        else
            return ProductStatus.ERROR;
    }

    public String getStatus() {
        return status;
    }
}
