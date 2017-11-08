package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;

public class DashboardEvent implements Serializable {

	private static final long serialVersionUID = -4108181804263076837L;

	public static final String I18N_PREFIX = "Event";

	public static final String EVENT_TYPE = "eventType";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String DISEASE = "disease";

	private EventType eventType;
	private EventStatus eventStatus;
	private Disease disease;
	
	public DashboardEvent(EventType eventType, EventStatus eventStatus, Disease disease) {
		this.eventType = eventType;
		this.eventStatus = eventStatus;
		this.disease = disease;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
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
	
}
