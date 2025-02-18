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

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.components.DiseaseConfigurationAgeGroupComponent;

public class DiseaseConfigurationEditForm extends AbstractEditForm<DiseaseConfigurationDto> {

	private DiseaseConfigurationAgeGroupComponent ageGroupsComponent;

	private static final String HTML_LAYOUT = fluidRowLocs(DiseaseConfigurationDto.DISEASE, DiseaseConfigurationDto.UUID)
		+ fluidRowLocsCss(CssStyles.VSPACE_3, DiseaseConfigurationDto.ACTIVE, DiseaseConfigurationDto.PRIMARY_DISEASE)
		+ fluidRowLocsCss(CssStyles.VSPACE_3, DiseaseConfigurationDto.CASE_SURVEILLANCE_ENABLED, DiseaseConfigurationDto.AGGREGATE_REPORTING_ENABLED)
		+ fluidRowLocsCss(CssStyles.VSPACE_3, DiseaseConfigurationDto.FOLLOW_UP_ENABLED)
		+ fluidRowLocsCss(
			CssStyles.VSPACE_3,
			DiseaseConfigurationDto.FOLLOW_UP_DURATION,
			DiseaseConfigurationDto.CASE_FOLLOW_UP_DURATION,
			DiseaseConfigurationDto.EVENT_PARTICIPANT_FOLLOW_UP_DURATION)
		+ fluidRowLocs(
			DiseaseConfigurationDto.EXTENDED_CLASSIFICATION,
			DiseaseConfigurationDto.EXTENDED_CLASSIFICATION_MULTI,
			DiseaseConfigurationDto.AUTOMATIC_SAMPLE_ASSIGNMENT_THRESHOLD)
		+ fluidRowLocsCss(CssStyles.VSPACE_3, DiseaseConfigurationDto.AGE_GROUPS);

	private ComboBox cbDisease;
	private CheckBox cbCaseSurveillance;
	private CheckBox cbFollowUpEnabled;
	private TextField tfFollowUpDuration;
	private TextField tfCaseFollowUpDuration;
	private TextField tfEventParticipantFollowUpDuration;
	private CheckBox cbExtendedClassification;
	private CheckBox cbExtendedClassificationMulti;
	private TextField tfAutomaticSampleAssignmentThreshold;

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

		cbDisease = addDiseaseField(DiseaseConfigurationDto.DISEASE, true, false);
		addField(DiseaseConfigurationDto.UUID);
		addField(DiseaseConfigurationDto.ACTIVE, CheckBox.class);
		addField(DiseaseConfigurationDto.PRIMARY_DISEASE, CheckBox.class);
		cbCaseSurveillance = addField(DiseaseConfigurationDto.CASE_SURVEILLANCE_ENABLED, CheckBox.class);
		addField(DiseaseConfigurationDto.AGGREGATE_REPORTING_ENABLED, CheckBox.class);

		cbFollowUpEnabled = addField(DiseaseConfigurationDto.FOLLOW_UP_ENABLED, CheckBox.class);
		tfFollowUpDuration = addField(DiseaseConfigurationDto.FOLLOW_UP_DURATION);
		tfCaseFollowUpDuration = addField(DiseaseConfigurationDto.CASE_FOLLOW_UP_DURATION);
		tfEventParticipantFollowUpDuration = addField(DiseaseConfigurationDto.EVENT_PARTICIPANT_FOLLOW_UP_DURATION);
		cbExtendedClassification = addField(DiseaseConfigurationDto.EXTENDED_CLASSIFICATION, CheckBox.class);
		cbExtendedClassificationMulti = addField(DiseaseConfigurationDto.EXTENDED_CLASSIFICATION_MULTI, CheckBox.class);

		ageGroupsComponent = addField(DiseaseConfigurationDto.AGE_GROUPS, DiseaseConfigurationAgeGroupComponent.class);
		ageGroupsComponent.setCaption(I18nProperties.getPrefixCaption(DiseaseConfigurationDto.I18N_PREFIX, DiseaseConfigurationDto.AGE_GROUPS));

		tfAutomaticSampleAssignmentThreshold = addField(DiseaseConfigurationDto.AUTOMATIC_SAMPLE_ASSIGNMENT_THRESHOLD);

		setReadOnly(true, DiseaseConfigurationDto.DISEASE, DiseaseConfigurationDto.UUID);
		FieldHelper.setEnabledWhen(
			cbCaseSurveillance,
			Collections.singletonList(Boolean.TRUE),
			Arrays.asList(cbFollowUpEnabled, cbExtendedClassification, cbExtendedClassificationMulti, tfAutomaticSampleAssignmentThreshold),
			false);
		FieldHelper.setEnabledWhen(
			cbFollowUpEnabled,
			Collections.singletonList(Boolean.TRUE),
			Arrays.asList(tfFollowUpDuration, tfCaseFollowUpDuration, tfEventParticipantFollowUpDuration),
			false);
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
