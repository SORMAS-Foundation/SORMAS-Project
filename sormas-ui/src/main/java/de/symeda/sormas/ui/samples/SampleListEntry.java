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
import com.vaadin.shared.ui.ContentMode;
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
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleListEntry extends HorizontalLayout {

	private final SampleIndexDto sample;
	private Button editButton;

	public SampleListEntry(SampleIndexDto sample) {

		setMargin(false);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);
		this.sample = sample;

		VerticalLayout labelLayout = new VerticalLayout();
		labelLayout.setWidth(100, Unit.PERCENTAGE);
		labelLayout.setMargin(false);
		labelLayout.setSpacing(false);
		addComponent(labelLayout);
		setExpandRatio(labelLayout, 1);

		// very hacky: clean up when needed elsewher! 
		HorizontalLayout topLabelLayout = new HorizontalLayout();
		topLabelLayout.setWidth(100, Unit.PERCENTAGE);
		labelLayout.addComponent(topLabelLayout);
		String htmlLeft = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE,
				DataHelper.toStringNullable(sample.getSampleMaterial()))
				+ LayoutUtil.div(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME)
						+ ": " + DateHelper.formatLocalShortDate(sample.getSampleDateTime()))
				+ LayoutUtil.div(DataHelper.toStringNullable(sample.getLab()));
		Label labelLeft = new Label(htmlLeft, ContentMode.HTML);
		labelLeft.setWidth(100, Unit.PERCENTAGE);
		topLabelLayout.addComponent(labelLeft);
		topLabelLayout.setExpandRatio(labelLeft, 0.7f);

		String htmlRight = "";
		if (sample.getPathogenTestResult() != null) {
			htmlRight = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE + " "
					+ (sample.getPathogenTestResult() == PathogenTestResultType.POSITIVE ? CssStyles.LABEL_CRITICAL
							: (sample.getPathogenTestResult() == PathogenTestResultType.INDETERMINATE
									? CssStyles.LABEL_WARNING
									: "")),
					DataHelper.toStringNullable(sample.getPathogenTestResult()));
		} else if (sample.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
			htmlRight = LayoutUtil.divCss(
					CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE + " " + CssStyles.LABEL_WARNING,
					sample.getSpecimenCondition().toString());
		} 
		
		
		if (sample.isReferred()) {
			htmlRight += LayoutUtil.divCss(
					CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE + " " + CssStyles.LABEL_NOT, I18nProperties.getCaption(Captions.sampleReferredShort));
		} else if (sample.isReceived()) {
			htmlRight += LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE, I18nProperties.getCaption(Captions.sampleReceived))
					+ LayoutUtil.div(DateHelper.formatLocalShortDate(sample.getReceivedDate()));
		} else if (sample.isShipped()) {
			htmlRight += LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE, I18nProperties.getCaption(Captions.sampleShipped))
					+ LayoutUtil.div(DateHelper.formatLocalShortDate(sample.getShipmentDate()));
		} else {
			htmlRight += LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE, I18nProperties.getCaption(Captions.sampleNotShippedLong));
		}
		Label labelRight = new Label(htmlRight, ContentMode.HTML);
		labelRight.setWidth(100, Unit.PERCENTAGE);
		labelRight.addStyleName(CssStyles.ALIGN_RIGHT);
		topLabelLayout.addComponent(labelRight);
		topLabelLayout.setExpandRatio(labelRight, 0.3f);
		topLabelLayout.setComponentAlignment(labelRight, Alignment.TOP_RIGHT);
		
		if (UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
			Label labelAdditionalTests = new Label(I18nProperties.getString(Strings.entityAdditionalTests) + " " + sample.getAdditionalTestingStatus().toString().toLowerCase());
			labelLayout.addComponent(labelAdditionalTests);
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
