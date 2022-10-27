package de.symeda.sormas.api.externalmessage;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;

@AuditedClass
public class ExternalMessageFetchResult implements Serializable {

	private static final long serialVersionUID = -8759739371441385454L;
	@AuditInclude
	private boolean success;
	@AuditInclude
	private NewMessagesState newMessagesState;
	private String error;

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
