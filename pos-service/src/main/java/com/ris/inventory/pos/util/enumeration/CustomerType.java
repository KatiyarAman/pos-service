package com.ris.inventory.pos.util.enumeration;

public enum CustomerType {
    WALK_IN("WALK IN"),
    ONLINE("ONLINE"),
    WHOLE_SELLER("WHOLE SELLER"),
    ERROR("Error");

    private String type;

    CustomerType(String type) {
        this.type = type;
    }

    public static CustomerType from(String type) {
        if (type.equalsIgnoreCase("walk_in") || type.equalsIgnoreCase("walk in"))
            return CustomerType.WALK_IN;
        else if (type.equalsIgnoreCase("online"))
            return CustomerType.ONLINE;
        else if (type.equalsIgnoreCase("whole_seller") || type.equalsIgnoreCase("whole seller"))
            return CustomerType.WHOLE_SELLER;
        else
            return CustomerType.ERROR;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return type;
    }
}
