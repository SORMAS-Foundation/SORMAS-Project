package de.symeda.sormas.backend.event;

import java.util.Date;

import de.symeda.sormas.api.event.EventStatus;

public class EventSummaryDetails {

	private Long caseId;
	private String eventUuid;
	private EventStatus eventStatus;
	private String eventTitle;
	private Date eventDate;

	public EventSummaryDetails(Long caseId, String eventUuid, EventStatus eventStatus, String eventTitle, Date eventDate) {
		this.caseId = caseId;
		this.eventUuid = eventUuid;
		this.eventStatus = eventStatus;
		this.eventTitle = eventTitle;
		this.eventDate = eventDate;
	}

	public Long getCaseId() {
		return caseId;
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public Date getEventDate() {
		return eventDate;
	}
}
