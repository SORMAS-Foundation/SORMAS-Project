package de.symeda.sormas.app.component;

/**
 * Created by Orson on 02/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class InvalidValueException extends Exception {

    private Object value;

    public InvalidValueException(Object e) {
        this(e, (Throwable) null);
    }

    public InvalidValueException(Object e, String message) {
        this(e, message, null);
    }

    public InvalidValueException(Object e, Throwable cause) {
        this(e, "Field value is not valid: " + e.getClass().getName(), cause);
    }

    public InvalidValueException(Object e, String message, Throwable cause) {
        super(message, cause);

        this.value = e;
    }
}
