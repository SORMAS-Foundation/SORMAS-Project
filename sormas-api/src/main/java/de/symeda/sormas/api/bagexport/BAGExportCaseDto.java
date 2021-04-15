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
import de.symeda.sormas.api.caze.EndOfIsolationReason;
import de.symeda.sormas.api.caze.QuarantineReason;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SamplingReason;
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
	private String workPlaceCity;
	private String workPlacePostalCode;
	private String workPlaceCountry;

	private YesNoUnknown symptomatic;

	private SamplingReason pcrReason;
	private String otherPcrReason;

	private Date symptomOnsetDate;

	private Date sampleDate;
	private Date labReportDate;
	private PathogenTestType testType;
	private PathogenTestResultType testResult;

	// can't calculate as there can ba more than one converted contacts for a case
	private YesNoUnknown contactCaseLinkCaseYn;
	private Date contactCaseLinkContactDate;
	private Integer contactCaseLinkCaseIdIsm;
	private Long contactCaseLinkCaseId;

	private YesNoUnknown exposureLocationYn;
	private String activityMappingYn;
	private String exposureCountry;

	private FacilityType exposureLocationType;
	private String exposureLocationTypeDetails;

	private String exposureLocationName;
	private String exposureLocationStreet;
	private String exposureLocationStreetNumber;
	private String exposureLocationCity;
	private String exposureLocationPostalCode;
	private String exposureLocationCountry;

	// missing
	private String exposureLocationFlightDetail;

	private Date contactTracingContactDate;

	private YesNoUnknown wasInQuarantineBeforeIsolation;
	// can't calculate as there are more contacts
	private Date startDateOfQuarantineBeforeIsolation;
	private QuarantineReason quarantineReasonBeforeIsolation;
	private String quarantineReasonBeforeIsolationDetails;

	private QuarantineType isolationType;
	private String isolationTypeDetails;

	private String isolationLocationStreet;
	private String isolationLocationStreetNumber;
	private String isolationLocationCity;
	private String isolationLocationPostalCode;
	private String isolationLocationCountry;

	// quarantine start date
	private Date followUpStartDate;
	private Date endOfIsolationDate;
	private EndOfIsolationReason endOfIsolationReason;
	private String endOfIsolationReasonDetails;

	//@formatter:off
	public BAGExportCaseDto(Integer caseIdIsm, Long caseId, Long personId,
							String lastName, String firstName,
							String homeAddressStreet, String homeAddressHouseNumber, String homeAddressCity, String homeAddressPostalCode, String homeAddressCountry,
							String phoneNumber, String mobileNumber, String emailAddress,
							Sex sex, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY,
							OccupationType occupationType,
							boolean symptomatic, Date symptomOnsetDate,
							String activityMappingYn,
							Date contactTracingContactDate,
							YesNoUnknown wasInQuarantineBeforeIsolation, QuarantineReason quarantineReasonBeforeIsolation, String quarantineReasonBeforeIsolationDetails,
							QuarantineType isolationType, String isolationTypeDetails,
							Date followUpStartDate, Date endOfIsolationDate, EndOfIsolationReason endOfIsolationReason, String endOfIsolationReasonDetails) {
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
		this.homeAddressCountry = homeAddressCountry;
		this.phoneNumber = phoneNumber;
		this.mobileNumber = mobileNumber;
		this.emailAddress = emailAddress;
		this.sex = sex;
		this.birthDate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
		this.occupationType = occupationType;
		this.symptomatic = symptomatic ? YesNoUnknown.YES : YesNoUnknown.NO;
		this.symptomOnsetDate = symptomOnsetDate;
		this.activityMappingYn = activityMappingYn;
		this.contactTracingContactDate = contactTracingContactDate;
		this.wasInQuarantineBeforeIsolation = wasInQuarantineBeforeIsolation;
		this.quarantineReasonBeforeIsolation = quarantineReasonBeforeIsolation;
		this.quarantineReasonBeforeIsolationDetails = quarantineReasonBeforeIsolationDetails;
		this.isolationType = isolationType;
		this.isolationTypeDetails = isolationTypeDetails;
		this.followUpStartDate = followUpStartDate;
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

	@Order(2)
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

	@Order(20)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Order(21)
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Order(22)
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Order(30)
	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	@Order(31)
	public BirthDateDto getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(BirthDateDto birthDate) {
		this.birthDate = birthDate;
	}

	@Order(32)
	public OccupationType getOccupationType() {
		return occupationType;
	}

	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}

	@Order(33)
	public String getWorkPlaceName() {
		return workPlaceName;
	}

	@Order(34)
	public void setWorkPlaceName(String workPlaceName) {
		this.workPlaceName = workPlaceName;
	}

	@Order(35)
	public String getWorkPlaceStreet() {
		return workPlaceStreet;
	}

	public void setWorkPlaceStreet(String workPlaceStreet) {
		this.workPlaceStreet = workPlaceStreet;
	}

	@Order(36)
	public String getWorkPlaceStreetNumber() {
		return workPlaceStreetNumber;
	}

	public void setWorkPlaceStreetNumber(String workPlaceStreetNumber) {
		this.workPlaceStreetNumber = workPlaceStreetNumber;
	}

	@Order(37)
	public String getWorkPlaceCity() {
		return workPlaceCity;
	}

	public void setWorkPlaceCity(String workPlaceCity) {
		this.workPlaceCity = workPlaceCity;
	}

	@Order(38)
	public String getWorkPlacePostalCode() {
		return workPlacePostalCode;
	}

	public void setWorkPlacePostalCode(String workPlacePostalCode) {
		this.workPlacePostalCode = workPlacePostalCode;
	}

	@Order(39)
	public String getWorkPlaceCountry() {
		return workPlaceCountry;
	}

	public void setWorkPlaceCountry(String workPlaceCountry) {
		this.workPlaceCountry = workPlaceCountry;
	}

	@Order(50)
	public YesNoUnknown getSymptomatic() {
		return symptomatic;
	}

	public void setSymptomatic(YesNoUnknown symptomatic) {
		this.symptomatic = symptomatic;
	}

	@Order(51)
	public SamplingReason getPcrReason() {
		return pcrReason;
	}

	public void setPcrReason(SamplingReason pcrReason) {
		this.pcrReason = pcrReason;
	}

	@Order(52)
	public String getOtherPcrReason() {
		return otherPcrReason;
	}

	public void setOtherPcrReason(String otherPcrReason) {
		this.otherPcrReason = otherPcrReason;
	}

	@Order(53)
	public Date getSymptomOnsetDate() {
		return symptomOnsetDate;
	}

	public void setSymptomOnsetDate(Date symptomOnsetDate) {
		this.symptomOnsetDate = symptomOnsetDate;
	}

	@Order(60)
	public Date getSampleDate() {
		return sampleDate;
	}

	public void setSampleDate(Date sampleDate) {
		this.sampleDate = sampleDate;
	}

	@Order(61)
	public Date getLabReportDate() {
		return labReportDate;
	}

	public void setLabReportDate(Date labReportDate) {
		this.labReportDate = labReportDate;
	}

	@Order(62)
	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	@Order(63)
	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	@Order(70)
	public YesNoUnknown getContactCaseLinkCaseYn() {
		return contactCaseLinkCaseYn;
	}

	public void setContactCaseLinkCaseYn(YesNoUnknown contactCaseLinkCaseYn) {
		this.contactCaseLinkCaseYn = contactCaseLinkCaseYn;
	}

	@Order(71)
	public Date getContactCaseLinkContactDate() {
		return contactCaseLinkContactDate;
	}

	public void setContactCaseLinkContactDate(Date contactCaseLinkContactDate) {
		this.contactCaseLinkContactDate = contactCaseLinkContactDate;
	}

	@Order(72)
	public Integer getContactCaseLinkCaseIdIsm() {
		return contactCaseLinkCaseIdIsm;
	}

	public void setContactCaseLinkCaseIdIsm(Integer contactCaseLinkCaseIdIsm) {
		this.contactCaseLinkCaseIdIsm = contactCaseLinkCaseIdIsm;
	}

	@Order(73)
	public Long getContactCaseLinkCaseId() {
		return contactCaseLinkCaseId;
	}

	public void setContactCaseLinkCaseId(Long contactCaseLinkCaseId) {
		this.contactCaseLinkCaseId = contactCaseLinkCaseId;
	}

	@Order(80)
	public YesNoUnknown getExposureLocationYn() {
		return exposureLocationYn;
	}

	public void setExposureLocationYn(YesNoUnknown exposureLocationYn) {
		this.exposureLocationYn = exposureLocationYn;
	}

	@Order(81)
	public String getActivityMappingYn() {
		return activityMappingYn;
	}

	public void setActivityMappingYn(String activityMappingYn) {
		this.activityMappingYn = activityMappingYn;
	}

	@Order(82)
	public String getExposureCountry() {
		return exposureCountry;
	}

	public void setExposureCountry(String exposureCountry) {
		this.exposureCountry = exposureCountry;
	}

	@Order(83)
	public FacilityType getExposureLocationType() {
		return exposureLocationType;
	}

	public void setExposureLocationType(FacilityType exposureLocationType) {
		this.exposureLocationType = exposureLocationType;
	}

	@Order(84)
	public String getExposureLocationTypeDetails() {
		return exposureLocationTypeDetails;
	}

	public void setExposureLocationTypeDetails(String exposureLocationTypeDetails) {
		this.exposureLocationTypeDetails = exposureLocationTypeDetails;
	}

	@Order(85)
	public String getExposureLocationName() {
		return exposureLocationName;
	}

	public void setExposureLocationName(String exposureLocationName) {
		this.exposureLocationName = exposureLocationName;
	}

	@Order(86)
	public String getExposureLocationStreet() {
		return exposureLocationStreet;
	}

	public void setExposureLocationStreet(String exposureLocationStreet) {
		this.exposureLocationStreet = exposureLocationStreet;
	}

	@Order(87)
	public String getExposureLocationStreetNumber() {
		return exposureLocationStreetNumber;
	}

	public void setExposureLocationStreetNumber(String exposureLocationStreetNumber) {
		this.exposureLocationStreetNumber = exposureLocationStreetNumber;
	}

	@Order(88)
	public String getExposureLocationCity() {
		return exposureLocationCity;
	}

	public void setExposureLocationCity(String exposureLocationCity) {
		this.exposureLocationCity = exposureLocationCity;
	}

	@Order(89)
	public String getExposureLocationPostalCode() {
		return exposureLocationPostalCode;
	}

	public void setExposureLocationPostalCode(String exposureLocationPostalCode) {
		this.exposureLocationPostalCode = exposureLocationPostalCode;
	}

	@Order(90)
	public String getExposureLocationCountry() {
		return exposureLocationCountry;
	}

	public void setExposureLocationCountry(String exposureLocationCountry) {
		this.exposureLocationCountry = exposureLocationCountry;
	}

	@Order(91)
	public String getExposureLocationFlightDetail() {
		return exposureLocationFlightDetail;
	}

	public void setExposureLocationFlightDetail(String exposureLocationFlightDetail) {
		this.exposureLocationFlightDetail = exposureLocationFlightDetail;
	}

	@Order(100)
	public Date getContactTracingContactDate() {
		return contactTracingContactDate;
	}

	public void setContactTracingContactDate(Date contactTracingContactDate) {
		this.contactTracingContactDate = contactTracingContactDate;
	}

	@Order(101)
	public YesNoUnknown getWasInQuarantineBeforeIsolation() {
		return wasInQuarantineBeforeIsolation;
	}

	public void setWasInQuarantineBeforeIsolation(YesNoUnknown wasInQuarantineBeforeIsolation) {
		this.wasInQuarantineBeforeIsolation = wasInQuarantineBeforeIsolation;
	}

	@Order(102)
	public Date getStartDateOfQuarantineBeforeIsolation() {
		return startDateOfQuarantineBeforeIsolation;
	}

	public void setStartDateOfQuarantineBeforeIsolation(Date startDateOfQuarantineBeforeIsolation) {
		this.startDateOfQuarantineBeforeIsolation = startDateOfQuarantineBeforeIsolation;
	}

	@Order(103)
	public QuarantineReason getQuarantineReasonBeforeIsolation() {
		return quarantineReasonBeforeIsolation;
	}

	public void setQuarantineReasonBeforeIsolation(QuarantineReason quarantineReasonBeforeIsolation) {
		this.quarantineReasonBeforeIsolation = quarantineReasonBeforeIsolation;
	}

	@Order(104)
	public String getQuarantineReasonBeforeIsolationDetails() {
		return quarantineReasonBeforeIsolationDetails;
	}

	public void setQuarantineReasonBeforeIsolationDetails(String quarantineReasonBeforeIsolationDetails) {
		this.quarantineReasonBeforeIsolationDetails = quarantineReasonBeforeIsolationDetails;
	}

	@Order(105)
	public QuarantineType getIsolationType() {
		return isolationType;
	}

	public void setIsolationType(QuarantineType isolationType) {
		this.isolationType = isolationType;
	}

	@Order(106)
	public String getIsolationTypeDetails() {
		return isolationTypeDetails;
	}

	public void setIsolationTypeDetails(String isolationTypeDetails) {
		this.isolationTypeDetails = isolationTypeDetails;
	}

	@Order(107)
	public String getIsolationLocationStreet() {
		return isolationLocationStreet;
	}

	public void setIsolationLocationStreet(String isolationLocationStreet) {
		this.isolationLocationStreet = isolationLocationStreet;
	}

	@Order(108)
	public String getIsolationLocationStreetNumber() {
		return isolationLocationStreetNumber;
	}

	public void setIsolationLocationStreetNumber(String isolationLocationStreetNumber) {
		this.isolationLocationStreetNumber = isolationLocationStreetNumber;
	}

	@Order(109)
	public String getIsolationLocationCity() {
		return isolationLocationCity;
	}

	public void setIsolationLocationCity(String isolationLocationCity) {
		this.isolationLocationCity = isolationLocationCity;
	}

	@Order(110)
	public String getIsolationLocationPostalCode() {
		return isolationLocationPostalCode;
	}

	public void setIsolationLocationPostalCode(String isolationLocationPostalCode) {
		this.isolationLocationPostalCode = isolationLocationPostalCode;
	}

	@Order(111)
	public String getIsolationLocationCountry() {
		return isolationLocationCountry;
	}

	public void setIsolationLocationCountry(String isolationLocationCountry) {
		this.isolationLocationCountry = isolationLocationCountry;
	}

	@Order(120)
	public Date getFollowUpStartDate() {
		return followUpStartDate;
	}

	public void setFollowUpStartDate(Date followUpStartDate) {
		this.followUpStartDate = followUpStartDate;
	}

	@Order(121)
	public Date getEndOfIsolationDate() {
		return endOfIsolationDate;
	}

	public void setEndOfIsolationDate(Date endOfIsolationDate) {
		this.endOfIsolationDate = endOfIsolationDate;
	}

	@Order(122)
	public EndOfIsolationReason getEndOfIsolationReason() {
		return endOfIsolationReason;
	}

	public void setEndOfIsolationReason(EndOfIsolationReason endOfIsolationReason) {
		this.endOfIsolationReason = endOfIsolationReason;
	}

	@Order(123)
	public String getEndOfIsolationReasonDetails() {
		return endOfIsolationReasonDetails;
	}

	public void setEndOfIsolationReasonDetails(String endOfIsolationReasonDetails) {
		this.endOfIsolationReasonDetails = endOfIsolationReasonDetails;
	}
}
