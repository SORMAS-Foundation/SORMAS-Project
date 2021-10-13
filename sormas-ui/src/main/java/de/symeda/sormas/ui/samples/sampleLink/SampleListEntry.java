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
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SamplingReason;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class SampleListEntry extends HorizontalLayout {

	private final SampleIndexDto sample;
	private Button editButton;
	private Button associatedLabMessagesButton;

	public SampleListEntry(SampleIndexDto sample) {

		this.sample = sample;

		setMargin(false);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1);

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		mainLayout.addComponent(topLayout);

		VerticalLayout topLeftLayout = new VerticalLayout();
		{
			topLeftLayout.setMargin(false);
			topLeftLayout.setSpacing(false);

			Label materialLabel = new Label(DataHelper.toStringNullable(sample.getSampleMaterial()));
			CssStyles.style(materialLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			materialLabel.setWidth(50, Unit.PERCENTAGE);
			topLeftLayout.addComponent(materialLabel);

			Label resultLabel = new Label();
			CssStyles.style(resultLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			if (sample.getPathogenTestResult() != null) {
				resultLabel.setValue(DataHelper.toStringNullable(sample.getPathogenTestResult()));
				if (sample.getPathogenTestResult() == PathogenTestResultType.POSITIVE) {
					resultLabel.addStyleName(CssStyles.LABEL_CRITICAL);
				} else if (sample.getPathogenTestResult() == PathogenTestResultType.INDETERMINATE) {
					resultLabel.addStyleName(CssStyles.LABEL_WARNING);
				}
			} else if (sample.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
				resultLabel.setValue(DataHelper.toStringNullable(sample.getSpecimenCondition()));
				resultLabel.addStyleName(CssStyles.LABEL_WARNING);
			}
			topLeftLayout.addComponent(resultLabel);

			Label referredLabel = new Label();
			CssStyles.style(referredLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			if (sample.isReferred()) {
				referredLabel.setValue(I18nProperties.getCaption(Captions.sampleReferredShort));
				referredLabel.addStyleName(CssStyles.LABEL_NOT);
			} else if (sample.getSamplePurpose() != SamplePurpose.INTERNAL) {
				if (sample.isReceived()) {
					referredLabel
						.setValue(I18nProperties.getCaption(Captions.sampleReceived) + " " + DateFormatHelper.formatDate(sample.getReceivedDate()));
				} else if (sample.isShipped()) {
					referredLabel
						.setValue(I18nProperties.getCaption(Captions.sampleShipped) + " " + DateFormatHelper.formatDate((sample.getShipmentDate())));
				} else {
					referredLabel.setValue(I18nProperties.getCaption(Captions.sampleNotShippedLong));
				}
			}
			topLeftLayout.addComponent(referredLabel);

			Label dateTimeLabel = new Label(
				I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME) + ": "
					+ DateFormatHelper.formatDate(sample.getSampleDateTime()));
			topLeftLayout.addComponent(dateTimeLabel);

			if (sample.getSamplePurpose() == SamplePurpose.INTERNAL) {
				Label purposeLabel = new Label(SamplePurpose.INTERNAL.toString());
				topLeftLayout.addComponent(purposeLabel);
			} else {
				Label labLabel = new Label(DataHelper.toStringNullable(sample.getLab()));
				topLeftLayout.addComponent(labLabel);
			}

			SamplingReason samplingReason = sample.getSamplingReason();
			if (samplingReason != null) {
				String samplingReasonCaption = samplingReason.toString();
				if (samplingReason == SamplingReason.OTHER_REASON && sample.getSamplingReasonDetails() != null) {
					samplingReasonCaption = sample.getSamplingReasonDetails();
				}
				Label samplingReasonLabel =
					new Label(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLING_REASON) + ": " + samplingReasonCaption);
				topLeftLayout.addComponent(samplingReasonLabel);
			}

			Label testCountLabel = new Label(
				I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleIndexDto.PATHOGEN_TEST_COUNT) + ": " + sample.getPathogenTestCount());
			topLeftLayout.addComponent(testCountLabel);

			if (sample.getPathogenTestCount() > 0) {
				VerticalLayout latestTestLayout = new VerticalLayout();
				latestTestLayout.setMargin(false);
				latestTestLayout.setSpacing(false);

				Label heading = new Label(I18nProperties.getCaption(Captions.latestPathogenTest));
				CssStyles.style(heading, CssStyles.LABEL_BOLD);
				PathogenTestDto latestTest = FacadeProvider.getPathogenTestFacade().getLatestPathogenTest(sample.getUuid());
				Label testDate = new Label(
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME) + ": "
						+ DateFormatHelper.formatDate(latestTest.getTestDateTime()));
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

		VerticalLayout topRightLayout = new VerticalLayout();
		{
			topRightLayout.addStyleName(CssStyles.ALIGN_RIGHT);
			topRightLayout.setMargin(false);
			topRightLayout.setSpacing(false);

			topLayout.addComponent(topLeftLayout);
		}
		topLayout.addComponent(topRightLayout);
		topLayout.setComponentAlignment(topRightLayout, Alignment.TOP_RIGHT);

		if (UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)
			&& sample.getAdditionalTestingStatus() != AdditionalTestingStatus.NOT_REQUESTED
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.ADDITIONAL_TESTS)) {
			Label labelAdditionalTests = new Label(
				I18nProperties.getString(Strings.entityAdditionalTests) + " " + sample.getAdditionalTestingStatus().toString().toLowerCase());
			mainLayout.addComponent(labelAdditionalTests);
		}
	}

	public void addEditListener(ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"edit-sample-" + sample.getUuid(),
				null,
				VaadinIcons.PENCIL,
				editClickListener,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.TOP_RIGHT);
			setExpandRatio(editButton, 0);
		}
	}

	public void addAssociatedLabMessagesListener(ClickListener associatedLabMessagesClickListener) {
		if (associatedLabMessagesButton == null) {
			associatedLabMessagesButton = ButtonHelper.createIconButtonWithCaption(
				"see-associated-lab-messages-" + sample.getUuid(),
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

	public SampleIndexDto getSample() {
		return sample;
	}
}
