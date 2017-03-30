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
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String DESCRIPTION = "description";
	public static final String TASKS = "tasks";
	public static final String RELATION_TO_CASE = "relationToCase";
	
	private Person person;
	private Case caze;
	private Date reportDateTime;
	private User reportingUser;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private User contactOfficer;
	private String description;
	private ContactRelation relationToCase;
	
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (caze == null) {
			if (other.caze != null)
				return false;
		} else if (!caze.equals(other.caze))
			return false;
		if (contactClassification != other.contactClassification)
			return false;
		if (contactOfficer == null) {
			if (other.contactOfficer != null)
				return false;
		} else if (!contactOfficer.equals(other.contactOfficer))
			return false;
		if (contactProximity != other.contactProximity)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (followUpStatus != other.followUpStatus)
			return false;
		if (followUpUntil == null) {
			if (other.followUpUntil != null)
				return false;
		} else if (!followUpUntil.equals(other.followUpUntil))
			return false;
		if (lastContactDate == null) {
			if (other.lastContactDate != null)
				return false;
		} else if (!lastContactDate.equals(other.lastContactDate))
			return false;
		if (person == null) {
			if (other.person != null)
				return false;
		} else if (!person.equals(other.person))
			return false;
		if (relationToCase != other.relationToCase)
			return false;
		if (reportDateTime == null) {
			if (other.reportDateTime != null)
				return false;
		} else if (!reportDateTime.equals(other.reportDateTime))
			return false;
		if (reportingUser == null) {
			if (other.reportingUser != null)
				return false;
		} else if (!reportingUser.equals(other.reportingUser))
			return false;
		if (tasks == null) {
			if (other.tasks != null)
				return false;
		} else if (!tasks.equals(other.tasks))
			return false;
		return true;
	}
	
}
