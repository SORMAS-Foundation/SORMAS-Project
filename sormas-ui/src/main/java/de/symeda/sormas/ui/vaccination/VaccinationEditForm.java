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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.oneOfTwoCol;

import java.util.Collections;

import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.clinicalcourse.HealthConditionsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.UserField;

public class VaccinationEditForm extends AbstractEditForm<VaccinationDto> {

	private static final String HTML_LAYOUT = fluidRowLocs(VaccinationDto.REPORT_DATE, VaccinationDto.REPORTING_USER)
		+ fluidRow(oneOfTwoCol(VaccinationDto.VACCINATION_DATE))
		+ fluidRowLocs(VaccinationDto.VACCINE_NAME, VaccinationDto.OTHER_VACCINE_NAME)
		+ fluidRowLocs(VaccinationDto.VACCINE_MANUFACTURER, VaccinationDto.OTHER_VACCINE_MANUFACTURER)
		+ fluidRowLocs(VaccinationDto.VACCINE_TYPE, VaccinationDto.VACCINATION_INFO_SOURCE)
		+ fluidRow(oneOfTwoCol(VaccinationDto.VACCINE_DOSE))
		+ fluidRowLocs(VaccinationDto.VACCINE_INN, VaccinationDto.VACCINE_UNII_CODE)
		+ fluidRowLocs(VaccinationDto.VACCINE_BATCH_NUMBER, VaccinationDto.VACCINE_ATC_CODE)
		+ fluidRowLocs(VaccinationDto.PREGNANT, VaccinationDto.TRIMESTER)
		+ fluidRowLocs(VaccinationDto.HEALTH_CONDITIONS);

	public VaccinationEditForm(boolean create, Disease disease, UiFieldAccessCheckers fieldAccessCheckers) {
		super(
			VaccinationDto.class,
			VaccinationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale())
				.andWithDisease(disease)
				.andWithFeatureType(FacadeProvider.getFeatureConfigurationFacade().getActiveServerFeatureConfigurations()),
			fieldAccessCheckers);

		setWidth(800, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(VaccinationDto.REPORT_DATE).setRequired(true);

		addField(VaccinationDto.REPORTING_USER, UserField.class).setReadOnly(true);

		addField(VaccinationDto.VACCINATION_DATE);
		Field vaccineName = addField(VaccinationDto.VACCINE_NAME);
		addField(VaccinationDto.OTHER_VACCINE_NAME);
		Field vaccineManufacturer = addField(VaccinationDto.VACCINE_MANUFACTURER);
		addField(VaccinationDto.OTHER_VACCINE_MANUFACTURER);
		vaccineName.addValueChangeListener(e -> {
			Vaccine vaccine = (Vaccine) e.getProperty().getValue();
			if (vaccine != null) {
				vaccineManufacturer.setValue(vaccine.getManufacturer());
			}
		});
		addField(VaccinationDto.VACCINE_TYPE);
		addField(VaccinationDto.VACCINATION_INFO_SOURCE);
		addField(VaccinationDto.VACCINE_DOSE);
		addField(VaccinationDto.VACCINE_INN);
		addField(VaccinationDto.VACCINE_UNII_CODE);
		addField(VaccinationDto.VACCINE_BATCH_NUMBER);
		addField(VaccinationDto.VACCINE_ATC_CODE);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			VaccinationDto.OTHER_VACCINE_NAME,
			VaccinationDto.VACCINE_NAME,
			Collections.singletonList(Vaccine.OTHER),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			VaccinationDto.OTHER_VACCINE_MANUFACTURER,
			VaccinationDto.VACCINE_MANUFACTURER,
			Collections.singletonList(VaccineManufacturer.OTHER),
			true);

		addField(VaccinationDto.PREGNANT);
		addField(VaccinationDto.TRIMESTER);
		addField(VaccinationDto.HEALTH_CONDITIONS, HealthConditionsForm.class).setCaption(null);

		initializeVisibilitiesAndAllowedVisibilities();

		if (isVisibleAllowed(VaccinationDto.PREGNANT)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationDto.TRIMESTER,
				VaccinationDto.PREGNANT,
				Collections.singletonList(YesNoUnknown.YES),
				true);
		}
	}
}
