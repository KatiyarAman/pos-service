package com.ris.inventory.pos.util.exception;

public class InvalidPaginationException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public InvalidPaginationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidPaginationException(final String message) {
        super(message);
    }

    public InvalidPaginationException(final Throwable cause) {
        super(cause);
    }

}
