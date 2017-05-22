package de.symeda.sormas.app.backend.common;

/**
 * Created by Mate Strysewske on 04.05.2017.
 */

public class DaoException extends Exception {

    public DaoException() {
        super();
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Exception e) {
        super(e);
    }

}
