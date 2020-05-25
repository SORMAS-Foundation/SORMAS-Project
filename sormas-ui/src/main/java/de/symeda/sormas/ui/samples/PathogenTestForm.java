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

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_4;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;

public class PathogenTestForm extends AbstractEditForm<PathogenTestDto> {
		
		private static final long serialVersionUID = 1L;
		

	private static final String HTML_LAYOUT = 
			fluidRowLocs(PathogenTestDto.TEST_TYPE, PathogenTestDto.TESTED_DISEASE) +
			fluidRowLocs(PathogenTestDto.TEST_TYPE_TEXT, PathogenTestDto.TESTED_DISEASE_DETAILS) +
			fluidRowLocs(PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.LAB) +
			fluidRowLocs("", PathogenTestDto.LAB_DETAILS) +
			fluidRowLocs(PathogenTestDto.TEST_RESULT, PathogenTestDto.TEST_RESULT_VERIFIED) +
			fluidRowLocs(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, "") + 
			fluidRowLocs(PathogenTestDto.SEROTYPE, "") + 
			fluidRowLocs(PathogenTestDto.CQ_VALUE, "") + 
			fluidRowLocs(PathogenTestDto.TEST_RESULT_TEXT);

	private final SampleDto sample;
	private int caseSampleCount;
	
	public PathogenTestForm(SampleDto sample, boolean create, int caseSampleCount) {
		super(PathogenTestDto.class, PathogenTestDto.I18N_PREFIX);
		
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
		TextField testTypeTextField = addField(PathogenTestDto.TEST_TYPE_TEXT, TextField.class);
		FieldHelper.addSoftRequiredStyle(testTypeTextField);
		DateTimeField sampleTestDateField = addField(PathogenTestDto.TEST_DATE_TIME, DateTimeField.class);
		sampleTestDateField.addValidator(new DateComparisonValidator(sampleTestDateField, sample.getSampleDateTime(), false, false,
				I18nProperties.getValidationError(Validations.afterDate, sampleTestDateField.getCaption(), I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME))));
		ComboBox lab = addField(PathogenTestDto.LAB, ComboBox.class);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		TextField labDetails = addField(PathogenTestDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		addDiseaseField(PathogenTestDto.TESTED_DISEASE, true);
		addField(PathogenTestDto.TESTED_DISEASE_DETAILS, TextField.class);
		
		addField(PathogenTestDto.TEST_RESULT, ComboBox.class);
		addField(PathogenTestDto.SEROTYPE, TextField.class);
		addField(PathogenTestDto.CQ_VALUE, TextField.class);
		OptionGroup testResultVerifiedField = addField(PathogenTestDto.TEST_RESULT_VERIFIED, OptionGroup.class);
		testResultVerifiedField.setRequired(true);
		CheckBox fourFoldIncrease = addField(PathogenTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER, CheckBox.class);
		CssStyles.style(fourFoldIncrease, VSPACE_3, VSPACE_TOP_4);
		fourFoldIncrease.setVisible(false);
		fourFoldIncrease.setEnabled(false);
		addField(PathogenTestDto.TEST_RESULT_TEXT, TextArea.class).setRows(3);
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.TEST_TYPE_TEXT, PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.PCR_RT_PCR, PathogenTestType.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.TESTED_DISEASE_DETAILS, PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.OTHER), true);
		
		Map<Object, List<Object>> serotypeVisibilityDependencies = new HashMap<Object, List<Object>> () {{
	        put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.CSM));
	        put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
	    }};
		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.SEROTYPE, serotypeVisibilityDependencies, true);

		FieldHelper.setVisibleWhen(getFieldGroup(), PathogenTestDto.CQ_VALUE, PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.CQ_VALUE_DETECTION), true);
		
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
		
		lab.addValueChangeListener(event -> {
			if (event.getProperty().getValue() != null && ((FacilityReferenceDto) event.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
				labDetails.setVisible(true);
				labDetails.setRequired(true);
			} else {
				labDetails.setVisible(false);
				labDetails.setRequired(false);
				labDetails.clear();
			}
		});

		if (sample.getSamplePurpose() != SamplePurpose.INTERNAL) {
			setRequired(true, PathogenTestDto.LAB);
		}
		setRequired(true, PathogenTestDto.TEST_TYPE, PathogenTestDto.TESTED_DISEASE, PathogenTestDto.TEST_DATE_TIME, PathogenTestDto.TEST_RESULT);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
