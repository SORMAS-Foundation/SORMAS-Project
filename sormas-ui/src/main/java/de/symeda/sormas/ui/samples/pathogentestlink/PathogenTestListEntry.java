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
package de.symeda.sormas.ui.samples.pathogentestlink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

@SuppressWarnings("serial")
public class PathogenTestListEntry extends SideComponentField {

	private final PathogenTestDto pathogenTest;
	//@formatter:off
	public static final Map<Disease, List<PathogenTestType>> VARIANT_MAP = Collections.unmodifiableMap(new HashMap<>() {
		{
			put(Disease.MALARIA, Collections.unmodifiableList(Arrays.asList(PathogenTestType.THIN_BLOOD_SMEAR, PathogenTestType.RAPID_TEST, PathogenTestType.PCR_RT_PCR,
					PathogenTestType.Q_PCR, PathogenTestType.LAMP, PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY, PathogenTestType.OTHER_MOLECULAR_ASSAY,
					PathogenTestType.OTHER_SEROLOGICAL_TEST, PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST, PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY, PathogenTestType.ANTIGEN_DETECTION)));
			put(Disease.DENGUE, Collections.unmodifiableList(Arrays.asList(PathogenTestType.NAAT, PathogenTestType.NEUTRALIZING_ANTIBODIES, PathogenTestType.PCR_RT_PCR)));
			put(Disease.MEASLES, Collections.unmodifiableList(Arrays.asList(PathogenTestType.GENOTYPING)));
			put(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, Collections.unmodifiableList(Arrays.asList(PathogenTestType.SEROGROUPING, PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
					PathogenTestType.SLIDE_AGGLUTINATION,PathogenTestType.WHOLE_GENOME_SEQUENCING, PathogenTestType.SEQUENCING)));
			put(Disease.TUBERCULOSIS, Collections.unmodifiableList(Arrays.asList(PathogenTestType.MICROSCOPY, PathogenTestType.BEIJINGGENOTYPING,
							PathogenTestType.SPOLIGOTYPING, PathogenTestType.MIRU_PATTERN_CODE)));
		}
	});
	//@formatter:on
	public PathogenTestListEntry(PathogenTestDto pathogenTest, boolean showTestResultText) {

		this.pathogenTest = pathogenTest;

		HorizontalLayout topLabelLayout = new HorizontalLayout();
		topLabelLayout.setSpacing(false);
		topLabelLayout.setMargin(false);
		topLabelLayout.setWidth(100, Unit.PERCENTAGE);
		addComponentToField(topLabelLayout);
		Label labelTopLeft =
			new Label(PathogenTestType.toString(pathogenTest.getTestType(), pathogenTest.getTestTypeText(), pathogenTest.getTestedDisease()));
		CssStyles.style(labelTopLeft, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		topLabelLayout.addComponent(labelTopLeft);

		if (Boolean.TRUE.equals(pathogenTest.getTestResultVerified())) {
			Label labelTopRight = new Label(VaadinIcons.CHECK_CIRCLE.getHtml(), ContentMode.HTML);
			labelTopRight.setSizeUndefined();
			labelTopRight.addStyleName(CssStyles.LABEL_LARGE);
			labelTopRight.setDescription(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_VERIFIED));
			topLabelLayout.addComponent(labelTopRight);
			topLabelLayout.setComponentAlignment(labelTopRight, Alignment.TOP_RIGHT);
		}

		if (showTestResultText && !DataHelper.isNullOrEmpty(pathogenTest.getTestResultText())) {
			Label resultTextLabel = new Label(StringUtils.abbreviate(pathogenTest.getTestResultText(), 125));
			resultTextLabel.setDescription(pathogenTest.getTestResultText());
			resultTextLabel.setWidthFull();
			addComponentToField(resultTextLabel);
		}

		HorizontalLayout middleLabelLayout = new HorizontalLayout();
		middleLabelLayout.setSpacing(false);
		middleLabelLayout.setMargin(false);
		middleLabelLayout.setWidth(100, Unit.PERCENTAGE);
		addComponentToField(middleLabelLayout);

		Label labelMiddleLeft = new Label(getDiseaseOrPathogenCaption(pathogenTest));
		middleLabelLayout.addComponent(labelMiddleLeft);

		Label labelMiddleRight = new Label(DateFormatHelper.formatLocalDateTime(pathogenTest.getTestDateTime()));
		labelMiddleRight.addStyleName(CssStyles.ALIGN_RIGHT);
		middleLabelLayout.addComponent(labelMiddleRight);
		middleLabelLayout.setComponentAlignment(labelMiddleRight, Alignment.TOP_RIGHT);

		if (pathogenTest.getTestedDiseaseVariant() != null || pathogenTest.getCqValue() != null) {
			HorizontalLayout bottomLabelLayout = new HorizontalLayout();
			bottomLabelLayout.setSpacing(false);
			bottomLabelLayout.setMargin(false);
			bottomLabelLayout.setWidth(100, Unit.PERCENTAGE);
			addComponentToField(bottomLabelLayout);

			if (pathogenTest.getTestedDiseaseVariant() != null) {
				Label labelBottomLeft = new Label(pathogenTest.getTestedDiseaseVariant().toString());
				CssStyles.style(labelBottomLeft, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.LABEL_CRITICAL);
				bottomLabelLayout.addComponent(labelBottomLeft);
			}

			if (pathogenTest.getCqValue() != null) {
				Label labelBottomRight = new Label(
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.CQ_VALUE) + ": " + pathogenTest.getCqValue());
				labelBottomRight.addStyleName(CssStyles.ALIGN_RIGHT);
				bottomLabelLayout.addComponent(labelBottomRight);
				bottomLabelLayout.setComponentAlignment(labelBottomRight, Alignment.TOP_RIGHT);
			}
		}

		PathogenTestType testType = pathogenTest.getTestType();

		// Quantitative result (issue #13952): combine whichever value-type fields the method populated.
		String quantitativeResult = formatQuantitativeResult(pathogenTest);
		boolean astWithoutQualitativeResult =
			pathogenTest.getTestedDisease() == Disease.TUBERCULOSIS && testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY;
		boolean suppressResultLabel =
			pathogenTest.getTestResult() == PathogenTestResultType.NOT_APPLICABLE && (quantitativeResult != null || astWithoutQualitativeResult);
		if (!suppressResultLabel) {
			Object resultText = getResultText(pathogenTest, testType);
			Label labelResult = new Label(DataHelper.toStringNullable(resultText));
			CssStyles.style(labelResult, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			if (pathogenTest.getTestResult() == PathogenTestResultType.POSITIVE) {
				CssStyles.style(labelResult, CssStyles.LABEL_CRITICAL);
			} else {
				CssStyles.style(labelResult, CssStyles.LABEL_WARNING);
			}
			addComponentToField(labelResult);
		}

		if (quantitativeResult != null) {
			Label quantitativeLabel = new Label(StringUtils.abbreviate(quantitativeResult, 125));
			quantitativeLabel.setDescription(quantitativeResult);
			CssStyles.style(quantitativeLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.LABEL_WARNING);
			quantitativeLabel.setWidthFull();
			addComponentToField(quantitativeLabel);
		}

		if (pathogenTest.getTestedDisease() == Disease.TUBERCULOSIS) {
			if (testType == PathogenTestType.PCR_RT_PCR) {
				Label pcrRifampicinTextLabel = new Label(
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.RIFAMPICIN_RESISTANT) + ": " + StringUtils
						.abbreviate((pathogenTest.getRifampicinResistant() != null ? pathogenTest.getRifampicinResistant().toString() : ""), 125));
				pcrRifampicinTextLabel.setWidthFull();
				addComponentToField(pcrRifampicinTextLabel);

				Label pcrIsoniazidTextLabel = new Label(
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.ISONIAZID_RESISTANT) + ": " + StringUtils
						.abbreviate((pathogenTest.getIsoniazidResistant() != null ? pathogenTest.getIsoniazidResistant().toString() : ""), 125));
				pcrIsoniazidTextLabel.setWidthFull();
				addComponentToField(pcrIsoniazidTextLabel);
			}
		}
	}

	private Object getResultText(PathogenTestDto pathogenTest, PathogenTestType testType) {
		if (testType == null) {
			return "";
		}
		Object resultText = "";
		if (pathogenTest.getTestedDisease() == Disease.TUBERCULOSIS && VARIANT_MAP.get(pathogenTest.getTestedDisease()).contains(testType)) {
			if (testType == PathogenTestType.MICROSCOPY) {
				resultText = StringUtils.abbreviate((pathogenTest.getTestScale() != null ? pathogenTest.getTestScale().toString() : ""), 125);
			} else if (testType == PathogenTestType.BEIJINGGENOTYPING) {
				resultText =
					StringUtils.abbreviate((pathogenTest.getStrainCallStatus() != null ? pathogenTest.getStrainCallStatus().toString() : ""), 125);
			} else if (testType == PathogenTestType.SPOLIGOTYPING) {
				resultText = StringUtils.abbreviate((pathogenTest.getSpecie() != null ? pathogenTest.getSpecie().toString() : ""), 125);
			} else if (testType == PathogenTestType.MIRU_PATTERN_CODE) {
				resultText = StringUtils.abbreviate(pathogenTest.getPatternProfile(), 125);
			}
		} else if (testType == PathogenTestType.GENOTYPING) {
			resultText = StringUtils.abbreviate((pathogenTest.getGenoType() != null ? pathogenTest.getGenoType().toString() : ""), 125);
		} else if (pathogenTest.getTestedDisease() == Disease.MALARIA
			&& VARIANT_MAP.get(pathogenTest.getTestedDisease()).stream().anyMatch(testType::equals)) {
			// handling other specie
			if (pathogenTest.getSpecie() == PathogenSpecie.OTHER) {
				resultText = StringUtils.abbreviate((pathogenTest.getSpecieText() != null ? pathogenTest.getSpecieText().toString() : ""), 125);
			} else {
				resultText = StringUtils.abbreviate((pathogenTest.getSpecie() != null ? pathogenTest.getSpecie().toString() : ""), 125);
			}

		} else if (pathogenTest.getTestedDisease() == Disease.INVASIVE_PNEUMOCOCCAL_INFECTION
			&& VARIANT_MAP.get(pathogenTest.getTestedDisease()).stream().anyMatch(testType::equals)) {
			// IPI serotyping stores the serogroup/serotype in the free-text field; show it instead of the plain result
			if (!DataHelper.isNullOrEmpty(pathogenTest.getSerotypeText())) {
				resultText = StringUtils.abbreviate(pathogenTest.getSerotypeText(), 125);
			} else if (pathogenTest.getSerotype() != null) {
				resultText = StringUtils.abbreviate(pathogenTest.getSerotype().toString(), 125);
			} else {
				resultText = pathogenTest.getTestResult();
			}

		} else if (pathogenTest.getTestedDisease() == Disease.DENGUE
			&& VARIANT_MAP.get(pathogenTest.getTestedDisease()).stream().anyMatch(testType::equals)) {
			// handling other serotypes
			if (pathogenTest.getSerotype() == Serotype.OTHER) {
				resultText = StringUtils.abbreviate((pathogenTest.getSerotypeText() != null ? pathogenTest.getSerotypeText().toString() : ""), 125);
			} else {
				resultText = StringUtils.abbreviate((pathogenTest.getSerotype() != null ? pathogenTest.getSerotype().toString() : ""), 125);
			}

		} else {
			resultText = pathogenTest.getTestResult();
		}
		return resultText;
	}

	/**
	 * @return a short label combining every quantitative result field the test recorded (Western Blot
	 *         interpretation, detected flag, smear grade, numeric value with optional unit, free text), or
	 *         {@code null} when none is set. A method can populate several of these at once (e.g. Western
	 *         Blot stores both an interpretation and a band-pattern text), so all are shown.
	 */
	@Nullable
	static String formatQuantitativeResult(PathogenTestDto test) {
		List<String> parts = new ArrayList<>();
		if (test.getWesternBlotInterpretation() != null) {
			parts.add(test.getWesternBlotInterpretation().toString());
		}
		if (test.getSmearGrade() != null) {
			parts.add(test.getSmearGrade().toString());
		}
		if (test.getQuantitativeBoolean() != null) {
			parts.add(test.getQuantitativeBoolean().toString());
		}
		if (test.getQuantitativeValue() != null) {
			String unit = test.getQuantitativeUnit();
			parts.add(DataHelper.isNullOrEmpty(unit) ? test.getQuantitativeValue().toString() : test.getQuantitativeValue() + " " + unit);
		}
		if (StringUtils.isNotBlank(test.getQuantitativeText())) {
			parts.add(test.getQuantitativeText());
		}
		return parts.isEmpty() ? null : String.join(": ", parts);
	}

	@Nullable
	private static String getDiseaseOrPathogenCaption(PathogenTestDto pathogenTest) {
		final String diseaseOrPathogen;
		if (pathogenTest.getTestedDisease() != null) {
			diseaseOrPathogen = DiseaseHelper.toString(pathogenTest.getTestedDisease(), pathogenTest.getTestedDiseaseDetails());
		} else if (pathogenTest.getTestedPathogen() != null) {
			diseaseOrPathogen = DataHelper.getPathogenString(pathogenTest.getTestedPathogen(), pathogenTest.getTestedPathogenDetails());
		} else {
			diseaseOrPathogen = null;
		}
		return diseaseOrPathogen;
	}

	public PathogenTestDto getPathogenTest() {
		return pathogenTest;
	}
}
