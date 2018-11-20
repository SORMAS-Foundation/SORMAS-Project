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

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleListEntry extends HorizontalLayout {

	private final SampleIndexDto sample;
	private Button editButton;

	public SampleListEntry(SampleIndexDto sample) {

		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);
		this.sample = sample;

		VerticalLayout labelLayout = new VerticalLayout();
		labelLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(labelLayout);
		setExpandRatio(labelLayout, 1);

		// very hacky: clean up when needed elsewher! 
		HorizontalLayout topLabelLayout = new HorizontalLayout();
		topLabelLayout.setWidth(100, Unit.PERCENTAGE);
		labelLayout.addComponent(topLabelLayout);
		String htmlLeft = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE,
				DataHelper.toStringNullable(sample.getSampleMaterial()))
				+ LayoutUtil.div(I18nProperties.getPrefixFieldCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME)
						+ ": " + DateHelper.formatLocalShortDate(sample.getSampleDateTime()));
		Label labelLeft = new Label(htmlLeft, ContentMode.HTML);
		topLabelLayout.addComponent(labelLeft);

		String htmlRight;
		if (sample.getSampleTestResult() != null && sample.getSampleTestResult() != SampleTestResultType.PENDING) {
			htmlRight = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE + " "
					+ (sample.getSampleTestResult() == SampleTestResultType.POSITIVE ? CssStyles.LABEL_WARNING
							: (sample.getSampleTestResult() == SampleTestResultType.INDETERMINATE
									? CssStyles.LABEL_IMPORTANT
									: "")),
					DataHelper.toStringNullable(sample.getSampleTestResult()));
		} else if (sample.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
			htmlRight = LayoutUtil.divCss(
					CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE + " " + CssStyles.LABEL_WARNING,
					sample.getSpecimenCondition().toString());
		} else if (sample.isReferred()) {
			htmlRight = LayoutUtil.divCss(
					CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE + " " + CssStyles.LABEL_NOT, "Referred");
		} else if (sample.isReceived()) {
			htmlRight = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE, "Received")
					+ LayoutUtil.div(DateHelper.formatLocalShortDate(sample.getReceivedDate()));
		} else if (sample.isShipped()) {
			htmlRight = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE, "Shipped")
					+ LayoutUtil.div(DateHelper.formatLocalShortDate(sample.getShipmentDate()));
		} else {
			htmlRight = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE, "Not shipped yet");
		}
		Label labelRight = new Label(htmlRight, ContentMode.HTML);
		labelRight.addStyleName(CssStyles.ALIGN_RIGHT);
		topLabelLayout.addComponent(labelRight);
		topLabelLayout.setComponentAlignment(labelRight, Alignment.TOP_RIGHT);

		String htmlBottom = LayoutUtil.div(DataHelper.toStringNullable(sample.getLab()));
		Label labelBottom = new Label(htmlBottom, ContentMode.HTML);
		labelLayout.addComponent(labelBottom);
	}

	public void addEditListener(ClickListener editClickListener) {
		if (editButton == null) {
			editButton = new Button(FontAwesome.PENCIL);
			CssStyles.style(editButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_NO_PADDING);
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
