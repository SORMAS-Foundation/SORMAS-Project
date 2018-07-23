package de.symeda.sormas.app.backend.common;

public class DaoException extends Exception {

    public DaoException() {
        super();
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Exception cause) {
        super(cause.getLocalizedMessage(), cause);
    }

}
