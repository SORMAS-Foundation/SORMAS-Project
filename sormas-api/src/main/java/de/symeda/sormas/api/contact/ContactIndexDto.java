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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.utils.PersonalData;

public class ContactIndexDto implements Serializable {

	private static final long serialVersionUID = 7511900591141885152L;

	public static final String I18N_PREFIX = "Contact";

	public static final String UUID = "uuid";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String PERSON_FIRST_NAME = "firstName";
	public static final String PERSON_LAST_NAME = "lastName";
	public static final String CAZE = "caze";
	public static final String DISEASE = "disease";
	public static final String REGION_UUID = "regionUuid";
	public static final String DISTRICT_UUID = "districtUuid";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String CONTACT_STATUS = "contactStatus";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER_UUID = "contactOfficerUuid";
	public static final String CONTACT_CATEGORY = "contactCategory";
	public static final String CASE_CLASSIFICATION = "caseClassification";

	private String uuid;
	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	private CaseReferenceDto caze;
	private Disease disease;
	private String diseaseDetails;
	private String regionUuid;
	private String districtUuid;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private String contactOfficerUuid;
	private String reportingUserUuid;
	private Date reportDateTime;
	private ContactCategory contactCategory;
	private CaseClassification caseClassification;
	private int visitCount;

	private ContactJurisdictionDto jurisdiction;

	public ContactIndexDto(String uuid, String personFirstName, String personLastName, String cazeUuid,
						   Disease disease, String diseaseDetails, String caseFirstName, String caseLastName, String regionUuid,
						   String districtUuid, Date lastContactDate, ContactCategory contactCategory, ContactProximity contactProximity,
						   ContactClassification contactClassification, ContactStatus contactStatus, FollowUpStatus followUpStatus,
						   Date followUpUntil, String contactOfficerUuid, String reportingUserUuid, Date reportDateTime,
						   CaseClassification caseClassification, int visitCount,
						   String caseReportingUserUid, String caseRegionUuid, String caseDistrictUud, String caseCommunityUuid, String caseHealthFacilityUuid, String casePointOfEntryUuid) {
		this.uuid = uuid;
		this.firstName = personFirstName;
		this.lastName = personLastName;
		this.caze = new CaseReferenceDto(cazeUuid, caseFirstName, caseLastName, new CaseJurisdictionDto(caseReportingUserUid, caseRegionUuid, caseDistrictUud,
				caseCommunityUuid, caseHealthFacilityUuid, casePointOfEntryUuid));
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.lastContactDate = lastContactDate;
		this.contactCategory = contactCategory;
		this.contactProximity = contactProximity;
		this.contactClassification = contactClassification;
		this.contactStatus = contactStatus;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.contactOfficerUuid = contactOfficerUuid;
		this.reportingUserUuid = reportingUserUuid;
		this.reportDateTime = reportDateTime;
		this.caseClassification = caseClassification;
		this.visitCount = visitCount;
		this.jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid,
				new CaseJurisdictionDto(caseReportingUserUid, caseRegionUuid, caseDistrictUud, caseCommunityUuid, caseHealthFacilityUuid, casePointOfEntryUuid));
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public ContactProximity getContactProximity() {
		return contactProximity;
	}

	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public String getRegionUuid() {
		return regionUuid;
	}

	public void setRegionUuid(String regionUuid) {
		this.regionUuid = regionUuid;
	}

	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public String getContactOfficerUuid() {
		return contactOfficerUuid;
	}

	public void setContactOfficerUuid(String contactOfficerUuid) {
		this.contactOfficerUuid = contactOfficerUuid;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public ContactCategory getContactCategory() {
		return contactCategory;
	}

	public void setContactCategory(ContactCategory contactCategory) {
		this.contactCategory = contactCategory;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}
	public int getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(uuid);
	}

	public ContactJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}
}
