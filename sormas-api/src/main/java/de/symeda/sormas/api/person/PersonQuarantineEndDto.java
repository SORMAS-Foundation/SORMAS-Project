package de.symeda.sormas.api.person;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class PersonQuarantineEndDto implements Serializable {

	private String personUuid;
	private Date latestQuarantineEndDate;

	public PersonQuarantineEndDto(String personUuid, Date latestQuarantineEndDate) {
		this.personUuid = personUuid;
		this.latestQuarantineEndDate = latestQuarantineEndDate;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}

	public Date getLatestQuarantineEndDate() {
		return latestQuarantineEndDate;
	}

	public void setLatestQuarantineEndDate(Date latestQuarantineEndDate) {
		this.latestQuarantineEndDate = latestQuarantineEndDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PersonQuarantineEndDto that = (PersonQuarantineEndDto) o;
		return Objects.equals(personUuid, that.personUuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personUuid);
	}
}
