package com.ris.inventory.pos.util.exception;

public class PaymentUpdateException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public PaymentUpdateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PaymentUpdateException(final String message) {
        super(message);
    }

    public PaymentUpdateException(final Throwable cause) {
        super(cause);
    }
}
