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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

@SuppressWarnings("serial")
public class TravelEntryListComponent extends SideComponent {

	public static final String TRAVEL_ENTRIES_LOC = "travelEntries";

	public TravelEntryListComponent(CaseReferenceDto caseReferenceDto, PersonReferenceDto personReferenceDto) {
		super(I18nProperties.getString(Strings.entityTravelEntries));

		if (caseReferenceDto != null && UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_CREATE)) {
			Button createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.travelEntryNewTravelEntry));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(e -> ControllerProvider.getTravelEntryController().create(caseReferenceDto));
			addCreateButton(createButton);
		}

		TravelEntryCriteria travelEntryCriteria = new TravelEntryCriteria();

		if (caseReferenceDto != null) {
			travelEntryCriteria.caze(caseReferenceDto);
		}
		if (personReferenceDto != null) {
			travelEntryCriteria.person(personReferenceDto);
		}

		TravelEntryList travelEntryList = new TravelEntryList(travelEntryCriteria);
		addComponent(travelEntryList);
		travelEntryList.reload();
	}

	public static void addTravelEntryListComponent(CustomLayout layout, PersonReferenceDto personReferenceDto) {
		addTravelEntryListComponent(layout, null, personReferenceDto);
	}

	public static void addTravelEntryListComponent(CustomLayout layout, CaseReferenceDto caseReferenceDto) {
		addTravelEntryListComponent(layout, caseReferenceDto, null);
	}

	private static void addTravelEntryListComponent(CustomLayout layout, CaseReferenceDto caseReferenceDto, PersonReferenceDto personReferenceDto) {
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TRAVEL_ENTRIES)
			&& UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_VIEW)) {
			layout.addComponent(new SideComponentLayout(new TravelEntryListComponent(caseReferenceDto, personReferenceDto)), TRAVEL_ENTRIES_LOC);
		}
	}
}
