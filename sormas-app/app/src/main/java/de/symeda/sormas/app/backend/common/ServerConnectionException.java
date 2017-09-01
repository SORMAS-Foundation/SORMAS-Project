package de.symeda.sormas.app.backend.common;

/**
 * Created by Mate Strysewske on 04.05.2017.
 */

public class ServerConnectionException extends Exception {

    public ServerConnectionException() {
        super();
    }

    public ServerConnectionException(String message) {
        super(message);
    }

    public ServerConnectionException(Exception e) {
        super(e);
    }

}
