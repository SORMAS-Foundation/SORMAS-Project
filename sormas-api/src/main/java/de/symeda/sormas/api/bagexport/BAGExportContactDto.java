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
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.YesNoUnknown;

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

	private Date sampleDate;
	private Date labReportDate;
	private PathogenTestType testType;
	private PathogenTestResultType testResult;

	private YesNoUnknown exposureLocationYn;
	private String otherExposureLocation;
	private String exposureLocationName;
	private String exposureLocationStreet;
	private String exposureLocationStreetNumber;
	private String exposureLocationCity;
	private String exposureLocationPostalCode;
	private String exposureLocationCountry;

	private QuarantineType quarantineType;
	private String quarantineDetails;

	private String quarantineLocationStreet;
	private String quarantineLocationStreetNumber;
	private String quarantineLocationCity;
	private String quarantineLocationPostalCode;
	private String quarantineLocationCountry;

	private Date followUpUntilDate;
	private Date endOfQuarantineDate;
	private EndOfQuarantineReason endOfQuarantineReason;
	private String endOfQuarantineReasonDetails;

	//@formatter:off
	public BAGExportContactDto(Long contactId, Long personId, String lastName, String firstName,
							   String homeAddressStreet, String homeAddressHouseNumber, String homeAddressCity, String homeAddressPostalCode,
							   String phoneNumber, String mobileNumber, String emailAddress, Sex sex,
							   Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY,
							   OccupationType occupationType,
							   QuarantineType quarantineType, String quarantineDetails,
							   Date followUpUntilDate, Date endOfQuarantineDate, EndOfQuarantineReason endOfQuarantineReason, String endOfQuarantineReasonDetails


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
		this.emailAddress = emailAddress;
		this.sex = sex;
		this.birthDate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
		this.occupationType = occupationType;

		this.quarantineType = quarantineType;
		this.quarantineDetails = quarantineDetails;

		this.followUpUntilDate = followUpUntilDate;
		this.endOfQuarantineDate = endOfQuarantineDate;
		this.endOfQuarantineReason = endOfQuarantineReason;
		this.endOfQuarantineReasonDetails = endOfQuarantineReasonDetails;
	}

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

	@Order(60)
	public YesNoUnknown getExposureLocationYn() {
		return exposureLocationYn;
	}

	public void setExposureLocationYn(YesNoUnknown exposureLocationYn) {
		this.exposureLocationYn = exposureLocationYn;
	}

	@Order(61)
	public String getOtherExposureLocation() {
		return otherExposureLocation;
	}

	public void setOtherExposureLocation(String otherExposureLocation) {
		this.otherExposureLocation = otherExposureLocation;
	}

	@Order(62)
	public String getExposureLocationName() {
		return exposureLocationName;
	}

	public void setExposureLocationName(String exposureLocationName) {
		this.exposureLocationName = exposureLocationName;
	}

	@Order(63)
	public String getExposureLocationStreet() {
		return exposureLocationStreet;
	}

	public void setExposureLocationStreet(String exposureLocationStreet) {
		this.exposureLocationStreet = exposureLocationStreet;
	}

	@Order(64)
	public String getExposureLocationStreetNumber() {
		return exposureLocationStreetNumber;
	}

	public void setExposureLocationStreetNumber(String exposureLocationStreetNumber) {
		this.exposureLocationStreetNumber = exposureLocationStreetNumber;
	}

	@Order(65)
	public String getExposureLocationCity() {
		return exposureLocationCity;
	}

	public void setExposureLocationCity(String exposureLocationCity) {
		this.exposureLocationCity = exposureLocationCity;
	}

	@Order(66)
	public String getExposureLocationPostalCode() {
		return exposureLocationPostalCode;
	}

	public void setExposureLocationPostalCode(String exposureLocationPostalCode) {
		this.exposureLocationPostalCode = exposureLocationPostalCode;
	}

	@Order(67)
	public String getExposureLocationCountry() {
		return exposureLocationCountry;
	}

	public void setExposureLocationCountry(String exposureLocationCountry) {
		this.exposureLocationCountry = exposureLocationCountry;
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
	public String getQuarantineLocationStreet() {
		return quarantineLocationStreet;
	}

	public void setQuarantineLocationStreet(String quarantineLocationStreet) {
		this.quarantineLocationStreet = quarantineLocationStreet;
	}

	@Order(84)
	public String getQuarantineLocationStreetNumber() {
		return quarantineLocationStreetNumber;
	}

	public void setQuarantineLocationStreetNumber(String quarantineLocationStreetNumber) {
		this.quarantineLocationStreetNumber = quarantineLocationStreetNumber;
	}

	@Order(85)
	public String getQuarantineLocationCity() {
		return quarantineLocationCity;
	}

	public void setQuarantineLocationCity(String quarantineLocationCity) {
		this.quarantineLocationCity = quarantineLocationCity;
	}

	@Order(86)
	public String getQuarantineLocationPostalCode() {
		return quarantineLocationPostalCode;
	}

	public void setQuarantineLocationPostalCode(String quarantineLocationPostalCode) {
		this.quarantineLocationPostalCode = quarantineLocationPostalCode;
	}

	@Order(87)
	public String getQuarantineLocationCountry() {
		return quarantineLocationCountry;
	}

	public void setQuarantineLocationCountry(String quarantineLocationCountry) {
		this.quarantineLocationCountry = quarantineLocationCountry;
	}

	@Order(95)
	public Date getFollowUpUntilDate() {
		return followUpUntilDate;
	}

	public void setFollowUpUntilDate(Date followUpUntilDate) {
		this.followUpUntilDate = followUpUntilDate;
	}

	@Order(96)
	public Date getEndOfQuarantineDate() {
		return endOfQuarantineDate;
	}

	public void setEndOfQuarantineDate(Date endOfQuarantineDate) {
		this.endOfQuarantineDate = endOfQuarantineDate;
	}

	@Order(97)
	public EndOfQuarantineReason getEndOfQuarantineReason() {
		return endOfQuarantineReason;
	}

	public void setEndOfQuarantineReason(EndOfQuarantineReason endOfQuarantineReason) {
		this.endOfQuarantineReason = endOfQuarantineReason;
	}

	@Order(98)
	public String getEndOfQuarantineReasonDetails() {
		return endOfQuarantineReasonDetails;
	}

	public void setEndOfQuarantineReasonDetails(String endOfQuarantineReasonDetails) {
		this.endOfQuarantineReasonDetails = endOfQuarantineReasonDetails;
	}
}
