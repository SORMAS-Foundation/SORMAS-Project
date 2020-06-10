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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public class AbstractEventView extends AbstractSubNavigationView {

	public static final String ROOT_VIEW_NAME = EventsView.VIEW_NAME;

	private EventReferenceDto eventRef;

	protected AbstractEventView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		if (params.endsWith("/")) {
			params = params.substring(0, params.length() - 1);
		}

		eventRef = FacadeProvider.getEventFacade().getReferenceByUuid(params);

		menu.removeAllViews();
		menu.addView(EventsView.VIEW_NAME, I18nProperties.getCaption(Captions.eventEventsList));
		menu.addView(EventDataView.VIEW_NAME, I18nProperties.getCaption(EventDto.I18N_PREFIX), params);
		menu.addView(EventParticipantsView.VIEW_NAME, I18nProperties.getCaption(Captions.eventEventParticipants), params);
		infoLabel.setValue(eventRef.getCaption());
		infoLabelSub.setValue(DataHelper.getShortUuid(eventRef.getUuid()));
	}

	@Override
	protected void setSubComponent(Component newComponent) {
		super.setSubComponent(newComponent);

		if (FacadeProvider.getEventFacade().isDeleted(eventRef.getUuid())) {
			newComponent.setEnabled(false);
		}
	}

	public EventReferenceDto getEventRef() {
		return eventRef;
	}
}
