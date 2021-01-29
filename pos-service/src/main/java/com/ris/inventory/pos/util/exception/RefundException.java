package com.ris.inventory.pos.util.exception;

public class RefundException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public RefundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RefundException(final String message) {
        super(message);
    }

    public RefundException(final Throwable cause) {
        super(cause);
    }
}
