package com.ris.inventory.pos.util.exception;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(final String message) {
        super(message);
    }

    public BadRequestException(final Throwable cause) {
        super(cause);
    }
}
