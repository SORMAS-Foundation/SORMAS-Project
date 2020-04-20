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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.AdditionalTestingStatus;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class SampleListEntry extends HorizontalLayout {

	private final SampleIndexDto sample;
	private Button editButton;

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
			topLeftLayout.addComponent(materialLabel);
			
			Label dateTimeLabel = new Label(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME)
					+ ": " + DateFormatHelper.formatDate(sample.getSampleDateTime()));
			topLeftLayout.addComponent(dateTimeLabel);
			
			if (sample.getSamplePurpose() == SamplePurpose.INTERNAL) {
				Label purposeLabel = new Label(SamplePurpose.INTERNAL.toString());
				topLeftLayout.addComponent(purposeLabel);
			} else {
				Label labLabel = new Label(DataHelper.toStringNullable(sample.getLab()));
				topLeftLayout.addComponent(labLabel);
			}
		}
		topLayout.addComponent(topLeftLayout);

		VerticalLayout topRightLayout = new VerticalLayout();
		{
			topRightLayout.addStyleName(CssStyles.ALIGN_RIGHT);
			topRightLayout.setMargin(false);
			topRightLayout.setSpacing(false);
			
			Label resultLabel = new Label();
			CssStyles.style(resultLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			if (sample.getPathogenTestResult() != null) {
				resultLabel.setValue(DataHelper.toStringNullable(sample.getPathogenTestResult()));
				if (sample.getPathogenTestResult() == PathogenTestResultType.POSITIVE) {
					resultLabel.addStyleName(CssStyles.LABEL_CRITICAL);
				} else if (sample.getPathogenTestResult() == PathogenTestResultType.INDETERMINATE){
					resultLabel.addStyleName(CssStyles.LABEL_WARNING);
				}
			} else if (sample.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
				resultLabel.setValue(DataHelper.toStringNullable(sample.getSpecimenCondition()));
				resultLabel.addStyleName(CssStyles.LABEL_WARNING);
			} 
			topRightLayout.addComponent(resultLabel);
			
			Label referredLabel = new Label();
			CssStyles.style(referredLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			if (sample.isReferred()) {
				referredLabel.setValue(I18nProperties.getCaption(Captions.sampleReferredShort));
				referredLabel.addStyleName(CssStyles.LABEL_NOT);
			} else if (sample.getSamplePurpose() != SamplePurpose.INTERNAL) {
				if (sample.isReceived()) {
					referredLabel.setValue(I18nProperties.getCaption(Captions.sampleReceived) + " " + DateFormatHelper.formatDate(sample.getReceivedDate()));
				} else if (sample.isShipped()) {
					referredLabel.setValue(I18nProperties.getCaption(Captions.sampleShipped) + " " + DateFormatHelper.formatDate((sample.getShipmentDate())));
				} else {
					referredLabel.setValue(I18nProperties.getCaption(Captions.sampleNotShippedLong));
				}
			}
			topRightLayout.addComponent(referredLabel);
		}
		topLayout.addComponent(topRightLayout);
		topLayout.setComponentAlignment(topRightLayout, Alignment.TOP_RIGHT);

		if (UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_VIEW) && sample.getAdditionalTestingStatus() != AdditionalTestingStatus.NOT_REQUESTED) {
			Label labelAdditionalTests = new Label(I18nProperties.getString(Strings.entityAdditionalTests) + " " + sample.getAdditionalTestingStatus().toString().toLowerCase());
			mainLayout.addComponent(labelAdditionalTests);
		}
	}

	public void addEditListener(ClickListener editClickListener) {
		if (editButton == null) {
			editButton = new Button(VaadinIcons.PENCIL);
			CssStyles.style(editButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}
		editButton.addClickListener(editClickListener);
	}

	public SampleIndexDto getSample() {
		return sample;
	}
}
