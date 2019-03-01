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
package de.symeda.sormas.ui.dashboard.surveillance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
//import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DiseaseBurdenGrid;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseTileComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	public DiseaseTileComponent(DiseaseBurdenDto disease) {		
		addTopLayout(disease.getDisease().toShortString(), disease.getCaseCount());	
		addStatsLayout(disease.getCaseDeathCount(), disease.getEventCount(), "Adabraka");
	}
	
	void addTopLayout (String diseaseName, Long casesCount) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(230, Unit.PIXELS);
		
		HorizontalLayout nameLayout = new HorizontalLayout();
		CssStyles.style(nameLayout, CssStyles.BACKGROUND_CONFIRMED_CRITERIA);
		nameLayout.setHeight(80, Unit.PIXELS);
		nameLayout.setWidth(100, Unit.PERCENTAGE);
		
		Label nameLabel = new Label(diseaseName);
		CssStyles.style(nameLabel, CssStyles.ERROR_COLOR_PRIMARY, CssStyles.ALIGN_CENTER);
		nameLayout.addComponent(nameLabel);
		nameLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_CENTER);
		
		layout.addComponent(nameLayout);
		layout.setExpandRatio(nameLayout, 1);
		
		HorizontalLayout countLayout = new HorizontalLayout();
		CssStyles.style(countLayout, CssStyles.BACKGROUND_SUSPECT_CRITERIA);
		countLayout.setHeight(80, Unit.PIXELS);
		countLayout.setWidth(100, Unit.PERCENTAGE);
		
		Label countLabel = new Label(casesCount.toString());
		CssStyles.style(countLabel, CssStyles.ERROR_COLOR_PRIMARY, CssStyles.ALIGN_CENTER);
		countLayout.addComponent(countLabel);
		countLayout.setComponentAlignment(countLabel, Alignment.MIDDLE_CENTER);
		
		layout.addComponent(countLayout);
		layout.setExpandRatio(countLayout, 0.65f);
		
		addComponent(layout);
	}
	
	void addStatsLayout (Long fatalities, Long events, String district) {
		VerticalLayout layout = new VerticalLayout();
		CssStyles.style(layout, CssStyles.BACKGROUND_SUB_CRITERIA);
		layout.setWidth(230, Unit.PIXELS);
		
		layout.addComponent(createStatsItem("Last report", district, false));
		layout.addComponent(createStatsItem("Fatalities", fatalities.toString(), true));
		layout.addComponent(createStatsItem("Number of events", events.toString(), false));
		
		addComponent(layout);
	}
	
	HorizontalLayout createStatsItem (String label, String value, boolean isCritical) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		
		Label nameLabel = new Label(label);
		CssStyles.style(nameLabel, isCritical ? CssStyles.LABEL_CRITICAL : "");
		layout.addComponent(nameLabel);
		layout.setExpandRatio(nameLabel, 1);
		
		Label valueLabel = new Label(value);
		CssStyles.style(valueLabel, CssStyles.ALIGN_CENTER, isCritical ? CssStyles.LABEL_CRITICAL : "");
		layout.addComponent(valueLabel);
		layout.setExpandRatio(valueLabel, 0.65f);
		
		return layout;	
	}
}
