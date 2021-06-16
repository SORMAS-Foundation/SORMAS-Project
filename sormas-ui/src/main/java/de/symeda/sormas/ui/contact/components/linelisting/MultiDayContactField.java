package de.symeda.sormas.ui.contact.components.linelisting;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.ui.utils.components.multidayselector.MultiDaySelectorField;

public class MultiDayContactField extends MultiDaySelectorField {

	public MultiDayContactField() {
		properties.setProperty("prefix", ContactDto.I18N_PREFIX);
		properties.setProperty("multiDay", ContactDto.MULTI_DAY_CONTACT);
		properties.setProperty("firstDate", ContactDto.FIRST_CONTACT_DATE);
		properties.setProperty("lastDate", ContactDto.LAST_CONTACT_DATE);
	}
}
