/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.contact;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactIdentificationSource;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.OrderMeans;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.contact.TracingApp;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;

@Entity(name = Contact.TABLE_NAME)
@DatabaseTable(tableName = Contact.TABLE_NAME)
public class Contact extends AbstractDomainObject {

	private static final long serialVersionUID = -7799607075875188799L;

	public static final String TABLE_NAME = "contacts";
	public static final String I18N_PREFIX = "Contact";

	public static final String PERSON = "person_id";
	public static final String CASE_UUID = "caseUuid";
	public static final String DISEASE = "disease";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_IDENTIFICATION_SOURCE = "contactIdentificationSource";
	public static final String CONTACT_IDENTIFICATION_SOURCE_DETAILS = "contactIdentificationSourceDetails";
	public static final String TRACING_APP = "tracingApp";
	public static final String TRACING_APP_DETAILS = "tracingAppDetails";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_COMMENT = "followUpComment";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String DESCRIPTION = "description";
	public static final String RELATION_TO_CASE = "relationToCase";
	public static final String RELATION_DESCRIPTION = "relationDescription";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	public static final String REPORT_LAT_LON_ACCURACY = "reportLatLonAccuracy";

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date reportDateTime;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;
	@DatabaseField
	private Double reportLat;
	@DatabaseField
	private Double reportLon;
	@DatabaseField
	private Float reportLatLonAccuracy;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region region;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District district;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	private Person person;
	@DatabaseField
	private String caseUuid;
	@DatabaseField(dataType = DataType.ENUM_STRING, columnName = "caseDisease")
	private Disease disease;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String diseaseDetails;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date lastContactDate;
	@Enumerated(EnumType.STRING)
	private ContactIdentificationSource contactIdentificationSource;
	@DatabaseField
	private String contactIdentificationSourceDetails;
	@Enumerated(EnumType.STRING)
	private TracingApp tracingApp;
	@DatabaseField
	private String tracingAppDetails;
	@Enumerated(EnumType.STRING)
	private ContactProximity contactProximity;
	@Enumerated(EnumType.STRING)
	private ContactClassification contactClassification;
	@Enumerated(EnumType.STRING)
	private ContactStatus contactStatus;
	@Enumerated(EnumType.STRING)
	private FollowUpStatus followUpStatus;
	@Column(length = COLUMN_LENGTH_BIG)
	private String followUpComment;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date followUpUntil;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User contactOfficer;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String description;
	@Enumerated(EnumType.STRING)
	private ContactRelation relationToCase;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String relationDescription;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String externalID;

	@DatabaseField
	private String resultingCaseUuid;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User resultingCaseUser;

	@DatabaseField
	private boolean highPriority;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown immunosuppressiveTherapyBasicDisease;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String immunosuppressiveTherapyBasicDiseaseDetails;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown careForPeopleOver60;

	@Enumerated(EnumType.STRING)
	private QuarantineType quarantine;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String quarantineTypeDetails;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date quarantineFrom;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date quarantineTo;

	@Column
	private String caseIdExternalSystem;
	@Column(length = COLUMN_LENGTH_BIG)
	private String caseOrEventInformation;

	@Enumerated(EnumType.STRING)
	private ContactCategory contactCategory;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String contactProximityDetails;

	@Enumerated(EnumType.STRING)
	@Deprecated
	private OrderMeans quarantineOrderMeans;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String quarantineHelpNeeded;
	@DatabaseField
	private boolean quarantineOrderedVerbally;
	@DatabaseField
	private boolean quarantineOrderedOfficialDocument;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date quarantineOrderedVerballyDate;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date quarantineOrderedOfficialDocumentDate;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown quarantineHomePossible;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String quarantineHomePossibleComment;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown quarantineHomeSupplyEnsured;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String quarantineHomeSupplyEnsuredComment;
	@Column(length = COLUMN_LENGTH_BIG)
	private String additionalDetails;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public ContactIdentificationSource getContactIdentificationSource() {
		return contactIdentificationSource;
	}

	public void setContactIdentificationSource(ContactIdentificationSource contactIdentificationSource) {
		this.contactIdentificationSource = contactIdentificationSource;
	}

	public String getContactIdentificationSourceDetails() {
		return contactIdentificationSourceDetails;
	}

	public void setContactIdentificationSourceDetails(String contactIdentificationSourceDetails) {
		this.contactIdentificationSourceDetails = contactIdentificationSourceDetails;
	}

	public TracingApp getTracingApp() {
		return tracingApp;
	}

	public void setTracingApp(TracingApp tracingApp) {
		this.tracingApp = tracingApp;
	}

	public String getTracingAppDetails() {
		return tracingAppDetails;
	}

	public void setTracingAppDetails(String tracingAppDetails) {
		this.tracingAppDetails = tracingAppDetails;
	}

	public ContactProximity getContactProximity() {
		return contactProximity;
	}

	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public String getFollowUpComment() {
		return followUpComment;
	}

	public void setFollowUpComment(String followUpComment) {
		this.followUpComment = followUpComment;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getContactOfficer() {
		return contactOfficer;
	}

	public void setContactOfficer(User contactOfficer) {
		this.contactOfficer = contactOfficer;
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

	@Override
	public String toString() {
		return super.toString() + " " + (getPerson() != null ? getPerson().toString() : "") + " (" + DataHelper.getShortUuid(getUuid()) + ")";
	}

	@Override
	public boolean isModifiedOrChildModified() {
		boolean modified = super.isModifiedOrChildModified();
		return person.isModifiedOrChildModified() || modified;
	}

	@Override
	public boolean isUnreadOrChildUnread() {
		boolean unread = super.isUnreadOrChildUnread();
		return person.isUnreadOrChildUnread() || unread;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	public String getResultingCaseUuid() {
		return resultingCaseUuid;
	}

	public void setResultingCaseUuid(String resultingCaseUuid) {
		this.resultingCaseUuid = resultingCaseUuid;
	}

	public User getResultingCaseUser() {
		return resultingCaseUser;
	}

	public void setResultingCaseUser(User resultingCaseUser) {
		this.resultingCaseUser = resultingCaseUser;
	}

	public String getCaseUuid() {
		return caseUuid;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
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

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
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

	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	public void setQuarantineTypeDetails(String quarantineTypeDetails) {
		this.quarantineTypeDetails = quarantineTypeDetails;
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

	public ContactCategory getContactCategory() {
		return contactCategory;
	}

	public void setContactCategory(ContactCategory contactCategory) {
		this.contactCategory = contactCategory;
	}

	public String getContactProximityDetails() {
		return contactProximityDetails;
	}

	public void setContactProximityDetails(String contactProximityDetails) {
		this.contactProximityDetails = contactProximityDetails;
	}

	@Deprecated
	public OrderMeans getQuarantineOrderMeans() {
		return quarantineOrderMeans;
	}

	@Deprecated
	public void setQuarantineOrderMeans(OrderMeans quarantineOrderMeans) {
		this.quarantineOrderMeans = quarantineOrderMeans;
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

	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}
}
