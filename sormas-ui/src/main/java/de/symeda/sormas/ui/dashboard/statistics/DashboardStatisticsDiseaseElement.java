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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardStatisticsDiseaseElement extends HorizontalLayout {

	public DashboardStatisticsDiseaseElement(String caption, int count, int previousCount) {
		setMargin(false);
		setSpacing(true);
		Label captionLabel = new Label(caption);
		captionLabel.setWidthUndefined();
		CssStyles.style(captionLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_BOLD, CssStyles.LABEL_LARGE);
		addComponent(captionLabel);

		Label lineLabel = new Label("&nbsp;", ContentMode.HTML);
		CssStyles.style(lineLabel, CssStyles.LABEL_BOTTOM_LINE);
		addComponent(lineLabel);

		Label countLabel = new Label(Integer.toString(count));
		countLabel.setWidthUndefined();
		CssStyles.style(countLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_BOLD, CssStyles.LABEL_LARGE);
		addComponent(countLabel);

		Label growthLabel = new Label();
		growthLabel.setContentMode(ContentMode.HTML);
		growthLabel.setWidth(15, Unit.PIXELS);

		CssStyles.removeStyles(growthLabel, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_POSITIVE, CssStyles.LABEL_IMPORTANT);
		if (count > previousCount) {
			growthLabel.setValue(VaadinIcons.CHEVRON_UP.getHtml());
			CssStyles.style(growthLabel, CssStyles.LABEL_CRITICAL);
		} else if (count == previousCount) {
			growthLabel.setValue(VaadinIcons.CHEVRON_RIGHT.getHtml());
			CssStyles.style(growthLabel, CssStyles.LABEL_IMPORTANT, CssStyles.ALIGN_CENTER);
		} else {
			growthLabel.setValue(VaadinIcons.CHEVRON_DOWN.getHtml());
			CssStyles.style(growthLabel, CssStyles.LABEL_POSITIVE);
		}

		CssStyles.style(growthLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_LARGE);
		addComponent(growthLabel);

		setExpandRatio(lineLabel, 1);
	}
}
