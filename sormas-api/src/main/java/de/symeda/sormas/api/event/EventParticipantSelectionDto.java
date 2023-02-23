package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class EventParticipantSelectionDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String EVENT_UUID = "eventUuid";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_PARTICIPANT_UUID = "eventParticipantUuid";
	public static final String PERSON_UUID = "personUuid";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String SEX = "sex";
	public static final String DISTRICT_NAME = "districtName";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String RESULTING_CASE_UUID = "resultingCaseUuid";
	public static final String CONTACT_COUNT = "contactCount";
	public static final String I18N_PREFIX = "EventParticipantSelection";

	private String eventUuid;
	private String eventTitle;
	private String eventParticipantUuid;
	private String personUuid;
	private String firstName;
	private String lastName;
	private String ageAndBirthDate;
	private Sex sex;
	private String districtName;
	private String involvementDescription;
	private String resultingCaseUuid;
	private int contactCount;

	public EventParticipantSelectionDto(String eventUuid, String eventParticipantUuid) {
		super(eventParticipantUuid);
		this.eventUuid = eventUuid;
		this.eventParticipantUuid = eventParticipantUuid;
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public void setEventUuid(String eventUuid) {
		this.eventUuid = eventUuid;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getEventParticipantUuid() {
		return eventParticipantUuid;
	}

	public void setEventParticipantUuid(String eventParticipantUuid) {
		this.eventParticipantUuid = eventParticipantUuid;
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

	public String getAgeAndBirthDate() {
		return ageAndBirthDate;
	}

	public void setAgeAndBirthDate(String ageAndBirthDate) {
		this.ageAndBirthDate = ageAndBirthDate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getInvolvementDescription() {
		return involvementDescription;
	}

	public void setInvolvementDescription(String involvementDescription) {
		this.involvementDescription = involvementDescription;
	}

	public String getResultingCaseUuid() {
		return resultingCaseUuid;
	}

	public void setResultingCaseUuid(String resultingCaseUuid) {
		this.resultingCaseUuid = resultingCaseUuid;
	}

	public int getContactCount() {
		return contactCount;
	}

	public void setContactCount(int contactCount) {
		this.contactCount = contactCount;
	}
}
