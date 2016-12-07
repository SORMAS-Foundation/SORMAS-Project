package de.symeda.sormas.api.contact;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
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
	
	private PersonReferenceDto person;
	private CaseReferenceDto caze;
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private UserReferenceDto contactOfficer;
	private String description;
	
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

}
