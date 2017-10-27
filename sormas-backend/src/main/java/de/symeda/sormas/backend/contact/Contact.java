package de.symeda.sormas.backend.contact;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.user.User;

@Entity
@Audited
public class Contact extends AbstractDomainObject {

	private static final long serialVersionUID = -7764607075875188799L;

	public static final String PERSON = "person";
	public static final String CAZE = "caze";
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
	public static final String TASKS = "tasks";
	public static final String RELATION_TO_CASE = "relationToCase";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	
	private Person person;
	private Case caze;
	private Date reportDateTime;
	private User reportingUser;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private FollowUpStatus followUpStatus;
	private String followUpComment;
	private Date followUpUntil;
	private User contactOfficer;
	private String description;
	private ContactRelation relationToCase;
	
	private Double reportLat;
	private Double reportLon;
	private Float reportLatLonAccuracy;
	
	private List<Task> tasks;
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public Person getPerson() {
		return person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public Case getCaze() {
		return caze;
	}
	public void setCaze(Case caze) {
		this.caze = caze;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	public Date getReportDateTime() {
		return reportDateTime;
	}
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public User getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastContactDate() {
		return lastContactDate;
	}
	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}
	
	@Enumerated(EnumType.STRING)
	public ContactProximity getContactProximity() {
		return contactProximity;
	}
	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}

	@Column(length=512)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public User getContactOfficer() {
		return contactOfficer;
	}

	public void setContactOfficer(User contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	@Enumerated(EnumType.STRING)
	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	@Enumerated(EnumType.STRING)
	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}	
	
	@Enumerated(EnumType.STRING)
	public ContactRelation getRelationToCase() {
		return relationToCase;
	}
	
	public void setRelationToCase(ContactRelation relationToCase) {
		this.relationToCase = relationToCase;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Person contactPerson = getPerson();
		Person casePerson = getCaze().getPerson();
		builder.append(contactPerson.getFirstName()).append(" ").append(contactPerson.getLastName().toUpperCase());
		builder.append(" to case ");
		builder.append(casePerson.getFirstName()).append(" ").append(casePerson.getLastName().toUpperCase());
		return builder.toString();
	}
	
	@OneToMany(cascade = {}, mappedBy = Task.CONTACT)
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
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

	@Column(length=512)
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
}
