package de.symeda.sormas.backend.event;

import java.util.Date;

public class ContactEventSummaryDetails {

	private final String contactUuid;
	private final String eventUuid;
	private final String eventTitle;
	private final Date eventDate;

	public ContactEventSummaryDetails(String contactUuid, String eventUuid, String eventTitle, Date eventDate) {
		this.contactUuid = contactUuid;
		this.eventUuid = eventUuid;
		this.eventTitle = eventTitle;
		this.eventDate = eventDate;
	}

	public String getContactUuid() {
		return contactUuid;
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public Date getEventDate() {
		return eventDate;
	}
}
