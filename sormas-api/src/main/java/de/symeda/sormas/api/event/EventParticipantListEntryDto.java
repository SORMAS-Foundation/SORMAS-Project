package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class EventParticipantListEntryDto extends PseudonymizableIndexDto implements IsEventParticipant, Serializable {

	private static final long serialVersionUID = 725252055850399647L;
	private String eventUuid;
	private EventStatus eventStatus;
	private Disease disease;
	private String eventTitle;
	private Date eventStartDate;
	private Date eventEndDate;
	private Boolean isInJurisdictionOrOwned;

	public EventParticipantListEntryDto(
		String uuid,
		String eventUuid,
		EventStatus eventStatus,
		Disease disease,
		String eventTitle,
		Date eventStartDate,
		Date eventEndDate,
		boolean isInJurisdictionOrOwned) {
		super(uuid);
		this.eventUuid = eventUuid;
		this.eventStatus = eventStatus;
		this.disease = disease;
		this.eventTitle = eventTitle;
		this.eventStartDate = eventStartDate;
		this.eventEndDate = eventEndDate;
		this.isInJurisdictionOrOwned = isInJurisdictionOrOwned;
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

	public Boolean getInJurisdiction() {
		return isInJurisdictionOrOwned;
	}

	public Date getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(Date eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	public Date getEventEndDate() {
		return eventEndDate;
	}

	public void setEventEndDate(Date eventEndDate) {
		this.eventEndDate = eventEndDate;
	}
}
