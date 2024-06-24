/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.samples.sampleLink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.AdditionalTestingStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleListEntryDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SamplingReason;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

@SuppressWarnings("serial")
public class SampleListEntry extends SideComponentField {

	private final SampleListEntryDto sampleListEntryDto;
	private Button associatedLabMessagesButton;

	public SampleListEntry(SampleListEntryDto sampleListEntryDto) {

		this.sampleListEntryDto = sampleListEntryDto;

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		addComponentToField(topLayout);

		VerticalLayout topLeftLayout = new VerticalLayout();
		{
			topLeftLayout.setMargin(false);
			topLeftLayout.setSpacing(false);

			Label materialLabel = new Label(DataHelper.toStringNullable(sampleListEntryDto.getSampleMaterial()));
			CssStyles.style(materialLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			materialLabel.setWidth(50, Unit.PERCENTAGE);
			topLeftLayout.addComponent(materialLabel);

			Label resultLabel = new Label();
			CssStyles.style(resultLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			if (sampleListEntryDto.getPathogenTestResult() != null) {
				resultLabel.setValue(DataHelper.toStringNullable(sampleListEntryDto.getPathogenTestResult()));
				if (sampleListEntryDto.getPathogenTestResult() == PathogenTestResultType.POSITIVE) {
					resultLabel.addStyleName(CssStyles.LABEL_CRITICAL);
				} else if (sampleListEntryDto.getPathogenTestResult() == PathogenTestResultType.INDETERMINATE) {
					resultLabel.addStyleName(CssStyles.LABEL_WARNING);
				}
			} else if (sampleListEntryDto.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
				resultLabel.setValue(DataHelper.toStringNullable(sampleListEntryDto.getSpecimenCondition()));
				resultLabel.addStyleName(CssStyles.LABEL_WARNING);
			}
			topLeftLayout.addComponent(resultLabel);

			Label referredLabel = new Label();
			CssStyles.style(referredLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			if (sampleListEntryDto.isReferred()) {
				referredLabel.setValue(I18nProperties.getCaption(Captions.sampleReferredShort));
				referredLabel.addStyleName(CssStyles.LABEL_NOT);
			} else if (sampleListEntryDto.getSamplePurpose() != SamplePurpose.INTERNAL) {
				if (sampleListEntryDto.isReceived()) {
					referredLabel.setValue(
						I18nProperties.getCaption(Captions.sampleReceived) + " " + DateFormatHelper.formatDate(sampleListEntryDto.getReceivedDate()));
				} else if (sampleListEntryDto.isShipped()) {
					referredLabel.setValue(
						I18nProperties.getCaption(Captions.sampleShipped) + " "
							+ DateFormatHelper.formatDate((sampleListEntryDto.getShipmentDate())));
				} else {
					referredLabel.setValue(I18nProperties.getCaption(Captions.sampleNotShippedLong));
				}
			}
			topLeftLayout.addComponent(referredLabel);

			Label dateTimeLabel = new Label(
				I18nProperties.getPrefixCaption(SampleListEntryDto.I18N_PREFIX, SampleListEntryDto.SAMPLE_DATE_TIME) + ": "
					+ DateFormatHelper.formatDate(sampleListEntryDto.getSampleDateTime()));
			topLeftLayout.addComponent(dateTimeLabel);

			if (sampleListEntryDto.getSamplePurpose() == SamplePurpose.INTERNAL) {
				Label purposeLabel = new Label(SamplePurpose.INTERNAL.toString());
				topLeftLayout.addComponent(purposeLabel);
			} else {
				Label labLabel = new Label(DataHelper.toStringNullable(sampleListEntryDto.getLab()));
				topLeftLayout.addComponent(labLabel);
			}

			SamplingReason samplingReason = sampleListEntryDto.getSamplingReason();
			if (samplingReason != null) {
				String samplingReasonCaption = samplingReason.toString();
				if (samplingReason == SamplingReason.OTHER_REASON && sampleListEntryDto.getSamplingReasonDetails() != null) {
					samplingReasonCaption = sampleListEntryDto.getSamplingReasonDetails();
				}
				Label samplingReasonLabel = new Label(
					I18nProperties.getPrefixCaption(SampleListEntryDto.I18N_PREFIX, SampleListEntryDto.SAMPLING_REASON) + ": "
						+ samplingReasonCaption);
				topLeftLayout.addComponent(samplingReasonLabel);
			}

			Label testCountLabel = new Label(
				I18nProperties.getPrefixCaption(SampleListEntryDto.I18N_PREFIX, SampleListEntryDto.PATHOGEN_TEST_COUNT) + ": "
					+ sampleListEntryDto.getPathogenTestCount());
			topLeftLayout.addComponent(testCountLabel);

			if (sampleListEntryDto.getPathogenTestCount() > 0) {
				VerticalLayout latestTestLayout = new VerticalLayout();
				latestTestLayout.setMargin(false);
				latestTestLayout.setSpacing(false);

				Label heading = new Label(I18nProperties.getCaption(Captions.latestPathogenTest));
				CssStyles.style(heading, CssStyles.LABEL_BOLD);
				PathogenTestDto latestTest = FacadeProvider.getPathogenTestFacade().getLatestPathogenTest(sampleListEntryDto.getUuid());
				Label testDate = new Label(
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME) + ": "
						+ DateFormatHelper.formatLocalDateTime(latestTest.getTestDateTime()));
				HorizontalLayout bottomLayout = new HorizontalLayout();
				Label testType = new Label(latestTest.getTestType().toString());
				bottomLayout.addComponent(testType);
				if (latestTest.getCqValue() != null) {
					Label cqValue = new Label(
						I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.CQ_VALUE) + ": " + latestTest.getCqValue());
					cqValue.addStyleName(CssStyles.ALIGN_RIGHT);
					bottomLayout.addComponent(cqValue);
				}
				latestTestLayout.addComponents(heading, testDate, bottomLayout);
				topLeftLayout.addComponent(latestTestLayout);
			}
		}

		topLayout.addComponent(topLeftLayout);
		topLayout.setComponentAlignment(topLeftLayout, Alignment.TOP_LEFT);

		if (sampleListEntryDto.getAdditionalTestingStatus() != AdditionalTestingStatus.NOT_REQUESTED
			&& UiUtil.permitted(FeatureType.ADDITIONAL_TESTS, UserRight.ADDITIONAL_TEST_VIEW)) {
			Label labelAdditionalTests = new Label(
				I18nProperties.getString(Strings.entityAdditionalTests) + " "
					+ sampleListEntryDto.getAdditionalTestingStatus().toString().toLowerCase());
			addComponentToField(labelAdditionalTests);
		}
	}

	public void addAssociatedLabMessagesListener(ClickListener associatedLabMessagesClickListener) {
		if (associatedLabMessagesButton == null) {
			associatedLabMessagesButton = ButtonHelper.createIconButtonWithCaption(
				"see-associated-lab-messages-" + sampleListEntryDto.getUuid(),
				null,
				VaadinIcons.NOTEBOOK,
				associatedLabMessagesClickListener,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(associatedLabMessagesButton);
			setComponentAlignment(associatedLabMessagesButton, Alignment.TOP_RIGHT);
			setExpandRatio(associatedLabMessagesButton, 0);
			associatedLabMessagesButton.setDescription(I18nProperties.getDescription(Descriptions.Sample_associatedLabMessages));
		}
	}

	public SampleListEntryDto getSampleListEntryDto() {
		return sampleListEntryDto;
	}
}
