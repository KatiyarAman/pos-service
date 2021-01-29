package com.ris.inventory.pos.util.exception;

public class ProductNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public ProductNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProductNotFoundException(final String message) {
        super(message);
    }

    public ProductNotFoundException(final Throwable cause) {
        super(cause);
    }
}
