package com.ris.inventory.pos.util.exception;

public class CancellationException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public CancellationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CancellationException(final String message) {
        super(message);
    }

    public CancellationException(final Throwable cause) {
        super(cause);
    }
}
