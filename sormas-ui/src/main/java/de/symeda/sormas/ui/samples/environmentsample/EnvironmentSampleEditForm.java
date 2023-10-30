/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.samples.environmentsample;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.H4;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.environment.environmentsample.WeatherCondition;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CheckBoxTree;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class EnvironmentSampleEditForm extends AbstractEditForm<EnvironmentSampleDto> {

	private static final String LABORATORY_SAMPLE_HEADING_LOC = "labeSampleHeadingLoc";
	private static final String REPORT_INFO_LOC = "reportInfoLoc";
	private static final String SAMPLE_MEASUREMENTS_HEADING_LOC = "sampleMeasurementsHeadingLoc";
	private static final String LOCATION_HEADING_LOC = "locationHeadingLoc";
	private static final String SAMPLE_MANAGEMENT_HEADING_LOC = "sampleManagementHeadingLoc";
	private static final String REQUESTED_PATHOGENS_SUBHEADING_LOC = "requestedPathogensSubheadingLoc";
	private static final String HTML_LAYOUT = loc(LABORATORY_SAMPLE_HEADING_LOC)
		+ fluidRowLocs(3, EnvironmentSampleDto.UUID, 3, EnvironmentSampleDto.ENVIRONMENT, 6, REPORT_INFO_LOC)
		+ fluidRowLocs(EnvironmentSampleDto.SAMPLE_DATE_TIME, "")
		+ fluidRowLocs(EnvironmentSampleDto.SAMPLE_MATERIAL, EnvironmentSampleDto.OTHER_SAMPLE_MATERIAL)
		+ fluidRowLocs(EnvironmentSampleDto.FIELD_SAMPLE_ID, "")
		+ loc(SAMPLE_MEASUREMENTS_HEADING_LOC)
		+ fluidRowLocs(EnvironmentSampleDto.SAMPLE_VOLUME, EnvironmentSampleDto.TURBIDITY)
		+ fluidRowLocs(EnvironmentSampleDto.SAMPLE_TEMPERATURE, EnvironmentSampleDto.CHLORINE_RESIDUALS)
		+ fluidRowLocs(EnvironmentSampleDto.PH_VALUE, "")
		+ fluidRowLocs(EnvironmentSampleDto.WEATHER_CONDITIONS, EnvironmentSampleDto.HEAVY_RAIN)
		+ loc(LOCATION_HEADING_LOC)
		+ loc(EnvironmentSampleDto.LOCATION)
		+ loc(SAMPLE_MANAGEMENT_HEADING_LOC)
		+ fluidRowLocs(EnvironmentSampleDto.LABORATORY, EnvironmentSampleDto.LABORATORY_DETAILS)
		+ fluidRowLocs(REQUESTED_PATHOGENS_SUBHEADING_LOC)
		+ fluidRowLocs(EnvironmentSampleDto.REQUESTED_PATHOGEN_TESTS)
		+ fluidRowLocs(EnvironmentSampleDto.OTHER_REQUESTED_PATHOGEN_TESTS)
		+ fluidRowLocs(EnvironmentSampleDto.DISPATCHED, "")
		+ fluidRowLocs(EnvironmentSampleDto.DISPATCH_DATE, EnvironmentSampleDto.DISPATCH_DETAILS)
		+ fluidRowLocs(EnvironmentSampleDto.RECEIVED, "")
		+ fluidRowLocs(EnvironmentSampleDto.RECEIVAL_DATE, EnvironmentSampleDto.LAB_SAMPLE_ID)
		+ fluidRowLocs(EnvironmentSampleDto.SPECIMEN_CONDITION, "")
		+ fluidRowLocs(EnvironmentSampleDto.GENERAL_COMMENT)
		+ fluidRowLocs(CaseDataDto.DELETION_REASON)
		+ fluidRowLocs(CaseDataDto.OTHER_DELETION_REASON);

	private static final List<String> RECEIVAL_FIELDS = Arrays.asList(
		EnvironmentSampleDto.RECEIVED,
		EnvironmentSampleDto.RECEIVAL_DATE,
		EnvironmentSampleDto.LAB_SAMPLE_ID,
		EnvironmentSampleDto.SPECIMEN_CONDITION);

	private CheckBoxTree<WeatherCondition> weatherConditionCheckBoxTree;

	private final boolean isCreate;

	public EnvironmentSampleEditForm(boolean isPseudonymized, boolean isCreate) {
		super(
			EnvironmentSampleDto.class,
			EnvironmentSampleDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(isPseudonymized));
		this.isCreate = isCreate;
		addFields();
		addValueChangeListener(e -> defaultValueChangeListener((EnvironmentSampleDto) e.getProperty().getValue()));
	}

	@NotNull
	private static Label buildHeadingLabel(String stringProperty) {
		Label labSampleHeadingLabel = new Label(I18nProperties.getString(stringProperty));
		labSampleHeadingLabel.addStyleName(H3);
		return labSampleHeadingLabel;
	}

	@NotNull
	private static Label buildSubHeadingLabel(String stringProperty) {
		Label labSampleHeadingLabel = new Label(I18nProperties.getString(stringProperty));
		labSampleHeadingLabel.addStyleName(H4);
		return labSampleHeadingLabel;
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		getContent().addComponent(buildHeadingLabel(Strings.headingLaboratoryEnvironmentSample), LABORATORY_SAMPLE_HEADING_LOC);
		addField(EnvironmentSampleDto.UUID).setReadOnly(true);
		addField(EnvironmentSampleDto.ENVIRONMENT).setReadOnly(true);

		addField(EnvironmentSampleDto.SAMPLE_DATE_TIME).setRequired(true);
		addField(EnvironmentSampleDto.SAMPLE_MATERIAL).setRequired(true);
		addField(EnvironmentSampleDto.OTHER_SAMPLE_MATERIAL);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EnvironmentSampleDto.OTHER_SAMPLE_MATERIAL,
			EnvironmentSampleDto.SAMPLE_MATERIAL,
			EnvironmentSampleMaterial.OTHER,
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			EnvironmentSampleDto.SAMPLE_MATERIAL,
			Collections.singletonList(EnvironmentSampleDto.OTHER_SAMPLE_MATERIAL),
			Collections.singletonList(EnvironmentSampleMaterial.OTHER));
		addField(EnvironmentSampleDto.FIELD_SAMPLE_ID);

		getContent().addComponent(buildHeadingLabel(Strings.headingEnvironmentSampleMeasurements), SAMPLE_MEASUREMENTS_HEADING_LOC);

		TextField sampleVolumeField = addField(EnvironmentSampleDto.SAMPLE_VOLUME);
		sampleVolumeField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, sampleVolumeField.getCaption()));

		TextField turbidityField = addField(EnvironmentSampleDto.TURBIDITY);
		turbidityField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, turbidityField.getCaption()));

		TextField temperatureField = addField(EnvironmentSampleDto.SAMPLE_TEMPERATURE);
		temperatureField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, temperatureField.getCaption()));

		TextField chlorineResidualsField = addField(EnvironmentSampleDto.CHLORINE_RESIDUALS);
		chlorineResidualsField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, chlorineResidualsField.getCaption()));

		TextField phValueField = addField(EnvironmentSampleDto.PH_VALUE);
		phValueField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, phValueField.getCaption()));

		final Component weatherConditionsLayout = buildWeatherConditionComponent();
		getContent().addComponent(weatherConditionsLayout, EnvironmentSampleDto.WEATHER_CONDITIONS);

		addField(EnvironmentSampleDto.HEAVY_RAIN, NullableOptionGroup.class);

		getContent().addComponent(buildHeadingLabel(Strings.headingEnvironmentSampleLocation), LOCATION_HEADING_LOC);
		LocationEditForm locationForm = addField(EnvironmentSampleDto.LOCATION, LocationEditForm.class);
		locationForm.setCaption(null);
		locationForm.setDistrictRequired();
		locationForm.setGpsCoordinatesRequired();

		getContent().addComponent(buildHeadingLabel(Strings.headingEnvironmentSampleManagement), SAMPLE_MANAGEMENT_HEADING_LOC);

		ComboBox laboratoryField = addField(EnvironmentSampleDto.LABORATORY);
		laboratoryField.setRequired(true);
		FieldHelper.updateItems(laboratoryField, FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));

		Field labDetailsField = addField(EnvironmentSampleDto.LABORATORY_DETAILS);
		labDetailsField.setVisible(false);
		laboratoryField.addValueChangeListener(event -> {
			if (event.getProperty().getValue() != null
				&& ((FacilityReferenceDto) event.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
				labDetailsField.setVisible(true);
			} else {
				labDetailsField.setVisible(false);
				labDetailsField.clear();
			}
		});

		getContent().addComponent(buildSubHeadingLabel(Strings.headingEnvironmentSampleRequestedPathogenTests), REQUESTED_PATHOGENS_SUBHEADING_LOC);
		OptionGroup requestedPathogenTestsField = addField(EnvironmentSampleDto.REQUESTED_PATHOGEN_TESTS, OptionGroup.class);
		CssStyles.style(requestedPathogenTestsField, CssStyles.OPTIONGROUP_CHECKBOXES_HORIZONTAL);
		requestedPathogenTestsField.setMultiSelect(true);
		List<CustomizableEnum> pathogens = FacadeProvider.getCustomizableEnumFacade()
			.getEnumValues(CustomizableEnumType.PATHOGEN, null)
			.stream()
			// Remove default "OTHER" pathogen because it's covered by a separate field
			.filter(pathogen -> !pathogen.getValue().equals("OTHER"))
			.collect(Collectors.toList());
		requestedPathogenTestsField.addItems(pathogens);
		requestedPathogenTestsField.setCaption(null);

		addField(EnvironmentSampleDto.OTHER_REQUESTED_PATHOGEN_TESTS);

		Field dispatchedField = addField(EnvironmentSampleDto.DISPATCHED);
		dispatchedField.addStyleName(CssStyles.VSPACE_3);

		addField(EnvironmentSampleDto.DISPATCH_DATE);
		addField(EnvironmentSampleDto.DISPATCH_DETAILS);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EnvironmentSampleDto.DISPATCH_DATE, EnvironmentSampleDto.DISPATCH_DETAILS),
			EnvironmentSampleDto.DISPATCHED,
			true,
			true);

		Field receivedField = addField(EnvironmentSampleDto.RECEIVED);
		receivedField.addStyleName(CssStyles.VSPACE_3);

		addField(EnvironmentSampleDto.RECEIVAL_DATE);
		addField(EnvironmentSampleDto.LAB_SAMPLE_ID);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EnvironmentSampleDto.RECEIVAL_DATE, EnvironmentSampleDto.LAB_SAMPLE_ID),
			EnvironmentSampleDto.RECEIVED,
			true,
			true);

		addField(EnvironmentSampleDto.SPECIMEN_CONDITION);

		addField(EnvironmentSampleDto.GENERAL_COMMENT, TextArea.class).setRows(3);

		addField(EventDto.DELETION_REASON);
		addField(EventDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, EventDto.DELETION_REASON, EventDto.OTHER_DELETION_REASON);

		initializeAccessAndAllowedAccesses();
	}

	private void disableFieldsBasedOnRights(EnvironmentSampleDto sample) {
		UserProvider currentUserProvider = UserProvider.getCurrent();
		boolean hasEditReceivalRight = currentUserProvider.hasUserRight(UserRight.ENVIRONMENT_SAMPLE_EDIT_RECEIVAL);
		boolean hasEditDispatchRight = currentUserProvider.hasUserRight(UserRight.ENVIRONMENT_SAMPLE_EDIT_DISPATCH);
		boolean isOwner = isCreate || DataHelper.isSame(sample.getReportingUser(), currentUserProvider.getUser());
		boolean canEditDispatchField = isCreate || (isOwner && hasEditDispatchRight);

		getFieldGroup().getFields().forEach(f -> {
			if (f.isEnabled()) {
				String propertyId = f.getId();
				boolean isReceivalField = RECEIVAL_FIELDS.contains(propertyId);
				boolean canEdit =
					EnvironmentSampleDto.GENERAL_COMMENT.equals(propertyId) || (isReceivalField ? hasEditReceivalRight : canEditDispatchField);

				if (!canEdit) {
					f.setEnabled(false);
				}
			}
		});
		if (!(canEditDispatchField)) {
			weatherConditionCheckBoxTree.setEnabled(false);
		}
	}

	@Override
	public void commit() throws SourceException, Validator.InvalidValueException {
		super.commit();
		getValue().setWeatherConditions(weatherConditionCheckBoxTree.getValues());
	}

	@Override
	public void setValue(EnvironmentSampleDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		weatherConditionCheckBoxTree.setValues(newFieldValue.getWeatherConditions());
		weatherConditionCheckBoxTree.initCheckboxes();

		disableFieldsBasedOnRights(newFieldValue);
	}

	@NotNull
	private Component buildWeatherConditionComponent() {
		final VerticalLayout weatherConditionsLayout = new VerticalLayout();
		CssStyles.style(weatherConditionsLayout, CssStyles.VSPACE_3);
		Label weatherConditionsLabel =
			new Label(I18nProperties.getPrefixCaption(EnvironmentSampleDto.I18N_PREFIX, EnvironmentSampleDto.WEATHER_CONDITIONS));
		CssStyles.style(weatherConditionsLabel, CssStyles.VAADIN_CAPTION);
		weatherConditionsLayout.addComponent(weatherConditionsLabel);
		weatherConditionCheckBoxTree = new CheckBoxTree<>(
			Arrays.stream(WeatherCondition.values()).map(c -> new CheckBoxTree.CheckBoxElement<>(null, c)).collect(Collectors.toList()));
		weatherConditionCheckBoxTree.setValues(new EnumMap<>(WeatherCondition.class));
		weatherConditionsLayout.addComponent(weatherConditionCheckBoxTree);
		return weatherConditionsLayout;
	}

	protected void defaultValueChangeListener(EnvironmentSampleDto sample) {
		StringBuilder reportInfoText = new StringBuilder().append(I18nProperties.getString(Strings.reportedOn))
			.append(" ")
			.append(DateFormatHelper.formatLocalDateTime(sample.getReportDate()));
		UserReferenceDto reportingUser = sample.getReportingUser();
		if (reportingUser != null) {
			reportInfoText.append(" ").append(I18nProperties.getString(Strings.by)).append(" ").append(reportingUser.buildCaption());
		}

		Label reportInfoLabel = new Label(reportInfoText.toString());
		reportInfoLabel.setEnabled(false);
		getContent().addComponent(reportInfoLabel, REPORT_INFO_LOC);
	}
}
