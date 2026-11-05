/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.ui.immunization.immunizationlink;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.immunization.components.panel.VaccinationStatusPanel;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class ImmunizationListComponent extends SideComponent {

	private final VaccinationStatusPanel vaccinationStatusPanel;

	public ImmunizationListComponent(
		Supplier<ImmunizationListCriteria> criteriaSupplier,
		String activeUuid,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed) {
		this(criteriaSupplier, activeUuid, actionCallback, isEditAllowed, null, null);
	}

	public ImmunizationListComponent(
		Supplier<ImmunizationListCriteria> criteriaSupplier,
		String activeUuid,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed,
		VaccinationStatusPanel vaccinationStatusPanel) {
		this(criteriaSupplier, activeUuid, actionCallback, isEditAllowed, vaccinationStatusPanel, null);
	}

	public ImmunizationListComponent(
		Supplier<ImmunizationListCriteria> criteriaSupplier,
		String activeUuid,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed,
		VaccinationStatusPanel vaccinationStatusPanel,
		Runnable quickSavedCallback) {
		this(criteriaSupplier, activeUuid, actionCallback, isEditAllowed, vaccinationStatusPanel, quickSavedCallback, null);
	}

	/**
	 * Creates the immunization side component and remembers the case, contact or event participant it is shown on, so that an
	 * immunization opened from this list offers a link back to it.
	 * <p>
	 * Example: {@code ContactDataView} passes its contact reference {@code ABC123-...}; clicking the edit button of immunization
	 * {@code XYZ789-...} then navigates to {@code immunizations/data/XYZ789-...?contactUuid=ABC123-...}. Passing {@code null} as
	 * {@code origin} (as the person views do) navigates to {@code immunizations/data/XYZ789-...} without a back link.
	 *
	 * @param origin
	 *            A {@code CaseReferenceDto}, {@code ContactReferenceDto} or {@code EventParticipantReferenceDto} of the entity this
	 *            component is displayed on, or {@code null}. Other reference types are ignored (no back link).
	 */
	public ImmunizationListComponent(
		Supplier<ImmunizationListCriteria> criteriaSupplier,
		String activeUuid,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed,
		VaccinationStatusPanel vaccinationStatusPanel,
		Runnable quickSavedCallback,
		ReferenceDto origin) {
		super(I18nProperties.getString(Strings.entityImmunization), actionCallback);

		if (isEditAllowed) {
			addCreateButton(I18nProperties.getCaption(Captions.immunizationNewImmunization), () -> {
				ImmunizationListCriteria immunizationListCriteria = criteriaSupplier.get();
				ControllerProvider.getImmunizationController()
					.create(
						immunizationListCriteria.getPerson(),
						immunizationListCriteria.getDisease(),
						immunizationListCriteria.getDiseaseDetails(),
						quickSavedCallback,
						origin);
			}, UserRight.IMMUNIZATION_CREATE);
		}

		if (vaccinationStatusPanel != null) {
			this.vaccinationStatusPanel = vaccinationStatusPanel;
			addComponent(vaccinationStatusPanel);
		} else {
			this.vaccinationStatusPanel = null;
		}

		ImmunizationList immunizationList = new ImmunizationList(criteriaSupplier.get(), isEditAllowed, origin);
		immunizationList.setActiveUuid(activeUuid);
		addComponent(immunizationList);
		immunizationList.reload();
	}

	public VaccinationStatusPanel getVaccinationStatusPanel() {
		return vaccinationStatusPanel;
	}
}
