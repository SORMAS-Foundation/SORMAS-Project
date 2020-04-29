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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public class CaseCountDifferenceComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private DashboardDataProvider dashboardDataProvider;
	private HighChart chart;
	private Label subtitleLabel;

	public CaseCountDifferenceComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		Label title = new Label(I18nProperties.getCaption(Captions.dashboardDiseaseDifference));
		CssStyles.style(title, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		subtitleLabel = new Label();
		updateSubHeader();

		chart = new HighChart();
		chart.setSizeFull();

		// layout
		setWidth(100, Unit.PERCENTAGE);

		addComponent(title);
		addComponent(subtitleLabel);
		addComponent(chart);
		setExpandRatio(chart, 1);

		setMargin(new MarginInfo(true, true, false, true));
		setSpacing(false);
		setSizeFull();
	}

	public void refresh(int limitDiseasesCount) {
		List<DiseaseBurdenDto> diseasesBurden = dashboardDataProvider.getDiseasesBurden();
		
		Stream<DiseaseBurdenDto> diseasesBurdenStream = diseasesBurden.stream()
									   .sorted((dto1, dto2) -> {
										   long caseDifference1 = dto1.getCasesDifference();
										   long caseDifference2 = dto2.getCasesDifference();
										   if (caseDifference1 == 0) caseDifference1 = Long.MIN_VALUE;
										   if (caseDifference2 == 0) caseDifference2 = Long.MIN_VALUE;
										   return Long.compare(caseDifference2, caseDifference1);
									   });
									   
		if (limitDiseasesCount > 0) {
			diseasesBurdenStream = diseasesBurdenStream.limit(limitDiseasesCount);
		}
		diseasesBurden = diseasesBurdenStream.collect(Collectors.toList());

		refreshChart(diseasesBurden);
		if (limitDiseasesCount > 0) {
			chart.setHeight(diseasesBurden.size() * 20 + 70, Unit.PIXELS); // compact mode
		} else {
			chart.setHeight(diseasesBurden.size() * 40 + 70, Unit.PIXELS);
		}
	}

	private void refreshChart(List<DiseaseBurdenDto> data) {	
		int maxCasesDifference = data.stream().map(d -> Math.abs(d.getCasesDifference())).max(Long::compare).orElse(5L).intValue();
		maxCasesDifference = Math.max(5, maxCasesDifference);
		
		StringBuilder hcjs = new StringBuilder();
		
		hcjs.append(
			"var options = {" + 
				"plotOptions: {" + 
					"bar: {" + 
						"colorByPoint: true," +
						"groupPadding: 0.05" + 
					"}" + 
				"}," + 
				 
				"chart: {" + 
					"type: 'bar'," + 
					"styledMode: true," + 
				"}," + 
					
				"series: [" +
					"{" +
						"name: ''," + 
						"data: [" +
							data.stream().map((d) -> 
							"{" +
								"y: " + d.getCasesDifference() + "," +
								"className: '" + CssStyles.getDiseaseColor(d.getDisease()) + " " + CssStyles.BACKGROUND_DARKER + "'," +
							"},")
							.reduce((fullText, nextText) -> fullText + nextText).orElse("") + 
						"]," +
					"}" +
				"]," +
					
				"xAxis: {" +
					"categories: [" + 
						data.stream().map((d) -> "'" + d.getDisease().toString() + "'").reduce((fullText, nextText) -> fullText + ", " + nextText).orElse("") + 
					"]" +
				"}," + 
					
				"yAxis: {" + 
					"title: { text: '" + I18nProperties.getCaption(Captions.dashboardDiseaseDifferenceYAxisLabel) + "' }," + 
					"allowDecimals: false," + 
					"max: " + maxCasesDifference + "," + 
					"min: " + -maxCasesDifference + "," + 
				"}," + 
					
				"tooltip: { " + 
					"headerFormat: '<b>{point.x}: </b>{point.y}<br/>'," + 
					"pointFormat: ' '" + 
				"}," + 
				
				"title: { text: '' }, " + 
				"legend: { enabled: false }," + 
				"credits: { enabled: false }," + 
				"exporting: { enabled: false }," + 
				"" + 
			"}"
		);

		chart.setHcjs(hcjs.toString());
	}
	
	public void updateSubHeader() {
		subtitleLabel.setValue(String.format(I18nProperties.getCaption(Captions.dashboardComparedToPreviousPeriod),
				DateFormatHelper.buildPeriodString(dashboardDataProvider.getFromDate(), dashboardDataProvider.getToDate()),
				DateFormatHelper.buildPeriodString(dashboardDataProvider.getPreviousFromDate(), dashboardDataProvider.getPreviousToDate())));
	}

}
