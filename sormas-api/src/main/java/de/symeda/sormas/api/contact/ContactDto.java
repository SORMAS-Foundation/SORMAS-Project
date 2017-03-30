package de.symeda.sormas.api.contact;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class ContactDto extends ContactReferenceDto {

	private static final long serialVersionUID = -7764607075875188799L;

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
	
	private PersonReferenceDto person;
	private CaseReferenceDto caze;
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA})
	private FollowUpStatus followUpStatus;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA})
	private Date followUpUntil;
	private UserReferenceDto contactOfficer;
	private String description;
	private ContactRelation relationToCase;
	
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
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getReportDateTime() {
		return reportDateTime;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getLastContactDate() {
		return lastContactDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
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
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getFollowUpUntil() {
		return followUpUntil;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}
	public ContactRelation getRelationToCase() {
		return relationToCase;
	}
	public void setRelationToCase(ContactRelation relationToCase) {
		this.relationToCase = relationToCase;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactDto other = (ContactDto) obj;
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
		return true;
	}
	
}
