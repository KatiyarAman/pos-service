package com.ris.inventory.pos.util.exception;

public class TransactionNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public TransactionNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TransactionNotFoundException(final String message) {
        super(message);
    }

    public TransactionNotFoundException(final Throwable cause) {
        super(cause);
    }
}
