package de.symeda.sormas.app.backend.common;

/**
 * Created by Mate Strysewske on 04.05.2017.
 */

public class SynchronizationException extends Exception {

    public SynchronizationException() {
        super();
    }

    public SynchronizationException(String message) {
        super(message);
    }

    public SynchronizationException(Exception e) {
        super(e);
    }

}
