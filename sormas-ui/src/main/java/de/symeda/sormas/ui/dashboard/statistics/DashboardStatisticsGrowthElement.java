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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardStatisticsGrowthElement extends VerticalLayout {

	protected HorizontalLayout growthLayout;
	protected Label countLabel;
	protected Label growthLabel;
	protected Label percentageLabel;
	protected Label captionLabel;

	protected String caption;
	private String captionClass;

	public DashboardStatisticsGrowthElement(String caption) {
		this(caption, null, null);
	}

	public DashboardStatisticsGrowthElement(String caption, String captionClass, Alignment alignment) {
		this.setMargin(false);
		this.setSpacing(false);

		this.caption = caption;
		this.captionClass = captionClass;

		if (alignment != null) {
			setDefaultComponentAlignment(alignment);
		}
		CssStyles.style(this, CssStyles.VSPACE_3);

		growthLayout = new HorizontalLayout();

		createCountLabel();
		growthLayout.addComponent(countLabel);
		createGrowthLabel();
		growthLayout.addComponent(growthLabel);
		growthLayout.setComponentAlignment(growthLabel, Alignment.MIDDLE_CENTER);
		createPercentageLabel();
		growthLayout.addComponent(percentageLabel);

		addComponent(growthLayout);

		createCaptionLabel();
		addComponent(captionLabel);
	}

	protected void createCountLabel() {
		countLabel = new Label();
		CssStyles.style(countLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD, CssStyles.HSPACE_RIGHT_4);
	}

	protected void createGrowthLabel() {
		growthLabel = new Label();
		growthLabel.setHeightUndefined();
		growthLabel.setContentMode(ContentMode.HTML);
		CssStyles.style(growthLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD, CssStyles.HSPACE_RIGHT_4);

	}

	protected void createPercentageLabel() {
		percentageLabel = new Label();
		CssStyles.style(percentageLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD);
	}

	protected void createCaptionLabel() {
		captionLabel = new Label(caption);
		captionLabel.setWidthUndefined();
		CssStyles.style(captionLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_UPPERCASE, CssStyles.LABEL_BOLD, captionClass);

	}

	public void update(int count, int percentage, boolean increaseIsPositive) {
		countLabel.setValue(Integer.toString(count));
		percentageLabel.setValue(percentage != Integer.MIN_VALUE ? (percentage + " %") : "");
		CssStyles.removeStyles(growthLabel, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_POSITIVE, CssStyles.LABEL_IMPORTANT);
		if (percentage > 0) {
			growthLabel.setValue(VaadinIcons.CHEVRON_UP.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.LABEL_POSITIVE : CssStyles.LABEL_CRITICAL);
		} else if (percentage < 0) {
			growthLabel.setValue(VaadinIcons.CHEVRON_DOWN.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.LABEL_CRITICAL : CssStyles.LABEL_POSITIVE);
		} else {
			growthLabel.setValue(VaadinIcons.CHEVRON_RIGHT.getHtml());
			CssStyles.style(growthLabel, CssStyles.LABEL_IMPORTANT);
		}
	}
}
