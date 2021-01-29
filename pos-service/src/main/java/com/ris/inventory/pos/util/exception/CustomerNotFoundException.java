package com.ris.inventory.pos.util.exception;

public class CustomerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public CustomerNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CustomerNotFoundException(final String message) {
        super(message);
    }

    public CustomerNotFoundException(final Throwable cause) {
        super(cause);
    }

}
