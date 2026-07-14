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

import java.util.Arrays;
import java.util.Collections;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.RichTextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
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

	private static final String HTML_LAYOUT = fluidRowLocs(DiseaseConfigurationDto.DISEASE) + fluidRowLocs(MAIN_ACCORDION_LOC);

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

		exposureCategoriesField = addField(generalLayout, DiseaseConfigurationDto.EXPOSURE_CATEGORIES, CheckboxSet.class);
		exposureCategoriesField.setColumnCount(3);
		exposureCategoriesField.setItems(Arrays.asList(ExposureCategory.values()), null, null);

		ageGroupsComponent = addField(ageGroupLayout, DiseaseConfigurationDto.AGE_GROUPS, DiseaseConfigurationAgeGroupComponent.class);
		ageGroupsComponent.setCaption(I18nProperties.getPrefixCaption(DiseaseConfigurationDto.I18N_PREFIX, DiseaseConfigurationDto.AGE_GROUPS));

		tfAutomaticSampleAssignmentThreshold = addField(generalLayout, DiseaseConfigurationDto.AUTOMATIC_SAMPLE_ASSIGNMENT_THRESHOLD);

		RichTextArea caseDefinitionText = addField(caseDefinitionLayout, DiseaseConfigurationDto.CASE_DEFINITION_TEXT, RichTextArea.class);
		caseDefinitionText.setNullRepresentation("");
		caseDefinitionText.setImmediate(true);

		accordion.addFormSectionPanel(I18nProperties.getCaption(Captions.titleDiseaseConfigurationGeneral), true, generalLayout);

		accordion.addFormSectionPanel(I18nProperties.getCaption(Captions.titleDiseaseConfigurationCaseDefinition), false, caseDefinitionLayout);
		accordion.addFormSectionPanel(I18nProperties.getCaption(Captions.titleDiseaseConfigurationAgeGroup), false, ageGroupLayout);

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

		super.setValue(newFieldValue);
		ageGroupsComponent.setValue(newFieldValue.getAgeGroups());
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
