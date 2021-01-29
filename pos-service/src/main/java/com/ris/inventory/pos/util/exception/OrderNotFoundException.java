package com.ris.inventory.pos.util.exception;

public class OrderNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public OrderNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(final String message) {
        super(message);
    }

    public OrderNotFoundException(final Throwable cause) {
        super(cause);
    }
}
