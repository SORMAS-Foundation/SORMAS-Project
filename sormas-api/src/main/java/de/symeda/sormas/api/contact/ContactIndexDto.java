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
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.jurisdiction.WithJurisdiction;

public class ContactIndexDto implements WithJurisdiction<ContactJurisdictionDto>, Serializable, Cloneable {

	private static final long serialVersionUID = 7511900591141885152L;

	public static final String I18N_PREFIX = "Contact";

	public static final String UUID = "uuid";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String PERSON_FIRST_NAME = "firstName";
	public static final String PERSON_LAST_NAME = "lastName";
	public static final String CAZE = "caze";
	public static final String DISEASE = "disease";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String CONTACT_STATUS = "contactStatus";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER_UUID = "contactOfficerUuid";
	public static final String CONTACT_CATEGORY = "contactCategory";
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String COMPLETENESS = "completeness";
	public static final String CREATION_DATE = "creationDate";
	public static final String ID = "id";
	public static final String VISIT_COUNT = "visitCount";

	private Long id;
	private Date creationDate;
	private Float completeness;
	private String uuid;
	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	private CaseReferenceDto caze;
	private Disease disease;
	private String diseaseDetails;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private String contactOfficerUuid;
	private Date reportDateTime;
	private ContactCategory contactCategory;
	private CaseClassification caseClassification;
	private int visitCount;

	private ContactJurisdictionDto jurisdiction;
	private CaseJurisdictionDto caseJurisdiction;

	//@formatter:off
	public ContactIndexDto(String uuid, String personFirstName, String personLastName, String cazeUuid,
						   Disease disease, String diseaseDetails, String caseFirstName, String caseLastName, String regionUuid,
						   String districtUuid, String communityUuid, Date lastContactDate, ContactCategory contactCategory, ContactProximity contactProximity,
						   ContactClassification contactClassification, ContactStatus contactStatus, FollowUpStatus followUpStatus,
						   Date followUpUntil, String contactOfficerUuid, String reportingUserUuid, Date reportDateTime,
						   CaseClassification caseClassification,
						   String caseReportingUserUid, String caseRegionUuid, String caseDistrictUud, String caseCommunityUuid, String caseHealthFacilityUuid, String casePointOfEntryUuid,
						   Long id,Date creationDate, Float completeness,int visitCount) {
		this(uuid, personFirstName, personLastName, cazeUuid,
				disease, diseaseDetails, caseFirstName, caseLastName, regionUuid,
				districtUuid, lastContactDate, contactCategory, contactProximity,
				contactClassification, contactStatus, followUpStatus,
				followUpUntil, contactOfficerUuid, reportingUserUuid, reportDateTime,
				caseClassification,
				caseReportingUserUid, caseRegionUuid, caseDistrictUud, caseCommunityUuid, caseHealthFacilityUuid, casePointOfEntryUuid,
				id,creationDate, completeness);

		this.visitCount = visitCount;
	}

	//@formatter:off
	public ContactIndexDto(String uuid, String personFirstName, String personLastName, String cazeUuid,
						   Disease disease, String diseaseDetails, String caseFirstName, String caseLastName, String regionUuid,
						   String districtUuid, Date lastContactDate, ContactCategory contactCategory, ContactProximity contactProximity,
						   ContactClassification contactClassification, ContactStatus contactStatus, FollowUpStatus followUpStatus,
						   Date followUpUntil, String contactOfficerUuid, String reportingUserUuid, Date reportDateTime,
						   CaseClassification caseClassification,
						   String caseReportingUserUid, String caseRegionUuid, String caseDistrictUud, String caseCommunityUuid, String caseHealthFacilityUuid, String casePointOfEntryUuid,
						   Long id,Date creationDate, Float completeness) {
		//@formatter:on

		this.id = id;
		this.uuid = uuid;
		this.firstName = personFirstName;
		this.lastName = personLastName;

		if (cazeUuid != null) {
			this.caze = new CaseReferenceDto(cazeUuid, caseFirstName, caseLastName);
			this.caseJurisdiction = new CaseJurisdictionDto(
					caseReportingUserUid,
					caseRegionUuid,
					caseDistrictUud,
					caseCommunityUuid,
					caseHealthFacilityUuid,
					casePointOfEntryUuid);
		}

		this.disease = disease;
		this.contactOfficerUuid = contactOfficerUuid;
		this.reportDateTime = reportDateTime;
		this.caseClassification = caseClassification;
		this.visitCount = visitCount;

		this.jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, communityUuid, caseJurisdiction);
		this.creationDate = creationDate;
		this.completeness = completeness;
		this.jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, caseJurisdiction);
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

	public String getDistrictUuid() {
		return jurisdiction.getDistrictUuid();
	}

	public String getContactOfficerUuid() {
		return contactOfficerUuid;
	}

	public void setContactOfficerUuid(String contactOfficerUuid) {
		this.contactOfficerUuid = contactOfficerUuid;
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

	public CaseJurisdictionDto getCaseJurisdiction() {
		return caseJurisdiction;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Float getCompleteness() {
		return completeness;
	}

	public void setCompleteness(Float completeness) {
		this.completeness = completeness;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
