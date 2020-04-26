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

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Required;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ContactDto extends EntityDto {

	private static final long serialVersionUID = -7764607075875188799L;

	public static final String I18N_PREFIX = "Contact";
	
	public static final String PERSON = "person";
	public static final String CAZE = "caze";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String CONTACT_STATUS = "contactStatus";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_COMMENT = "followUpComment";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String DESCRIPTION = "description";
	public static final String RELATION_TO_CASE = "relationToCase";
	public static final String RELATION_DESCRIPTION = "relationDescription";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	public static final String RESULTING_CASE = "resultingCase";
	public static final String RESULTING_CASE_USER = "resultingCaseUser";
	public static final String VISITS = "visits";
	public static final String EXTERNAL_ID = "externalID";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String HIGH_PRIORITY = "highPriority";
	public static final String IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE = "immunosuppressiveTherapyBasicDisease";
	public static final String IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS = "immunosuppressiveTherapyBasicDiseaseDetails";
	public static final String CARE_FOR_PEOPLE_OVER_60 = "careForPeopleOver60";
	public static final String QUARANTINE = "quarantine";
	public static final String QUARANTINE_FROM = "quarantineFrom";
	public static final String QUARANTINE_TO = "quarantineTo";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String CASE_ID_EXTERNAL_SYSTEM = "caseIdExternalSystem";
	public static final String CASE_OR_EVENT_INFORMATION = "caseOrEventInformation";
	public static final String CONTACT_PROXIMITY_DETAILS = "contactProximityDetails";
	public static final String CONTACT_CATEGORY = "contactCategory";
	public static final String OVERWRITE_FOLLOW_UP_UTIL = "overwriteFollowUpUntil";
	public static final String QUARANTINE_HELP_NEEDED = "quarantineHelpNeeded";
	public static final String QUARANTINE_ORDERED_VERBALLY = "quarantineOrderedVerbally";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT = "quarantineOrderedOfficialDocument";
	public static final String QUARANTINE_ORDERED_VERBALLY_DATE = "quarantineOrderedVerballyDate";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE = "quarantineOrderedOfficialDocumentDate";
	public static final String QUARANTINE_HOME_POSSIBLE = "quarantineHomePossible";
	public static final String QUARANTINE_HOME_POSSIBLE_COMMENT = "quarantineHomePossibleComment";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED = "quarantineHomeSupplyEnsured";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT = "quarantineHomeSupplyEnsuredComment";

	private CaseReferenceDto caze;
	private String caseIdExternalSystem;
	private String caseOrEventInformation;
	private Disease disease;
	private String diseaseDetails;

	@Required
	private Date reportDateTime;
	@Required
	private UserReferenceDto reportingUser;
	private Double reportLat;
	private Double reportLon;
	private Float reportLatLonAccuracy;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	@Required
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private String contactProximityDetails;
	private ContactCategory contactCategory;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private String followUpComment;
	private Date followUpUntil;
	private boolean overwriteFollowUpUntil;
	private String description;
	private ContactRelation relationToCase;
	private String relationDescription;
	private String externalID;
	
	private boolean highPriority;
	private YesNoUnknown immunosuppressiveTherapyBasicDisease;
	private String immunosuppressiveTherapyBasicDiseaseDetails;
	private YesNoUnknown careForPeopleOver60;

	private QuarantineType quarantine;
	private Date quarantineFrom;
	private Date quarantineTo;

	@Required
	private PersonReferenceDto person;

	private UserReferenceDto contactOfficer;

	private CaseReferenceDto resultingCase; // read-only now, but editable long-term
	private UserReferenceDto resultingCaseUser;

	private String quarantineHelpNeeded;
	private boolean quarantineOrderedVerbally;
	private boolean quarantineOrderedOfficialDocument;
	private Date quarantineOrderedVerballyDate;
	private Date quarantineOrderedOfficialDocumentDate;
	private YesNoUnknown quarantineHomePossible;
	private String quarantineHomePossibleComment;
	private YesNoUnknown quarantineHomeSupplyEnsured;
	private String quarantineHomeSupplyEnsuredComment;

	public static ContactDto build() {
		return build(null, null, null);
	}
	
	public static ContactDto build(CaseDataDto caze) {
		return build(caze.toReference(), caze.getDisease(), caze.getDiseaseDetails());
	}
	
	public static ContactDto build(CaseReferenceDto caze, Disease disease, String diseaseDetails) {
		ContactDto contact = new ContactDto();
		contact.setUuid(DataHelper.createUuid());

		contact.assignCase(caze, disease, diseaseDetails);
		contact.setPerson(new PersonReferenceDto(DataHelper.createUuid()));

		contact.setReportDateTime(new Date());
		contact.setContactClassification(ContactClassification.UNCONFIRMED);
		contact.setContactStatus(ContactStatus.ACTIVE);

		return contact;
	}
	
	public void assignCase(CaseDataDto caze) {
		assignCase(caze.toReference(), caze.getDisease(), caze.getDiseaseDetails());
	}
	
	public void assignCase(CaseReferenceDto caze, Disease disease, String diseaseDetails) {
		setCaze(caze);
		setDisease(disease);
		setDiseaseDetails(diseaseDetails);
	}
	
	public PersonReferenceDto getPerson() {
		return person;
	}
	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}
	public CaseReferenceDto getCaze() {
		return caze;
	}
	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}
	public Date getReportDateTime() {
		return reportDateTime;
	}
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public UserReferenceDto getContactOfficer() {
		return contactOfficer;
	}
	public void setContactOfficer(UserReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
	}
	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}
	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
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
	public Date getFollowUpUntil() {
		return followUpUntil;
	}
	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}
	public ContactRelation getRelationToCase() {
		return relationToCase;
	}
	public void setRelationToCase(ContactRelation relationToCase) {
		this.relationToCase = relationToCase;
	}
	public String getRelationDescription() {
		return relationDescription;
	}
	public void setRelationDescription(String relationDescription) {
		this.relationDescription = relationDescription;
	}
	public CaseReferenceDto getResultingCase() {
		return resultingCase;
	}
	public void setResultingCase(CaseReferenceDto resultingCase) {
		this.resultingCase = resultingCase;
	}
	public Double getReportLat() {
		return reportLat;
	}
	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}
	public Double getReportLon() {
		return reportLon;
	}
	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}
	public String getFollowUpComment() {
		return followUpComment;
	}
	public void setFollowUpComment(String followUpComment) {
		this.followUpComment = followUpComment;
	}
	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}
	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}
	
	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(getUuid());
	}
	public UserReferenceDto getResultingCaseUser() {
		return resultingCaseUser;
	}
	public void setResultingCaseUser(UserReferenceDto resultingCaseUser) {
		this.resultingCaseUser = resultingCaseUser;
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

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public boolean isHighPriority() {
		return highPriority;
	}

	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

	public YesNoUnknown getImmunosuppressiveTherapyBasicDisease() {
		return immunosuppressiveTherapyBasicDisease;
	}

	public void setImmunosuppressiveTherapyBasicDisease(YesNoUnknown immunosuppressiveTherapyBasicDisease) {
		this.immunosuppressiveTherapyBasicDisease = immunosuppressiveTherapyBasicDisease;
	}

	public String getImmunosuppressiveTherapyBasicDiseaseDetails() {
		return immunosuppressiveTherapyBasicDiseaseDetails;
	}

	public void setImmunosuppressiveTherapyBasicDiseaseDetails(String immunosuppressiveTherapyBasicDiseaseDetails) {
		this.immunosuppressiveTherapyBasicDiseaseDetails = immunosuppressiveTherapyBasicDiseaseDetails;
	}

	public YesNoUnknown getCareForPeopleOver60() {
		return careForPeopleOver60;
	}

	public void setCareForPeopleOver60(YesNoUnknown careForPeopleOver60) {
		this.careForPeopleOver60 = careForPeopleOver60;
	}
	
	public QuarantineType getQuarantine() {
		return quarantine;
	}

	public void setQuarantine(QuarantineType quarantine) {
		this.quarantine = quarantine;
	}

	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	public void setQuarantineFrom(Date quarantineFrom) {
		this.quarantineFrom = quarantineFrom;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public String getCaseIdExternalSystem() {
		return caseIdExternalSystem;
	}

	public void setCaseIdExternalSystem(String caseIdExternalSystem) {
		this.caseIdExternalSystem = caseIdExternalSystem;
	}

	public String getCaseOrEventInformation() {
		return caseOrEventInformation;
	}

	public void setCaseOrEventInformation(String caseOrEventInformation) {
		this.caseOrEventInformation = caseOrEventInformation;
	}

	public boolean isOverwriteFollowUpUntil() {
		return overwriteFollowUpUntil;
	}

	public void setOverwriteFollowUpUntil(boolean overwriteFollowUpUntil) {
		this.overwriteFollowUpUntil = overwriteFollowUpUntil;
	}
	
	public String getContactProximityDetails() {
		return contactProximityDetails;
	}

	public void setContactProximityDetails(String contactProximityDetails) {
		this.contactProximityDetails = contactProximityDetails;
	}

	public ContactCategory getContactCategory() {
		return contactCategory;
	}

	public void setContactCategory(ContactCategory contactCategory) {
		this.contactCategory = contactCategory;
	}

	public String getQuarantineHelpNeeded() {
		return quarantineHelpNeeded;
	}

	public void setQuarantineHelpNeeded(String quarantineHelpNeeded) {
		this.quarantineHelpNeeded = quarantineHelpNeeded;
	}

	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	public void setQuarantineOrderedVerbally(boolean quarantineOrderedVerbally) {
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
	}

	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	public void setQuarantineOrderedOfficialDocument(boolean quarantineOrderedOfficialDocument) {
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
	}

	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	public void setQuarantineOrderedVerballyDate(Date quarantineOrderedVerballyDate) {
		this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
	}

	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	public void setQuarantineOrderedOfficialDocumentDate(Date quarantineOrderedOfficialDocumentDate) {
		this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
	}

	public YesNoUnknown getQuarantineHomePossible() {
		return quarantineHomePossible;
	}

	public void setQuarantineHomePossible(YesNoUnknown quarantineHomePossible) {
		this.quarantineHomePossible = quarantineHomePossible;
	}

	public String getQuarantineHomePossibleComment() {
		return quarantineHomePossibleComment;
	}

	public void setQuarantineHomePossibleComment(String quarantineHomePossibleComment) {
		this.quarantineHomePossibleComment = quarantineHomePossibleComment;
	}

	public YesNoUnknown getQuarantineHomeSupplyEnsured() {
		return quarantineHomeSupplyEnsured;
	}

	public void setQuarantineHomeSupplyEnsured(YesNoUnknown quarantineHomeSupplyEnsured) {
		this.quarantineHomeSupplyEnsured = quarantineHomeSupplyEnsured;
	}

	public String getQuarantineHomeSupplyEnsuredComment() {
		return quarantineHomeSupplyEnsuredComment;
	}

	public void setQuarantineHomeSupplyEnsuredComment(String quarantineHomeSupplyEnsuredComment) {
		this.quarantineHomeSupplyEnsuredComment = quarantineHomeSupplyEnsuredComment;
	}
	
}
