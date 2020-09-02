package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class EventParticipantCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 5981720569585071845L;

	private EventReferenceDto event;

	@IgnoreForUrl
	public EventReferenceDto getEvent() {
		return event;
	}

	public EventParticipantCriteria event(EventReferenceDto event) {
		this.event = event;
		return this;
	}
}
