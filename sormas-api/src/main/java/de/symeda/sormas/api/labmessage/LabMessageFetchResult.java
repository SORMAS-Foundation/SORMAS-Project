package de.symeda.sormas.api.labmessage;

import java.io.Serializable;

public class LabMessageFetchResult implements Serializable {

	boolean success;
	NewMessagesState newMessages;
	String error;

	public LabMessageFetchResult() {
	}

	public LabMessageFetchResult(boolean success, NewMessagesState newMessages, String error) {
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

	public NewMessagesState getNewMessageState() {
		return newMessages;
	}

	public void setNewMessagesState(NewMessagesState newMessages) {
		this.newMessages = newMessages;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
