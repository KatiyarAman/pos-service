package com.ris.inventory.pos.util.exception;

public class DiscoveryException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public DiscoveryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DiscoveryException(final String message) {
        super(message);
    }

    public DiscoveryException(final Throwable cause) {
        super(cause);
    }

}
