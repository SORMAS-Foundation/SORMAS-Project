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
package de.symeda.sormas.ui.dashboard.statistics;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardStatisticsPercentageElement extends VerticalLayout {

	private SvgBarElement svgBarElement;
	private Label percentageLabel;

	public DashboardStatisticsPercentageElement(String caption, String svgFillClass) {
		this.setMargin(false);
		this.setSpacing(false);

		HorizontalLayout captionAndValueLayout = new HorizontalLayout();
		captionAndValueLayout.setWidth(100, Unit.PERCENTAGE);

		Label captionLabel = new Label(caption);
		captionLabel.setWidthUndefined();
		CssStyles.style(captionLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_BOLD);
		captionAndValueLayout.addComponent(captionLabel);

		percentageLabel = new Label();
		CssStyles.style(percentageLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD);
		percentageLabel.setWidthUndefined();
		captionAndValueLayout.addComponent(percentageLabel);

		captionAndValueLayout.setComponentAlignment(captionLabel, Alignment.MIDDLE_LEFT);
		captionAndValueLayout.setComponentAlignment(percentageLabel, Alignment.MIDDLE_RIGHT);

		addComponent(captionAndValueLayout);

		svgBarElement = new SvgBarElement(svgFillClass);
		svgBarElement.setWidth(100, Unit.PERCENTAGE);
		addComponent(svgBarElement);
	}

	public void updatePercentageValue(int percentageValue) {
		percentageLabel.setValue(Integer.toString(percentageValue) + "%");
		svgBarElement.updateSvg(percentageValue);
	}

	public void updatePercentageValueWithCount(int count, int percentageValue) {
		percentageLabel.setValue(Integer.toString(count) + " (" + Integer.toString(percentageValue) + " %)");
		svgBarElement.updateSvg(percentageValue);
	}
}
