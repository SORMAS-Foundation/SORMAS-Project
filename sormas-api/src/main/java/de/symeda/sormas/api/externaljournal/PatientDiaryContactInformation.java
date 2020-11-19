package de.symeda.sormas.api.externaljournal;

import java.io.Serializable;

class PatientDiaryContactInformation implements Serializable {

	private static final long serialVersionUID = -144631462985961640L;

	private String email;
	private PatientDiaryPhone phone;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public PatientDiaryPhone getPhone() {
		return phone;
	}

	public void setPhone(PatientDiaryPhone phone) {
		this.phone = phone;
	}
}
