/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.dashboard.diagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.dashboard.BaseDashboardCriteria;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.AbstractDashboardDataProvider;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public abstract class AbstractEpiCurveBuilder<C extends BaseDashboardCriteria<C>, P extends AbstractDashboardDataProvider<C>> {

	protected final EpiCurveGrouping epiCurveGrouping;
	private final String yAxisCaption;

	public AbstractEpiCurveBuilder(String yAxisCaption, EpiCurveGrouping epiCurveGrouping) {
		this.yAxisCaption = yAxisCaption;
		this.epiCurveGrouping = epiCurveGrouping;
	}

	public String buildFrom(List<Date> datesGroupedBy, P dashboardDataProvider) {
		StringBuilder hcjs = new StringBuilder();

		//@formatter:off
		hcjs.append(
				"var options = {"
						+ "chart:{ "
						+ " type: 'column', "
						+ " backgroundColor: 'transparent' "
						+ "},"
						+ "credits:{ enabled: false },"
						+ "exporting:{ "
						+ " enabled: true,"
						+ " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }"
						+ "},"
						+ "title:{ text: '' },"
		);
		//@formatter:on

		List<String> newLabels = buildLabels(datesGroupedBy);
		hcjs.append("xAxis: { categories: [");
		for (String s : newLabels) {
			if (newLabels.indexOf(s) == newLabels.size() - 1) {
				hcjs.append("'" + s + "']},");
			} else {
				hcjs.append("'" + s + "', ");
			}
		}

		hcjs.append(
			"yAxis: { min: 0, title: { text: '" + I18nProperties.getCaption(yAxisCaption) + "' }, allowDecimals: false, softMax: 10, "
				+ "stackLabels: { enabled: true, "
				+ "style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
				+ "legend: { verticalAlign: 'top', backgroundColor: 'transparent', align: 'left', "
				+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },"
				+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>"
				+ I18nProperties.getCaption(Captions.dashboardTotal) + ": {point.stackTotal}'},"
				+ "plotOptions: { column: { borderWidth: 0, stacking: 'normal', groupPadding: 0, pointPadding: 0, dataLabels: {"
				+ "enabled: true, formatter: function() { if (this.y > 0) return this.y; },"
				+ "color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white' } } },");

		hcjs.append(buildEpiCurve(datesGroupedBy, dashboardDataProvider)).append(", ");

		//@formatter:off
		hcjs.append("exporting: {\n" +
				"        buttons: {\n" +
				"            contextButton: {\n" +
				"                menuItems: [\n" +
				"                    'printChart',\n" +
				"                    'separator',\n" +
				"                    'downloadPNG',\n" +
				"                    'downloadJPEG',\n" +
				"                    'downloadPDF',\n" +
				"                    'downloadSVG',\n" +
				"                    'downloadCSV',\n" +
				"                    'downloadXLS'\n" +
				"                ]\n" +
				"            }\n" +
				"        }\n" +
				"    }");
		//@formatter:on

		hcjs.append("};");

		return hcjs.toString();
	}

	private List<String> buildLabels(List<Date> datesGroupedBy) {
		List<String> newLabels = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		for (Date date : datesGroupedBy) {
			if (epiCurveGrouping == EpiCurveGrouping.DAY) {
				String label = DateFormatHelper.formatDate(date);
				newLabels.add(label);
			} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
				calendar.setTime(date);
				String label = DateHelper.getEpiWeek(date).toShortString();
				newLabels.add(label);
			} else {
				String label = DateHelper.formatDateWithMonthAbbreviation(date);
				newLabels.add(label);
			}
		}
		return newLabels;
	}

	protected String buildEpiCurve(List<Date> datesGroupedBy, P dashboardDataProvider) {
		List<EpiCurveSeriesElement> elements = getEpiCurveElements(datesGroupedBy, dashboardDataProvider);

		return "series: [" + elements.stream().map(this::buildSeriesElement).collect(Collectors.joining(", ")) + "]";
	}

	protected abstract List<EpiCurveSeriesElement> getEpiCurveElements(List<Date> datesGroupedBy, P dashboardDataProvider);

	private String buildSeriesElement(EpiCurveSeriesElement element) {
		return "{ name: '" + I18nProperties.getCaption(element.getCaption()) + "', color: '" + element.getColor()
			+ "', dataLabels: { allowOverlap: false }, data: ["
			+ Arrays.stream(element.getValues()).mapToObj(String::valueOf).collect(Collectors.joining(", ")) + "]}";
	}
}
