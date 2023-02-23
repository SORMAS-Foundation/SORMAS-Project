package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class EventSelectionDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String EVENT_UUID = "eventUuid";
	public static final String EVENT_TITLE = "eventTitle";

	private String eventUuid;
	private String eventTitle;

	public EventSelectionDto(String eventUuid, String eventTitle) {
		super(eventUuid);
		this.eventTitle = eventTitle;
		this.eventUuid = eventUuid;
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
}
