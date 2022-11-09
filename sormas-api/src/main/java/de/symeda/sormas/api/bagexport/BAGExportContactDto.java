/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.bagexport;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.contact.EndOfQuarantineReason;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.Order;

public class BAGExportContactDto implements Serializable {

	private Long contactId;
	private Long personId;

	private String lastName;
	private String firstName;

	private String homeAddressStreet;
	private String homeAddressHouseNumber;
	private String homeAddressCity;
	private String homeAddressPostalCode;
	private String homeAddressCountry;

	private String phoneNumber;
	private String mobileNumber;
	private Sex sex;
	private BirthDateDto birthDate;

	private OccupationType occupationType;

	private String workPlaceName;
	private String workPlacePostalCode;
	private String workPlaceCountry;

	private QuarantineType quarantineType;
	private String quarantineDetails;

	private Integer caseLinkCaseIdIsm;
	private Long caseLinkCaseId;
	// missing
	private Date caseLinkContactDate;

	private String exposureLocationCountry;
	private FacilityType exposureLocationType;
	private String exposureLocationTypeDetails;
	private String exposureLocationName;
	// same as `exposureLocationTypeDetails`
	private String otherExposureLocation;
	private String exposureLocationStreet;
	private String exposureLocationStreetNumber;
	private String exposureLocationCity;
	private String exposureLocationPostalCode;
	//	missing
	private String exposureLocationFlightDetail;

	private Date symptomOnsetDate;

	private PathogenTestType testType;
	private Date sampleDate;
	private PathogenTestResultType testResult;

	private Date startOfQuarantineDate;
	private Date endOfQuarantineDate;
	private EndOfQuarantineReason endOfQuarantineReason;
	private String endOfQuarantineReasonDetails;

// int, long, java.lang.String, java.util.Date, java.util.Date, de.symeda.sormas.api.contact.EndOfQuarantineReason, java.lang.String
	//@formatter:off
	public BAGExportContactDto(Long contactId, Long personId, String lastName, String firstName,
							   String homeAddressStreet, String homeAddressHouseNumber, String homeAddressCity, String homeAddressPostalCode,
							   String phoneNumber, String mobileNumber, Sex sex,
							   Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY,
							   OccupationType occupationType,
							   QuarantineType quarantineType, String quarantineDetails,
							   Integer caseLinkCaseIdIsm, Long caseLinkCaseId, Date caseLinkContactDate,
							   Date startOfQuarantineDate, Date endOfQuarantineDate, EndOfQuarantineReason endOfQuarantineReason, String endOfQuarantineReasonDetails


	) {
		//@formatter:on

		this.contactId = contactId;
		this.personId = personId;
		this.lastName = lastName;
		this.firstName = firstName;
		this.homeAddressStreet = homeAddressStreet;
		this.homeAddressHouseNumber = homeAddressHouseNumber;
		this.homeAddressCity = homeAddressCity;
		this.homeAddressPostalCode = homeAddressPostalCode;
		this.phoneNumber = phoneNumber;
		this.mobileNumber = mobileNumber;
		this.sex = sex;
		this.birthDate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
		this.occupationType = occupationType;

		this.quarantineType = quarantineType;
		this.quarantineDetails = quarantineDetails;

		this.caseLinkCaseIdIsm = caseLinkCaseIdIsm;
		this.caseLinkCaseId = caseLinkCaseId;
		this.caseLinkContactDate = caseLinkContactDate;

		this.startOfQuarantineDate = startOfQuarantineDate;
		this.endOfQuarantineDate = endOfQuarantineDate;
		this.endOfQuarantineReason = endOfQuarantineReason;
		this.endOfQuarantineReasonDetails = endOfQuarantineReasonDetails;
	}

	@Order(1)
	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	@Order(2)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Order(3)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Order(10)
	public String getHomeAddressStreet() {
		return homeAddressStreet;
	}

	public void setHomeAddressStreet(String homeAddressStreet) {
		this.homeAddressStreet = homeAddressStreet;
	}

	@Order(11)
	public String getHomeAddressHouseNumber() {
		return homeAddressHouseNumber;
	}

	public void setHomeAddressHouseNumber(String homeAddressHouseNumber) {
		this.homeAddressHouseNumber = homeAddressHouseNumber;
	}

	@Order(12)
	public String getHomeAddressCity() {
		return homeAddressCity;
	}

	public void setHomeAddressCity(String homeAddressCity) {
		this.homeAddressCity = homeAddressCity;
	}

	@Order(13)
	public String getHomeAddressPostalCode() {
		return homeAddressPostalCode;
	}

	public void setHomeAddressPostalCode(String homeAddressPostalCode) {
		this.homeAddressPostalCode = homeAddressPostalCode;
	}

	@Order(14)
	public String getHomeAddressCountry() {
		return homeAddressCountry;
	}

	public void setHomeAddressCountry(String homeAddressCountry) {
		this.homeAddressCountry = homeAddressCountry;
	}

	@Order(15)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Order(16)
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Order(20)
	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	@Order(21)
	public BirthDateDto getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(BirthDateDto birthDate) {
		this.birthDate = birthDate;
	}

	@Order(22)
	public OccupationType getOccupationType() {
		return occupationType;
	}

	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}

	@Order(30)
	public String getWorkPlaceName() {
		return workPlaceName;
	}

	public void setWorkPlaceName(String workPlaceName) {
		this.workPlaceName = workPlaceName;
	}

	@Order(31)
	public String getWorkPlacePostalCode() {
		return workPlacePostalCode;
	}

	public void setWorkPlacePostalCode(String workPlacePostalCode) {
		this.workPlacePostalCode = workPlacePostalCode;
	}

	@Order(32)
	public String getWorkPlaceCountry() {
		return workPlaceCountry;
	}

	public void setWorkPlaceCountry(String workPlaceCountry) {
		this.workPlaceCountry = workPlaceCountry;
	}

	@Order(33)
	public QuarantineType getQuarantineType() {
		return quarantineType;
	}

	public void setQuarantineType(QuarantineType quarantineType) {
		this.quarantineType = quarantineType;
	}

	@Order(34)
	public String getQuarantineDetails() {
		return quarantineDetails;
	}

	public void setQuarantineDetails(String quarantineDetails) {
		this.quarantineDetails = quarantineDetails;
	}

	@Order(40)
	public Integer getCaseLinkCaseIdIsm() {
		return caseLinkCaseIdIsm;
	}

	public void setCaseLinkCaseIdIsm(Integer caseLinkCaseIdIsm) {
		this.caseLinkCaseIdIsm = caseLinkCaseIdIsm;
	}

	@Order(41)
	public Long getCaseLinkCaseId() {
		return caseLinkCaseId;
	}

	public void setCaseLinkCaseId(Long caseLinkCaseId) {
		this.caseLinkCaseId = caseLinkCaseId;
	}

	@Order(42)
	public Date getCaseLinkContactDate() {
		return caseLinkContactDate;
	}

	public void setCaseLinkContactDate(Date caseLinkContactDate) {
		this.caseLinkContactDate = caseLinkContactDate;
	}

	@Order(43)
	public String getExposureLocationCountry() {
		return exposureLocationCountry;
	}

	public void setExposureLocationCountry(String exposureLocationCountry) {
		this.exposureLocationCountry = exposureLocationCountry;
	}

	@Order(44)
	public FacilityType getExposureLocationType() {
		return exposureLocationType;
	}

	@Order(45)
	public void setExposureLocationType(FacilityType exposureLocationType) {
		this.exposureLocationType = exposureLocationType;
	}

	@Order(46)
	public String getExposureLocationTypeDetails() {
		return exposureLocationTypeDetails;
	}

	public void setExposureLocationTypeDetails(String exposureLocationTypeDetails) {
		this.exposureLocationTypeDetails = exposureLocationTypeDetails;
	}

	@Order(47)
	public String getExposureLocationName() {
		return exposureLocationName;
	}

	public void setExposureLocationName(String exposureLocationName) {
		this.exposureLocationName = exposureLocationName;
	}

	@Order(48)
	public String getOtherExposureLocation() {
		return otherExposureLocation;
	}

	public void setOtherExposureLocation(String otherExposureLocation) {
		this.otherExposureLocation = otherExposureLocation;
	}

	@Order(49)
	public String getExposureLocationStreet() {
		return exposureLocationStreet;
	}

	public void setExposureLocationStreet(String exposureLocationStreet) {
		this.exposureLocationStreet = exposureLocationStreet;
	}

	@Order(50)
	public String getExposureLocationStreetNumber() {
		return exposureLocationStreetNumber;
	}

	public void setExposureLocationStreetNumber(String exposureLocationStreetNumber) {
		this.exposureLocationStreetNumber = exposureLocationStreetNumber;
	}

	@Order(51)
	public String getExposureLocationCity() {
		return exposureLocationCity;
	}

	public void setExposureLocationCity(String exposureLocationCity) {
		this.exposureLocationCity = exposureLocationCity;
	}

	@Order(52)
	public String getExposureLocationPostalCode() {
		return exposureLocationPostalCode;
	}

	public void setExposureLocationPostalCode(String exposureLocationPostalCode) {
		this.exposureLocationPostalCode = exposureLocationPostalCode;
	}

	@Order(53)
	public String getExposureLocationFlightDetail() {
		return exposureLocationFlightDetail;
	}

	public void setExposureLocationFlightDetail(String exposureLocationFlightDetail) {
		this.exposureLocationFlightDetail = exposureLocationFlightDetail;
	}

	@Order(60)
	public Date getSymptomOnsetDate() {
		return symptomOnsetDate;
	}

	public void setSymptomOnsetDate(Date symptomOnsetDate) {
		this.symptomOnsetDate = symptomOnsetDate;
	}

	@Order(61)
	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	@Order(62)
	public Date getSampleDate() {
		return sampleDate;
	}

	public void setSampleDate(Date sampleDate) {
		this.sampleDate = sampleDate;
	}

	@Order(63)
	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	@Order(70)
	public Date getStartOfQuarantineDate() {
		return startOfQuarantineDate;
	}

	public void setStartOfQuarantineDate(Date startOfQuarantineDate) {
		this.startOfQuarantineDate = startOfQuarantineDate;
	}

	@Order(71)
	public Date getEndOfQuarantineDate() {
		return endOfQuarantineDate;
	}

	public void setEndOfQuarantineDate(Date endOfQuarantineDate) {
		this.endOfQuarantineDate = endOfQuarantineDate;
	}

	@Order(72)
	public EndOfQuarantineReason getEndOfQuarantineReason() {
		return endOfQuarantineReason;
	}

	public void setEndOfQuarantineReason(EndOfQuarantineReason endOfQuarantineReason) {
		this.endOfQuarantineReason = endOfQuarantineReason;
	}

	@Order(73)
	public String getEndOfQuarantineReasonDetails() {
		return endOfQuarantineReasonDetails;
	}

	public void setEndOfQuarantineReasonDetails(String endOfQuarantineReasonDetails) {
		this.endOfQuarantineReasonDetails = endOfQuarantineReasonDetails;
	}
}
