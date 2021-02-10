package de.symeda.sormas.api.labmessage;

import java.io.Serializable;

public class LabMessageFetchResult implements Serializable {

    boolean success;
    String error;

    public LabMessageFetchResult() {}

    public LabMessageFetchResult(boolean success, String error) {
        this.success = success;
        this.error = error;
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
