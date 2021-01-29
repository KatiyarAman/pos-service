package com.ris.inventory.pos.util.exception;

public class OrderStateException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public OrderStateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public OrderStateException(final String message) {
        super(message);
    }

    public OrderStateException(final Throwable cause) {
        super(cause);
    }
}
