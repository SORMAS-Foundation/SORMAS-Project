package de.symeda.sormas.api.personaldata;

import java.time.LocalDate;
import java.util.Objects;

import de.symeda.sormas.api.person.Sex;

public class PersonalDataSearchRequest {

	private String lastName;
	private String firstName;
	private Sex sex;
	private LocalDate birthDate;
	private String countryOfResidence;
	private String localityOfResidence;
	private String municipalityOfResidence;
	private Integer birthYearFrom;
	private Integer birthYearTo;
	private Integer ageFrom;
	private Integer ageTo;
	private Boolean alive;

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

	public String getCountryOfResidence() {
		return countryOfResidence;
	}

	public void setCountryOfResidence(String countryOfResidence) {
		this.countryOfResidence = countryOfResidence;
	}

	public String getLocalityOfResidence() {
		return localityOfResidence;
	}

	public void setLocalityOfResidence(String localityOfResidence) {
		this.localityOfResidence = localityOfResidence;
	}

	public String getMunicipalityOfResidence() {
		return municipalityOfResidence;
	}

	public void setMunicipalityOfResidence(String municipalityOfResidence) {
		this.municipalityOfResidence = municipalityOfResidence;
	}

	public Integer getBirthYearFrom() {
		return birthYearFrom;
	}

	public void setBirthYearFrom(Integer birthYearFrom) {
		this.birthYearFrom = birthYearFrom;
	}

	public Integer getBirthYearTo() {
		return birthYearTo;
	}

	public void setBirthYearTo(Integer birthYearTo) {
		this.birthYearTo = birthYearTo;
	}

	public Integer getAgeFrom() {
		return ageFrom;
	}

	public void setAgeFrom(Integer ageFrom) {
		this.ageFrom = ageFrom;
	}

	public Integer getAgeTo() {
		return ageTo;
	}

	public void setAgeTo(Integer ageTo) {
		this.ageTo = ageTo;
	}

	public Boolean getAlive() {
		return alive;
	}

	public void setAlive(Boolean alive) {
		this.alive = alive;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PersonalDataSearchRequest that = (PersonalDataSearchRequest) o;
		return Objects.equals(lastName, that.lastName)
			&& Objects.equals(firstName, that.firstName)
			&& sex == that.sex
			&& Objects.equals(birthDate, that.birthDate)
			&& Objects.equals(countryOfResidence, that.countryOfResidence)
			&& Objects.equals(localityOfResidence, that.localityOfResidence)
			&& Objects.equals(municipalityOfResidence, that.municipalityOfResidence)
			&& Objects.equals(birthYearFrom, that.birthYearFrom)
			&& Objects.equals(birthYearTo, that.birthYearTo)
			&& Objects.equals(ageFrom, that.ageFrom)
			&& Objects.equals(ageTo, that.ageTo)
			&& Objects.equals(alive, that.alive);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			lastName,
			firstName,
			sex,
			birthDate,
			countryOfResidence,
			localityOfResidence,
			municipalityOfResidence,
			birthYearFrom,
			birthYearTo,
			ageFrom,
			ageTo,
			alive);
	}
}
