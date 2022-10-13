package de.symeda.sormas.api.externaljournal.patientdiary;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;

@AuditedClass
public class PatientDiaryResult implements Serializable {

	@AuditInclude
	private boolean success;
	private String message;

	public PatientDiaryResult(boolean success, String message) {
		this.success = success;
		this.message = message;
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
}
