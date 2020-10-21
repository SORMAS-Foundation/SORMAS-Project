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
import de.symeda.sormas.api.caze.CovidTestReason;
import de.symeda.sormas.api.caze.EndOfIsolationReason;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class BAGExportCaseDto implements Serializable {

	private Integer caseIdIsm;
	private Long caseId;
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
	private String emailAddress;
	private Sex sex;
	private BirthDateDto birthDate;

	private OccupationType occupationType;

	private String workPlaceName;
	private String workPlaceStreet;
	private String workPlaceStreetNumber;
	private String workPlaceLocation;
	private String workPlacePostalCode;
	private String workPlaceCountry;

	private YesNoUnknown symptomatic;

	private CovidTestReason pcrReason;
	private String otherPcrReason;

	private Date symptomOnsetDate;

	private Date sampleDate;
	private Date labReportDate;
	private PathogenTestType testType;
	private PathogenTestResultType testResult;

	private String contactConfirmedCaseYn;
	private String contactConfirmedCaseDate;
	private String contactConfirmedCaseFallIdIsm;

	private YesNoUnknown infectionLocationYn;
	private String activityMappingYn;
	private String infectionCountry;
	private String infectionLocation;
	private String otherInfectionLocation;
	private String infectionLocationName;
	private String infectionLocationStreet;
	private String infectionLocationStreetNumber;
	private String infectionLocationCity;
	private String infectionLocationPostalCode;
	private String infectionLocationCountry;

	private Date contactTracingContactDate;

	private QuarantineType quarantineType;
	private String quarantineDetails;

	private String isolationLocationStreet;
	private String isolationLocationStreetNumber;
	private String isolationLocationCity;
	private String isolationLocationPostalCode;
	private String isolationLocationCountry;

	private Date followUpUntilDate;
	private Date endOfIsolationDate;
	private EndOfIsolationReason endOfIsolationReason;
	private String endOfIsolationReasonDetails;

	//@formatter:off
	public BAGExportCaseDto(Integer caseIdIsm, Long caseId, Long personId, String lastName, String firstName,
							String homeAddressStreet, String homeAddressHouseNumber, String homeAddressCity, String homeAddressPostalCode,
							String phoneNumber, String mobileNumber, String emailAddress, Sex sex,
							Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY,
							OccupationType occupationType, YesNoUnknown symptomatic,
							CovidTestReason covidTestReason, String covidTestReasonDetails,
							Date symptomOnsetDate,
							Date contactTracingContactDate,
							QuarantineType quarantineType, String quarantineDetails,
							Date followUpUntilDate, Date endOfIsolationDate, EndOfIsolationReason endOfIsolationReason, String endOfIsolationReasonDetails


	) {
		//@formatter:on

		this.caseIdIsm = caseIdIsm;
		this.caseId = caseId;
		this.personId = personId;
		this.lastName = lastName;
		this.firstName = firstName;
		this.homeAddressStreet = homeAddressStreet;
		this.homeAddressHouseNumber = homeAddressHouseNumber;
		this.homeAddressCity = homeAddressCity;
		this.homeAddressPostalCode = homeAddressPostalCode;
		this.phoneNumber = phoneNumber;
		this.mobileNumber = mobileNumber;
		this.emailAddress = emailAddress;
		this.sex = sex;
		this.birthDate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
		this.occupationType = occupationType;
		this.symptomatic = symptomatic;
		this.pcrReason = covidTestReason;
		this.otherPcrReason = covidTestReasonDetails;
		this.symptomOnsetDate = symptomOnsetDate;

		this.contactTracingContactDate = contactTracingContactDate;

		this.quarantineType = quarantineType;
		this.quarantineDetails = quarantineDetails;

		this.followUpUntilDate = followUpUntilDate;
		this.endOfIsolationDate = endOfIsolationDate;
		this.endOfIsolationReason = endOfIsolationReason;
		this.endOfIsolationReasonDetails = endOfIsolationReasonDetails;
	}

	@Order(1)
	public Integer getCaseIdIsm() {
		return caseIdIsm;
	}

	public void setCaseIdIsm(Integer caseIdIsm) {
		this.caseIdIsm = caseIdIsm;
	}

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	@Order(3)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Order(4)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Order(5)
	public String getHomeAddressStreet() {
		return homeAddressStreet;
	}

	@Order(6)
	public void setHomeAddressStreet(String homeAddressStreet) {
		this.homeAddressStreet = homeAddressStreet;
	}

	@Order(7)
	public String getHomeAddressHouseNumber() {
		return homeAddressHouseNumber;
	}

	public void setHomeAddressHouseNumber(String homeAddressHouseNumber) {
		this.homeAddressHouseNumber = homeAddressHouseNumber;
	}

	@Order(8)
	public String getHomeAddressCity() {
		return homeAddressCity;
	}

	public void setHomeAddressCity(String homeAddressCity) {
		this.homeAddressCity = homeAddressCity;
	}

	@Order(9)
	public String getHomeAddressPostalCode() {
		return homeAddressPostalCode;
	}

	public void setHomeAddressPostalCode(String homeAddressPostalCode) {
		this.homeAddressPostalCode = homeAddressPostalCode;
	}

	@Order(10)
	public String getHomeAddressCountry() {
		return homeAddressCountry;
	}

	public void setHomeAddressCountry(String homeAddressCountry) {
		this.homeAddressCountry = homeAddressCountry;
	}

	@Order(11)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Order(12)
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Order(13)
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Order(14)
	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	@Order(15)
	public BirthDateDto getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(BirthDateDto birthDate) {
		this.birthDate = birthDate;
	}

	@Order(16)
	public OccupationType getOccupationType() {
		return occupationType;
	}

	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}

	@Order(20)
	public String getWorkPlaceName() {
		return workPlaceName;
	}

	public void setWorkPlaceName(String workPlaceName) {
		this.workPlaceName = workPlaceName;
	}

	@Order(21)
	public String getWorkPlaceStreet() {
		return workPlaceStreet;
	}

	public void setWorkPlaceStreet(String workPlaceStreet) {
		this.workPlaceStreet = workPlaceStreet;
	}

	@Order(22)
	public String getWorkPlaceStreetNumber() {
		return workPlaceStreetNumber;
	}

	public void setWorkPlaceStreetNumber(String workPlaceStreetNumber) {
		this.workPlaceStreetNumber = workPlaceStreetNumber;
	}

	@Order(23)
	public String getWorkPlaceLocation() {
		return workPlaceLocation;
	}

	public void setWorkPlaceLocation(String workPlaceLocation) {
		this.workPlaceLocation = workPlaceLocation;
	}

	@Order(24)
	public String getWorkPlacePostalCode() {
		return workPlacePostalCode;
	}

	public void setWorkPlacePostalCode(String workPlacePostalCode) {
		this.workPlacePostalCode = workPlacePostalCode;
	}

	@Order(25)
	public String getWorkPlaceCountry() {
		return workPlaceCountry;
	}

	public void setWorkPlaceCountry(String workPlaceCountry) {
		this.workPlaceCountry = workPlaceCountry;
	}

	@Order(30)
	public YesNoUnknown getSymptomatic() {
		return symptomatic;
	}

	public void setSymptomatic(YesNoUnknown symptomatic) {
		this.symptomatic = symptomatic;
	}

	@Order(31)
	public CovidTestReason getPcrReason() {
		return pcrReason;
	}

	public void setPcrReason(CovidTestReason pcrReason) {
		this.pcrReason = pcrReason;
	}

	@Order(32)
	public String getOtherPcrReason() {
		return otherPcrReason;
	}

	public void setOtherPcrReason(String otherPcrReason) {
		this.otherPcrReason = otherPcrReason;
	}

	@Order(40)
	public Date getSymptomOnsetDate() {
		return symptomOnsetDate;
	}

	public void setSymptomOnsetDate(Date symptomOnsetDate) {
		this.symptomOnsetDate = symptomOnsetDate;
	}

	@Order(41)
	public Date getSampleDate() {
		return sampleDate;
	}

	public void setSampleDate(Date sampleDate) {
		this.sampleDate = sampleDate;
	}

	@Order(50)
	public Date getLabReportDate() {
		return labReportDate;
	}

	public void setLabReportDate(Date labReportDate) {
		this.labReportDate = labReportDate;
	}

	@Order(51)
	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	@Order(52)
	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	@Order(53)
	public String getContactConfirmedCaseYn() {
		return contactConfirmedCaseYn;
	}

	public void setContactConfirmedCaseYn(String contactConfirmedCaseYn) {
		this.contactConfirmedCaseYn = contactConfirmedCaseYn;
	}

	@Order(54)
	public String getContactConfirmedCaseDate() {
		return contactConfirmedCaseDate;
	}

	public void setContactConfirmedCaseDate(String contactConfirmedCaseDate) {
		this.contactConfirmedCaseDate = contactConfirmedCaseDate;
	}

	@Order(55)
	public String getContactConfirmedCaseFallIdIsm() {
		return contactConfirmedCaseFallIdIsm;
	}

	public void setContactConfirmedCaseFallIdIsm(String contactConfirmedCaseFallIdIsm) {
		this.contactConfirmedCaseFallIdIsm = contactConfirmedCaseFallIdIsm;
	}

	@Order(56)
	public YesNoUnknown getInfectionLocationYn() {
		return infectionLocationYn;
	}

	public void setInfectionLocationYn(YesNoUnknown infectionLocationYn) {
		this.infectionLocationYn = infectionLocationYn;
	}

	@Order(57)
	public String getActivityMappingYn() {
		return activityMappingYn;
	}

	public void setActivityMappingYn(String activityMappingYn) {
		this.activityMappingYn = activityMappingYn;
	}

	@Order(58)
	public String getInfectionCountry() {
		return infectionCountry;
	}

	public void setInfectionCountry(String infectionCountry) {
		this.infectionCountry = infectionCountry;
	}

	@Order(59)
	public String getInfectionLocation() {
		return infectionLocation;
	}

	public void setInfectionLocation(String infectionLocation) {
		this.infectionLocation = infectionLocation;
	}

	@Order(60)
	public String getOtherInfectionLocation() {
		return otherInfectionLocation;
	}

	public void setOtherInfectionLocation(String otherInfectionLocation) {
		this.otherInfectionLocation = otherInfectionLocation;
	}

	@Order(61)
	public String getInfectionLocationName() {
		return infectionLocationName;
	}

	public void setInfectionLocationName(String infectionLocationName) {
		this.infectionLocationName = infectionLocationName;
	}

	@Order(62)
	public String getInfectionLocationStreet() {
		return infectionLocationStreet;
	}

	public void setInfectionLocationStreet(String infectionLocationStreet) {
		this.infectionLocationStreet = infectionLocationStreet;
	}

	@Order(63)
	public String getInfectionLocationStreetNumber() {
		return infectionLocationStreetNumber;
	}

	public void setInfectionLocationStreetNumber(String infectionLocationStreetNumber) {
		this.infectionLocationStreetNumber = infectionLocationStreetNumber;
	}

	@Order(64)
	public String getInfectionLocationCity() {
		return infectionLocationCity;
	}

	public void setInfectionLocationCity(String infectionLocationCity) {
		this.infectionLocationCity = infectionLocationCity;
	}

	@Order(65)
	public String getInfectionLocationPostalCode() {
		return infectionLocationPostalCode;
	}

	public void setInfectionLocationPostalCode(String infectionLocationPostalCode) {
		this.infectionLocationPostalCode = infectionLocationPostalCode;
	}

	@Order(66)
	public String getInfectionLocationCountry() {
		return infectionLocationCountry;
	}

	public void setInfectionLocationCountry(String infectionLocationCountry) {
		this.infectionLocationCountry = infectionLocationCountry;
	}

	@Order(80)
	public Date getContactTracingContactDate() {
		return contactTracingContactDate;
	}

	public void setContactTracingContactDate(Date contactTracingContactDate) {
		this.contactTracingContactDate = contactTracingContactDate;
	}

	@Order(81)
	public QuarantineType getQuarantineType() {
		return quarantineType;
	}

	public void setQuarantineType(QuarantineType quarantineType) {
		this.quarantineType = quarantineType;
	}

	@Order(82)
	public String getQuarantineDetails() {
		return quarantineDetails;
	}

	public void setQuarantineDetails(String quarantineDetails) {
		this.quarantineDetails = quarantineDetails;
	}

	@Order(83)
	public String getIsolationLocationStreet() {
		return isolationLocationStreet;
	}

	public void setIsolationLocationStreet(String isolationLocationStreet) {
		this.isolationLocationStreet = isolationLocationStreet;
	}

	@Order(84)
	public String getIsolationLocationStreetNumber() {
		return isolationLocationStreetNumber;
	}

	public void setIsolationLocationStreetNumber(String isolationLocationStreetNumber) {
		this.isolationLocationStreetNumber = isolationLocationStreetNumber;
	}

	@Order(85)
	public String getIsolationLocationCity() {
		return isolationLocationCity;
	}

	public void setIsolationLocationCity(String isolationLocationCity) {
		this.isolationLocationCity = isolationLocationCity;
	}

	@Order(86)
	public String getIsolationLocationPostalCode() {
		return isolationLocationPostalCode;
	}

	public void setIsolationLocationPostalCode(String isolationLocationPostalCode) {
		this.isolationLocationPostalCode = isolationLocationPostalCode;
	}

	@Order(87)
	public String getIsolationLocationCountry() {
		return isolationLocationCountry;
	}

	public void setIsolationLocationCountry(String isolationLocationCountry) {
		this.isolationLocationCountry = isolationLocationCountry;
	}

	@Order(95)
	public Date getFollowUpUntilDate() {
		return followUpUntilDate;
	}

	public void setFollowUpUntilDate(Date followUpUntilDate) {
		this.followUpUntilDate = followUpUntilDate;
	}

	@Order(96)
	public Date getEndOfIsolationDate() {
		return endOfIsolationDate;
	}

	public void setEndOfIsolationDate(Date endOfIsolationDate) {
		this.endOfIsolationDate = endOfIsolationDate;
	}

	@Order(97)
	public EndOfIsolationReason getEndOfIsolationReason() {
		return endOfIsolationReason;
	}

	public void setEndOfIsolationReason(EndOfIsolationReason endOfIsolationReason) {
		this.endOfIsolationReason = endOfIsolationReason;
	}

	@Order(98)
	public String getEndOfIsolationReasonDetails() {
		return endOfIsolationReasonDetails;
	}

	public void setEndOfIsolationReasonDetails(String endOfIsolationReasonDetails) {
		this.endOfIsolationReasonDetails = endOfIsolationReasonDetails;
	}
}
