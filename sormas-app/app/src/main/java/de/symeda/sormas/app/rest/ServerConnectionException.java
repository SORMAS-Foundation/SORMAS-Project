package de.symeda.sormas.app.rest;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Should be thrown when connecting or communicating with the server failed, because:
 * * 401, 403: the user was not properly authorized
 * * 404: the server was not found
 * * 503: the server is currently not available
 * * 600: no network connection
 * * 601: app version is too new
 * @see ServerCommunicationException
 */
public class ServerConnectionException extends Exception {

    public static final List<Integer> RelatedErrorCodes = Arrays.asList(401, 403, 404, 503, 600, 601);

    private final int customHtmlErrorCode;

    public ServerConnectionException(int customHtmlErrorCode) {
        this.customHtmlErrorCode = customHtmlErrorCode;
    }

    public int getCustomHtmlErrorCode() {
        return customHtmlErrorCode;
    }

    public String getMessage(Context context) {

        switch (getCustomHtmlErrorCode()) {
            case 401:
                return context.getResources().getString(R.string.snackbar_http_401);
            case 403:
                return context.getResources().getString(R.string.snackbar_http_403);
            case 404:
                return String.format(context.getResources().getString(R.string.snackbar_http_404), ConfigProvider.getServerRestUrl());
            case 503:
                return context.getResources().getString(R.string.snackbar_http_503);
            case 600:
                return context.getResources().getString(R.string.snackbar_no_connection);
            case 601:
                return context.getResources().getString(R.string.snackbar_version_too_new);
            default:
                throw new IllegalArgumentException(""+ getCustomHtmlErrorCode());
        }
    }
}
