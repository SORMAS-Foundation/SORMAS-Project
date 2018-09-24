package de.symeda.sormas.app.rest;

public class ApiVersionException extends Exception {
    private String appUrl;
    private String version;

    public ApiVersionException() {
        super();
    }

    public ApiVersionException(String message) {
        super(message);
    }

    public ApiVersionException(String message, String appUrl, String version) {
        super(message);
        this.appUrl = appUrl;
        this.version = version;
    }

    public ApiVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiVersionException(Throwable cause) {
        super(cause);
    }

    public String getAppUrl() {
        return appUrl;
    }

    public String getVersion() {
        return version;
    }
}
