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

package de.symeda.sormas.ui.vaccination;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Collections;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.vaccinationinfo.VaccinationInfoDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NumberValidator;

public class VaccinationInfoForm extends AbstractEditForm<VaccinationInfoDto> {

	private static final String HTML_LAYOUT = fluidRowLocs(VaccinationInfoDto.VACCINATION, VaccinationInfoDto.VACCINATION_DOSES)
		+ fluidRowLocs(
			VaccinationInfoDto.FIRST_VACCINATION_DATE,
			VaccinationInfoDto.LAST_VACCINATION_DATE,
			VaccinationInfoDto.VACCINATION_INFO_SOURCE)
		+ fluidRowLocs(VaccinationInfoDto.VACCINE_NAME, VaccinationInfoDto.OTHER_VACCINE_NAME)
		+ fluidRowLocs(VaccinationInfoDto.VACCINE_MANUFACTURER, VaccinationInfoDto.OTHER_VACCINE_MANUFACTURER)
		+ fluidRowLocs(VaccinationInfoDto.VACCINE_INN, VaccinationInfoDto.VACCINE_BATCH_NUMBER)
		+ fluidRowLocs(VaccinationInfoDto.VACCINE_UNII_CODE, VaccinationInfoDto.VACCINE_ATC_CODE);

	public VaccinationInfoForm(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(VaccinationInfoDto.class, VaccinationInfoDto.I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(VaccinationInfoDto.VACCINATION);
		addField(VaccinationInfoDto.VACCINATION_DOSES)
			.addValidator(new NumberValidator(I18nProperties.getValidationError(Validations.vaccineDosesFormat), 1, 10));
		addFields(VaccinationInfoDto.VACCINATION_INFO_SOURCE, VaccinationInfoDto.FIRST_VACCINATION_DATE, VaccinationInfoDto.LAST_VACCINATION_DATE);

		ComboBox vaccineName = addField(VaccinationInfoDto.VACCINE_NAME);
		ComboBox vaccineManufacturer = addField(VaccinationInfoDto.VACCINE_MANUFACTURER);
		vaccineName.addValueChangeListener(e -> {
			Vaccine vaccine = (Vaccine) e.getProperty().getValue();
			if (vaccine != null) {
				vaccineManufacturer.setValue(vaccine.getManufacturer());
			}
		});

		addFields(
			VaccinationInfoDto.OTHER_VACCINE_NAME,
			VaccinationInfoDto.OTHER_VACCINE_MANUFACTURER,
			VaccinationInfoDto.VACCINE_INN,
			VaccinationInfoDto.VACCINE_BATCH_NUMBER,
			VaccinationInfoDto.VACCINE_UNII_CODE,
			VaccinationInfoDto.VACCINE_ATC_CODE);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		if (isVisibleAllowed(VaccinationInfoDto.VACCINATION_DOSES)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.VACCINATION_DOSES,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
		}

		if (isVisibleAllowed(VaccinationInfoDto.FIRST_VACCINATION_DATE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.FIRST_VACCINATION_DATE,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
		}

		if (isVisibleAllowed(VaccinationInfoDto.LAST_VACCINATION_DATE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.LAST_VACCINATION_DATE,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
		}

		if (isVisibleAllowed(VaccinationInfoDto.VACCINE_NAME)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.VACCINE_NAME,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.OTHER_VACCINE_NAME,
				VaccinationInfoDto.VACCINE_NAME,
				Collections.singletonList(Vaccine.OTHER),
				true);
		}

		if (isVisibleAllowed(VaccinationInfoDto.VACCINATION_INFO_SOURCE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.VACCINATION_INFO_SOURCE,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
		}

		if (isVisibleAllowed(VaccinationInfoDto.VACCINE_MANUFACTURER)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.VACCINE_MANUFACTURER,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.OTHER_VACCINE_MANUFACTURER,
				VaccinationInfoDto.VACCINE_MANUFACTURER,
				Collections.singletonList(VaccineManufacturer.OTHER),
				true);
		}

		if (isVisibleAllowed(VaccinationInfoDto.VACCINE_INN)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.VACCINE_INN,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
		}

		if (isVisibleAllowed(VaccinationInfoDto.VACCINE_BATCH_NUMBER)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.VACCINE_BATCH_NUMBER,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
		}

		if (isVisibleAllowed(VaccinationInfoDto.VACCINE_UNII_CODE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.VACCINE_UNII_CODE,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
		}

		if (isVisibleAllowed(VaccinationInfoDto.VACCINE_ATC_CODE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationInfoDto.VACCINE_ATC_CODE,
				VaccinationInfoDto.VACCINATION,
				Collections.singletonList(Vaccination.VACCINATED),
				true);
		}
	}

	public boolean isVisibleAllowed() {
		return isVisibleAllowed(VaccinationInfoDto.VACCINATION);
	}

	@Override
	public String getCaption() {
		// never render caption
		return null;
	}
}
