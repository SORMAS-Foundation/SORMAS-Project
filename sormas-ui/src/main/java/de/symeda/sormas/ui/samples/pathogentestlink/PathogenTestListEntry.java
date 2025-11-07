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

import java.util.Arrays;
import java.util.List;

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
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

@SuppressWarnings("serial")
public class PathogenTestListEntry extends SideComponentField {

	private final PathogenTestDto pathogenTest;
	List<PathogenTestType> seroGrpTests = Arrays.asList(
		PathogenTestType.SEROGROUPING,
		PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
		PathogenTestType.SLIDE_AGGLUTINATION,
		PathogenTestType.WHOLE_GENOME_SEQUENCING,
		PathogenTestType.SEQUENCING);

	public PathogenTestListEntry(PathogenTestDto pathogenTest, boolean showTestResultText) {

		this.pathogenTest = pathogenTest;

		HorizontalLayout topLabelLayout = new HorizontalLayout();
		topLabelLayout.setSpacing(false);
		topLabelLayout.setMargin(false);
		topLabelLayout.setWidth(100, Unit.PERCENTAGE);
		addComponentToField(topLabelLayout);
		Label labelTopLeft = new Label(PathogenTestType.toString(pathogenTest.getTestType(), pathogenTest.getTestTypeText()));
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

		if (pathogenTest.getTestedDisease() != Disease.TUBERCULOSIS) {
			if (showTestResultText && !DataHelper.isNullOrEmpty(pathogenTest.getTestResultText())) {
				Label resultTextLabel = new Label(StringUtils.abbreviate(pathogenTest.getTestResultText(), 125));
				resultTextLabel.setDescription(pathogenTest.getTestResultText());
				resultTextLabel.setWidthFull();
				addComponentToField(resultTextLabel);
			}
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

		Object resultText = "";
		PathogenTestType testType = pathogenTest.getTestType();
		if (seroGrpTests.contains(pathogenTest.getTestType())) {
			resultText = pathogenTest.getSeroGroupSpecification() != null ? pathogenTest.getSeroGroupSpecification() : pathogenTest.getSerotype();
		} else if (pathogenTest.getTestedDisease() == Disease.TUBERCULOSIS
			&& Arrays
				.asList(
					PathogenTestType.MICROSCOPY,
					PathogenTestType.BEIJINGGENOTYPING,
					PathogenTestType.SPOLIGOTYPING,
					PathogenTestType.MIRU_PATTERN_CODE)
				.contains(testType)) {
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
			resultText = StringUtils.abbreviate((pathogenTest.getGenoTypeResult() != null ? pathogenTest.getGenoTypeResult().toString() : ""), 125);
		} else {
			resultText = pathogenTest.getTestResult();
		}

		Label labelResult = new Label(DataHelper.toStringNullable(resultText));
		CssStyles.style(labelResult, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		if (pathogenTest.getTestResult() == PathogenTestResultType.POSITIVE) {
			CssStyles.style(labelResult, CssStyles.LABEL_CRITICAL);
		} else {
			CssStyles.style(labelResult, CssStyles.LABEL_WARNING);
		}
		addComponentToField(labelResult);

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
			} else if (testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
				labelResult.setVisible(false);
			}
		}
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
