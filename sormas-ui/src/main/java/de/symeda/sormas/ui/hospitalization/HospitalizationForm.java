/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.hospitalization;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLocCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.server.UserError;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ViewMode;

public class HospitalizationForm extends AbstractEditForm<HospitalizationDto> {

	private static final long serialVersionUID = 1L;

	private static final String HEALTH_FACILITY = Captions.CaseHospitalization_healthFacility;
	private final CaseDataDto caze;
	private final ViewMode viewMode;

	private OptionGroup intensiveCareUnit;
	private DateField intensiveCareUnitStart;
	private DateField intensiveCareUnitEnd;

	//@formatter:off
	private static final String HTML_LAYOUT =
			h3(I18nProperties.getString(Strings.headingHospitalization)) +
			fluidRowLocs(HEALTH_FACILITY, HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY) +
			fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE, HospitalizationDto.LEFT_AGAINST_ADVICE, "") +
					fluidRowLocs(3, HospitalizationDto.INTENSIVE_CARE_UNIT, 3,
							HospitalizationDto.INTENSIVE_CARE_UNIT_START,
							3,
							HospitalizationDto.INTENSIVE_CARE_UNIT_END)
					+ fluidRowLocs(HospitalizationDto.ISOLATED, HospitalizationDto.ISOLATION_DATE, "")
					+
			fluidRow(
					fluidColumnLocCss(VSPACE_TOP_3, 6, 0, HospitalizationDto.HOSPITALIZED_PREVIOUSLY)) +
			fluidRowLocs(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS);
	//@formatter:on

	public HospitalizationForm(CaseDataDto caze, ViewMode viewMode) {

		super(
			HospitalizationDto.class,
			HospitalizationDto.I18N_PREFIX,
			new FieldVisibilityCheckers().add(new OutbreakFieldVisibilityChecker(viewMode)));
		this.caze = caze;
		this.viewMode = viewMode;
		addFields();
	}

	@Override
	protected void addFields() {

		if (caze == null || viewMode == null) {
			return;
		}

		TextField facilityField = addCustomField(HEALTH_FACILITY, FacilityReferenceDto.class, TextField.class);
		FacilityReferenceDto healthFacility = caze.getHealthFacility();
		facilityField.setValue(healthFacility == null ? null : healthFacility.toString());
		facilityField.setReadOnly(true);

		addField(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, OptionGroup.class);
		DateField admissionDateField = addField(HospitalizationDto.ADMISSION_DATE, DateField.class);
		DateField dischargeDateField = addDateField(HospitalizationDto.DISCHARGE_DATE, DateField.class, 7);
		intensiveCareUnit = addField(HospitalizationDto.INTENSIVE_CARE_UNIT, OptionGroup.class);
		intensiveCareUnit.addValueChangeListener(e -> setDateFieldVisibilties());
		intensiveCareUnitStart = addField(HospitalizationDto.INTENSIVE_CARE_UNIT_START, DateField.class);
		intensiveCareUnitStart.setVisible(false);
		intensiveCareUnitEnd = addField(HospitalizationDto.INTENSIVE_CARE_UNIT_END, DateField.class);
		intensiveCareUnitEnd.setVisible(false);
		addField(HospitalizationDto.ISOLATION_DATE);
		addField(HospitalizationDto.ISOLATED, OptionGroup.class);
		addField(HospitalizationDto.LEFT_AGAINST_ADVICE, OptionGroup.class);
		OptionGroup hospitalizedPreviouslyField = addField(HospitalizationDto.HOSPITALIZED_PREVIOUSLY, OptionGroup.class);
		CssStyles.style(hospitalizedPreviouslyField, CssStyles.ERROR_COLOR_PRIMARY);
		PreviousHospitalizationsField previousHospitalizationsField =
			addField(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS, PreviousHospitalizationsField.class);

		initializeVisibilitiesAndAllowedVisibilities();

		if (isVisibleAllowed(HospitalizationDto.ISOLATION_DATE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				HospitalizationDto.ISOLATION_DATE,
				HospitalizationDto.ISOLATED,
				Arrays.asList(YesNoUnknown.YES),
				true);
		}
		if (isVisibleAllowed(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				HospitalizationDto.PREVIOUS_HOSPITALIZATIONS,
				HospitalizationDto.HOSPITALIZED_PREVIOUSLY,
				Arrays.asList(YesNoUnknown.YES),
				true);
		}

		// Validations
		admissionDateField.addValidator(
			new DateComparisonValidator(
				admissionDateField,
				caze.getSymptoms().getOnsetDate(),
				false,
				false,
				I18nProperties.getValidationError(
					Validations.afterDateSoft,
					admissionDateField.getCaption(),
					I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE))));
		admissionDateField.addValidator(
			new DateComparisonValidator(
				admissionDateField,
				dischargeDateField,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, admissionDateField.getCaption(), dischargeDateField.getCaption())));
		admissionDateField.setInvalidCommitted(true);
		dischargeDateField.addValidator(
			new DateComparisonValidator(
				dischargeDateField,
				admissionDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, dischargeDateField.getCaption(), admissionDateField.getCaption())));
		intensiveCareUnitStart.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitStart,
				admissionDateField,
				false,
				true,
				I18nProperties.getValidationError(Validations.afterDate, intensiveCareUnitStart.getCaption(), admissionDateField.getCaption())));
		intensiveCareUnitStart.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitStart,
				intensiveCareUnitEnd,
				true,
				true,
				I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitStart.getCaption(), intensiveCareUnitEnd.getCaption())));
		intensiveCareUnitEnd.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitEnd,
				intensiveCareUnitStart,
				false,
				true,
				I18nProperties.getValidationError(Validations.afterDate, intensiveCareUnitEnd.getCaption(), intensiveCareUnitStart.getCaption())));
		intensiveCareUnitEnd.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitEnd,
				dischargeDateField,
				true,
				true,
				I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitEnd.getCaption(), dischargeDateField.getCaption())));

		hospitalizedPreviouslyField.addValueChangeListener(e -> updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField));
		previousHospitalizationsField.addValueChangeListener(e -> updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField));
	}

	private void setDateFieldVisibilties() {

		boolean visible = YesNoUnknown.YES.equals(intensiveCareUnit.getValue());
		intensiveCareUnitStart.setVisible(visible);
		intensiveCareUnitEnd.setVisible(visible);
	}

	private void updatePrevHospHint(OptionGroup hospitalizedPreviouslyField, PreviousHospitalizationsField previousHospitalizationsField) {

		YesNoUnknown value = (YesNoUnknown) hospitalizedPreviouslyField.getValue();
		Collection<PreviousHospitalizationDto> previousHospitalizations = previousHospitalizationsField.getValue();
		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT)
			&& value == YesNoUnknown.YES
			&& (previousHospitalizations == null || previousHospitalizations.size() == 0)) {
			hospitalizedPreviouslyField.setComponentError(new UserError(I18nProperties.getValidationError(Validations.softAddEntryToList)));
		} else {
			hospitalizedPreviouslyField.setComponentError(null);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
