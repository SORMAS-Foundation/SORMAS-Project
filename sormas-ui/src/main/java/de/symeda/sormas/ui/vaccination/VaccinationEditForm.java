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

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.oneOfTwoCol;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.clinicalcourse.HealthConditionsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.UserField;

public class VaccinationEditForm extends AbstractEditForm<VaccinationDto> {

	private static final String MEDICAL_INFORMATION_LOC = "medicalInformationLoc";

	private static final String HTML_LAYOUT = fluidRowLocs(6, VaccinationDto.REPORT_DATE, 3, VaccinationDto.REPORTING_USER, 3, "")
		+ fluidRow(oneOfTwoCol(VaccinationDto.VACCINATION_DATE))
		+ fluidRowLocs(VaccinationDto.VACCINE_NAME, VaccinationDto.OTHER_VACCINE_NAME)
		+ fluidRowLocs(VaccinationDto.VACCINE_MANUFACTURER, VaccinationDto.OTHER_VACCINE_MANUFACTURER)
		+ fluidRowLocs(VaccinationDto.VACCINE_TYPE, VaccinationDto.VACCINATION_INFO_SOURCE)
		+ fluidRow(oneOfTwoCol(VaccinationDto.VACCINE_DOSE))
		+ fluidRowLocs(VaccinationDto.VACCINE_INN, VaccinationDto.VACCINE_UNII_CODE)
		+ fluidRowLocs(VaccinationDto.VACCINE_BATCH_NUMBER, VaccinationDto.VACCINE_ATC_CODE)
		+ fluidRowLocs(VaccinationDto.HEALTH_CONDITIONS)
		+ loc(MEDICAL_INFORMATION_LOC)
		+ fluidRowLocs(VaccinationDto.PREGNANT, VaccinationDto.TRIMESTER);

	private final Sex personSex;

	public VaccinationEditForm(boolean create, Disease disease, Sex personSex, UiFieldAccessCheckers fieldAccessCheckers) {
		super(
			VaccinationDto.class,
			VaccinationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale())
				.andWithDisease(disease)
				.andWithFeatureType(FacadeProvider.getFeatureConfigurationFacade().getActiveServerFeatureConfigurations()),
			fieldAccessCheckers);

		this.personSex = personSex;

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

		UserField reportingUser = addField(VaccinationDto.REPORTING_USER, UserField.class);
		reportingUser.setParentPseudonymizedSupplier(() -> getValue().isPseudonymized());
		reportingUser.setReadOnly(true);

		addField(VaccinationDto.VACCINATION_DATE);
		Field vaccineName = addField(VaccinationDto.VACCINE_NAME);
		addField(VaccinationDto.OTHER_VACCINE_NAME);
		Field vaccineManufacturer = addField(VaccinationDto.VACCINE_MANUFACTURER);
		Field vaccineType = addField(VaccinationDto.VACCINE_TYPE);
		addField(VaccinationDto.OTHER_VACCINE_MANUFACTURER);
		Field atcCode = addField(VaccinationDto.VACCINE_ATC_CODE);
		Field inn = addField(VaccinationDto.VACCINE_INN);
		Field uniiCode = addField(VaccinationDto.VACCINE_UNII_CODE);

		// Disable the manufacturer and type fields if the vaccine has its values.
		vaccineName.addValueChangeListener(e -> {
			Vaccine vaccine = (Vaccine) e.getProperty().getValue();
			if (vaccine != null) {
				vaccineManufacturer.setValue(vaccine.getManufacturer().isEmpty() ? null : vaccine.getManufacturer().get());

				// VaccineType
				if (vaccine.getVaccineType().isPresent()) {
					String vacTypeVal = vaccine.getVaccineType().get();
					vaccineType
						.setValue(StringUtils.isBlank(vacTypeVal) ? I18nProperties.getString(Strings.Vaccine_vaccineType_notAvailable) : vacTypeVal);
					vaccineType.setEnabled(false);
				} else {
					vaccineType.setEnabled(true);
					vaccineType.setValue(null);
				}
				// AtcCode
				if (vaccine.getAtcCode().isPresent()) {
					String atcCodeVal = vaccine.getAtcCode().get();
					atcCode.setValue(StringUtils.isBlank(atcCodeVal) ? I18nProperties.getString(Strings.Vaccine_atcCode_notAvailable) : atcCodeVal);
					atcCode.setEnabled(false);
				} else {
					atcCode.setEnabled(true);
					atcCode.setValue(null);
				}
				// inn
				if (vaccine.getInn().isPresent()) {
					String innVal = vaccine.getInn().get();
					inn.setValue(StringUtils.isBlank(innVal) ? I18nProperties.getString(Strings.Vaccine_inn_notAvailable) : innVal);
					inn.setEnabled(false);
				} else {
					inn.setEnabled(true);
					inn.setValue(null);
				}
				// Uniicode
				if (vaccine.getUniiCode().isPresent()) {
					String uniiCodeVal = vaccine.getUniiCode().get();
					uniiCode
						.setValue((StringUtils.isBlank(uniiCodeVal)) ? I18nProperties.getString(Strings.Vaccine_uniiCode_notAvailable) : uniiCodeVal);
					uniiCode.setEnabled(false);
				} else {
					uniiCode.setEnabled(true);
					uniiCode.setValue(null);
				}

			} else {
				FieldHelper.setClearEnabled(true, vaccineManufacturer, vaccineType, atcCode, inn, uniiCode);
			}
		});
		addField(VaccinationDto.VACCINATION_INFO_SOURCE);
		addField(VaccinationDto.VACCINE_DOSE);

		addField(VaccinationDto.VACCINE_BATCH_NUMBER);

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

		Field<?> pregnantField = addField(VaccinationDto.PREGNANT, NullableOptionGroup.class);
		Field<?> trimesterField = addField(VaccinationDto.TRIMESTER, NullableOptionGroup.class);
		addField(VaccinationDto.HEALTH_CONDITIONS, HealthConditionsForm.class).setCaption(null);

		initializeVisibilitiesAndAllowedVisibilities();

		boolean isMale = Sex.MALE.equals(this.personSex);

		if (isMale) {
			pregnantField.setVisible(false);
			trimesterField.setVisible(false);
		} else if (isVisibleAllowed(VaccinationDto.PREGNANT)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				VaccinationDto.TRIMESTER,
				VaccinationDto.PREGNANT,
				Collections.singletonList(YesNoUnknown.YES),
				true);
		}

		if (pregnantField.isVisible()) {
			Label medicalInformationCaptionLabel = new Label(I18nProperties.getString(Strings.headingMedicalInformation));
			medicalInformationCaptionLabel.addStyleName(H3);
			getContent().addComponent(medicalInformationCaptionLabel, MEDICAL_INFORMATION_LOC);
		}
	}
}
