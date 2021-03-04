package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;

public class SimilarEventParticipantDto implements Serializable {

	public static final String I18N_PREFIX = "EventParticipant";

	public static final Object UUID = "uuid";
	public static final Object FIRST_NAME = "firstName";
	public static final Object LAST_NAME = "lastName";
	public static final Object INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final Object EVENT_UUID = "eventUuid";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String START_DATE = "startDate";

	private String eventUuid;
	@PersonalData
	@SensitiveData
	private String firstName;
	@SensitiveData
	private String lastName;
	@SensitiveData
	private String involvementDescription;
	private String uuid;
	private EventStatus eventStatus;
	private String eventTitle;
	private Date startDate;

	public SimilarEventParticipantDto(
		String uuid,
		String firstName,
		String lastName,
		String involvementDescription,
		String eventUuid,
		EventStatus eventStatus,
		String eventTitle,
		Date startDate) {

		this.uuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.involvementDescription = involvementDescription;
		this.eventUuid = eventUuid;
		this.eventStatus = eventStatus;
		this.eventTitle = eventTitle;
		this.startDate = startDate;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public void setEventUuid(String eventUuid) {
		this.eventUuid = eventUuid;
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

	public String getInvolvementDescription() {
		return involvementDescription;
	}

	public void setInvolvementDescription(String involvementDescription) {
		this.involvementDescription = involvementDescription;
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
