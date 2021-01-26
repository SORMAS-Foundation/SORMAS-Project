package de.symeda.sormas.ui.events.eventParticipantLink;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventParticipantListComponent extends VerticalLayout {

	private EventParticipantList list;

	public EventParticipantListComponent(PersonReferenceDto personReferenceDto) {
		createEventParticipantListComponent(new EventParticipantList(personReferenceDto), I18nProperties.getString(Strings.entityEventParticipants));
	}

	private void createEventParticipantListComponent(EventParticipantList eventParticipantList, String heading) {
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Sizeable.Unit.PERCENTAGE);
		addComponent(componentHeader);

		list = eventParticipantList;
		addComponent(list);
		list.reload();

		Label eventLabel = new Label(heading);
		eventLabel.addStyleName(CssStyles.H3);
		componentHeader.addComponent(eventLabel);
	}
}
