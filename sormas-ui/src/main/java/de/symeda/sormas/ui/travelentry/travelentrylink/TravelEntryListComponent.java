/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.travelentry.travelentrylink;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class TravelEntryListComponent extends SideComponent {

	public TravelEntryListComponent(TravelEntryListCriteria travelEntryListCriteria) {
		super(I18nProperties.getString(Strings.entityTravelEntries));

		if (FacadeProvider.getTravelEntryFacade().count(new TravelEntryCriteria(), true) > 0) {
			addCreateButton(
				I18nProperties.getCaption(Captions.travelEntryNewTravelEntry),
				UserRight.TRAVEL_ENTRY_CREATE,
				e -> ControllerProvider.getTravelEntryController().create(travelEntryListCriteria));
		}

		TravelEntryList travelEntryList = new TravelEntryList(travelEntryListCriteria);
		addComponent(travelEntryList);
		travelEntryList.reload();
	}
}
