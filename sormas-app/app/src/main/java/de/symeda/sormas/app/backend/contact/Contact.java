package de.symeda.sormas.app.backend.contact;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.user.User;

@Entity(name=Contact.TABLE_NAME)
@DatabaseTable(tableName = Contact.TABLE_NAME)
public class Contact extends AbstractDomainObject {

	private static final long serialVersionUID = -7799607075875188799L;

	public static final String TABLE_NAME = "contacts";
	public static final String I18N_PREFIX = "Contact";

	public static final String PERSON = "person_id";
	public static final String CASE_UUID = "caseUuid";
	public static final String CASE_DISEASE = "caseDisease";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_COMMENT = "followUpComment";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String DESCRIPTION = "description";
	public static final String RELATION_TO_CASE = "relationToCase";
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

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	private Person person;
	@DatabaseField
	private String caseUuid;
	@Enumerated(EnumType.STRING)
	private Disease caseDisease;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date lastContactDate;
	@Enumerated(EnumType.STRING)
	private ContactProximity contactProximity;
	@Enumerated(EnumType.STRING)
	private ContactClassification contactClassification;
	@Enumerated(EnumType.STRING)
	private ContactStatus contactStatus;
	@Enumerated(EnumType.STRING)
	private FollowUpStatus followUpStatus;
	@Column(length=512)
	private String followUpComment;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date followUpUntil;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User contactOfficer;
	@Column(length=512)
	private String description;
	@Enumerated(EnumType.STRING)
	private ContactRelation relationToCase;

	@DatabaseField
	private String resultingCaseUuid;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User resultingCaseUser;

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

	public Disease getCaseDisease() {
		return caseDisease;
	}

	public void setCaseDisease(Disease caseDisease) {
		this.caseDisease = caseDisease;
	}
}
