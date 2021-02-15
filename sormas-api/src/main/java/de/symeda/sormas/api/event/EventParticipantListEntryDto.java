package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class EventParticipantListEntryDto extends PseudonymizableIndexDto implements Serializable {

	private String uuid;
	private String eventUuid;
	private EventStatus eventStatus;
	private Disease disease;
	private String eventTitle;

	public EventParticipantListEntryDto(String uuid, String eventUuid, EventStatus eventStatus, Disease disease, String eventTitle) {
		this.uuid = uuid;
		this.eventUuid = eventUuid;
		this.eventStatus = eventStatus;
		this.disease = disease;
		this.eventTitle = eventTitle;
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

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}
}
