package de.symeda.sormas.api.labmessage;

import java.io.Serializable;

public class ExternalMessageResult<T> implements Serializable {

    private static final long serialVersionUID = 9198218330996945958L;

    private T value;
    private boolean success = true;
    private String error = "";

    public ExternalMessageResult() {
    }

    public ExternalMessageResult(T value, boolean success, String error) {
        this.value = value;
        this.success = success;
        this.error = error;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
