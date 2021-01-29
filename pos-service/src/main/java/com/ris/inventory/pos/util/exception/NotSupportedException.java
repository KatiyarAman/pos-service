package com.ris.inventory.pos.util.exception;

public class NotSupportedException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public NotSupportedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NotSupportedException(final String message) {
        super(message);
    }

    public NotSupportedException(final Throwable cause) {
        super(cause);
    }

}
