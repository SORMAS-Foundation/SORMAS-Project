/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.selfreport;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizable;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class SelfReportIndexDto extends AbstractUuidDto implements Pseudonymizable {

	private static final long serialVersionUID = -4984417669514467918L;

	public static final String TYPE = "type";
	public static final String REPORT_DATE = "reportDate";
	public static final String DISEASE = "disease";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String SEX = "sex";
	public static final String DISTRICT = "district";
	public static final String STREET = "street";
	public static final String HOUSE_NUMBER = "houseNumber";
	public static final String POSTAL_CODE = "postalCode";
	public static final String CITY = "city";
	public static final String EMAIL = "email";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PROCESSING_STATUS = "processingStatus";

	private SelfReportType type;
	private final Date reportDate;
	private Disease disease;
	private String firstName;
	private String lastName;
	private AgeAndBirthDateDto ageAndBirthDate;
	private Sex sex;
	private String district;
	private String street;
	private String houseNumber;
	private String postalCode;
	private String city;
	private String email;
	private String phoneNumber;
	private final UserReferenceDto responsibleUser;
	private final SelfReportInvestigationStatus investigationStatus;
	private final SelfReportProcessingStatus processingStatus;
	private DeletionReason deletionReason;
	private String otherDeletionReason;

	public SelfReportIndexDto(
		String uuid,
		Date reportDate,
		UserReferenceDto responsibleUser,
		SelfReportInvestigationStatus investigationStatus,
		SelfReportProcessingStatus processingStatus,
		DeletionReason deletionReason,
		String otherDeletionReason) {
		super(uuid);
		this.reportDate = reportDate;
		this.responsibleUser = responsibleUser;
		this.investigationStatus = investigationStatus;
		this.processingStatus = processingStatus;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
	}

	public SelfReportType getType() {
		return type;
	}

	public void setType(SelfReportType type) {
		this.type = type;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
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

	public AgeAndBirthDateDto getAgeAndBirthDate() {
		return ageAndBirthDate;
	}

	public void setAgeAndBirthDate(AgeAndBirthDateDto ageAndBirthDate) {
		this.ageAndBirthDate = ageAndBirthDate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public SelfReportInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public SelfReportProcessingStatus getProcessingStatus() {
		return processingStatus;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}

	@Override
	public boolean isPseudonymized() {
		return false;
	}

	@Override
	public void setPseudonymized(boolean pseudonymized) {

	}

	@Override
	public boolean isInJurisdiction() {
		return false;
	}

	@Override
	public void setInJurisdiction(boolean inJurisdiction) {

	}
}
