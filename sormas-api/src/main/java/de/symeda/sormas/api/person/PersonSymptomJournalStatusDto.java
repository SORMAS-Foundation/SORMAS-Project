package de.symeda.sormas.api.person;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.Validations;

public class PersonSymptomJournalStatusDto implements Serializable {

	private static final long serialVersionUID = 6985497943624025440L;

	@NotNull(message = Validations.requiredField)
	private SymptomJournalStatus status;
	private String statusDateTime; // is not used, but given according to API specification.

	public static PersonSymptomJournalStatusDto build(SymptomJournalStatus status, String statusDateTime) {
		final PersonSymptomJournalStatusDto personSymptomJournalStatusDto = new PersonSymptomJournalStatusDto();
		personSymptomJournalStatusDto.setStatus(status);
		personSymptomJournalStatusDto.setStatusDateTime(statusDateTime);
		return personSymptomJournalStatusDto;
	}

	public SymptomJournalStatus getStatus() {
		return status;
	}

	public void setStatus(SymptomJournalStatus status) {
		this.status = status;
	}

	public String getStatusDateTime() {
		return statusDateTime;
	}

	public void setStatusDateTime(String statusDateTime) {
		this.statusDateTime = statusDateTime;
	}
}
