package com.ris.inventory.pos.util.exception;

public class DuplicateRecordException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public DuplicateRecordException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DuplicateRecordException(final String message) {
        super(message);
    }

    public DuplicateRecordException(final Throwable cause) {
        super(cause);
    }
}
