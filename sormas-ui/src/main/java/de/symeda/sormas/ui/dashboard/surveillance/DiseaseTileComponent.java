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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
//import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DiseaseBurdenGrid;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseTileComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	public DiseaseTileComponent(DiseaseBurdenDto diseaseBurden) {		
		addTopLayout(diseaseBurden.getDisease().toShortString(), diseaseBurden.getCaseCount());	
		addStatsLayout(diseaseBurden.getCaseDeathCount(), diseaseBurden.getEventCount(), diseaseBurden.getLastReportedCommunityName());
	}
	
	void addTopLayout (String diseaseName, Long casesCount) {
		HorizontalLayout layout = new HorizontalLayout();
		CssStyles.style(layout, CssStyles.getDiseaseColor(diseaseName));
		layout.setHeight(75, Unit.PIXELS);
		layout.setWidth(100, Unit.PERCENTAGE);
		
		HorizontalLayout nameLayout = new HorizontalLayout();
		nameLayout.setHeight(100, Unit.PERCENTAGE);
		nameLayout.setWidth(100, Unit.PERCENTAGE);
		
		Label nameLabel = new Label(
				"<div style=\"font-size: " + (diseaseName.length() > 10 ? 18 : 20) + "px; "
				+ "color: white; text-align: center; text-transform: uppercase;\">" 
				+ diseaseName 
				+ "</div>"
				, ContentMode.HTML);
		CssStyles.style(nameLabel, CssStyles.ALIGN_CENTER);
		nameLayout.addComponent(nameLabel);
		nameLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_CENTER);
		
		layout.addComponent(nameLayout);
		layout.setExpandRatio(nameLayout, 1);
		
		HorizontalLayout countLayout = new HorizontalLayout();
		CssStyles.style(countLayout, CssStyles.getDiseaseColor(diseaseName), CssStyles.BACKGROUND_DARKEN);
		countLayout.setHeight(100, Unit.PERCENTAGE);
		countLayout.setWidth(100, Unit.PERCENTAGE);
		
		Label countLabel = new Label(
				"<div style=\"font-size: 40px; color: white; text-align: center; font-weight: bold;\">" 
				+ casesCount.toString() 
				+ "</div>"
				, ContentMode.HTML);
		countLayout.addComponent(countLabel);
		countLayout.setComponentAlignment(countLabel, Alignment.MIDDLE_CENTER);
		
		layout.addComponent(countLayout);
		layout.setExpandRatio(countLayout, 0.65f);
		
		addComponent(layout);
	}
	
	void addStatsLayout (Long fatalities, Long events, String community) {
		HorizontalLayout container = new HorizontalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(container, CssStyles.BACKGROUND_COLOR_HIGHLIGHT);
		
		VerticalLayout layout = new VerticalLayout();
		CssStyles.style(layout, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_4);
		
		layout.addComponent(createStatsItem("Last report", community.length() == 0 ? "None" : community, false));
		layout.addComponent(createStatsItem("Fatalities", fatalities.toString(), fatalities > 0));
		layout.addComponent(createStatsItem("Number of events", events.toString(), false));
		
		container.addComponent(layout);
		container.setExpandRatio(layout, 1);
		addComponent(container);
	}
	
	HorizontalLayout createStatsItem (String label, String value, boolean isCritical) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		
		Label nameLabel = new Label(label);
		CssStyles.style(nameLabel, isCritical ? CssStyles.LABEL_CRITICAL : "", CssStyles.HSPACE_LEFT_3, CssStyles.LABEL_PRIMARY);
		layout.addComponent(nameLabel);
		layout.setExpandRatio(nameLabel, 1);
		
		Label valueLabel = new Label(value);
		CssStyles.style(valueLabel, CssStyles.ALIGN_CENTER, isCritical ? CssStyles.LABEL_CRITICAL : "", CssStyles.LABEL_PRIMARY);
		layout.addComponent(valueLabel);
		layout.setExpandRatio(valueLabel, 0.65f);
		
		return layout;	
	}
}
