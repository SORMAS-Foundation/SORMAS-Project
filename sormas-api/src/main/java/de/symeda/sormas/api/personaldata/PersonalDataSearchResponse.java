package de.symeda.sormas.api.personaldata;

import java.time.LocalDate;
import java.util.Objects;

import de.symeda.sormas.api.person.LivingStatus;
import de.symeda.sormas.api.person.Sex;

public class PersonalDataSearchResponse {

	private String nationalHealthId;
	private String lastName;
	private String firstName;
	private Sex sex;
	private LocalDate birthDate;
	private String municipality;
	private LivingStatus livingStatus;

	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getMunicipality() {
		return municipality;
	}

	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}

	public LivingStatus getLivingStatus() {
		return livingStatus;
	}

	public void setLivingStatus(LivingStatus livingStatus) {
		this.livingStatus = livingStatus;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PersonalDataSearchResponse that = (PersonalDataSearchResponse) o;
		return Objects.equals(nationalHealthId, that.nationalHealthId)
			&& Objects.equals(lastName, that.lastName)
			&& Objects.equals(firstName, that.firstName)
			&& sex == that.sex
			&& Objects.equals(birthDate, that.birthDate)
			&& Objects.equals(municipality, that.municipality)
			&& livingStatus == that.livingStatus;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nationalHealthId, lastName, firstName, sex, birthDate, municipality, livingStatus);
	}
}
