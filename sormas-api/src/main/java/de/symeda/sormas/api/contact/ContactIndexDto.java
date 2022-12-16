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
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Light-weight index infomation on contact entries for larger queries.")
public class ContactIndexDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	private static final long serialVersionUID = 7511900591141885152L;

	public static final String I18N_PREFIX = "Contact";

	public static final String UUID = "uuid";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String PERSON_UUID = "personUuid";
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
	public static final String SYMPTOM_JOURNAL_STATUS = "symptomJournalStatus";
	public static final String VACCINATION_STATUS = "vaccinationStatus";
	public static final String CONTACT_OFFICER_UUID = "contactOfficerUuid";
	public static final String CONTACT_CATEGORY = "contactCategory";
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String EXTERNAL_ID = "externalID";
	public static final String EXTERNAL_TOKEN = "externalToken";
	public static final String INTERNAL_TOKEN = "internalToken";
	public static final String COMPLETENESS = "completeness";
	public static final String REGION_UUID = "regionUuid";
	public static final String DISTRICT_UUID = "districtUuid";
	public static final String COMMUNITY_UUID = "communityUuid";
	@Schema(description = "Universal ID (UUID) of the person that had a contact")
	private String personUuid;
	@PersonalData
	@Schema(description = "First name of the person that had a contact")
	private String firstName;
	@PersonalData
	@Schema(description = "Last name of the person that had a contact")
	private String lastName;
	private CaseReferenceDto caze;
	@Schema(description = "Disease with which the contact occured")
	private Disease disease;
	@Schema(description = "Details about the disease in free text")
	private String diseaseDetails;
	@Schema(description = "Date when the last contact occured")
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	@Schema(description = "Percentage, indicates how many of all available data entries have actually been filled")
	private Float completeness;
	private FollowUpStatus followUpStatus;
	@Schema(description = "Date until the case has to be followed up.")
	private Date followUpUntil;
	private SymptomJournalStatus symptomJournalStatus;
	private VaccinationStatus vaccinationStatus;
	@Schema(description = "Universal ID (UUID) of the district where the contact occured")
	private String districtUuid;
	@Schema(description = "Universal ID (UUID) of the SORMAS user working on the contact")
	private String contactOfficerUuid;
	@Schema(description = "Date and time at which the contact was reported")
	private Date reportDateTime;
	private ContactCategory contactCategory;
	private CaseClassification caseClassification;
	@Schema(description = "Number of follow-up calls assessing the health condition of the person that had a contact")
	private int visitCount;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private Integer missedVisitsCount;
	@Schema(description = "Case Id in external systems.")
	private String externalID;
	@Schema(description = "External token ID / file number of the contact.")
	private String externalToken;
	@Schema(description = "Internal token ID / file number of the contact.")
	private String internalToken;
	@Schema(description = "Name of the region where the person that had a contact lives")
	private String regionName;
	@Schema(description = "Name of the district where the person that had a contact lives")
	private String districtName;
	@Schema(description = "Name of the region where the person that has a case of the disease lives")
	private String caseRegionName;
	@Schema(description = "Name of the district where the person that has a case of the disease lives")
	private String caseDistrictName;

	private ContactJurisdictionFlagsDto contactJurisdictionFlagsDto;

	//@formatter:off
	public ContactIndexDto(String uuid, String personUuid, String personFirstName, String personLastName, String cazeUuid,
						   Disease disease, String diseaseDetails, String caseFirstName, String caseLastName, String regionName,
						   String districtName, Date lastContactDate, ContactCategory contactCategory,
						   ContactProximity contactProximity, ContactClassification contactClassification, ContactStatus contactStatus, Float completeness,
						   FollowUpStatus followUpStatus, Date followUpUntil, SymptomJournalStatus symptomJournalStatus, VaccinationStatus vaccinationStatus, String contactOfficerUuid,
						   String reportingUserUuid, Date reportDateTime,
						   CaseClassification caseClassification, String caseRegionName, String caseDistrictName,
						   Date changeDate, // XXX: unused, only here for TypedQuery mapping
						   String externalID, String externalToken, String internalToken, boolean isInJurisdiction, boolean isCaseInJurisdiction,
						   int visitCount,
						   Date latestChangedDate // unused, only here for TypedQuery mapping
	) {
	//@formatter:on

		super(uuid);
		this.personUuid = personUuid;
		this.firstName = personFirstName;
		this.lastName = personLastName;

		if (cazeUuid != null) {
			this.caze = new CaseReferenceDto(cazeUuid, caseFirstName, caseLastName);
		}

		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.lastContactDate = lastContactDate;
		this.contactCategory = contactCategory;
		this.contactProximity = contactProximity;
		this.contactClassification = contactClassification;
		this.contactStatus = contactStatus;
		this.completeness = completeness;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.symptomJournalStatus = symptomJournalStatus;
		this.vaccinationStatus = vaccinationStatus;
		this.contactOfficerUuid = contactOfficerUuid;
		this.reportDateTime = reportDateTime;
		this.caseClassification = caseClassification;
		this.visitCount = visitCount;
		this.externalID = externalID;
		this.externalToken = externalToken;
		this.internalToken = internalToken;
		this.regionName = regionName;
		this.districtName = districtName;
		this.caseRegionName = caseRegionName;
		this.caseDistrictName = caseDistrictName;

		this.contactJurisdictionFlagsDto = new ContactJurisdictionFlagsDto(isInJurisdiction, isCaseInJurisdiction);
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
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

	public Float getCompleteness() {
		return completeness;
	}

	public void setCompleteness(Float completeness) {
		this.completeness = completeness;
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

	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	public void setSymptomJournalStatus(SymptomJournalStatus symptomJournalStatus) {
		this.symptomJournalStatus = symptomJournalStatus;
	}

	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
	}

	public String getDistrictUuid() {
		return districtUuid;
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

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	public String getExternalToken() {
		return externalToken;
	}

	public void setExternalToken(String externalToken) {
		this.externalToken = externalToken;
	}

	public String getInternalToken() {
		return internalToken;
	}

	public void setInternalToken(String internalToken) {
		this.internalToken = internalToken;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getCaseRegionName() {
		return caseRegionName;
	}

	public void setCaseRegionName(String caseRegionName) {
		this.caseRegionName = caseRegionName;
	}

	public String getCaseDistrictName() {
		return caseDistrictName;
	}

	public void setCaseDistrictName(String caseDistrictName) {
		this.caseDistrictName = caseDistrictName;
	}

	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(getUuid());
	}

	public boolean getInJurisdiction() {
		return contactJurisdictionFlagsDto.getInJurisdiction();
	}

	public boolean getCaseInJurisdiction() {
		return contactJurisdictionFlagsDto.getCaseInJurisdiction();
	}

	public Integer getMissedVisitsCount() {
		return missedVisitsCount;
	}

	public void setMissedVisitsCount(Integer missedVisitsCount) {
		this.missedVisitsCount = missedVisitsCount;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
