package de.symeda.sormas.app.event;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.app.backend.event.Event;

public enum EventEditTabs {
	EVENT_DATA
//	EVENT_PERSONS
	;
	
	public String toString() {
		return I18nProperties.getFieldCaption(EventDto.I18N_PREFIX+"."+this.name());
	};
}
