package de.symeda.sormas.api.externaljournal;

import java.io.Serializable;

class ExternalPatientContactInformation implements Serializable {

	private static final long serialVersionUID = -144631462985961640L;

	private String email;
	private ExternalPatientPhone phone;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ExternalPatientPhone getPhone() {
		return phone;
	}

	public void setPhone(ExternalPatientPhone phone) {
		this.phone = phone;
	}
}
