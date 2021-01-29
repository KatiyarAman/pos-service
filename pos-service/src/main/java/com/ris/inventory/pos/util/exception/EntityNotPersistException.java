package com.ris.inventory.pos.util.exception;

public class EntityNotPersistException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public EntityNotPersistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EntityNotPersistException(final String message) {
        super(message);
    }

    public EntityNotPersistException(final Throwable cause) {
        super(cause);
    }
}
