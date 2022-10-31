/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.person;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class SimilarPersonDto extends AbstractUuidDto {

	public static final String I18N_PREFIX = "Person";
	public static final String I18N_PREFIX_LOCATION = "Location";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String NICKNAME = "nickname";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String SEX = "sex";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String PHONE = "phone";
	public static final String DISTRICT_NAME = "districtName";
	public static final String COMMUNITY_NAME = "communityName";
	public static final String POSTAL_CODE = "postalCode";
	public static final String CITY = "city";
	public static final String STREET = "street";
	public static final String HOUSE_NUMBER = "houseNumber";
	public static final String NATIONAL_HEALTH_ID = "nationalHealthId";
	public static final String PASSPORT_NUMBER = "passportNumber";

	private static final List<String> LOCATION_DETAILS = Arrays.asList(POSTAL_CODE, CITY, STREET, HOUSE_NUMBER);

	private String firstName;
	private String lastName;
	@HideForCountries
	private String nickname;
	private String ageAndBirthDate;
	private Sex sex;
	private PresentCondition presentCondition;
	private String phone;
	private String districtName;
	private String communityName;
	private String postalCode;
	private String city;
	private String street;
	private String houseNumber;
	@HideForCountries
	private String nationalHealthId;
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	private String passportNumber;

	public SimilarPersonDto(String uuid) {
		super(uuid);
	}

	public SimilarPersonDto(
		String uuid,
		String firstName,
		String lastName,
		String nickname,
		String ageAndBirthDate,
		Sex sex,
		PresentCondition presentCondition,
		String phone,
		String districtName,
		String communityName,
		String postalCode,
		String city,
		String street,
		String houseNumber,
		String nationalHealthId,
		String passportNumber) {
		super(uuid);
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickname = nickname;
		this.ageAndBirthDate = ageAndBirthDate;
		this.sex = sex;
		this.presentCondition = presentCondition;
		this.phone = phone;
		this.districtName = districtName;
		this.communityName = communityName;
		this.postalCode = postalCode;
		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
		this.nationalHealthId = nationalHealthId;
		this.passportNumber = passportNumber;
	}

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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAgeAndBirthDate() {
		return ageAndBirthDate;
	}

	public void setAgeAndBirthDate(String ageAndBirthDate) {
		this.ageAndBirthDate = ageAndBirthDate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getNationalHealthId() {
		return nationalHealthId;
	}

	public void setNationalHealthId(String nationalHealthId) {
		this.nationalHealthId = nationalHealthId;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public PersonReferenceDto toReference() {
		return new PersonReferenceDto(getUuid(), firstName, lastName);
	}

	@Override
	public String toString() {
		return PersonDto.buildCaption(firstName, lastName);
	}

	public static String getI18nPrefix(String propertyId) {
		return LOCATION_DETAILS.contains(propertyId) ? I18N_PREFIX_LOCATION : I18N_PREFIX;
	}
}
