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

package de.symeda.sormas.ui.configuration.disease;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.RichTextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.form.FormSectionAccordion;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.components.CheckboxSet;
import de.symeda.sormas.ui.utils.components.DiseaseConfigurationAgeGroupComponent;

public class DiseaseConfigurationEditForm extends AbstractEditForm<DiseaseConfigurationDto> {

	private DiseaseConfigurationAgeGroupComponent ageGroupsComponent;
	//@formatter:off
	public static final String CASE_DEFINITION_HTML_LAYOUT = fluidRowLocs(DiseaseConfigurationDto.CASE_DEFINITION_TEXT);

	public static final String AGE_GROUP_HTML_LAYOUT = fluidRowLocs(DiseaseConfigurationDto.AGE_GROUPS);

	private static final String LOC_USUAL_RANGE_INFO = "locUsualRangeInfo";

	public static final String CLIMATE_HTML_LAYOUT = fluidRowLocsCss(CssStyles.VSPACE_3, DiseaseConfigurationDto.CLIMATE_SENSITIVE)
			+ fluidRowLocs(DiseaseConfigurationDto.USUAL_RANGE_EXCLUDED_YEARS)
			+ loc(LOC_USUAL_RANGE_INFO);

	public static final String GENERAL_HTML_LAYOUT = fluidRowLocsCss(CssStyles.VSPACE_3,DiseaseConfigurationDto.UUID)
			+ fluidRowLocsCss(CssStyles.VSPACE_3, DiseaseConfigurationDto.ACTIVE, DiseaseConfigurationDto.PRIMARY_DISEASE, DiseaseConfigurationDto.AGGREGATE_REPORTING_ENABLED)
			+ fluidRowLocsCss(CssStyles.VSPACE_3, DiseaseConfigurationDto.CASE_SURVEILLANCE_ENABLED)
			+ fluidRowLocs(DiseaseConfigurationDto.EXTENDED_CLASSIFICATION, DiseaseConfigurationDto.EXTENDED_CLASSIFICATION_MULTI, DiseaseConfigurationDto.AUTOMATIC_SAMPLE_ASSIGNMENT_THRESHOLD)
			+ fluidRowLocsCss(CssStyles.VSPACE_3, DiseaseConfigurationDto.FOLLOW_UP_ENABLED)
			+ fluidRowLocsCss(CssStyles.VSPACE_3,DiseaseConfigurationDto.FOLLOW_UP_DURATION,DiseaseConfigurationDto.CASE_FOLLOW_UP_DURATION,DiseaseConfigurationDto.EVENT_PARTICIPANT_FOLLOW_UP_DURATION)
			+ fluidRowLocsCss(CssStyles.VSPACE_5, DiseaseConfigurationDto.INCUBATION_PERIOD_ENABLED,DiseaseConfigurationDto.IS_CONTAGIOUS)
			+ fluidRowLocsCss(CssStyles.VSPACE_1, DiseaseConfigurationDto.MIN_INCUBATION_PERIOD, DiseaseConfigurationDto.MAX_INCUBATION_PERIOD,DiseaseConfigurationDto.MIN_CONTAGIOUS_PERIOD, DiseaseConfigurationDto.MAX_CONTAGIOUS_PERIOD)
			+ fluidRowLocs(DiseaseConfigurationDto.EXPOSURE_CATEGORIES);
	//@formatter:on
	public static final String MAIN_ACCORDION_LOC = "mainAccordionLoc";
	private static final String LOC_DEPRECATED_VALUES_WARNING = "locDeprecatedValuesWarning";

	private static final String HTML_LAYOUT =
		fluidRowLocs(DiseaseConfigurationDto.DISEASE) + loc(LOC_DEPRECATED_VALUES_WARNING) + fluidRowLocs(MAIN_ACCORDION_LOC);

	private ComboBox cbDisease;
	private CheckBox cbCaseSurveillance;
	private CheckBox cbFollowUpEnabled;
	private TextField tfFollowUpDuration;
	private TextField tfCaseFollowUpDuration;
	private TextField tfEventParticipantFollowUpDuration;
	private CheckBox cbIncubationPeriodEnabled;
	private TextField tfMaxIncubationPeriod;
	private TextField tfMinIncubationPeriod;
	private CheckBox cbExtendedClassification;
	private CheckBox cbExtendedClassificationMulti;
	private TextField tfAutomaticSampleAssignmentThreshold;
	private CheckBox cbIsContagious;
	private TextField tfMinContagiousPeriod;
	private TextField tfMaxContagiousPeriod;

	private CheckboxSet<ExposureCategory> exposureCategoriesField;
	private CheckboxSet<Integer> usualRangeExcludedYearsField;
	private Label usualRangeInfoLabel;
	private Label deprecatedValuesWarningLabel;
	private boolean updatingExposureCategoryItems;
	private boolean settingFormValue;

	public DiseaseConfigurationEditForm() {

		super(
			DiseaseConfigurationDto.class,
			DiseaseConfigurationDto.I18N_PREFIX,
			true,
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getNoop());

		setWidth(840, Unit.PIXELS);
	}

	@Override
	protected void addFields() {

		FormSectionAccordion accordion = new FormSectionAccordion();

		CustomLayout caseDefinitionLayout = new CustomLayout();
		caseDefinitionLayout.setTemplateContents(CASE_DEFINITION_HTML_LAYOUT);

		CustomLayout ageGroupLayout = new CustomLayout();
		ageGroupLayout.setTemplateContents(AGE_GROUP_HTML_LAYOUT);

		CustomLayout generalLayout = new CustomLayout();
		generalLayout.setTemplateContents(GENERAL_HTML_LAYOUT);

		cbDisease = addDiseaseField(DiseaseConfigurationDto.DISEASE, true, false);
		cbDisease.setStyleName(CssStyles.H3);
		addField(generalLayout, DiseaseConfigurationDto.UUID);
		addField(generalLayout, DiseaseConfigurationDto.ACTIVE, CheckBox.class);
		addField(generalLayout, DiseaseConfigurationDto.PRIMARY_DISEASE, CheckBox.class);
		cbCaseSurveillance = addField(generalLayout, DiseaseConfigurationDto.CASE_SURVEILLANCE_ENABLED, CheckBox.class);
		addField(generalLayout, DiseaseConfigurationDto.AGGREGATE_REPORTING_ENABLED, CheckBox.class);

		cbFollowUpEnabled = addField(generalLayout, DiseaseConfigurationDto.FOLLOW_UP_ENABLED, CheckBox.class);
		tfFollowUpDuration = addField(generalLayout, DiseaseConfigurationDto.FOLLOW_UP_DURATION);
		tfCaseFollowUpDuration = addField(generalLayout, DiseaseConfigurationDto.CASE_FOLLOW_UP_DURATION);
		tfEventParticipantFollowUpDuration = addField(generalLayout, DiseaseConfigurationDto.EVENT_PARTICIPANT_FOLLOW_UP_DURATION);
		cbIncubationPeriodEnabled = addField(generalLayout, DiseaseConfigurationDto.INCUBATION_PERIOD_ENABLED, CheckBox.class);
		tfMaxIncubationPeriod = addField(generalLayout, DiseaseConfigurationDto.MAX_INCUBATION_PERIOD, TextField.class);
		tfMinIncubationPeriod = addField(generalLayout, DiseaseConfigurationDto.MIN_INCUBATION_PERIOD, TextField.class);
		cbExtendedClassification = addField(generalLayout, DiseaseConfigurationDto.EXTENDED_CLASSIFICATION, CheckBox.class);
		cbExtendedClassificationMulti = addField(generalLayout, DiseaseConfigurationDto.EXTENDED_CLASSIFICATION_MULTI, CheckBox.class);

		cbIsContagious = addField(generalLayout, DiseaseConfigurationDto.IS_CONTAGIOUS, CheckBox.class);
		tfMinContagiousPeriod = addField(generalLayout, DiseaseConfigurationDto.MIN_CONTAGIOUS_PERIOD, TextField.class);
		tfMaxContagiousPeriod = addField(generalLayout, DiseaseConfigurationDto.MAX_CONTAGIOUS_PERIOD, TextField.class);

		deprecatedValuesWarningLabel = new Label(
			VaadinIcons.WARNING.getHtml() + " " + I18nProperties.getString(Strings.messageDiseaseConfigurationContainsDeprecatedValues),
			ContentMode.HTML);
		CssStyles.style(deprecatedValuesWarningLabel, CssStyles.LABEL_RELEVANT, CssStyles.LABEL_BOLD, CssStyles.VSPACE_3);
		deprecatedValuesWarningLabel.setVisible(false);
		getContent().addComponent(deprecatedValuesWarningLabel, LOC_DEPRECATED_VALUES_WARNING);

		exposureCategoriesField = addField(generalLayout, DiseaseConfigurationDto.EXPOSURE_CATEGORIES, CheckboxSet.class);
		exposureCategoriesField.setColumnCount(3);
		updateExposureCategoryItems(Collections.emptySet());
		exposureCategoriesField.addValueChangeListener(e -> {
			if (updatingExposureCategoryItems || settingFormValue) {
				return;
			}

			@SuppressWarnings("unchecked")
			Set<ExposureCategory> selectedCategories = (Set<ExposureCategory>) e.getProperty().getValue();
			updateExposureCategoryItems(selectedCategories);
		});

		ageGroupsComponent = addField(ageGroupLayout, DiseaseConfigurationDto.AGE_GROUPS, DiseaseConfigurationAgeGroupComponent.class);
		ageGroupsComponent.setCaption(I18nProperties.getPrefixCaption(DiseaseConfigurationDto.I18N_PREFIX, DiseaseConfigurationDto.AGE_GROUPS));

		tfAutomaticSampleAssignmentThreshold = addField(generalLayout, DiseaseConfigurationDto.AUTOMATIC_SAMPLE_ASSIGNMENT_THRESHOLD);

		RichTextArea caseDefinitionText = addField(caseDefinitionLayout, DiseaseConfigurationDto.CASE_DEFINITION_TEXT, RichTextArea.class);
		caseDefinitionText.setNullRepresentation("");
		caseDefinitionText.setImmediate(true);

		accordion.addFormSectionPanel(I18nProperties.getCaption(Captions.titleDiseaseConfigurationGeneral), true, generalLayout);

		accordion.addFormSectionPanel(I18nProperties.getCaption(Captions.titleDiseaseConfigurationCaseDefinition), false, caseDefinitionLayout);
		accordion.addFormSectionPanel(I18nProperties.getCaption(Captions.titleDiseaseConfigurationAgeGroup), false, ageGroupLayout);

		CustomLayout climateLayout = new CustomLayout();
		climateLayout.setTemplateContents(CLIMATE_HTML_LAYOUT);
		addField(climateLayout, DiseaseConfigurationDto.CLIMATE_SENSITIVE, CheckBox.class);
		addUsualRangeExcludedYearsField(climateLayout);
		if (UiUtil.enabled(FeatureType.CLIMATE_MANAGEMENT)) {
			accordion.addFormSectionPanel(I18nProperties.getCaption(Captions.titleDiseaseConfigurationClimate), false, climateLayout);
		}

		getContent().addComponent(accordion, MAIN_ACCORDION_LOC);

		setReadOnly(true, DiseaseConfigurationDto.DISEASE, DiseaseConfigurationDto.UUID);
		FieldHelper.setEnabledWhen(
			cbCaseSurveillance,
			Collections.singletonList(Boolean.TRUE),
			Arrays.asList(cbFollowUpEnabled, cbExtendedClassification, cbExtendedClassificationMulti, tfAutomaticSampleAssignmentThreshold),
			false);
		FieldHelper.setVisibleWhen(
			cbFollowUpEnabled,
			Arrays.asList(tfFollowUpDuration, tfCaseFollowUpDuration, tfEventParticipantFollowUpDuration),
			Arrays.asList(Boolean.TRUE),
			true);
		FieldHelper.setVisibleWhen(
			cbIncubationPeriodEnabled,
			Arrays.asList(tfMaxIncubationPeriod, tfMinIncubationPeriod),
			Arrays.asList(Boolean.TRUE),
			true);
		FieldHelper.setVisibleWhen(cbIsContagious, Arrays.asList(tfMaxContagiousPeriod, tfMinContagiousPeriod), Arrays.asList(Boolean.TRUE), true);
		FieldHelper.setVisibleWhen(
			cbCaseSurveillance,
			Arrays.asList(
				cbFollowUpEnabled,
				cbIncubationPeriodEnabled,
				cbExtendedClassification,
				cbExtendedClassificationMulti,
				tfAutomaticSampleAssignmentThreshold,
				cbIsContagious),
			Arrays.asList(Boolean.TRUE),
			true);
	}

	@Override
	public void setValue(DiseaseConfigurationDto newFieldValue) {
		settingFormValue = true;
		try {
			Set<ExposureCategory> selectedExposureCategories = newFieldValue != null && newFieldValue.getExposureCategories() != null
				? new LinkedHashSet<>(newFieldValue.getExposureCategories())
				: Collections.emptySet();
			List<String> ageGroups =
				newFieldValue != null && newFieldValue.getAgeGroups() != null ? new ArrayList<>(newFieldValue.getAgeGroups()) : null;

			super.setValue(newFieldValue);
			updateExposureCategoryItems(selectedExposureCategories);
			ageGroupsComponent.setValue(ageGroups);
			updateUsualRangeInfo();
		} finally {
			settingFormValue = false;
		}
	}

	/**
	 * Checkboxes for the last {@link ClimateConfiguration#EXCLUDABLE_YEARS} complete years; older years have no effect on the
	 * usual range, so they are not offered.
	 */
	@SuppressWarnings("unchecked")
	private void addUsualRangeExcludedYearsField(CustomLayout climateLayout) {

		usualRangeExcludedYearsField = addField(climateLayout, DiseaseConfigurationDto.USUAL_RANGE_EXCLUDED_YEARS, CheckboxSet.class);
		usualRangeExcludedYearsField.setColumnCount(5);

		int currentYear = LocalDate.now().getYear();
		List<Integer> years = IntStream.range(currentYear - ClimateConfiguration.EXCLUDABLE_YEARS, currentYear).boxed().collect(Collectors.toList());
		usualRangeExcludedYearsField.setItems(years, null, null, String::valueOf, null);

		usualRangeInfoLabel = new Label();
		CssStyles.style(usualRangeInfoLabel, CssStyles.LABEL_WHITE_SPACE_NORMAL, CssStyles.VSPACE_TOP_4);
		climateLayout.addComponent(usualRangeInfoLabel, LOC_USUAL_RANGE_INFO);

		usualRangeExcludedYearsField.addValueChangeListener(e -> updateUsualRangeInfo());
	}

	/**
	 * Shows how many of the reference years remain for the case usual range after the ticked years are left out.
	 */
	@SuppressWarnings("unchecked")
	private void updateUsualRangeInfo() {

		if (usualRangeInfoLabel == null) {
			return;
		}

		int historyYears = getClimateSetting(ClimateConfiguration.HISTORY_YEARS, ClimateConfiguration.DEFAULT_HISTORY_YEARS);
		int minimumYears = getClimateSetting(ClimateConfiguration.CASE_MIN_HISTORY_YEARS, 3);
		int lastYear = LocalDate.now().getYear() - 1;
		int firstYear = lastYear - historyYears + 1;

		Set<Integer> ticked =
			usualRangeExcludedYearsField.getValue() != null ? (Set<Integer>) usualRangeExcludedYearsField.getValue() : Collections.emptySet();
		List<Integer> excluded = ticked.stream().filter(year -> year >= firstYear && year <= lastYear).sorted().collect(Collectors.toList());
		int remaining = historyYears - excluded.size();
		String period = firstYear == lastYear ? String.valueOf(lastYear) : firstYear + "–" + lastYear;

		if (remaining < minimumYears) {
			usualRangeInfoLabel
				.setValue(String.format(I18nProperties.getString(Strings.infoClimateUsualRangeNotShown), remaining, historyYears, minimumYears));
		} else if (excluded.isEmpty()) {
			usualRangeInfoLabel
				.setValue(String.format(I18nProperties.getString(Strings.infoClimateUsualRangeYears), remaining, historyYears, period));
		} else {
			usualRangeInfoLabel.setValue(
				String.format(
					I18nProperties.getString(Strings.infoClimateUsualRangeYearsExcluded),
					remaining,
					historyYears,
					period,
					excluded.stream().map(String::valueOf).collect(Collectors.joining(", "))));
		}
	}

	private static int getClimateSetting(String key, int defaultValue) {

		try {
			int value = Integer.parseInt(FacadeProvider.getSystemConfigurationValueFacade().getValue(key).trim());
			return value > 0 ? value : defaultValue;
		} catch (NumberFormatException | NullPointerException e) {
			return defaultValue;
		}
	}

	private void updateExposureCategoryItems(Set<ExposureCategory> selectedCategories) {
		updatingExposureCategoryItems = true;
		try {
			Set<ExposureCategory> selected = selectedCategories != null ? new LinkedHashSet<>(selectedCategories) : Collections.emptySet();

			List<ExposureCategory> items = Arrays.stream(ExposureCategory.values())
				.filter(category -> !category.isDeprecated() || selected.contains(category))
				.collect(Collectors.toCollection(ArrayList::new));

			exposureCategoriesField.setItems(
				items,
				null,
				category -> category.isDeprecated() ? I18nProperties.getString(Strings.deprecatedValueRemovalOnly) : null,
				category -> formatExposureCategoryCaption(category),
				category -> !category.isDeprecated() || selected.contains(category),
				true);
			exposureCategoriesField.setValue(selected.stream().filter(items::contains).collect(Collectors.toCollection(LinkedHashSet::new)));
		} finally {
			updatingExposureCategoryItems = false;
		}

		updateDeprecatedValuesWarning();
	}

	@SuppressWarnings("unchecked")
	public boolean hasDeprecatedExposureCategories() {
		Set<ExposureCategory> selectedCategories = (Set<ExposureCategory>) exposureCategoriesField.getValue();
		return selectedCategories != null && selectedCategories.stream().anyMatch(ExposureCategory::isDeprecated);
	}

	private void updateDeprecatedValuesWarning() {
		if (deprecatedValuesWarningLabel != null) {
			deprecatedValuesWarningLabel.setVisible(hasDeprecatedExposureCategories());
		}
	}

	private static String formatExposureCategoryCaption(ExposureCategory category) {
		String caption = escapeHtml(String.valueOf(category));
		if (!category.isDeprecated()) {
			return caption;
		}

		return "<span style=\"text-decoration: line-through;\">" + caption + "</span>";
	}

	private static String escapeHtml(String value) {
		if (StringUtils.isEmpty(value)) {
			return "";
		}

		return StringEscapeUtils.escapeHtml4(value);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	public void attach() {
		super.attach();

		cbCaseSurveillance.addValueChangeListener(e -> {
			System.out.println("not enabled" + !cbFollowUpEnabled.isEnabled());
			System.out.println("value" + cbFollowUpEnabled.getValue());
			if (!cbFollowUpEnabled.isEnabled() && cbFollowUpEnabled.getValue()) {
				tfFollowUpDuration.setEnabled(false);
				tfCaseFollowUpDuration.setEnabled(false);
				tfEventParticipantFollowUpDuration.setEnabled(false);
			} else if (cbFollowUpEnabled.isEnabled() && cbFollowUpEnabled.getValue()) {
				tfFollowUpDuration.setEnabled(true);
				tfCaseFollowUpDuration.setEnabled(true);
				tfEventParticipantFollowUpDuration.setEnabled(true);
			}
		});
	}
}
