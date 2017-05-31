package de.symeda.sormas.app.backend.contact;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
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

	public static final String PERSON = "person";
	public static final String CAZE = "caze";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String DESCRIPTION = "description";
	public static final String RELATION_TO_CASE = "relationToCase";

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	private Person person;

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false)
	private Case caze;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date reportDateTime;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date lastContactDate;

	@Enumerated(EnumType.STRING)
	private ContactProximity contactProximity;

	@Enumerated(EnumType.STRING)
	private ContactClassification contactClassification;

	@Enumerated(EnumType.STRING)
	private FollowUpStatus followUpStatus;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date followUpUntil;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User contactOfficer;

	@Column(length=512)
	private String description;

	@Enumerated(EnumType.STRING)
	private ContactRelation relationToCase;
	
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Case getCaze() {
		return caze;
	}
	public void setCaze(Case caze) {
		this.caze = caze;
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

	@Override
	public String toString() {
		return (getPerson() != null ? getPerson().toString() : "") + " (" + DataHelper.getShortUuid(getUuid()) + ")";
	}

	@Override
	public boolean isModifiedOrChildModified() {
		super.isModifiedOrChildModified();
		return person.isModifiedOrChildModified();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
