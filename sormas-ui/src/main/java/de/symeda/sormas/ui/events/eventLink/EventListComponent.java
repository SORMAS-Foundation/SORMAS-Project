/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.events.eventLink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

import javax.validation.constraints.NotNull;

public class EventListComponent extends VerticalLayout {

	public EventListComponent(@NotNull final SormasUI ui, CaseReferenceDto caseRef) {

		createEventListComponent(new EventList(caseRef), I18nProperties.getString(Strings.entityEvents), e -> {

			EventCriteria eventCriteria = new EventCriteria();

			//check if there are active events in the database
			long events = FacadeProvider.getEventFacade().count(eventCriteria);
			if (events > 0) {
				ControllerProvider.getEventController().selectOrCreateEvent(ui, caseRef);
			} else {
				ControllerProvider.getEventController().create(ui, caseRef);
			}
		});

	}

	public EventListComponent(@NotNull final SormasUI ui, ContactReferenceDto contactRef, boolean eventParticipantDelete) {

		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(contactRef.getUuid());

		EventList eventList = new EventList(contact.getPerson(), eventParticipantDelete);

		createEventListComponent(eventList, I18nProperties.getString(Strings.entityEvents), e -> {

			EventCriteria eventCriteria = new EventCriteria();

			//check if there are active events in the database
			long events = FacadeProvider.getEventFacade().count(eventCriteria);
			if (events > 0) {
				ControllerProvider.getEventController().selectOrCreateEvent(ui, contact);
			} else {
				ControllerProvider.getEventController().create(ui, contact);
			}
		});

		if (contact.getCaze() != null) {
			CheckBox contactOnlyWithSourceCaseInEvent = new CheckBox(I18nProperties.getCaption(Captions.eventOnlyWithContactSourceCaseInvolved));
			contactOnlyWithSourceCaseInEvent.addStyleNames(CssStyles.CHECKBOX_FILTER_INLINE, CssStyles.VSPACE_4);
			contactOnlyWithSourceCaseInEvent.setWidthFull();
			contactOnlyWithSourceCaseInEvent.addValueChangeListener(e -> {
				if (e.getValue()) {
					eventList.filterEventListByCase(contact.getCaze());
				} else {
					eventList.filterEventListByCase(null);
				}
				eventList.reload();
			});
			addComponent(contactOnlyWithSourceCaseInEvent, 1);
		}
	}

	public EventListComponent(@NotNull final SormasUI ui, EventReferenceDto superordinateEvent) {

		EventList eventList = new EventList(superordinateEvent);
		createEventListComponent(eventList, I18nProperties.getCaption(Captions.eventSubordinateEvents), e -> {
			EventCriteria eventCriteria = new EventCriteria();
			long events = FacadeProvider.getEventFacade().count(eventCriteria);
			if (events > 0) {
				ControllerProvider.getEventController().selectOrCreateSubordinateEvent(ui, superordinateEvent);
			} else {
				ControllerProvider.getEventController().createSubordinateEvent(ui, superordinateEvent);
			}
		});
	}

	private void createEventListComponent(EventList eventList, String heading, Button.ClickListener clickListener) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		addComponent(eventList);
		eventList.reload();

		Label eventLabel = new Label(heading);
		eventLabel.addStyleName(CssStyles.H3);
		componentHeader.addComponent(eventLabel);

		SormasUI ui = (SormasUI)getUI();
		if (ui.getUserProvider().hasUserRight(UserRight.EVENT_CREATE)) {
			Button createButton = new Button(I18nProperties.getCaption(Captions.linkEvent));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(clickListener);
			componentHeader.addComponent(createButton);
			componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}
	}
}
