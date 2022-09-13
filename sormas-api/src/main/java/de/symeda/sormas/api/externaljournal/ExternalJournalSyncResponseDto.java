package de.symeda.sormas.api.externaljournal;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.symeda.sormas.api.audit.Auditable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalJournalSyncResponseDto implements Auditable, Serializable {

	private boolean success;
	private String message;
	private Map<String, String> errors;

	public ExternalJournalSyncResponseDto() {

	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	@Override
	public String getAuditRepresentation() {
		return getClass().getSimpleName();
	}
}
