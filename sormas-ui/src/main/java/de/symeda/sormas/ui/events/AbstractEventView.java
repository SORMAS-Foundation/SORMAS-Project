/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public class AbstractEventView extends AbstractSubNavigationView {

	public static final String ROOT_VIEW_NAME = EventsView.VIEW_NAME;
	
	private EventReferenceDto eventRef;
	
	protected AbstractEventView(String viewName) {
		super(viewName);
	}
	
	@Override
	public void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		eventRef = FacadeProvider.getEventFacade().getReferenceByUuid(params);
		
		menu.removeAllViews();
		menu.addView(EventsView.VIEW_NAME, "Events list");
		menu.addView(EventDataView.VIEW_NAME, "Event", params);
		menu.addView(EventParticipantsView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(EventDto.I18N_PREFIX, "eventParticipants"), params);
		infoLabel.setValue(eventRef.getCaption());
		infoLabelSub.setValue(DataHelper.getShortUuid(eventRef.getUuid()));
	}
	
	public EventReferenceDto getEventRef() {
		return eventRef;
	}

}
