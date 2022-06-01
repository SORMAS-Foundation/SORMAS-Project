package de.symeda.sormas.api.externalmessage;

import java.io.Serializable;

public class ExternalMessageFetchResult implements Serializable {

	boolean success;
	NewMessagesState newMessagesState;
	String error;

	public ExternalMessageFetchResult() {
	}

	public ExternalMessageFetchResult(boolean success, NewMessagesState newMessagesState, String error) {
		this.success = success;
		this.newMessagesState = newMessagesState;
		this.error = error;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public NewMessagesState getNewMessagesState() {
		return newMessagesState;
	}

	public void setNewMessagesState(NewMessagesState newMessagesState) {
		this.newMessagesState = newMessagesState;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
