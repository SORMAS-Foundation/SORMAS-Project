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
			addTopRightIcon(topLabelLayout, VaadinIcons.CHECK_CIRCLE, PathogenTestDto.TEST_RESULT_VERIFIED);
		}

		// At-a-glance lab indicators: reference laboratory, retest, and result details
		// (the latter only when the full result text is not already shown below).
		if (shouldShowRefLabIcon(pathogenTest)) {
			addTopRightIcon(topLabelLayout, VaadinIcons.INSTITUTION, PathogenTestDto.PERFORMED_BY_REFERENCE_LABORATORY);
		}
		if (shouldShowRetestIcon(pathogenTest)) {
			addTopRightIcon(topLabelLayout, VaadinIcons.REFRESH, PathogenTestDto.RETEST_REQUESTED);
		}
		if (shouldShowResultDetailsIcon(pathogenTest, showTestResultText)) {
			addTopRightIcon(topLabelLayout, VaadinIcons.INFO_CIRCLE, PathogenTestDto.TEST_RESULT_TEXT);
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
			Object resultText = determineSideComponentVariant(pathogenTest);
			Label labelResult = new Label(DataHelper.toStringNullable(resultText == null ? pathogenTest.getTestResult() : resultText));
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

	private static void addTopRightIcon(HorizontalLayout topLabelLayout, VaadinIcons icon, String captionProperty) {
		Label iconLabel = new Label(icon.getHtml(), ContentMode.HTML);
		iconLabel.setSizeUndefined();
		iconLabel.addStyleNames(CssStyles.LABEL_LARGE, CssStyles.HSPACE_LEFT_4);
		iconLabel.setDescription(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, captionProperty));
		topLabelLayout.addComponent(iconLabel);
		topLabelLayout.setComponentAlignment(iconLabel, Alignment.TOP_RIGHT);
	}

	static boolean shouldShowRefLabIcon(PathogenTestDto test) {
		return Boolean.TRUE.equals(test.getPerformedByReferenceLaboratory());
	}

	static boolean shouldShowRetestIcon(PathogenTestDto test) {
		return Boolean.TRUE.equals(test.getRetestRequested());
	}

	/**
	 * The result-details cue is only shown in the compact view (when the full result text is not rendered),
	 * to avoid a redundant icon next to text that is already visible.
	 */
	static boolean shouldShowResultDetailsIcon(PathogenTestDto test, boolean showTestResultText) {
		return !showTestResultText && !DataHelper.isNullOrEmpty(test.getTestResultText());
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
