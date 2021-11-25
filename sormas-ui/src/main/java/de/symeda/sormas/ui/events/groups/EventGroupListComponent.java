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

package de.symeda.sormas.ui.events.groups;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventGroupListComponent extends VerticalLayout {

	private EventGroupList list;
	private Button createButton;

	public EventGroupListComponent(EventReferenceDto eventReference) {

		EventGroupList eventList = new EventGroupList(eventReference);
		createEventGroupListComponent(eventList, I18nProperties.getCaption(Captions.eventGroups), e -> {
			EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventReference.getUuid(), false);
			UserProvider user = UserProvider.getCurrent();
			if (!user.hasNationalJurisdictionLevel() && !user.hasRegion(event.getEventLocation().getRegion())) {
				new Notification(
					I18nProperties.getString(Strings.headingEventGroupLinkEventIssue),
					I18nProperties.getString(Strings.errorEventFromAnotherJurisdiction),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
				return;
			}

			EventGroupCriteria eventGroupCriteria = new EventGroupCriteria();
			Set<String> eventGroupUuids = FacadeProvider.getEventGroupFacade()
				.getCommonEventGroupsByEvents(Collections.singletonList(event.toReference()))
				.stream()
				.map(EventGroupReferenceDto::getUuid)
				.collect(Collectors.toSet());
			eventGroupCriteria.setExcludedUuids(eventGroupUuids);
			if (user.hasUserRight(UserRight.EVENTGROUP_CREATE) && user.hasUserRight(UserRight.EVENTGROUP_LINK)) {
				long events = FacadeProvider.getEventGroupFacade().count(eventGroupCriteria);
				if (events > 0) {
					ControllerProvider.getEventGroupController().selectOrCreate(eventReference);
				} else {
					ControllerProvider.getEventGroupController().create(eventReference);
				}
			} else if (user.hasUserRight(UserRight.EVENTGROUP_CREATE)) {
				ControllerProvider.getEventGroupController().create(eventReference);
			} else {
				long events = FacadeProvider.getEventGroupFacade().count(eventGroupCriteria);
				if (events > 0) {
					ControllerProvider.getEventGroupController().select(eventReference);
				} else {
					new Notification(
						I18nProperties.getString(Strings.headingEventGroupLinkEventIssue),
						I18nProperties.getString(Strings.errorNotRequiredRights),
						Notification.Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}
		});
	}

	private void createEventGroupListComponent(EventGroupList eventList, String heading, Button.ClickListener clickListener) {
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

		UserProvider user = UserProvider.getCurrent();
		if (user.hasUserRight(UserRight.EVENTGROUP_CREATE) || user.hasUserRight(UserRight.EVENTGROUP_LINK)) {
			createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.linkEventGroup));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(clickListener);
			componentHeader.addComponent(createButton);
			componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}
	}
}
