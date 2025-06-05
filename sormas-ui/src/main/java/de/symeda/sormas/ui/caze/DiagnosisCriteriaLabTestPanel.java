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

package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public class DiagnosisCriteriaLabTestPanel extends CustomLayout {

	private static final long serialVersionUID = 3507451575384747784L;

	private static final String FORM_HEADING_LOC = "formHeadingLoc";
	private static final String IGRA_TEST_LOC = "igraTestLoc";
	private static final String IGRA_TEST_RESULT_LOC = "igraTestResultLoc";
	private static final String IGRA_TEST_RESULT_DATE_LOC = "igraTestResultDateLoc";
	private static final String TST_TEST_LOC = "tstTestLoc";
	private static final String TST_TEST_RESULT_LOC = "tstTestResultLoc";
	private static final String TST_TEST_RESULT_DATE_LOC = "tstTestResultDateLoc";
	private static final String PCR_TEST_LOC = "pcrTestLoc";
	private static final String PCR_TEST_RESULT_LOC = "pcrTestResultLoc";
	private static final String PCR_TEST_RESULT_DATE_LOC = "pcrTestResultDateLoc";
	private static final String PCR_RIFAMPICIN_LOC = "pcrRifampicinLoc";
	private static final String PCR_ISONIAZID_LOC = "pcrIsoniazidLoc";
	private static final String MICROSCOPY_TEST_LOC = "microscopyTestLoc";
	private static final String MICROSCOPY_TEST_RESULT_LOC = "microscopyTestResultLoc";
	private static final String MICROSCOPY_TEST_RESULT_DATE_LOC = "microscopyTestResultDateLoc";
	private static final String MICROSCOPY_TEST_SCALE_LOC = "microscopyTestScaleLoc";
	private static final String BIOPSY_TEST_LOC = "biopsyTestLoc";
	private static final String BIOPSY_TEST_RESULT_LOC = "biopsyTestResultLoc";
	private static final String BIOPSY_TEST_RESULT_DATE_LOC = "biopsyTestResultDateLoc";
	private static final String CULTURE_TEST_LOC = "cultureTestLoc";
	private static final String CULTURE_TEST_RESULT_LOC = "cultureTestResultLoc";
	private static final String CULTURE_TEST_RESULT_DATE_LOC = "cultureTestResultDateLoc";

	private static final String TEST_TYPE_YES = "YES";
	private static final String TEST_TYPE_NO = "NO";
	private static final String TEST_TYPE_NOT_APPLICABLE = "NOT_APPLICABLE";
	private static final String TEST_RESULT_POS = "POSITIVE";
	private static final String TEST_RESULT_NEG = "NEGATIVE";
	private static final String TEST_RESULT_ONGOING = "ONGOING";

	private Disease disease;
	private List<PathogenTestDto> pathogenTests;

	private Map<String, Map<String, String>> testDetails = new HashMap<>();
	private List<String> positiveNegativeResult = Arrays.asList(PathogenTestResultType.POSITIVE.name(), PathogenTestResultType.NEGATIVE.name());

	//@formatter:off
    private static final String HTML_LAYOUT =
            loc(FORM_HEADING_LOC) +
                fluidRowLocs(6, IGRA_TEST_LOC, 4, IGRA_TEST_RESULT_LOC, 2, IGRA_TEST_RESULT_DATE_LOC)
                + fluidRowLocs(6, TST_TEST_LOC, 4, TST_TEST_RESULT_LOC, 2, TST_TEST_RESULT_DATE_LOC)
                + fluidRowLocs(6, PCR_TEST_LOC, 4, PCR_TEST_RESULT_LOC, 2, PCR_TEST_RESULT_DATE_LOC)
                + fluidRowLocs(6, "", 4, PCR_RIFAMPICIN_LOC, 2, "")
                + fluidRowLocs(6, "", 4, PCR_ISONIAZID_LOC, 2, "")
                + fluidRowLocs(6, MICROSCOPY_TEST_LOC, 4, MICROSCOPY_TEST_RESULT_LOC, 2, MICROSCOPY_TEST_RESULT_DATE_LOC)
                + fluidRowLocs(6, "", 4, MICROSCOPY_TEST_SCALE_LOC, 2, "")
                + fluidRowLocs(6, BIOPSY_TEST_LOC, 4, BIOPSY_TEST_RESULT_LOC, 2, BIOPSY_TEST_RESULT_DATE_LOC)
                + fluidRowLocs(6, CULTURE_TEST_LOC, 4, CULTURE_TEST_RESULT_LOC, 2, CULTURE_TEST_RESULT_DATE_LOC);
    //@formatter:on

	public DiagnosisCriteriaLabTestPanel(Disease disease, List<PathogenTestDto> pathogenTests) {
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		this.addStyleNames(CssStyles.VSPACE_3);
		setTemplateContents(HTML_LAYOUT);

		this.disease = disease;
		this.pathogenTests = pathogenTests;

		initTestResultDetails();
		addFields();
	}

	private void initTestResultDetails() {
		String testResult = null;
		String testDate = null;
		for (PathogenTestDto pathogenTest : pathogenTests) {
			if (!testDetails.containsKey(pathogenTest.getTestType().name())) {
				if (positiveNegativeResult.contains(pathogenTest.getTestResult().name())) {
					testResult = pathogenTest.getTestResult().name();
				}

				if (pathogenTest.getTestDateTime() != null) {
					testDate = DateFormatHelper.formatDate(pathogenTest.getTestDateTime());
				}

				Map<String, String> testResultDetails = new HashMap<>();
				testResultDetails.put("RESULT", testResult);
				testResultDetails.put("DATE", testDate);
				testDetails.put(pathogenTest.getTestType().name(), testResultDetails);
			}
		}
	}

	private void addFields() {
		if (List.of(Disease.TUBERCULOSIS).contains(disease)) {
			OptionGroup igraTest = addTestTypeComponent(IGRA_TEST_LOC, I18nProperties.getEnumCaption(PathogenTestType.IGRA));
			OptionGroup igraTestResult = addTestResultComponent(IGRA_TEST_RESULT_LOC);
			TextField igraTestResultDate = addTestResultDateComponent(IGRA_TEST_RESULT_DATE_LOC);
			if (testDetails.containsKey(PathogenTestType.IGRA.name())) {
				igraTest.setValue(TEST_TYPE_YES);
				igraTestResult.setValue(testDetails.get(PathogenTestType.IGRA.name()).get("RESULT"));
				igraTestResultDate.setValue(testDetails.get(PathogenTestType.IGRA.name()).get("DATE"));
			} else {
				igraTest.setValue(TEST_TYPE_NO);
			}

			OptionGroup tstTest = addTestTypeComponent(TST_TEST_LOC, I18nProperties.getEnumCaption(PathogenTestType.TST));
			OptionGroup tstTestResult = addTestResultComponent(TST_TEST_RESULT_LOC);
			TextField tstTestResultDate = addTestResultDateComponent(TST_TEST_RESULT_DATE_LOC);
			if (testDetails.containsKey(PathogenTestType.TST.name())) {
				tstTest.setValue(TEST_TYPE_YES);
				tstTestResult.setValue(testDetails.get(PathogenTestType.TST.name()).get("RESULT"));
				tstTestResultDate.setValue(testDetails.get(PathogenTestType.TST.name()).get("DATE"));
			} else {
				tstTest.setValue(TEST_TYPE_NO);
			}

			OptionGroup pcrTest = addTestTypeComponent(PCR_TEST_LOC, I18nProperties.getEnumCaption(PathogenTestType.PCR_RT_PCR));
			OptionGroup pcrTestResult = addTestResultComponent(PCR_TEST_RESULT_LOC);
			TextField pcrTestResultDate = addTestResultDateComponent(PCR_TEST_RESULT_DATE_LOC);
			if (testDetails.containsKey(PathogenTestType.PCR_RT_PCR.name())) {
				pcrTest.setValue(TEST_TYPE_YES);
				pcrTestResult.setValue(testDetails.get(PathogenTestType.PCR_RT_PCR.name()).get("RESULT"));
				pcrTestResultDate.setValue(testDetails.get(PathogenTestType.PCR_RT_PCR.name()).get("DATE"));
			} else {
				pcrTest.setValue(TEST_TYPE_NO);
			}

			OptionGroup microscopyTest = addTestTypeComponent(MICROSCOPY_TEST_LOC, I18nProperties.getEnumCaption(PathogenTestType.MICROSCOPY));
			OptionGroup microscopyTestResult = addTestResultComponent(MICROSCOPY_TEST_RESULT_LOC);
			TextField microscopyTestResultDate = addTestResultDateComponent(MICROSCOPY_TEST_RESULT_DATE_LOC);
			if (testDetails.containsKey(PathogenTestType.MICROSCOPY.name())) {
				microscopyTest.setValue(TEST_TYPE_YES);
				microscopyTestResult.setValue(testDetails.get(PathogenTestType.MICROSCOPY.name()).get("RESULT"));
				microscopyTestResultDate.setValue(testDetails.get(PathogenTestType.MICROSCOPY.name()).get("DATE"));
			} else {
				microscopyTest.setValue(TEST_TYPE_NO);
			}

			OptionGroup cultureTest = addTestTypeComponent(CULTURE_TEST_LOC, I18nProperties.getEnumCaption(PathogenTestType.CULTURE));
			OptionGroup cultureTestResult = addTestResultComponent(CULTURE_TEST_RESULT_LOC);
			TextField cultureTestResultDate = addTestResultDateComponent(CULTURE_TEST_RESULT_DATE_LOC);
			if (testDetails.containsKey(PathogenTestType.CULTURE.name())) {
				cultureTest.setValue(TEST_TYPE_YES);
				cultureTestResult.setValue(testDetails.get(PathogenTestType.CULTURE.name()).get("RESULT"));
				cultureTestResultDate.setValue(testDetails.get(PathogenTestType.CULTURE.name()).get("DATE"));
			} else {
				cultureTest.setValue(TEST_TYPE_NO);
			}
		}
	}

	private OptionGroup addTestTypeComponent(String fieldId, String caption) {
		OptionGroup field = new OptionGroup();
		field.setId(fieldId);
		field.setCaption(caption);
		field.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_CAPTION_INLINE);
		field.addItems(List.of(TEST_TYPE_YES, TEST_TYPE_NO, TEST_TYPE_NOT_APPLICABLE));
		field.setItemCaption(TEST_TYPE_YES, I18nProperties.getCaption(Captions.diagnosisCriteriaDetailTestTypeYes));
		field.setItemCaption(TEST_TYPE_NO, I18nProperties.getCaption(Captions.diagnosisCriteriaDetailTestTypeNo));
		field.setItemCaption(TEST_TYPE_NOT_APPLICABLE, I18nProperties.getCaption(Captions.diagnosisCriteriaDetailTestTypeNotApplicable));
		field.setEnabled(false);
		addComponent(field, fieldId);
		return field;
	}

	private OptionGroup addTestResultComponent(String fieldId) {
		OptionGroup field = new OptionGroup();
		field.setId(fieldId);
		field.setCaption(I18nProperties.getCaption(Captions.diagnosisCriteriaDetailTestResult));
		field.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_CAPTION_INLINE);
		field.addItems(List.of(TEST_RESULT_POS, TEST_RESULT_NEG, TEST_RESULT_ONGOING));
		field.setItemCaption(TEST_RESULT_POS, I18nProperties.getCaption(Captions.diagnosisCriteriaDetailTestResultPos));
		field.setItemCaption(TEST_RESULT_NEG, I18nProperties.getCaption(Captions.diagnosisCriteriaDetailTestResultNeg));
		field.setItemCaption(TEST_RESULT_ONGOING, I18nProperties.getCaption(Captions.diagnosisCriteriaDetailTestResultOngoing));
		field.setEnabled(false);
		addComponent(field, fieldId);
		return field;
	}

	private TextField addTestResultDateComponent(String fieldId) {
		TextField field = new TextField();
		field.setId(fieldId);
		field.setWidth(100, Unit.PERCENTAGE);
		field.setCaption(null);
		field.setInputPrompt(I18nProperties.getCaption(Captions.diagnosisCriteriaDetailTestResultDate));
		//CssStyles.style(field, CssStyles.TEXTFIELD_ROW, CssStyles.TEXTFIELD_CAPTION_INLINE);
		field.setEnabled(false);
		addComponent(field, fieldId);
		return field;
	}
}
