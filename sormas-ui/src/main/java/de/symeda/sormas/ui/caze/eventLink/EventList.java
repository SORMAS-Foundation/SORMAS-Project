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

package de.symeda.sormas.ui.caze.eventLink;

import java.util.List;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

public class EventList extends PaginationList<EventIndexDto> {

	private final EventCriteria eventCriteria = new EventCriteria();

	public EventList(CaseReferenceDto caseReferenceDto) {
		super(5);
		eventCriteria.caze(caseReferenceDto);
		eventCriteria.setUserFilterIncluded(false);
	}

	@Override
	public void reload() {
		List<EventIndexDto> events = FacadeProvider.getEventFacade().getIndexList(eventCriteria, 0, maxDisplayedEntries * 20, null);

		setEntries(events);
		if (!events.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noEventLabel = new Label(I18nProperties.getCaption(Captions.eventNoEventLinkedToCase));
			listLayout.addComponent(noEventLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<EventIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			EventIndexDto event = displayedEntries.get(i);
			EventListEntry listEntry = new EventListEntry(event);

			if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_EDIT)) {
				listEntry.addEditListener(
					i,
					(ClickListener) clickEvent -> ControllerProvider.getEventController().navigateToData(listEntry.getEvent().getUuid()));
			}
			listLayout.addComponent(listEntry);
		}
	}
}
