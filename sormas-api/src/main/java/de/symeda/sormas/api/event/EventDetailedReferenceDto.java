package de.symeda.sormas.api.event;

import java.util.Date;

public class EventDetailedReferenceDto extends EventReferenceDto {

	private EventStatus eventStatus;
	private String eventTitle;
	private Date reportDateTime;

	public EventDetailedReferenceDto(String uuid, String caption, EventStatus eventStatus, String eventTitle, Date reportDateTime) {
		super(uuid, caption);
		this.eventStatus = eventStatus;
		this.eventTitle = eventTitle;
		this.reportDateTime = reportDateTime;
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

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
}
