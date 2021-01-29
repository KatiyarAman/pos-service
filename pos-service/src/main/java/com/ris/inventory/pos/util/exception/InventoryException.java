package com.ris.inventory.pos.util.exception;

public class InventoryException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public InventoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InventoryException(final String message) {
        super(message);
    }

    public InventoryException(final Throwable cause) {
        super(cause);
    }
}
