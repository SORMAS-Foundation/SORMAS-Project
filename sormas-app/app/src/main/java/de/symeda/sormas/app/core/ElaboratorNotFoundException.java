package de.symeda.sormas.app.core;

/**
 * Created by Orson on 09/01/2018.
 */

public class ElaboratorNotFoundException extends Exception {

    private Enum enumValue;

    public ElaboratorNotFoundException(Enum e) {
        this(e, null);
    }

    public ElaboratorNotFoundException(Enum e, Throwable cause) {
        this(e, "Elaborator not found for enum: " + e.getClass().getName(), cause);
    }

    public ElaboratorNotFoundException(Enum e, String message, Throwable cause) {
        super(message, cause);

        this.enumValue = e;
    }
}
