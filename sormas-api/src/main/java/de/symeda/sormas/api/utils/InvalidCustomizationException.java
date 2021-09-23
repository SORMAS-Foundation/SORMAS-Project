package de.symeda.sormas.api.utils;

/**
 * Thrown when there is a problem caused by an invalid customization of the system.
 */
public class InvalidCustomizationException extends RuntimeException {
    public InvalidCustomizationException() {
    }

    public InvalidCustomizationException(String message) {
        super(message);
    }

    public InvalidCustomizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCustomizationException(Throwable cause) {
        super(cause);
    }

    public InvalidCustomizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
