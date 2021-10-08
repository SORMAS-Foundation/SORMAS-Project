package de.symeda.sormas.ui.events.eventParticipantLink;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class EventParticipantListComponent extends SideComponent {

	public EventParticipantListComponent(PersonReferenceDto personReferenceDto) {
		super(I18nProperties.getString(Strings.entityEvents));

		EventParticipantList eventParticipantList = new EventParticipantList(personReferenceDto);
		addComponent(eventParticipantList);
		eventParticipantList.reload();

		if (!eventParticipantList.isEmpty()) {
			final Button seeEvents = ButtonHelper.createButton(I18nProperties.getCaption(Captions.personLinkToEvents));
			CssStyles.style(seeEvents, ValoTheme.BUTTON_PRIMARY);
			seeEvents
				.addClickListener(clickEvent -> ControllerProvider.getEventController().navigateTo(new EventCriteria().person(personReferenceDto)));
			addComponent(seeEvents);
			setComponentAlignment(seeEvents, Alignment.MIDDLE_LEFT);
		}
	}
}
