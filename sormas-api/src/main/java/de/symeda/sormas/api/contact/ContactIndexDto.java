package de.symeda.sormas.api.contact;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class ContactIndexDto extends ContactReferenceDto {

	private static final long serialVersionUID = -7764607075875188799L;

	public static final String I18N_PREFIX = "Contact";
	
	public static final String PERSON = "person";
	public static final String CAZE = "caze";
	public static final String CAZE_DISEASE = "cazeDisease";
	public static final String CAZE_PERSON = "cazePerson";
	public static final String CAZE_DISTRICT = "cazeDistrict";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String NUMBER_OF_COOPERATIVE_VISITS = "numberOfCooperativeVisits";
	public static final String NUMBER_OF_MISSED_VISITS = "numberOfMissedVisits";

	private PersonReferenceDto person;
	private CaseReferenceDto caze;
	private Disease cazeDisease;
	private PersonReferenceDto cazePerson;
	private DistrictReferenceDto cazeDistrict;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private int numberOfCooperativeVisits;
	private int numberOfMissedVisits;
	private UserReferenceDto contactOfficer;

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
	public Disease getCazeDisease() {
		return cazeDisease;
	}
	public void setCazeDisease(Disease cazeDisease) {
		this.cazeDisease = cazeDisease;
	}
	public PersonReferenceDto getCazePerson() {
		return cazePerson;
	}
	public void setCazePerson(PersonReferenceDto cazePerson) {
		this.cazePerson = cazePerson;
	}
	public DistrictReferenceDto getCazeDistrict() {
		return cazeDistrict;
	}
	public void setCazeDistrict(DistrictReferenceDto cazeDistrict) {
		this.cazeDistrict = cazeDistrict;
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
	public int getNumberOfCooperativeVisits() {
		return numberOfCooperativeVisits;
	}
	public void setNumberOfCooperativeVisits(int numberOfCooperativeVisits) {
		this.numberOfCooperativeVisits = numberOfCooperativeVisits;
	}
	public int getNumberOfMissedVisits() {
		return numberOfMissedVisits;
	}
	public void setNumberOfMissedVisits(int numberOfMissedVisits) {
		this.numberOfMissedVisits = numberOfMissedVisits;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactIndexDto other = (ContactIndexDto) obj;
		if (caze == null) {
			if (other.caze != null)
				return false;
		} else if (!caze.equals(other.caze))
			return false;
		if (cazeDisease != other.cazeDisease)
			return false;
		if (cazeDistrict == null) {
			if (other.cazeDistrict != null)
				return false;
		} else if (!cazeDistrict.equals(other.cazeDistrict))
			return false;
		if (cazePerson == null) {
			if (other.cazePerson != null)
				return false;
		} else if (!cazePerson.equals(other.cazePerson))
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
		if (numberOfCooperativeVisits != other.numberOfCooperativeVisits)
			return false;
		if (numberOfMissedVisits != other.numberOfMissedVisits)
			return false;
		if (person == null) {
			if (other.person != null)
				return false;
		} else if (!person.equals(other.person))
			return false;
		return true;
	}
	
}
