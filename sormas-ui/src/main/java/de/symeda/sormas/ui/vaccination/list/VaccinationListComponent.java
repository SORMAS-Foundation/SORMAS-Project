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

package de.symeda.sormas.ui.vaccination.list;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.api.vaccination.VaccinationListEntryDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class VaccinationListComponent extends SideComponent {

	public VaccinationListComponent(VaccinationListCriteria criteria) {
		this(criteria, null);
	}

	public VaccinationListComponent(VaccinationListCriteria criteria, Consumer<Runnable> actionCallback) {

		super(I18nProperties.getString(Strings.entityVaccinations), actionCallback);

		addCreateButton(
			I18nProperties.getCaption(Captions.vaccinationNewVaccination),
			() -> ControllerProvider.getVaccinationController()
				.create(
					criteria.getRegion(),
					criteria.getDistrict(),
					criteria.getPerson(),
					criteria.getDisease(),
					UiFieldAccessCheckers.getNoop(),
					v -> SormasUI.refreshView()),
			UserRight.IMMUNIZATION_CREATE);

		Function<Integer, List<VaccinationListEntryDto>> entriesListSupplier;

		if (criteria.getVaccinationAssociationType() != null) {
			switch (criteria.getVaccinationAssociationType()) {
			case CASE:
				entriesListSupplier = maxDisplayedEntries -> FacadeProvider.getVaccinationFacade()
					.getEntriesListWithRelevance(criteria.getCaseReference(), criteria, 0, maxDisplayedEntries);
				break;
			case CONTACT:
				entriesListSupplier = maxDisplayedEntries -> FacadeProvider.getVaccinationFacade()
					.getEntriesListWithRelevance(criteria.getContactReference(), criteria, 0, maxDisplayedEntries);
				break;
			case EVENT_PARTICIPANT:
				entriesListSupplier = maxDisplayedEntries -> FacadeProvider.getVaccinationFacade()
					.getEntriesListWithRelevance(criteria.getEventParticipantReference(), criteria, 0, maxDisplayedEntries);
				break;
			default:
				throw new IllegalArgumentException("Invalid vaccination association type: " + criteria.getVaccinationAssociationType());
			}
		} else {
			entriesListSupplier = maxDisplayedEntries -> FacadeProvider.getVaccinationFacade().getEntriesList(criteria, 0, maxDisplayedEntries, null);
		}

		VaccinationList vaccinationList = new VaccinationList(criteria.getDisease(), entriesListSupplier, actionCallback);
		addComponent(vaccinationList);
		vaccinationList.reload();
	}

}
