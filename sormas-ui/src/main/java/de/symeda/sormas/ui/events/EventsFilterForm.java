package de.symeda.sormas.ui.events;

import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class EventsFilterForm extends AbstractFilterForm<EventCriteria> {

	private static final long serialVersionUID = -1166745065032487009L;

	protected EventsFilterForm() {
		super(EventCriteria.class, EventIndexDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {

		return new String[] {
			EventIndexDto.DISEASE,
			EventCriteria.REPORTING_USER_ROLE };
	}

	@Override
	protected void addFields() {

		addField(FieldConfiguration.pixelSized(EventIndexDto.DISEASE, 140));
		addField(FieldConfiguration.withCaptionAndPixelSized(EventCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));
	}
}
