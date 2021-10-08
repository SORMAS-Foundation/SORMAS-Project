package de.symeda.sormas.api.labmessage;

import java.io.Serializable;
import java.util.Date;

public class ExternalMessageResult<T> implements Serializable {

	private static final long serialVersionUID = 6134397796300281952L;

	private T value;
	private Date synchronizationDate;
	private boolean success = true;
	private String error = "";

	public ExternalMessageResult() {
	}

	public ExternalMessageResult(T value, Date synchronizationDate, boolean success, String error) {
		this.value = value;
		this.synchronizationDate = synchronizationDate;
		this.success = success;
		this.error = error;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Date getSynchronizationDate() {
		return synchronizationDate;
	}

	public void setSynchronizationDate(Date synchronizationDate) {
		this.synchronizationDate = synchronizationDate;
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
