package de.symeda.sormas.ui.events.eventParticipantLink;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventParticipantListEntryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class EventParticipantListComponent extends SideComponent {

	private EventParticipantList eventParticipantList;

	public EventParticipantListComponent(
		PersonReferenceDto personReferenceDto,
		String activeUuid,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed) {
		super(I18nProperties.getString(Strings.entityEvents), actionCallback);

		if (UiUtil.permitted(isEditAllowed, UserRight.EVENTPARTICIPANT_CREATE)) {
			addCreateButton(I18nProperties.getCaption(Captions.linkEvent), () -> {
				EventCriteria eventCriteria = new EventCriteria();

				//check if there are active events in the database
				long events = FacadeProvider.getEventFacade().count(eventCriteria);
				if (events > 0) {
					ControllerProvider.getEventController().selectOrCreateEvent(personReferenceDto);
				} else {
					ControllerProvider.getEventController().create(personReferenceDto);
				}
			}, UserRight.EVENTPARTICIPANT_CREATE);
		}

		eventParticipantList = new EventParticipantList(personReferenceDto);
		eventParticipantList.setActiveUuid(activeUuid);
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

	public List<EventParticipantListEntryDto> getEntries() {
		return eventParticipantList.getEntries();
	}
}
