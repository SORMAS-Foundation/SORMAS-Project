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
package de.symeda.sormas.ui.dashboard.samples;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.sample.SampleCountType;
import de.symeda.sormas.ui.utils.CssStyles;

public class SampleCountTileComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	public SampleCountTileComponent(SampleCountType sampleCountType, Long count, Long countDifference) {
		createLayout(sampleCountType, count == null ? 0 : count, countDifference == null ? 0 : countDifference);
		setMargin(false);
		setSpacing(false);
	}

	private void createLayout(SampleCountType sampleCountType, Long count, Long countDifference) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		CssStyles.style(layout, CssStyles.getSampleCountColor(sampleCountType));
		layout.setHeight(80, Unit.PIXELS);
		layout.setWidth(100, Unit.PERCENTAGE);

		addCountLayout(layout, count, countDifference);
		addLabelLayout(layout, sampleCountType);
	}

	private void addCountLayout(VerticalLayout layout, Long count, Long countDifference) {
		VerticalLayout countLayout = new VerticalLayout();
		countLayout.setMargin(false);
		countLayout.setSpacing(false);
		countLayout.setHeight(65, Unit.PIXELS);
		countLayout.setWidth(100, Unit.PERCENTAGE);

		Label countLabel = new Label(count.toString());
		CssStyles.style(
			countLabel,
			CssStyles.LABEL_WHITE,
			CssStyles.LABEL_BOLD,
			CssStyles.LABEL_XXXLARGE,
			CssStyles.ALIGN_CENTER,
			CssStyles.VSPACE_TOP_NONE);
		countLayout.addComponent(countLabel);
		countLayout.setComponentAlignment(countLabel, Alignment.MIDDLE_CENTER);

		String chevronIconType = VaadinIcons.CHEVRON_LEFT.getHtml();
		Label countLabelDifference = new Label(countDifference.toString(), ContentMode.HTML);

		if (countDifference < count) {
			chevronIconType = VaadinIcons.CHEVRON_UP.getHtml();
		} else if (countDifference > count) {
			chevronIconType = VaadinIcons.CHEVRON_DOWN.getHtml();
		} else {
			chevronIconType = VaadinIcons.CHEVRON_RIGHT.getHtml();
		}
		countLabelDifference.setValue(
			"<div class=\"v-label v-widget " + CssStyles.LABEL_WHITE + " v-label-" + CssStyles.LABEL_WHITE
				+ " align-center v-label-align-center bold v-label-bold v-has-width\" " + ">" + "<span class=\"v-icon\" style=\"font-size: 12px;\">"
				+ chevronIconType + " </span> " + countDifference.toString() + "</div>");

		CssStyles.style(
			countLabelDifference,
			CssStyles.LABEL_WHITE,
			CssStyles.LABEL_BOLD,
			CssStyles.LABEL_VERTICAL_ALIGN_SUPER,
			CssStyles.LABEL_XLARGE,
			CssStyles.VSPACE_4);
		countLayout.addComponent(countLabelDifference);
		countLayout.setComponentAlignment(countLabelDifference, Alignment.BOTTOM_CENTER);

		layout.addComponent(countLayout);
		layout.setExpandRatio(countLayout, 1);
	}

	private void addLabelLayout(VerticalLayout layout, SampleCountType sampleCountType) {
		VerticalLayout nameAndOutbreakLayout = new VerticalLayout();
		nameAndOutbreakLayout.setMargin(new MarginInfo(1));
		nameAndOutbreakLayout.setSpacing(false);
		nameAndOutbreakLayout.setHeight(120, Unit.PERCENTAGE);
		nameAndOutbreakLayout.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout nameLayout = new HorizontalLayout();
		nameLayout.setMargin(false);
		nameLayout.setSpacing(false);
		nameLayout.setWidth(100, Unit.PERCENTAGE);
		nameLayout.setHeight(120, Unit.PERCENTAGE);
		CssStyles.style(nameLayout, CssStyles.getSampleCountColor(sampleCountType), CssStyles.BACKGROUND_DARKER);
		Label nameLabel = new Label(sampleCountType.toString());
		CssStyles.style(
			nameLabel,
			CssStyles.LABEL_WHITE,
			nameLabel.getValue().length() > 12 ? CssStyles.LABEL_LARGE : CssStyles.LABEL_LARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.ALIGN_CENTER,
			CssStyles.LABEL_UPPERCASE);
		nameLayout.addComponent(nameLabel);
		nameLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_CENTER);
		nameAndOutbreakLayout.addComponent(nameLayout);
		nameAndOutbreakLayout.setExpandRatio(nameLayout, 1);

		layout.addComponent(nameAndOutbreakLayout);
		layout.setExpandRatio(nameAndOutbreakLayout, 1);

		addComponent(layout);
	}

}
