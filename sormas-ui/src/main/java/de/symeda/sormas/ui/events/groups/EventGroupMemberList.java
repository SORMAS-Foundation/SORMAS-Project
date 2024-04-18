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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.PaginationList;

public class EventGroupMemberList extends PaginationList<EventIndexDto> {

	private final EventGroupReferenceDto eventGroupReference;
	private final EventCriteria eventCriteria = new EventCriteria();
	private final Label noEventLabel;

	public EventGroupMemberList(EventGroupReferenceDto eventGroupReference) {
		super(10);

		this.eventGroupReference = eventGroupReference;

		eventCriteria.setEventGroup(eventGroupReference);
		eventCriteria.setUserFilterIncluded(false);
		noEventLabel = new Label(I18nProperties.getCaption(Captions.eventNoEventLinkedToEventGroup));
	}

	@Override
	public void reload() {
		List<EventIndexDto> events = FacadeProvider.getEventFacade().getIndexList(eventCriteria, 0, maxDisplayedEntries * 20, null);

		setEntries(events);
		if (!events.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noEventLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<EventIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			EventIndexDto event = displayedEntries.get(i);
			EventGroupMemberListEntry listEntry = new EventGroupMemberListEntry(event);

			if (event.isInJurisdiction() && UiUtil.permitted(UserRight.EVENTGROUP_LINK)) {
				listEntry.addUnlinkEventButton(i, (ClickListener) clickEvent -> {
					if (!FacadeProvider.getEventFacade().isInJurisdictionOrOwned(event.getUuid())
						&& !UiUtil.hasNationJurisdictionLevel()
						&& !UiUtil.isAdmin()) {
						new Notification(
							I18nProperties.getString(Strings.headingEventGroupUnlinkEventIssue),
							I18nProperties.getString(Strings.errorEventFromAnotherJurisdiction),
							Notification.Type.WARNING_MESSAGE,
							false).show(Page.getCurrent());
						return;
					}

					ControllerProvider.getEventGroupController().unlinkEventGroup(event.toReference(), eventGroupReference);
					reload();
				});
			}
			listEntry.addNavigateToEventButton(
				i,
				event.isInJurisdiction(),
				(ClickListener) clickEvent -> ControllerProvider.getEventController().navigateToData(listEntry.getEvent().getUuid()));

			listLayout.addComponent(listEntry);
		}
	}

	protected void filterEventListByCase(CaseReferenceDto caseRef) {
		eventCriteria.caze(caseRef);
	}
}
