package com.ris.inventory.pos.util.exception;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public NotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(final String message) {
        super(message);
    }

    public NotFoundException(final Throwable cause) {
        super(cause);
    }
}
