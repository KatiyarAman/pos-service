package com.ris.inventory.pos.util.exception;

public class SplitPaymentException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public SplitPaymentException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SplitPaymentException(final String message) {
        super(message);
    }

    public SplitPaymentException(final Throwable cause) {
        super(cause);
    }
}
