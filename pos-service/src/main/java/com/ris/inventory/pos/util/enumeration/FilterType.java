package com.ris.inventory.pos.util.enumeration;

public enum FilterType {
    DATE("DATE"),
    PERIOD("PERIOD"),
    LOCATION("LOCATION"),
    TRANSACTION("TRANSACTION"),
    NAME("NAME");

    private String type;

    FilterType(String type) {
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
