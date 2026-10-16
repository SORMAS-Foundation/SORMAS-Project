package de.symeda.sormas.api.personaldata;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class PersonalDataSummaryDto {

	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private Map<String, String> additionalFields;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public Map<String, String> getAdditionalFields() {
		return additionalFields;
	}

	public void setAdditionalFields(Map<String, String> additionalFields) {
		this.additionalFields = additionalFields;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		PersonalDataSummaryDto that = (PersonalDataSummaryDto) o;
		return Objects.equals(firstName, that.firstName)
			&& Objects.equals(lastName, that.lastName)
			&& Objects.equals(birthDate, that.birthDate)
			&& Objects.equals(additionalFields, that.additionalFields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, birthDate, additionalFields);
	}
}
