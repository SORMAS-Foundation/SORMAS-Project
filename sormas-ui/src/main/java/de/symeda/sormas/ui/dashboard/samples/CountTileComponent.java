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
package de.symeda.sormas.ui.dashboard.samples;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleCountType;
import de.symeda.sormas.ui.utils.CssStyles;

public class CountTileComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	public CountTileComponent(SampleCountType sampleCountType, Long count) {
		setMargin(false);
		setSpacing(false);

		addTopLayout(sampleCountType, count);
	}

	private void addTopLayout(SampleCountType sampleCountType, Long count) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		CssStyles.style(layout, CssStyles.getSampleCountColor(sampleCountType));
		layout.setHeight(100, Unit.PIXELS);
		layout.setWidth(100, Unit.PERCENTAGE);

		VerticalLayout countLayout = new VerticalLayout();
		countLayout.setMargin(false);
		countLayout.setSpacing(false);
		CssStyles.style(countLayout, CssStyles.getSampleCountColor(sampleCountType), CssStyles.BACKGROUND_DARKER);
		countLayout.setHeight(100, Unit.PERCENTAGE);
		countLayout.setWidth(100, Unit.PERCENTAGE);

		Label countLabel = new Label(count.toString());
		CssStyles.style(countLabel, CssStyles.LABEL_WHITE, CssStyles.LABEL_BOLD, CssStyles.LABEL_XXXLARGE,
				CssStyles.ALIGN_CENTER, CssStyles.VSPACE_TOP_4);
		countLayout.addComponent(countLabel);
		countLayout.setComponentAlignment(countLabel, Alignment.MIDDLE_CENTER);

		layout.addComponent(countLayout);
		layout.setExpandRatio(countLayout, 1);

		VerticalLayout nameAndOutbreakLayout = new VerticalLayout();
		nameAndOutbreakLayout.setMargin(false);
		nameAndOutbreakLayout.setSpacing(false);
		nameAndOutbreakLayout.setHeight(100, Unit.PERCENTAGE);
		nameAndOutbreakLayout.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout nameLayout = new HorizontalLayout();
		nameLayout.setMargin(false);
		nameLayout.setSpacing(false);
		nameLayout.setWidth(100, Unit.PERCENTAGE);
		nameLayout.setHeight(100, Unit.PERCENTAGE);
		Label nameLabel = new Label(sampleCountType.toString());
		CssStyles.style(nameLabel, CssStyles.LABEL_WHITE,
				nameLabel.getValue().length() > 12 ? CssStyles.LABEL_LARGE : CssStyles.LABEL_XLARGE,
				CssStyles.LABEL_BOLD, CssStyles.ALIGN_CENTER, CssStyles.LABEL_UPPERCASE);
		nameLayout.addComponent(nameLabel);
		nameLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_CENTER);
		nameAndOutbreakLayout.addComponent(nameLayout);
		nameAndOutbreakLayout.setExpandRatio(nameLayout, 1);

		layout.addComponent(nameAndOutbreakLayout);
		layout.setExpandRatio(nameAndOutbreakLayout, 1);

		addComponent(layout);
	}

}
