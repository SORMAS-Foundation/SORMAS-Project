package de.symeda.sormas.api.externaljournal;

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class ExternalJournalValidation implements Serializable {

	@AuditInclude
	private boolean valid;
	private String message;

	public ExternalJournalValidation(boolean valid, String message) {
		this.valid = valid;
		this.message = message;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
