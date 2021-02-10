package de.symeda.sormas.ui.events.eventParticipantLink;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventParticipantListComponent extends VerticalLayout {

	private EventParticipantList list;

	public EventParticipantListComponent(PersonReferenceDto personReferenceDto) {
		createEventParticipantListComponent(
			new EventParticipantList(personReferenceDto),
			I18nProperties.getString(Strings.entityEvents),
			clickEvent -> ControllerProvider.getEventController().navigateTo(new EventCriteria().person(personReferenceDto)));
	}

	private void createEventParticipantListComponent(EventParticipantList eventParticipantList, String heading, Button.ClickListener clickListener) {
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Sizeable.Unit.PERCENTAGE);
		addComponent(componentHeader);

		Label label = new Label(heading);
		label.addStyleName(CssStyles.H3);
		componentHeader.addComponent(label);

		list = eventParticipantList;
		addComponent(list);
		list.reload();
		if (!list.isEmpty()) {
			final Button seeEvents = new Button(I18nProperties.getCaption(Captions.personLinkToEvents));
			CssStyles.style(seeEvents, ValoTheme.BUTTON_PRIMARY);
			seeEvents.addClickListener(clickListener);
			addComponent(seeEvents);
			setComponentAlignment(seeEvents, Alignment.MIDDLE_LEFT);
		}
	}
}
