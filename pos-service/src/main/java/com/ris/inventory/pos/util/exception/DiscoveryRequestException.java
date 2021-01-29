package com.ris.inventory.pos.util.exception;

public class DiscoveryRequestException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366163L;

    public DiscoveryRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DiscoveryRequestException(final String message) {
        super(message);
    }

    public DiscoveryRequestException(final Throwable cause) {
        super(cause);
    }

}
