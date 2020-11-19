package de.symeda.sormas.api.utils;

/**
 * Thrown when an entity that is supposed to be saved has an empty value which should not be empty
 */
@SuppressWarnings("serial")
public class EmptyValueException extends ValidationRuntimeException {

    public EmptyValueException(String message) {
        super(message);
    }
}
