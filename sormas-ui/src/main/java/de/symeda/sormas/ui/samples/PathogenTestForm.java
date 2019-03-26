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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.Arrays;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class PathogenTestForm extends AbstractEditForm<PathogenTestDto> {

	private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_TYPE_TEXT) +
			LayoutUtil.fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			LayoutUtil.fluidRowLocs(PathogenTestDto.TEST_RESULT, PathogenTestDto.TEST_RESULT_VERIFIED) +
			LayoutUtil.fluidRowLocs(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, "") +
			LayoutUtil.fluidRowLocs(PathogenTestDto.TEST_RESULT_TEXT);

	private final SampleDto sample;
	private int caseSampleCount;
	
	public PathogenTestForm(SampleDto sample, boolean create, UserRight editOrCreateUserRight, int caseSampleCount) {
		super(PathogenTestDto.class, PathogenTestDto.I18N_PREFIX, editOrCreateUserRight);
		
		this.sample = sample;
		this.caseSampleCount = caseSampleCount;
        setWidth(600, Unit.PIXELS);
        
        addFields();
        if (create) {
        	hideValidationUntilNextCommit();
        }
	}
	
	@Override
	protected void addFields() {
		if (sample == null) {
			return;
		}
		
		ComboBox testTypeField = addField(PathogenTestDto.TEST_TYPE, ComboBox.class);
		addField(PathogenTestDto.TEST_TYPE_TEXT, TextField.class);
		DateTimeField sampleTestDateField = addField(PathogenTestDto.TEST_DATE_TIME, DateTimeField.class);
		sampleTestDateField.addValidator(new DateComparisonValidator(sampleTestDateField, sample.getSampleDateTime(), false, false,
				I18nProperties.getValidationError(Validations.afterDate, sampleTestDateField.getCaption(), I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME))));
		ComboBox lab = addField(PathogenTestDto.LAB, ComboBox.class);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllLaboratories(true));
		
		addField(PathogenTestDto.TEST_RESULT, ComboBox.class);
		OptionGroup testResultVerifiedField = addField(PathogenTestDto.TEST_RESULT_VERIFIED, OptionGroup.class);
		testResultVerifiedField.setRequired(true);
		CheckBox fourFoldIncrease = addField(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, CheckBox.class);
		CssStyles.style(fourFoldIncrease, CssStyles.VSPACE_3, CssStyles.VSPACE_TOP_4);
		fourFoldIncrease.setVisible(false);
		fourFoldIncrease.setEnabled(false);
		addField(PathogenTestDto.TEST_RESULT_TEXT, TextArea.class).setRows(3);

		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.TEST_TYPE_TEXT, PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.PCR_RT_PCR, PathogenTestType.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestDto.TEST_TYPE_TEXT), Arrays.asList(PathogenTestType.OTHER));
		
		testTypeField.addValueChangeListener(e -> {
			PathogenTestType testType = (PathogenTestType) e.getProperty().getValue();
			if (testType == PathogenTestType.IGM_SERUM_ANTIBODY || testType == PathogenTestType.IGG_SERUM_ANTIBODY) {
				fourFoldIncrease.setVisible(true);
				fourFoldIncrease.setEnabled(caseSampleCount >= 2);
			} else {
				fourFoldIncrease.setVisible(false);
				fourFoldIncrease.setEnabled(false);
			}
		});
		
		setRequired(true, PathogenTestDto.TEST_TYPE, PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB,
				PathogenTestDto.TEST_RESULT);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
