package com.ris.inventory.pos.util.exception;

public class ExchangeException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public ExchangeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ExchangeException(final String message) {
        super(message);
    }

    public ExchangeException(final Throwable cause) {
        super(cause);
    }
}
