package de.symeda.sormas.app.rest;

/**
 * Should be thrown when communication with the server failed
 * due to some unexpected reason that probably goes down to a bug.
 * @see ServerConnectionException
 */
public class ServerCommunicationException extends Exception {

    public ServerCommunicationException(String message) {
        super(message);
    }

    public ServerCommunicationException(Throwable cause) {
        super(cause);
    }

    public ServerCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
