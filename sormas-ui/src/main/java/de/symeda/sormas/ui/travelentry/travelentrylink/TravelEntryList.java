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
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.PaginationList;

public class TravelEntryList extends PaginationList<TravelEntryListEntryDto> {

	private static final long serialVersionUID = -534579406662710137L;

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final TravelEntryListCriteria travelEntryListCriteria;
	private final boolean isEditAllowed;

	public TravelEntryList(TravelEntryListCriteria travelEntryListCriteria, boolean isEditAllowed) {
		super(MAX_DISPLAYED_ENTRIES);
		this.travelEntryListCriteria = travelEntryListCriteria;
		this.isEditAllowed = isEditAllowed;
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
			boolean isActiveTravelEntry = travelEntry.getUuid().equals(getActiveUuid());
			if (isActiveTravelEntry) {
				listEntry.setActive();
			}
			if (!isActiveTravelEntry) {
				addActionButton(listEntry);
			}
			listLayout.addComponent(listEntry);
		}
	}

	private void addActionButton(TravelEntryListEntry listEntry) {
		listEntry.addActionButton(
			listEntry.getTravelEntry().getUuid(),
			(Button.ClickListener) event -> ControllerProvider.getTravelEntryController().navigateToTravelEntry(listEntry.getTravelEntry().getUuid()),
			UiUtil.permitted(isEditAllowed, UserRight.TRAVEL_ENTRY_EDIT));
		listEntry.setEnabled(isEditAllowed);
	}
}
