package de.symeda.sormas.app.contact;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.contact.ContactDto;

public enum ContactEditTabs {
	CONTACT_DATA,
	PERSON
//	VISITS,
//	TASKS
	;
	
	public String toString() {
		return I18nProperties.getFieldCaption(ContactDto.I18N_PREFIX+"."+this.name());
	};
}
