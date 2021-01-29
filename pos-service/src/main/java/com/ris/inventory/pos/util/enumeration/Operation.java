package com.ris.inventory.pos.util.enumeration;

public enum Operation {

    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    ERROR("Error");

    private final String type;

    Operation(String type) {
        this.type = type;
    }

    public static Operation from(String type) {
        if (type.equalsIgnoreCase("insert"))
            return Operation.INSERT;
        else if (type.equalsIgnoreCase("update"))
            return Operation.UPDATE;
        else if (type.equalsIgnoreCase("delete"))
            return Operation.DELETE;
        else
            return Operation.ERROR;
    }

    public String getType() {
        return type;
    }
}
