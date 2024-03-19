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

import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.PaginationList;

public class EventGroupList extends PaginationList<EventGroupIndexDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final EventReferenceDto event;
	private final EventGroupCriteria eventGroupCriteria = new EventGroupCriteria();
	private final Label noEventGroupLabel;

	public EventGroupList(EventReferenceDto event) {
		super(MAX_DISPLAYED_ENTRIES);

		this.event = event;

		this.eventGroupCriteria.setEvent(event);
		this.eventGroupCriteria.setUserFilterIncluded(false);
		this.eventGroupCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		this.noEventGroupLabel = new Label(I18nProperties.getString(Strings.infoNoEventGroups));
	}

	@Override
	public void reload() {
		List<EventGroupIndexDto> events = FacadeProvider.getEventGroupFacade().getIndexList(eventGroupCriteria, 0, maxDisplayedEntries * 20, null);

		setEntries(events);
		if (!events.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noEventGroupLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(this.event.getUuid(), false);
		List<EventGroupIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			EventGroupIndexDto eventGroup = displayedEntries.get(i);
			EventGroupListEntry listEntry = new EventGroupListEntry(eventGroup);

			if (UiUtil.permitted(UserRight.EVENTGROUP_LINK)) {
				listEntry.addUnlinkEventListener(i, (ClickListener) clickEvent -> {
					if (!FacadeProvider.getEventFacade().isInJurisdictionOrOwned(event.getUuid())
						&& !UiUtil.hasNationJurisdictionLevel()
						&& !UiUtil.getCurrentUserProvider().hasRegion(event.getEventLocation().getRegion())
						&& !UiUtil.isAdmin()) {
						new Notification(
							I18nProperties.getString(Strings.headingEventGroupUnlinkEventIssue),
							I18nProperties.getString(Strings.errorEventFromAnotherJurisdiction),
							Notification.Type.ERROR_MESSAGE,
							false).show(Page.getCurrent());
						return;
					}

					ControllerProvider.getEventGroupController().unlinkEventGroup(this.event, listEntry.getEventGroup().toReference());
					reload();
				});
			}
			if (UiUtil.permitted(UserRight.EVENTGROUP_EDIT)) {
				listEntry.addEditListener(i, (ClickListener) clickEvent -> {
					ControllerProvider.getEventGroupController().navigateToData(listEntry.getEventGroup().getUuid());
				});
			}
			listEntry.addListEventsListener(i, (ClickListener) clickEvent -> {
				EventCriteria eventCriteria = new EventCriteria();
				eventCriteria.setEventGroup(listEntry.getEventGroup().toReference());
				ControllerProvider.getEventController().navigateTo(eventCriteria, true);
			});
			listLayout.addComponent(listEntry);
		}
	}
}
