/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.hospitalization;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateComparator;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ViewMode;

public class HospitalizationForm extends AbstractEditForm<HospitalizationDto> {

	private static final long serialVersionUID = 1L;

	private static final String HOSPITALIZATION_HEADING_LOC = "hospitalizationHeadingLoc";
	private static final String PREVIOUS_HOSPITALIZATIONS_HEADING_LOC = "previousHospitalizationsHeadingLoc";
	private static final String HEALTH_FACILITY = Captions.CaseHospitalization_healthFacility;
	private static final String HEALTH_FACILITY_DEPARTMENT = Captions.CaseData_department;
	private static final String HOSPITAL_NAME_DETAIL = " ( %s )";
	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(HOSPITALIZATION_HEADING_LOC) +
			fluidRowLocs(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY) +
			fluidRowLocs(HospitalizationDto.CURRENTLY_HOSPITALIZED) +
			fluidRowLocs(HEALTH_FACILITY, HEALTH_FACILITY_DEPARTMENT) +
			fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE, HospitalizationDto.LEFT_AGAINST_ADVICE, "") +
			fluidRowLocs( HospitalizationDto.DURATION_OF_HOSPITALIZATION,  HospitalizationDto.HOSPITALIZATION_REASON,  HospitalizationDto.OTHER_HOSPITALIZATION_REASON, "") +
			fluidRowLocs(3, HospitalizationDto.INTENSIVE_CARE_UNIT, 
						3, HospitalizationDto.INTENSIVE_CARE_UNIT_START, 
						3, HospitalizationDto.INTENSIVE_CARE_UNIT_END, 
						3, HospitalizationDto.ICU_LENGTH_OF_STAY) +
			fluidRowLocs(HospitalizationDto.OXYGEN_PRESCRIBED, HospitalizationDto.STILL_HOSPITALIZED) +
			fluidRowLocs(HospitalizationDto.ISOLATED, HospitalizationDto.ISOLATION_DATE, "") +
			fluidRowLocs(HospitalizationDto.DESCRIPTION) +
			loc(PREVIOUS_HOSPITALIZATIONS_HEADING_LOC) +
			fluidRowLocs(HospitalizationDto.HOSPITALIZED_PREVIOUSLY) +
			fluidRowLocs(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS);
	//@formatter:on
	private final CaseDataDto caze;
	private final ViewMode viewMode;
	private NullableOptionGroup intensiveCareUnit;
	private DateField intensiveCareUnitStart;
	private DateField intensiveCareUnitEnd;

	public HospitalizationForm(CaseDataDto caze, ViewMode viewMode, boolean isPseudonymized, boolean inJurisdiction, boolean isEditAllowed) {

		super(
			HospitalizationDto.class,
			HospitalizationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale())
				.add(new OutbreakFieldVisibilityChecker(viewMode)),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized),
			isEditAllowed);
		this.caze = caze;
		this.viewMode = viewMode;
		addFields();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {

		if (caze == null || viewMode == null) {
			return;
		}

		Label hospitalizationHeadingLabel = new Label(I18nProperties.getString(Strings.headingHospitalization));
		hospitalizationHeadingLabel.addStyleName(H3);
		getContent().addComponent(hospitalizationHeadingLabel, HOSPITALIZATION_HEADING_LOC);

		Label previousHospitalizationsHeadingLabel = new Label(I18nProperties.getString(Strings.headingPreviousHospitalizations));
		previousHospitalizationsHeadingLabel.addStyleName(H3);
		getContent().addComponent(previousHospitalizationsHeadingLabel, PREVIOUS_HOSPITALIZATIONS_HEADING_LOC);

		TextField facilityField = addCustomField(HEALTH_FACILITY, FacilityReferenceDto.class, TextField.class);
		FacilityReferenceDto healthFacility = caze.getHealthFacility();
		facilityField.setValue(getHospitalName(healthFacility, caze));
		facilityField.setReadOnly(true);

		if (!StringUtils.isEmpty(caze.getDepartment())) {
			TextField facilityDepartmentField = addCustomField(HEALTH_FACILITY_DEPARTMENT, String.class, TextField.class);
			String healthFacilityDepartment = caze.getDepartment();
			facilityDepartmentField.setValue(healthFacilityDepartment);
			facilityDepartmentField.setReadOnly(true);
		}

		final NullableOptionGroup admittedToHealthFacilityField = addField(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, NullableOptionGroup.class);
		final NullableOptionGroup currentlyHospitalizedField = addField(HospitalizationDto.CURRENTLY_HOSPITALIZED, NullableOptionGroup.class);
		final DateField admissionDateField = addField(HospitalizationDto.ADMISSION_DATE, DateField.class);
		final DateField dischargeDateField = addDateField(HospitalizationDto.DISCHARGE_DATE, DateField.class, 7);

		// RSV-specific fields
		intensiveCareUnit = addField(HospitalizationDto.INTENSIVE_CARE_UNIT, NullableOptionGroup.class);
		intensiveCareUnitStart = addField(HospitalizationDto.INTENSIVE_CARE_UNIT_START, DateField.class);
		intensiveCareUnitStart.setVisible(false);
		intensiveCareUnitEnd = addField(HospitalizationDto.INTENSIVE_CARE_UNIT_END, DateField.class);
		intensiveCareUnitEnd.setVisible(false);
		DateComparisonValidator.addStartEndValidators(intensiveCareUnitStart, intensiveCareUnitEnd, true);

		if (List.of(Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.GIARDIASIS, Disease.CRYPTOSPORIDIOSIS).contains(caze.getDisease())) {
			FieldHelper.setVisibleWhen(
				intensiveCareUnit,
				Arrays.asList(intensiveCareUnitStart, intensiveCareUnitEnd),
				Arrays.asList(YesNoUnknown.YES),
				true);
		} else {
			intensiveCareUnit.setVisible(false);
			intensiveCareUnitStart.setVisible(false);
			intensiveCareUnitEnd.setVisible(false);
		}

		// RSV-specific fields
		final TextField icuLengthOfStayField = addField(HospitalizationDto.ICU_LENGTH_OF_STAY, TextField.class);
		final NullableOptionGroup oxygenPrescribedField = addField(HospitalizationDto.OXYGEN_PRESCRIBED, NullableOptionGroup.class);
		final NullableOptionGroup stillHospitalizedField = addField(HospitalizationDto.STILL_HOSPITALIZED, NullableOptionGroup.class);

		if (!List.of(Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.GIARDIASIS, Disease.CRYPTOSPORIDIOSIS).contains(caze.getDisease())) {
			icuLengthOfStayField.setVisible(false);
			oxygenPrescribedField.setVisible(false);
			stillHospitalizedField.setVisible(false);
		}

		final Field isolationDateField = addField(HospitalizationDto.ISOLATION_DATE);
		final TextArea descriptionField = addField(HospitalizationDto.DESCRIPTION, TextArea.class);
		descriptionField.setRows(4);
		final NullableOptionGroup isolatedField = addField(HospitalizationDto.ISOLATED, NullableOptionGroup.class);
		final NullableOptionGroup leftAgainstAdviceField = addField(HospitalizationDto.LEFT_AGAINST_ADVICE, NullableOptionGroup.class);

		final ComboBox hospitalizationReason = addField(HospitalizationDto.HOSPITALIZATION_REASON);
		final TextField otherHospitalizationReason = addField(HospitalizationDto.OTHER_HOSPITALIZATION_REASON, TextField.class);

		// Default hospitalization reason to REPORTED_DISEASE for RSV cases
		if (caze.getDisease() == Disease.RESPIRATORY_SYNCYTIAL_VIRUS && hospitalizationReason.getValue() == null) {
			hospitalizationReason.setValue(HospitalizationReasonType.REPORTED_DISEASE);
		}

		// Add listener to trigger RSV hospitalization reason defaulting when admitted to health facility
		admittedToHealthFacilityField.addValueChangeListener(event -> {
			final Object eventValue = event.getProperty().getValue();
			final boolean isAdmitted =
				eventValue != null && eventValue instanceof Collection<?> ? ((Collection<?>) eventValue).contains(YesNoUnknown.YES) : false;
			if (caze.getDisease() == Disease.RESPIRATORY_SYNCYTIAL_VIRUS && isAdmitted && hospitalizationReason.getValue() == null) {
				hospitalizationReason.setValue(HospitalizationReasonType.REPORTED_DISEASE);
			}
		});

		NullableOptionGroup hospitalizedPreviouslyField = addField(HospitalizationDto.HOSPITALIZED_PREVIOUSLY, NullableOptionGroup.class);
		CssStyles.style(hospitalizedPreviouslyField, CssStyles.ERROR_COLOR_PRIMARY);
		PreviousHospitalizationsField previousHospitalizationsField =
			addField(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS, PreviousHospitalizationsField.class);
		final TextField durationOfHospitalization = addField(HospitalizationDto.DURATION_OF_HOSPITALIZATION, TextField.class);
		durationOfHospitalization.setVisible(false);

		FieldHelper.setEnabledWhen(
			admittedToHealthFacilityField,
			Arrays.asList(YesNoUnknown.YES, YesNoUnknown.NO, YesNoUnknown.UNKNOWN),
			Arrays.asList(
				facilityField,
				admissionDateField,
				dischargeDateField,
				intensiveCareUnit,
				intensiveCareUnitStart,
				intensiveCareUnitEnd,
				oxygenPrescribedField,
				stillHospitalizedField,
				isolationDateField,
				descriptionField,
				isolatedField,
				leftAgainstAdviceField,
				hospitalizationReason,
				icuLengthOfStayField,
				durationOfHospitalization,
				otherHospitalizationReason),
			false);
		FieldHelper.setEnabledWhen(
			currentlyHospitalizedField,
			Arrays.asList(YesNoUnknown.YES),
			Arrays.asList(
				admissionDateField,
				dischargeDateField,
				leftAgainstAdviceField,
				durationOfHospitalization,
				hospitalizationReason,
				otherHospitalizationReason),
			true);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		if (List.of(Disease.GIARDIASIS, Disease.CRYPTOSPORIDIOSIS).contains(caze.getDisease())) {
			FieldHelper
				.setRequiredWhenNotNull(getFieldGroup(), HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, HospitalizationDto.HOSPITALIZATION_REASON);
			durationOfHospitalization.setVisible(true);
		}

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

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			HospitalizationDto.OTHER_HOSPITALIZATION_REASON,
			HospitalizationDto.HOSPITALIZATION_REASON,
			Collections.singletonList(HospitalizationReasonType.OTHER),
			true);

		// Validations
		// Add a visual-only validator to check if symptomonsetdate<admissiondate, as saving should be possible either way
		admissionDateField.addValueChangeListener(event -> {
			if (caze.getSymptoms().getOnsetDate() != null
				&& DateComparator.getDateInstance().compare(admissionDateField.getValue(), caze.getSymptoms().getOnsetDate()) < 0) {
				admissionDateField.setComponentError(new ErrorMessage() {

					@Override
					public ErrorLevel getErrorLevel() {
						return ErrorLevel.INFO;
					}

					@Override
					public String getFormattedHtmlMessage() {
						return I18nProperties.getValidationError(
							Validations.afterDateSoft,
							admissionDateField.getCaption(),
							I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
					}
				});
			} else {
				// remove all invalidity-indicators and re-evaluate field
				admissionDateField.setComponentError(null);
				admissionDateField.markAsDirty();
			}
			// re-evaluate validity of dischargeDate (necessary because discharge has to be after admission)
			dischargeDateField.markAsDirty();
		});
		admissionDateField.addValidator(
			new DateComparisonValidator(
				admissionDateField,
				dischargeDateField,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, admissionDateField.getCaption(), dischargeDateField.getCaption())));
		dischargeDateField.addValidator(
			new DateComparisonValidator(
				dischargeDateField,
				admissionDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, dischargeDateField.getCaption(), admissionDateField.getCaption())));
		dischargeDateField.addValueChangeListener(event -> admissionDateField.markAsDirty()); // re-evaluate admission date for consistent validation of all fields

		// RSV specific logic
		if (List.of(Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.CRYPTOSPORIDIOSIS, Disease.GIARDIASIS).contains(caze.getDisease())) {
			intensiveCareUnitStart.addValidator(
				new DateComparisonValidator(
					intensiveCareUnitStart,
					admissionDateField,
					false,
					false,
					I18nProperties.getValidationError(Validations.afterDate, intensiveCareUnitStart.getCaption(), admissionDateField.getCaption())));
			intensiveCareUnitStart.addValidator(
				new DateComparisonValidator(
					intensiveCareUnitStart,
					intensiveCareUnitEnd,
					true,
					false,
					I18nProperties
						.getValidationError(Validations.beforeDate, intensiveCareUnitStart.getCaption(), intensiveCareUnitEnd.getCaption())));
			intensiveCareUnitEnd.addValidator(
				new DateComparisonValidator(
					intensiveCareUnitEnd,
					intensiveCareUnitStart,
					false,
					false,
					I18nProperties
						.getValidationError(Validations.afterDate, intensiveCareUnitEnd.getCaption(), intensiveCareUnitStart.getCaption())));
			intensiveCareUnitEnd.addValidator(
				new DateComparisonValidator(
					intensiveCareUnitEnd,
					dischargeDateField,
					true,
					false,
					I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitEnd.getCaption(), dischargeDateField.getCaption())));
			intensiveCareUnitStart.addValueChangeListener(event -> {
				intensiveCareUnitEnd.markAsDirty();
				boolean hasIcuStartDate = intensiveCareUnitStart.getValue() != null;
				if (hasIcuStartDate && intensiveCareUnitEnd.getValue() != null) {
					icuLengthOfStayField.setValue("" + DateHelper.getDaysBetween(intensiveCareUnitStart.getValue(), intensiveCareUnitEnd.getValue()));
				}
			});
			intensiveCareUnitEnd.addValueChangeListener(event -> {
				intensiveCareUnitStart.markAsDirty();
				boolean hasIcuEndDate = intensiveCareUnitEnd.getValue() != null;
				if (hasIcuEndDate && intensiveCareUnitStart.getValue() != null) {
					icuLengthOfStayField.setValue("" + DateHelper.getDaysBetween(intensiveCareUnitStart.getValue(), intensiveCareUnitEnd.getValue()));
				}
			});
			// RSV-specific conditional visibility logic
			// stillHospitalized should not be visible/writable if discharge date is filled

			dischargeDateField.addValueChangeListener(event -> {
				boolean hasDischargeDate = dischargeDateField.getValue() != null;
				stillHospitalizedField.setVisible(!hasDischargeDate);
				stillHospitalizedField.setEnabled(!hasDischargeDate);
				if (hasDischargeDate) {
					stillHospitalizedField.setValue(null);
				}
				// If the discharge date is set, the duration of hospitalization must be calculated based on admission and discharge date
				if (hasDischargeDate && admissionDateField.getValue() != null) {
					durationOfHospitalization.setValue("" + DateHelper.getDaysBetween(admissionDateField.getValue(), dischargeDateField.getValue()));
				}
			});
			// Show icuLengthOfStay when ICU dates are not available but survey has length information
			FieldHelper.setVisibleWhen(intensiveCareUnit, Collections.singletonList(icuLengthOfStayField), Arrays.asList(YesNoUnknown.YES), true);
		}

		hospitalizedPreviouslyField.addValueChangeListener(e -> updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField));
		previousHospitalizationsField.addValueChangeListener(e -> updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField));
	}

	private void updatePrevHospHint(NullableOptionGroup hospitalizedPreviouslyField, PreviousHospitalizationsField previousHospitalizationsField) {

		YesNoUnknown value = (YesNoUnknown) hospitalizedPreviouslyField.getNullableValue();
		Collection<PreviousHospitalizationDto> previousHospitalizations = previousHospitalizationsField.getValue();
		if (UiUtil.permitted(UserRight.CASE_EDIT)
			&& value == YesNoUnknown.YES
			&& (previousHospitalizations == null || previousHospitalizations.size() == 0)) {
			hospitalizedPreviouslyField.setComponentError(new UserError(I18nProperties.getValidationError(Validations.softAddEntryToList)));
		} else {
			hospitalizedPreviouslyField.setComponentError(null);
		}
		if (Objects.nonNull(previousHospitalizationsField.getValue())) {
			hospitalizedPreviouslyField.setEnabled(previousHospitalizationsField.isEmpty());
		} else {
			hospitalizedPreviouslyField.setEnabled(true);
		}

	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	private String getHospitalName(FacilityReferenceDto healthFacility, CaseDataDto caze) {
		final boolean noneFacility = healthFacility == null || healthFacility.getUuid().equalsIgnoreCase(FacilityDto.NONE_FACILITY_UUID);
		if (noneFacility || !FacilityType.HOSPITAL.equals(caze.getFacilityType())) {
			return null;
		}
		StringBuilder hospitalName = new StringBuilder();
		hospitalName.append(healthFacility.buildCaption());
		if (caze.getHealthFacilityDetails() != null && caze.getHealthFacilityDetails().trim().length() > 0) {
			hospitalName.append(String.format(HOSPITAL_NAME_DETAIL, caze.getHealthFacilityDetails()));
		}
		return hospitalName.toString();
	}
}
