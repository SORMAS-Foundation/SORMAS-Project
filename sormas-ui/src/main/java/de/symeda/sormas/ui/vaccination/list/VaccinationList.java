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

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.api.vaccination.VaccinationListEntryDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.PaginationList;

public class VaccinationList extends PaginationList<VaccinationListEntryDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final String personUuid;
	private final Disease disease;

	public VaccinationList(String personUuid, Disease disease) {
		super(MAX_DISPLAYED_ENTRIES);
		this.personUuid = personUuid;
		this.disease = disease;
	}

	@Override
	public void reload() {
		List<VaccinationListEntryDto> list = FacadeProvider.getVaccinationFacade()
			.getEntriesList(
				new VaccinationListCriteria.Builder(new PersonReferenceDto(personUuid)).withDisease(disease).build(),
				0,
				maxDisplayedEntries * 20);

		setEntries(list);
		if (!list.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noVaccinationsLabel = new Label(
				I18nProperties.getCaption(
					disease != null ? Captions.vaccinationNoVaccinationsForPersonAndDisease : Captions.vaccinationNoVaccinationsForPerson));
			listLayout.addComponent(noVaccinationsLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		for (VaccinationListEntryDto entryDto : getDisplayedEntries()) {
			VaccinationListEntry listEntry = new VaccinationListEntry(entryDto, disease == null);
			addEditButton(listEntry);
			listLayout.addComponent(listEntry);
		}
	}

	private void addEditButton(VaccinationListEntry listEntry) {
		listEntry.addEditListener(
			(Button.ClickListener) event -> ControllerProvider.getVaccinationController()
				.edit(
					FacadeProvider.getVaccinationFacade().getByUuid(listEntry.getVaccination().getUuid()),
					disease,
					UiFieldAccessCheckers.getDefault(listEntry.getVaccination().isPseudonymized()),
					true,
					v -> reload(),
					() -> {
						listLayout.removeAllComponents();
						reload();
					}));
	}
}
