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

import java.util.function.Consumer;

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
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventListComponent extends VerticalLayout {

	private EventList list;
	private Button createButton;
	private final Consumer<Runnable> actionCallback;

	public EventListComponent(CaseReferenceDto caseRef, Consumer<Runnable> actionCallback, boolean isEditAllowed) {

		this.actionCallback = actionCallback;
		createEventListComponent(new EventList(caseRef, actionCallback, isEditAllowed), I18nProperties.getString(Strings.entityEvents), false, () -> {
			EventCriteria eventCriteria = new EventCriteria();

			//check if there are active events in the database
			long events = FacadeProvider.getEventFacade().count(eventCriteria);
			if (events > 0) {
				ControllerProvider.getEventController().selectOrCreateEvent(caseRef);
			} else {
				ControllerProvider.getEventController().create(caseRef);
			}
		}, isEditAllowed);

	}

	public EventListComponent(ContactReferenceDto contactRef, Consumer<Runnable> actionCallback, boolean isEditAllowed) {

		this.actionCallback = actionCallback;
		ContactDto contact = FacadeProvider.getContactFacade().getByUuid(contactRef.getUuid());
		EventList eventList = new EventList(contact, actionCallback, isEditAllowed);

		createEventListComponent(eventList, I18nProperties.getString(Strings.entityEvents), false, () -> {
			EventCriteria eventCriteria = new EventCriteria();

			//check if there are active events in the database
			long events = FacadeProvider.getEventFacade().count(eventCriteria);
			if (events > 0) {
				ControllerProvider.getEventController().selectOrCreateEvent(contact);
			} else {
				ControllerProvider.getEventController().create(contact);
			}
		}, isEditAllowed);

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

	public EventListComponent(EventReferenceDto superordinateEvent, Consumer<Runnable> actionCallback, boolean isEditAllowed) {

		this.actionCallback = actionCallback;
		EventList eventList = new EventList(superordinateEvent, actionCallback, isEditAllowed);
		createEventListComponent(eventList, I18nProperties.getCaption(Captions.eventSubordinateEvents), true, () -> {
			EventCriteria eventCriteria = new EventCriteria();
			long events = FacadeProvider.getEventFacade().count(eventCriteria);
			if (events > 0) {
				ControllerProvider.getEventController().selectOrCreateSubordinateEvent(superordinateEvent);
			} else {
				ControllerProvider.getEventController().createSubordinateEvent(superordinateEvent);
			}
		}, isEditAllowed);
	}

	private void createEventListComponent(
		EventList eventList,
		String heading,
		boolean bottomCreateButton,
		Runnable linkEventCallback,
		boolean isEditAllowed) {

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		list = eventList;
		addComponent(list);
		list.reload();

		Label eventLabel = new Label(heading);
		eventLabel.addStyleName(CssStyles.H3);
		componentHeader.addComponent(eventLabel);

		if (UiUtil.permitted(isEditAllowed, UserRight.EVENT_CREATE, UserRight.EVENTPARTICIPANT_CREATE)) {
			createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.linkEvent));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(e -> actionCallback.accept(linkEventCallback));
			if (bottomCreateButton) {
				HorizontalLayout buttonLayout = new HorizontalLayout();
				buttonLayout.setMargin(false);
				buttonLayout.setSpacing(true);
				buttonLayout.setWidth(100, Unit.PERCENTAGE);
				CssStyles.style(buttonLayout, CssStyles.VSPACE_TOP_3);
				buttonLayout.addComponent(createButton);
				addComponent(buttonLayout);
			} else {
				componentHeader.addComponent(createButton);
				componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
			}
		}
	}
}
