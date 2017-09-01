package de.symeda.sormas.app.backend.common;

import java.io.IOException;

import retrofit2.Response;

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



    public static ServerConnectionException fromResponse(Response<?> response) {
        String responseErrorBodyString;
        try {
            responseErrorBodyString = response.errorBody().string();
        } catch (IOException e) {
            responseErrorBodyString = "Exception accessing error body: " + e.getMessage();
        }
        return new ServerConnectionException(responseErrorBodyString);
    }

    public ServerConnectionException(Exception e) {
        super(e);
    }

}
