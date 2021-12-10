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

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.travelentry.TravelEntryListCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryListEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

public class TravelEntryList extends PaginationList<TravelEntryListEntryDto> {

	private static final long serialVersionUID = -534579406662710137L;

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final TravelEntryListCriteria travelEntryListCriteria;

	public TravelEntryList(TravelEntryListCriteria travelEntryListCriteria) {
		super(MAX_DISPLAYED_ENTRIES);
		this.travelEntryListCriteria = travelEntryListCriteria;
	}

	@Override
	public void reload() {

		List<TravelEntryListEntryDto> travelEntries =
			FacadeProvider.getTravelEntryFacade().getEntriesList(travelEntryListCriteria, 0, maxDisplayedEntries * 20);

		setEntries(travelEntries);
		if (!travelEntries.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noTravelEntriesLabel = new Label(I18nProperties.getCaption(Captions.travelEntriesNoTravelEntriesForPerson));
			listLayout.addComponent(noTravelEntriesLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		for (TravelEntryListEntryDto travelEntry : getDisplayedEntries()) {
			TravelEntryListEntry listEntry = new TravelEntryListEntry(travelEntry);

			addEditButton(listEntry);
			listLayout.addComponent(listEntry);
		}
	}

	private void addEditButton(TravelEntryListEntry listEntry) {
		if (UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_EDIT)) {
			listEntry.addEditButton(
				"edit-travelEntry-" + listEntry.getTravelEntry().getUuid(),
				(Button.ClickListener) event -> ControllerProvider.getTravelEntryController()
					.navigateToTravelEntry(listEntry.getTravelEntry().getUuid()));
		}
	}
}
