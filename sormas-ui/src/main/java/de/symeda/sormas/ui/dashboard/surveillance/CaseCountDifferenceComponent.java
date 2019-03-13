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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.utils.CssStyles;

public class CaseCountDifferenceComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private DashboardDataProvider dashboardDataProvider;
	private HighChart chart;
	private Label subtitleLabel;

	public CaseCountDifferenceComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		Label title = new Label(I18nProperties.getCaption(Captions.dashboardDiseaseDifference));
		CssStyles.style(title, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		subtitleLabel = new Label(I18nProperties.getCaption(Captions.dashboardComparedToPreviousPeriod));

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
		//express previous period in contextual, user-friendly text
		//for subtitle and legend labels
		String previousPeriodExpression = I18nProperties.getString(Strings.previousPeriod);
		{
			long now = new Date().getTime();
			long fromDate = this.dashboardDataProvider.getFromDate().getTime();
			long toDate = this.dashboardDataProvider.getToDate().getTime();
			float millisecondsToDays = (1000 * 60 * 60 * 24);
			
			float diffBetweenFromAndToInDays = (toDate - fromDate) / millisecondsToDays;
			float diffBetweenNowAndToInDays = (now - toDate) / millisecondsToDays;
			
			if (diffBetweenFromAndToInDays == 1) {
				if (diffBetweenNowAndToInDays <= 0) //today
					previousPeriodExpression = I18nProperties.getString(Strings.yesterday);
				else if (diffBetweenNowAndToInDays < 1) //yesterday
					previousPeriodExpression = I18nProperties.getString(Strings.lastTwoDays);
			}
			else if (diffBetweenFromAndToInDays == 7) {
				if (diffBetweenNowAndToInDays <= 0) //this week
					previousPeriodExpression = I18nProperties.getString(Strings.lastWeek);
				else if (diffBetweenNowAndToInDays < 7) //last week
					previousPeriodExpression = I18nProperties.getString(Strings.lastTwoWeeks);
			}
			else if (diffBetweenFromAndToInDays == 365) {
				if (diffBetweenNowAndToInDays <= 0) //this year
					previousPeriodExpression = I18nProperties.getString(Strings.lastYear);
				else if (diffBetweenNowAndToInDays < 365)
					previousPeriodExpression = I18nProperties.getString(Strings.lastTwoYears);
			}
		}
		//~express prev...
		
		this.subtitleLabel.setValue(String.format(I18nProperties.getString(Strings.comparedTo), previousPeriodExpression));
				
		StringBuilder hcjs = new StringBuilder();
		hcjs.append("var options = {" + "chart:{ " + " type: 'bar', " + " backgroundColor: 'transparent' " + "},"
				+ "credits:{ enabled: false }," + "exporting:{ " + " enabled: false,"
				+ " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }" + "}," + "title:{ text: '' },");

		hcjs.append("xAxis: { categories: [");
		int max = 10;
		for (DiseaseBurdenDto s : data) {
			max = Math.max(max, Math.abs(s.getCaseCount().intValue()));
			hcjs.append("'" + s.getDisease().toString() + "', ");
		}
		hcjs.append("]},");

		hcjs.append("yAxis: { title: { text: '" + I18nProperties.getCaption(Captions.dashboardDiseaseDifferenceYAxisLabel) + "' }, "
					+ "min: " + -max + ", max: " + max + ","
					+ "allowDecimals: false, " + "stackLabels: { enabled: true, "
						+ "style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
				+ "legend: { enabled: false }, "
//				+ "legend: { verticalAlign: 'middle', backgroundColor: 'transparent', align: 'right', layout: 'vertical', "
//				+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },"
				+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'},"
				+ "plotOptions: { column: { borderWidth: 0, stacking: 'normal', groupPadding: 0, pointPadding: 0, dataLabels: {"
				+ "enabled: true, formatter: function() { if (this.y > 0) return this.y; },"
				+ "color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white' } }, series: { pointWidth: 20 } },");

		//use two series for this chart
		List<Long> positive_series = data.stream().map((d) -> d.getCasesDifference() < 0 ? 0 : d.getCasesDifference()).collect(Collectors.toList());
		List<Long> negative_series = data.stream().map((d) -> d.getCasesDifference() > 0 ? 0 : d.getCasesDifference()).collect(Collectors.toList());
		
//		Map<String, String> diseaseToColorMap = new Map
//		StringBuilder colors = new StringBuilder();
//		for (DiseaseBurdenDto s : data) {
//			colors.append("" + s.getDisease().toString() + "', ");
//		}

		hcjs.append("series: [");

		hcjs.append("{ color: '#FF4500', data: [");
		hcjs.append(positive_series.stream().map((d) -> d.toString()).reduce((fullText, nextText) -> fullText + ", " + nextText).orElse(""));
		hcjs.append("]},");

		hcjs.append("{ color: '#32CD32', data: [");
		hcjs.append(negative_series.stream().map((d) -> d.toString()).reduce((fullText, nextText) -> fullText + ", " + nextText).orElse(""));
		hcjs.append("]},");

		hcjs.append("],"); // series: []

		hcjs.append("}"); // options: {}

		chart.setHcjs(hcjs.toString());
	}

}
