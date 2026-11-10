package de.symeda.sormas.api.externalpersondata;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.person.Sex;

@AuditedClass
public class ExternalPersonDataSummaryDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private Sex sex;
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

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
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
		ExternalPersonDataSummaryDto that = (ExternalPersonDataSummaryDto) o;
		return Objects.equals(firstName, that.firstName)
			&& Objects.equals(lastName, that.lastName)
			&& Objects.equals(birthDate, that.birthDate)
			&& sex == that.sex
			&& Objects.equals(additionalFields, that.additionalFields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, birthDate, sex, additionalFields);
	}
}
