/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ContactExportDto implements Serializable {

	private static final long serialVersionUID = 2054231712903661096L;

	public static final String I18N_PREFIX = "ContactExport";

	private long id;
	private long personId;
	private String uuid;
	private String sourceCaseUuid;
	private CaseClassification caseClassification;
	private Disease internalDisease;
	private String disease;
	private ContactClassification contactClassification;
	private Date lastContactDate;
	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	private Sex sex;
	private String approximateAge;
	private Date reportDate;
	private ContactIdentificationSource contactIdentificationSource;
	private String contactIdentificationSourceDetails;
	private TracingApp tracingApp;
	private String tracingAppDetails;
	private ContactProximity contactProximity;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private PresentCondition presentCondition;
	private Date deathDate;
	private String addressRegion;
	private String addressDistrict;
	@PersonalData
	private String city;
	@PersonalData
	private String address;
	@PersonalData
	private String postalCode;
	private String phone;
	private String occupationType;
	private int numberOfVisits;
	private YesNoUnknown lastCooperativeVisitSymptomatic;
	private Date lastCooperativeVisitDate;
	private String lastCooperativeVisitSymptoms;
	private String region;
	private String district;

	private QuarantineType quarantine;
	private String quarantineTypeDetails;
	private Date quarantineFrom;
	private Date quarantineTo;
	private String quarantineHelpNeeded;
	private long epiDataId;
	private YesNoUnknown traveled;
	private String travelHistory;
	private YesNoUnknown burialAttended;
	private YesNoUnknown directContactConfirmedCase;
	private YesNoUnknown directContactProbableCase;
	private YesNoUnknown contactWithRodent;

	private boolean quarantineOrderedVerbally;
	private boolean quarantineOrderedOfficialDocument;
	private Date quarantineOrderedVerballyDate;
	private Date quarantineOrderedOfficialDocumentDate;

	private ContactJurisdictionDto jurisdiction;

	//@formatter:off
	public ContactExportDto(long id, long personId, String uuid, String sourceCaseUuid, CaseClassification caseClassification, Disease disease, String diseaseDetails,
							ContactClassification contactClassification, Date lastContactDate, String firstName, String lastName, Sex sex,
							Integer approximateAge, ApproximateAgeType approximateAgeType, Date reportDate, ContactIdentificationSource contactIdentificationSource, String contactIdentificationSourceDetails, TracingApp tracingApp, String tracingAppDetails, ContactProximity contactProximity,
							ContactStatus contactStatus, FollowUpStatus followUpStatus, Date followUpUntil,
							QuarantineType quarantine, String quarantineTypeDetails, Date quarantineFrom, Date quarantineTo, String quarantineHelpNeeded,
							boolean quarantineOrderedVerbally, boolean quarantineOrderedOfficialDocument, Date quarantineOrderedVerballyDate, Date quarantineOrderedOfficialDocumentDate,
							PresentCondition presentCondition, Date deathDate,
							String addressRegion, String addressDistrict, String city, String address, String postalCode,
							String phone, String phoneOwner, OccupationType occupationType, String occupationDetails,
							String occupationFacility, String occupationFacilityUuid, String occupationFacilityDetails,
							String region, String district,
							long epiDataId, YesNoUnknown traveled, YesNoUnknown burialAttended, YesNoUnknown directContactConfirmedCase, YesNoUnknown directContactProbableCase, YesNoUnknown contactWithRodent,
							String reportingUserUuid, String regionUuid, String districtUuid,
							String caseReportingUserUuid, String caseRegionUui, String caseDistrictUud, String caseCommunityUuid, String caseHealthFacilityUuid, String casePointOfEntryUuid
	) {
	//@formatter:on

		this.id = id;
		this.personId = personId;
		this.uuid = uuid;
		this.sourceCaseUuid = sourceCaseUuid;
		this.caseClassification = caseClassification;
		this.internalDisease = disease;
		this.disease = DiseaseHelper.toString(disease, diseaseDetails);
		this.contactClassification = contactClassification;
		this.lastContactDate = lastContactDate;
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex;
		this.approximateAge = ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
		this.reportDate = reportDate;
		this.contactIdentificationSource = contactIdentificationSource;
		this.contactIdentificationSourceDetails = contactIdentificationSourceDetails;
		this.tracingApp = tracingApp;
		this.tracingAppDetails = tracingAppDetails;
		this.contactProximity = contactProximity;
		this.contactStatus = contactStatus;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.quarantine = quarantine;
		this.quarantineTypeDetails = quarantineTypeDetails;
		this.quarantineFrom = quarantineFrom;
		this.quarantineTo = quarantineTo;
		this.quarantineHelpNeeded = quarantineHelpNeeded;
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
		this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
		this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
		this.presentCondition = presentCondition;
		this.deathDate = deathDate;
		this.addressRegion = addressRegion;
		this.addressDistrict = addressDistrict;
		this.city = city;
		this.address = address;
		this.postalCode = postalCode;
		this.phone = PersonHelper.buildPhoneString(phone, phoneOwner);
		this.occupationType = PersonHelper.buildOccupationString(
			occupationType,
			occupationDetails,
			FacilityHelper.buildFacilityString(occupationFacilityUuid, occupationFacility, occupationFacilityDetails));
		this.region = region;
		this.district = district;
		this.epiDataId = epiDataId;
		this.traveled = traveled;
		this.burialAttended = burialAttended;
		this.directContactConfirmedCase = directContactConfirmedCase;
		this.directContactProbableCase = directContactProbableCase;
		this.contactWithRodent = contactWithRodent;

		CaseJurisdictionDto caseJurisdiction = caseReportingUserUuid != null
			? null
			: new CaseJurisdictionDto(
				caseReportingUserUuid,
				caseRegionUui,
				caseDistrictUud,
				caseCommunityUuid,
				caseHealthFacilityUuid,
				casePointOfEntryUuid);
		this.jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, caseJurisdiction);
	}

	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(uuid);
	}

	public long getId() {
		return id;
	}

	public long getPersonId() {
		return personId;
	}

	public Disease getInternalDisease() {
		return internalDisease;
	}

	public long getEpiDataId() {
		return epiDataId;
	}

	@Order(0)
	public String getUuid() {
		return uuid;
	}

	@Order(1)
	public String getSourceCaseUuid() {
		return sourceCaseUuid;
	}

	@Order(2)
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	@Order(3)
	public String getDisease() {
		return disease;
	}

	@Order(4)
	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	@Order(5)
	public Date getLastContactDate() {
		return lastContactDate;
	}

	@Order(10)
	public String getFirstName() {
		return firstName;
	}

	@Order(11)
	public String getLastName() {
		return lastName;
	}

	@Order(12)
	public Sex getSex() {
		return sex;
	}

	@Order(13)
	public String getApproximateAge() {
		return approximateAge;
	}

	@Order(14)
	public Date getReportDate() {
		return reportDate;
	}

	@Order(15)
	public String getRegion() {
		return region;
	}

	@Order(16)
	public String getDistrict() {
		return district;
	}

	@Order(20)
	public ContactIdentificationSource getContactIdentificationSource() {
		return contactIdentificationSource;
	}

	@Order(21)
	public String getContactIdentificationSourceDetails() {
		return contactIdentificationSourceDetails;
	}

	@Order(22)
	public TracingApp getTracingApp() {
		return tracingApp;
	}

	@Order(23)
	public String getTracingAppDetails() {
		return tracingAppDetails;
	}

	@Order(24)
	public ContactProximity getContactProximity() {
		return contactProximity;
	}

	@Order(25)
	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	@Order(26)
	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	@Order(27)
	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	@Order(28)
	public QuarantineType getQuarantine() {
		return quarantine;
	}

	@Order(29)
	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	@Order(30)
	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	@Order(31)
	public Date getQuarantineTo() {
		return quarantineTo;
	}

	@Order(32)
	public String getQuarantineHelpNeeded() {
		return quarantineHelpNeeded;
	}

	@Order(33)
	@HideForCountriesExcept
	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	@Order(34)
	@HideForCountriesExcept
	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	@Order(35)
	@HideForCountriesExcept
	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	@Order(36)
	@HideForCountriesExcept
	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	@Order(37)
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(38)
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(39)
	public String getAddressRegion() {
		return addressRegion;
	}

	@Order(40)
	public String getAddressDistrict() {
		return addressDistrict;
	}

	@Order(41)
	public String getCity() {
		return city;
	}

	@Order(42)
	public String getAddress() {
		return address;
	}

	@Order(43)
	public String getPostalCode() {
		return postalCode;
	}

	@Order(44)
	public String getPhone() {
		return phone;
	}

	@Order(45)
	public String getOccupationType() {
		return occupationType;
	}

	@Order(46)
	public int getNumberOfVisits() {
		return numberOfVisits;
	}

	@Order(47)
	public YesNoUnknown getLastCooperativeVisitSymptomatic() {
		return lastCooperativeVisitSymptomatic;
	}

	@Order(48)
	public Date getLastCooperativeVisitDate() {
		return lastCooperativeVisitDate;
	}

	@Order(49)
	public String getLastCooperativeVisitSymptoms() {
		return lastCooperativeVisitSymptoms;
	}

	@Order(50)
	public YesNoUnknown getTraveled() {
		return traveled;
	}

	public void setTraveled(YesNoUnknown traveled) {
		this.traveled = traveled;
	}

	@Order(51)
	public String getTravelHistory() {
		return travelHistory;
	}

	public void setTravelHistory(String travelHistory) {
		this.travelHistory = travelHistory;
	}

	@Order(52)
	public YesNoUnknown getBurialAttended() {
		return burialAttended;
	}

	public void setBurialAttended(YesNoUnknown burialAttended) {
		this.burialAttended = burialAttended;
	}

	@Order(52)
	public YesNoUnknown getDirectContactConfirmedCase() {
		return directContactConfirmedCase;
	}

	public void setDirectContactConfirmedCase(YesNoUnknown directContactConfirmedCase) {
		this.directContactConfirmedCase = directContactConfirmedCase;
	}

	@Order(53)
	public YesNoUnknown getDirectContactProbableCase() {
		return directContactProbableCase;
	}

	public void setDirectContactProbableCase(YesNoUnknown directContactProbableCase) {
		this.directContactProbableCase = directContactProbableCase;
	}

	@Order(54)
	public YesNoUnknown getContactWithRodent() {
		return contactWithRodent;
	}

	public void setContactWithRodent(YesNoUnknown contactWithRodent) {
		this.contactWithRodent = contactWithRodent;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setSourceCaseUuid(String sourceCaseUuid) {
		this.sourceCaseUuid = sourceCaseUuid;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public void setApproximateAge(String approximateAge) {
		this.approximateAge = approximateAge;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public void setContactIdentificationSource(ContactIdentificationSource contactIdentificationSource) {
		this.contactIdentificationSource = contactIdentificationSource;
	}

	public void setContactIdentificationSourceDetails(String contactIdentificationSourceDetails) {
		this.contactIdentificationSourceDetails = contactIdentificationSourceDetails;
	}

	public void setTracingApp(TracingApp tracingApp) {
		this.tracingApp = tracingApp;
	}

	public void setTracingAppDetails(String tracingAppDetails) {
		this.tracingAppDetails = tracingAppDetails;
	}

	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}

	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public void setNumberOfVisits(int numberOfVisits) {
		this.numberOfVisits = numberOfVisits;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setOccupationType(String occupationType) {
		this.occupationType = occupationType;
	}

	public void setLastCooperativeVisitSymptomatic(YesNoUnknown lastCooperativeVisitSymptomatic) {
		this.lastCooperativeVisitSymptomatic = lastCooperativeVisitSymptomatic;
	}

	public void setLastCooperativeVisitDate(Date lastCooperativeVisitDate) {
		this.lastCooperativeVisitDate = lastCooperativeVisitDate;
	}

	public void setLastCooperativeVisitSymptoms(String lastCooperativeVisitSymptoms) {
		this.lastCooperativeVisitSymptoms = lastCooperativeVisitSymptoms;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setEpiDataId(long epiDataId) {
		this.epiDataId = epiDataId;
	}

	public String getReportingUserUuid() {
		return jurisdiction.getReportingUserUuid();
	}

	public String getRegionUuid() {
		return jurisdiction.getRegionUuid();
	}

	public String getDistrictUuid() {
		return jurisdiction.getDistrictUuid();
	}

	public ContactJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}
}
