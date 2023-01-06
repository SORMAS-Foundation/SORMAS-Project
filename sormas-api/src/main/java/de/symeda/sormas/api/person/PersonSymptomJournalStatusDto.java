package de.symeda.sormas.api.person;

import java.io.Serializable;

import de.symeda.sormas.api.utils.Required;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for data related to a persons symptoms journal")
public class PersonSymptomJournalStatusDto implements Serializable {

	private static final long serialVersionUID = 6985497943624025440L;

	@Required
	private SymptomJournalStatus status;
	@Schema(description = "Date and time of the journal status update. NOT USED!")
	private String statusDateTime; // is not used, but given according to API specification.

	public static PersonSymptomJournalStatusDto build(SymptomJournalStatus status, String statusDateTime) {
		final PersonSymptomJournalStatusDto personSymptomJournalStatusDto = new PersonSymptomJournalStatusDto();
		personSymptomJournalStatusDto.setStatus(status);
		personSymptomJournalStatusDto.setStatusDateTime(statusDateTime);
		return personSymptomJournalStatusDto;
	};

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
