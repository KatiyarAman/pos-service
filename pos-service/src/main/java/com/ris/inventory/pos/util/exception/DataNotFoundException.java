package com.ris.inventory.pos.util.exception;

public class DataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public DataNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DataNotFoundException(final String message) {
        super(message);
    }

    public DataNotFoundException(final Throwable cause) {
        super(cause);
    }

}
