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

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class PathogenTestListEntry extends HorizontalLayout {

	private final PathogenTestDto pathogenTest;
	private Button editButton;
	private Button viewAssociatedLabMessagesButton;

	public PathogenTestListEntry(PathogenTestDto pathogenTest) {

		setSpacing(true);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);
		this.pathogenTest = pathogenTest;

		VerticalLayout labelLayout = new VerticalLayout();
		labelLayout.setSpacing(false);
		labelLayout.setMargin(false);
		labelLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(labelLayout);
		setExpandRatio(labelLayout, 1);

		HorizontalLayout topLabelLayout = new HorizontalLayout();
		topLabelLayout.setSpacing(false);
		topLabelLayout.setMargin(false);
		topLabelLayout.setWidth(100, Unit.PERCENTAGE);
		labelLayout.addComponent(topLabelLayout);
		Label labelTopLeft = new Label(PathogenTestType.toString(pathogenTest.getTestType(), pathogenTest.getTestTypeText()));
		CssStyles.style(labelTopLeft, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		topLabelLayout.addComponent(labelTopLeft);

		if (pathogenTest.getTestResultVerified()) {
			Label labelTopRight = new Label(VaadinIcons.CHECK_CIRCLE.getHtml(), ContentMode.HTML);
			labelTopRight.setSizeUndefined();
			labelTopRight.addStyleName(CssStyles.LABEL_LARGE);
			labelTopRight.setDescription(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_VERIFIED));
			topLabelLayout.addComponent(labelTopRight);
			topLabelLayout.setComponentAlignment(labelTopRight, Alignment.TOP_RIGHT);
		}

		if (!DataHelper.isNullOrEmpty(pathogenTest.getTestResultText())) {
			Label resultTextLabel = new Label(StringUtils.abbreviate(pathogenTest.getTestResultText(), 125));
			resultTextLabel.setDescription(pathogenTest.getTestResultText());
			resultTextLabel.setWidthFull();
			labelLayout.addComponent(resultTextLabel);
		}

		HorizontalLayout middleLabelLayout = new HorizontalLayout();
		middleLabelLayout.setSpacing(false);
		middleLabelLayout.setMargin(false);
		middleLabelLayout.setWidth(100, Unit.PERCENTAGE);
		labelLayout.addComponent(middleLabelLayout);

		Label labelMiddleLeft =
			new Label(DataHelper.toStringNullable(DiseaseHelper.toString(pathogenTest.getTestedDisease(), pathogenTest.getTestedDiseaseDetails())));
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
			labelLayout.addComponent(bottomLabelLayout);

			if (pathogenTest.getTestedDiseaseVariant() != null) {
				Label labelBottomLeft = new Label(pathogenTest.getTestedDiseaseVariant().toString());
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

		Label labelResult = new Label(DataHelper.toStringNullable(pathogenTest.getTestResult()));
		CssStyles.style(labelResult, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		if (pathogenTest.getTestResult() == PathogenTestResultType.POSITIVE) {
			CssStyles.style(labelResult, CssStyles.LABEL_CRITICAL);
		} else {
			CssStyles.style(labelResult, CssStyles.LABEL_WARNING);
		}
		labelLayout.addComponent(labelResult);
	}

	public void addEditListener(ClickListener editClickListener) {

		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"edit-test-" + pathogenTest.getUuid(),
				null,
				VaadinIcons.PENCIL,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}

		editButton.addClickListener(editClickListener);
	}

	public void addAssociatedLabMessagesListener(ClickListener associatedLabMessagesClickListener) {
		if (viewAssociatedLabMessagesButton == null) {
			viewAssociatedLabMessagesButton = ButtonHelper.createIconButtonWithCaption(
				"see-associated-lab-messages-" + pathogenTest.getUuid(),
				null,
				VaadinIcons.NOTEBOOK,
				associatedLabMessagesClickListener,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(viewAssociatedLabMessagesButton);
			setComponentAlignment(viewAssociatedLabMessagesButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(viewAssociatedLabMessagesButton, 0);
			viewAssociatedLabMessagesButton.setDescription(I18nProperties.getDescription(Descriptions.Sample_associatedLabMessages));
		}
	}

	public PathogenTestDto getPathogenTest() {
		return pathogenTest;
	}
}
