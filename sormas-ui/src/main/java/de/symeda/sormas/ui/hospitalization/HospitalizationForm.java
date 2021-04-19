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

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLocCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.joda.time.DateTimeComparator;

import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ViewMode;

public class HospitalizationForm extends AbstractEditForm<HospitalizationDto> {

	private static final long serialVersionUID = 1L;

	private static final String HOSPITALIZATION_HEADING_LOC = "hospitalizationHeadingLoc";
	private static final String HEALTH_FACILITY = Captions.CaseHospitalization_healthFacility;
	private final CaseDataDto caze;
	private final ViewMode viewMode;

	private NullableOptionGroup intensiveCareUnit;
	private DateField intensiveCareUnitStart;
	private DateField intensiveCareUnitEnd;

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(HOSPITALIZATION_HEADING_LOC) +
			fluidRowLocs(HEALTH_FACILITY, HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY) +
			fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE, HospitalizationDto.LEFT_AGAINST_ADVICE, "") +
			fluidRowLocs(HospitalizationDto.HOSPITALIZATION_REASON, HospitalizationDto.OTHER_HOSPITALIZATION_REASON) +
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

	public HospitalizationForm(CaseDataDto caze, ViewMode viewMode, boolean isPseudonymized) {

		super(
			HospitalizationDto.class,
			HospitalizationDto.I18N_PREFIX,
			false,
			new FieldVisibilityCheckers().add(new OutbreakFieldVisibilityChecker(viewMode)),
			UiFieldAccessCheckers.forSensitiveData(isPseudonymized));
		this.caze = caze;
		this.viewMode = viewMode;
		addFields();
	}

	@Override
	protected void addFields() {

		if (caze == null || viewMode == null) {
			return;
		}

		Label hospitalizationHeadingLabel = new Label(I18nProperties.getString(Strings.headingHospitalization));
		hospitalizationHeadingLabel.addStyleName(H3);
		getContent().addComponent(hospitalizationHeadingLabel, HOSPITALIZATION_HEADING_LOC);

		TextField facilityField = addCustomField(HEALTH_FACILITY, FacilityReferenceDto.class, TextField.class);
		FacilityReferenceDto healthFacility = caze.getHealthFacility();
		final boolean noneFacility = healthFacility == null || healthFacility.getUuid().equalsIgnoreCase(FacilityDto.NONE_FACILITY_UUID);
		facilityField.setValue(noneFacility || !FacilityType.HOSPITAL.equals(caze.getFacilityType()) ? null : healthFacility.toString());
		facilityField.setReadOnly(true);

		final NullableOptionGroup admittedToHealthFacilityField = addField(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, NullableOptionGroup.class);
		final DateField admissionDateField = addField(HospitalizationDto.ADMISSION_DATE, DateField.class);
		final DateField dischargeDateField = addDateField(HospitalizationDto.DISCHARGE_DATE, DateField.class, 7);
		intensiveCareUnit = addField(HospitalizationDto.INTENSIVE_CARE_UNIT, NullableOptionGroup.class);
		intensiveCareUnitStart = addField(HospitalizationDto.INTENSIVE_CARE_UNIT_START, DateField.class);
		intensiveCareUnitStart.setVisible(false);
		intensiveCareUnitEnd = addField(HospitalizationDto.INTENSIVE_CARE_UNIT_END, DateField.class);
		intensiveCareUnitEnd.setVisible(false);
		FieldHelper
			.setVisibleWhen(intensiveCareUnit, Arrays.asList(intensiveCareUnitStart, intensiveCareUnitEnd), Arrays.asList(YesNoUnknown.YES), true);
		final Field isolationDateField = addField(HospitalizationDto.ISOLATION_DATE);
		final NullableOptionGroup isolatedField = addField(HospitalizationDto.ISOLATED, NullableOptionGroup.class);
		final NullableOptionGroup leftAgainstAdviceField = addField(HospitalizationDto.LEFT_AGAINST_ADVICE, NullableOptionGroup.class);

		final ComboBox hospitalizationReason = addField(HospitalizationDto.HOSPITALIZATION_REASON);
		final TextField otherHospitalizationReason = addField(HospitalizationDto.OTHER_HOSPITALIZATION_REASON, TextField.class);
		NullableOptionGroup hospitalizedPreviouslyField = addField(HospitalizationDto.HOSPITALIZED_PREVIOUSLY, NullableOptionGroup.class);
		CssStyles.style(hospitalizedPreviouslyField, CssStyles.ERROR_COLOR_PRIMARY);
		PreviousHospitalizationsField previousHospitalizationsField =
			addField(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS, PreviousHospitalizationsField.class);

		if (!FacilityType.HOSPITAL.equals(caze.getFacilityType())) {
			FieldHelper.setEnabled(
				false,
				facilityField,
				admittedToHealthFacilityField,
				admissionDateField,
				dischargeDateField,
				intensiveCareUnit,
				intensiveCareUnitStart,
				intensiveCareUnitEnd,
				isolationDateField,
				isolatedField,
				leftAgainstAdviceField,
				hospitalizationReason,
				otherHospitalizationReason);
		}

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

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
				&& DateTimeComparator.getDateOnlyInstance().compare(admissionDateField.getValue(), caze.getSymptoms().getOnsetDate()) < 0) {
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
				I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitStart.getCaption(), intensiveCareUnitEnd.getCaption())));
		intensiveCareUnitEnd.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitEnd,
				intensiveCareUnitStart,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, intensiveCareUnitEnd.getCaption(), intensiveCareUnitStart.getCaption())));
		intensiveCareUnitEnd.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitEnd,
				dischargeDateField,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitEnd.getCaption(), dischargeDateField.getCaption())));
		intensiveCareUnitStart.addValueChangeListener(event -> intensiveCareUnitEnd.markAsDirty());
		intensiveCareUnitEnd.addValueChangeListener(event -> intensiveCareUnitStart.markAsDirty());
		hospitalizedPreviouslyField.addValueChangeListener(e -> updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField));
		previousHospitalizationsField.addValueChangeListener(e -> updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField));
	}

	private void updatePrevHospHint(NullableOptionGroup hospitalizedPreviouslyField, PreviousHospitalizationsField previousHospitalizationsField) {

		YesNoUnknown value = (YesNoUnknown) hospitalizedPreviouslyField.getNullableValue();
		Collection<PreviousHospitalizationDto> previousHospitalizations = previousHospitalizationsField.getValue();
		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT)
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
}
