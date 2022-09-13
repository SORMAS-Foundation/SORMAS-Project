package de.symeda.sormas.api.person;

import de.symeda.sormas.api.audit.Auditable;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class PersonFollowUpEndDto implements Auditable, Serializable {

	private String personUuid;
	private Date latestFollowUpEndDate;

	public PersonFollowUpEndDto(String personUuid, Date latestFollowUpEndDate) {
		this.personUuid = personUuid;
		this.latestFollowUpEndDate = latestFollowUpEndDate;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}

	public Date getLatestFollowUpEndDate() {
		return latestFollowUpEndDate;
	}

	public void setLatestFollowUpEndDate(Date latestFollowUpEndDate) {
		this.latestFollowUpEndDate = latestFollowUpEndDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PersonFollowUpEndDto that = (PersonFollowUpEndDto) o;
		return Objects.equals(personUuid, that.personUuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personUuid);
	}

	@Override
	public String getAuditRepresentation() {
		return String.format("%s(personUuid=%s)", getClass().getSimpleName(), personUuid);
	}
}
