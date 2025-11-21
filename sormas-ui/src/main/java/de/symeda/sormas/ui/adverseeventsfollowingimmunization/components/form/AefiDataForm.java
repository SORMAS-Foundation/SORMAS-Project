/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.form;

import static de.symeda.sormas.ui.utils.CssStyles.FORCE_CAPTION;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.H4;
import static de.symeda.sormas.ui.utils.LayoutUtil.divCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.function.Consumer;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiOutcome;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.SeriousAefiReason;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.fields.vaccines.AefiVaccinationsField;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.UserField;

@SuppressWarnings("deprecation")
public class AefiDataForm extends AbstractEditForm<AefiDto> {

	private static final String ASSIGN_NEW_AEFI_ID_LOC = "assignNewAefiIdLoc";
	private static final String REPORTING_INFORMATION_HEADING_LOC = "reportingInformationHeadingLoc";
	private static final String PATIENTS_IDENTIFICATION_HEADING_LOC = "patientsIdentificationHeadingLoc";
	private static final String PATIENTS_IDENTIFICATION_AGE_AT_ONSET = "patientsIdentificationAgeAtOnsetHeadingLoc";
	private static final String VACCINATIONS_HEADING_LOC = "vaccinationsHeadingLoc";
	private static final String ADVERSE_EVENTS_HEADING_LOC = "adverseEventsHeadingLoc";
	private static final String FIRST_DECISION_LEVEL_HEADING_LOC = "firstDecisionLevelHeadingLoc";
	private static final String NATIONAL_DECISION_LEVEL_HEADING_LOC = "nationalDecisionLevelHeadingLoc";
	private static final String REPORTERS_INFORMATION_HEADING_LOC = "reportersInformationHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			divCss(CssStyles.VIEW_SECTION_MARGIN_TOP_4_MARGIN_X_4,
			loc(REPORTING_INFORMATION_HEADING_LOC)
					+ fluidRowLocs(4, AefiDto.UUID, 4, AefiDto.REPORT_DATE, 3, AefiDto.REPORTING_USER)
					+ fluidRowLocs(4, AefiDto.RESPONSIBLE_REGION, 4, AefiDto.RESPONSIBLE_DISTRICT, 3, AefiDto.RESPONSIBLE_COMMUNITY)
					+ fluidRowLocs(4, AefiDto.REPORTING_ID_NUMBER, 3, ASSIGN_NEW_AEFI_ID_LOC)
			)
			+ divCss(CssStyles.VIEW_SECTION_MARGIN_X_4 + " " + CssStyles.VSPACE_TOP_3,
			loc(PATIENTS_IDENTIFICATION_HEADING_LOC)
					+ fluidRowLocs(AefiDto.PREGNANT, AefiDto.TRIMESTER, AefiDto.LACTATING)
					+ loc(PATIENTS_IDENTIFICATION_AGE_AT_ONSET)
					+ fluidRow(
							fluidRowLocs(AefiDto.ONSET_AGE_YEARS, AefiDto.ONSET_AGE_MONTHS, AefiDto.ONSET_AGE_DAYS),
							fluidRowLocs(6, AefiDto.AGE_GROUP)
					)
			)
			+ divCss(CssStyles.VIEW_SECTION_MARGIN_X_4 + " " + CssStyles.VSPACE_TOP_3,
			loc(VACCINATIONS_HEADING_LOC)
					+ fluidRowLocs(AefiDto.VACCINATIONS)
			)
			+ divCss(CssStyles.VIEW_SECTION_MARGIN_X_4 + " " + CssStyles.VSPACE_TOP_3,
			loc(ADVERSE_EVENTS_HEADING_LOC)
					+ fluidRowLocs(AefiDto.ADVERSE_EVENTS)
					+ fluidRowLocs(6, AefiDto.START_DATE_TIME)
					+ fluidRowLocs(AefiDto.AEFI_DESCRIPTION)
					+ fluidRowLocs(4, AefiDto.SERIOUS, 4, AefiDto.SERIOUS_REASON, 4, AefiDto.SERIOUS_REASON_DETAILS)
					+ fluidRowLocs(AefiDto.OUTCOME)
					+ fluidRowLocs(4, AefiDto.DEATH_DATE, 4, AefiDto.AUTOPSY_DONE)
					+ fluidRowLocs(AefiDto.PAST_MEDICAL_HISTORY)
			)
			+ divCss(CssStyles.VIEW_SECTION_MARGIN_X_4 + " " + CssStyles.VSPACE_TOP_3,
			loc(FIRST_DECISION_LEVEL_HEADING_LOC)
					+ fluidRowLocs(4, AefiDto.INVESTIGATION_NEEDED, 4, AefiDto.INVESTIGATION_PLANNED_DATE)
			)
			+ divCss(CssStyles.VIEW_SECTION_MARGIN_X_4 + " " + CssStyles.VSPACE_2 + " " + CssStyles.VSPACE_TOP_3,
			loc(NATIONAL_DECISION_LEVEL_HEADING_LOC)
					+ fluidRowLocs(4, AefiDto.RECEIVED_AT_NATIONAL_LEVEL_DATE, 4, AefiDto.WORLD_WIDE_ID)
					+ fluidRowLocs(AefiDto.NATIONAL_LEVEL_COMMENT)
			);
	//@formatter:on

	private boolean isCreateAction;
	private final Consumer<Runnable> actionCallback;
	private TextField responsibleRegion;
	private TextField responsibleDistrict;
	private TextField responsibleCommunity;
	private AefiVaccinationsField vaccinationsField;

	public AefiDataForm(boolean isCreateAction, boolean isPseudonymized, boolean inJurisdiction, Consumer<Runnable> actionCallback) {
		super(
			AefiDto.class,
			AefiDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized));

		this.isCreateAction = isCreateAction;
		this.actionCallback = actionCallback;

		if (isCreateAction) {
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

		Label reportingInformationHeadingLabel = new Label(I18nProperties.getString(Strings.headingAefiReportingInformation));
		reportingInformationHeadingLabel.addStyleName(H3);
		getContent().addComponent(reportingInformationHeadingLabel, REPORTING_INFORMATION_HEADING_LOC);

		if (isCreateAction) {
			addField(AefiDto.UUID, PasswordField.class);
		} else {
			addField(AefiDto.UUID);
		}
		addField(AefiDto.REPORT_DATE, DateField.class);
		addField(AefiDto.REPORTING_USER, UserField.class);

		responsibleRegion = new TextField(I18nProperties.getPrefixCaption(AefiDto.I18N_PREFIX, AefiDto.RESPONSIBLE_REGION));
		getContent().addComponent(responsibleRegion, AefiDto.RESPONSIBLE_REGION);
		responsibleDistrict = new TextField(I18nProperties.getPrefixCaption(AefiDto.I18N_PREFIX, AefiDto.RESPONSIBLE_DISTRICT));
		getContent().addComponent(responsibleDistrict, AefiDto.RESPONSIBLE_DISTRICT);
		responsibleCommunity = new TextField(I18nProperties.getPrefixCaption(AefiDto.I18N_PREFIX, AefiDto.RESPONSIBLE_COMMUNITY));
		getContent().addComponent(responsibleCommunity, AefiDto.RESPONSIBLE_COMMUNITY);

		TextField reportIdField = addField(AefiDto.REPORTING_ID_NUMBER, TextField.class);
		/*
		 * reportIdField.setInvalidCommitted(true);
		 * reportIdField.setMaxLength(24);
		 * style(reportIdField, ERROR_COLOR_PRIMARY);
		 */

		// Button to automatically assign a new reporting ID
		Button assignNewReportingIdNumberButton = ButtonHelper.createButton(Captions.actionAefiAssignNewReportingIdNumber, e -> {
		}, ValoTheme.BUTTON_DANGER, FORCE_CAPTION);

		getContent().addComponent(assignNewReportingIdNumberButton, ASSIGN_NEW_AEFI_ID_LOC);
		assignNewReportingIdNumberButton.setVisible(false);

		Label patientsIdentificationHeadingLabel = new Label(I18nProperties.getString(Strings.headingAefiPatientsIdentification));
		patientsIdentificationHeadingLabel.addStyleName(H3);
		getContent().addComponent(patientsIdentificationHeadingLabel, PATIENTS_IDENTIFICATION_HEADING_LOC);

		addField(AefiDto.PREGNANT, NullableOptionGroup.class);
		addField(AefiDto.TRIMESTER, NullableOptionGroup.class);
		addField(AefiDto.LACTATING, NullableOptionGroup.class);

		Label patientsAgeAtOnsetHeadingLabel = new Label(I18nProperties.getString(Strings.headingAefiPatientsAgeAtOnset));
		patientsAgeAtOnsetHeadingLabel.addStyleName(H4);
		getContent().addComponent(patientsAgeAtOnsetHeadingLabel, PATIENTS_IDENTIFICATION_AGE_AT_ONSET);

		TextField onsetAgeYearsField = addField(AefiDto.ONSET_AGE_YEARS, TextField.class);
		onsetAgeYearsField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, onsetAgeYearsField.getCaption()));

		TextField onsetAgeMonthsField = addField(AefiDto.ONSET_AGE_MONTHS, TextField.class);
		onsetAgeMonthsField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, onsetAgeMonthsField.getCaption()));

		TextField onsetAgeDaysField = addField(AefiDto.ONSET_AGE_DAYS, TextField.class);
		onsetAgeDaysField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, onsetAgeDaysField.getCaption()));

		addField(AefiDto.AGE_GROUP, ComboBox.class);

		Label vaccinationsHeadingLabel = new Label(I18nProperties.getString(Strings.headingAefiVaccinations));
		vaccinationsHeadingLabel.addStyleName(H3);
		getContent().addComponent(vaccinationsHeadingLabel, VACCINATIONS_HEADING_LOC);

		vaccinationsField = addField(AefiDto.VACCINATIONS, AefiVaccinationsField.class);

		addField(AefiDto.ADVERSE_EVENTS, AdverseEventsForm.class).setCaption(null);

		final DateTimeField startDateField = addField(AefiDto.START_DATE_TIME, DateTimeField.class);
		startDateField.setInvalidCommitted(false);

		TextArea aefiDescriptionField = addField(AefiDto.AEFI_DESCRIPTION, TextArea.class);
		aefiDescriptionField.setRows(6);
		aefiDescriptionField.setDescription(
			I18nProperties.getPrefixDescription(AefiDto.I18N_PREFIX, AefiDto.AEFI_DESCRIPTION, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		addField(AefiDto.SERIOUS, NullableOptionGroup.class);
		addField(AefiDto.SERIOUS_REASON, ComboBox.class);
		addField(AefiDto.SERIOUS_REASON_DETAILS, TextField.class);
		addField(AefiDto.OUTCOME, NullableOptionGroup.class);
		addField(AefiDto.DEATH_DATE, DateField.class);
		addField(AefiDto.AUTOPSY_DONE, NullableOptionGroup.class);

		TextArea pastMedicalHistoryField = addField(AefiDto.PAST_MEDICAL_HISTORY, TextArea.class);
		pastMedicalHistoryField.setRows(6);
		pastMedicalHistoryField.setDescription(
			I18nProperties.getPrefixDescription(AefiDto.I18N_PREFIX, AefiDto.PAST_MEDICAL_HISTORY, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		Label firstDecisionLevelHeadingLabel = new Label(I18nProperties.getString(Strings.headingAefiFirstDecisionLevel));
		firstDecisionLevelHeadingLabel.addStyleName(H3);
		getContent().addComponent(firstDecisionLevelHeadingLabel, FIRST_DECISION_LEVEL_HEADING_LOC);

		addField(AefiDto.INVESTIGATION_NEEDED, NullableOptionGroup.class);
		addDateField(AefiDto.INVESTIGATION_PLANNED_DATE, DateField.class, -1);

		Label nationalDecisionLevelHeadingLabel = new Label(I18nProperties.getString(Strings.headingAefiNationalDecisionLevel));
		nationalDecisionLevelHeadingLabel.addStyleName(H3);
		getContent().addComponent(nationalDecisionLevelHeadingLabel, NATIONAL_DECISION_LEVEL_HEADING_LOC);

		addField(AefiDto.RECEIVED_AT_NATIONAL_LEVEL_DATE, DateField.class);
		addField(AefiDto.WORLD_WIDE_ID, TextField.class);

		TextArea nationalLevelCommentField = addField(AefiDto.NATIONAL_LEVEL_COMMENT, TextArea.class);
		nationalLevelCommentField.setRows(6);
		nationalLevelCommentField.setDescription(
			I18nProperties.getPrefixDescription(AefiDto.I18N_PREFIX, AefiDto.NATIONAL_LEVEL_COMMENT, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		//set visibility, read only and required status
		FieldHelper.setVisibleWhen(getFieldGroup(), AefiDto.TRIMESTER, AefiDto.PREGNANT, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), AefiDto.SERIOUS_REASON, AefiDto.SERIOUS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), AefiDto.SERIOUS_REASON_DETAILS, AefiDto.SERIOUS_REASON, Arrays.asList(SeriousAefiReason.OTHER), true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(AefiDto.DEATH_DATE, AefiDto.AUTOPSY_DONE),
			AefiDto.OUTCOME,
			Arrays.asList(AefiOutcome.DIED),
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), AefiDto.INVESTIGATION_PLANNED_DATE, AefiDto.INVESTIGATION_NEEDED, Arrays.asList(YesNoUnknown.YES), true);

		setReadOnly(true, AefiDto.UUID, AefiDto.REPORTING_USER);

		setRequired(true, AefiDto.REPORT_DATE, AefiDto.REPORTING_ID_NUMBER, AefiDto.SERIOUS, AefiDto.OUTCOME);
		FieldHelper.setRequiredWhen(getFieldGroup(), AefiDto.SERIOUS_REASON, Arrays.asList(AefiDto.SERIOUS_REASON), Arrays.asList(YesNoUnknown.YES));
	}

	@Override
	public void attach() {
		super.attach();

		AefiDto dataFormValue = getValue();

		ImmunizationDto immunizationDto = FacadeProvider.getImmunizationFacade().getByUuid(dataFormValue.getImmunization().getUuid());

		responsibleRegion.setValue(immunizationDto.getResponsibleRegion().getCaption());
		responsibleDistrict.setValue(immunizationDto.getResponsibleDistrict().getCaption());
		if (immunizationDto.getResponsibleCommunity() != null) {
			responsibleCommunity.setValue(immunizationDto.getResponsibleCommunity().getCaption());
		}

		responsibleRegion.setReadOnly(true);
		responsibleDistrict.setReadOnly(true);
		responsibleCommunity.setReadOnly(true);

		vaccinationsField.applyAefiReportContext(dataFormValue);
		if (dataFormValue.getPrimarySuspectVaccine() != null) {
			vaccinationsField.selectPrimarySuspectVaccination(dataFormValue.getPrimarySuspectVaccine());
		}
	}

	@Override
	public void setValue(AefiDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		getValue();

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		discard();
	}

	@Override
	public void discard() throws SourceException {
		super.discard();
	}
}
