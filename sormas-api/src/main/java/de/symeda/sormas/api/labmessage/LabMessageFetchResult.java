package de.symeda.sormas.api.labmessage;

import java.io.Serializable;

public class LabMessageFetchResult implements Serializable {

    boolean success;
    boolean newMessages;
    String error;

    public LabMessageFetchResult() {}

    public LabMessageFetchResult(boolean success, boolean newMessages, String error) {
        this.success = success;
        this.newMessages = newMessages;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean hasNewMessages() {
        return newMessages;
    }

    public void setNewMessages(boolean newMessages) {
        this.newMessages = newMessages;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
