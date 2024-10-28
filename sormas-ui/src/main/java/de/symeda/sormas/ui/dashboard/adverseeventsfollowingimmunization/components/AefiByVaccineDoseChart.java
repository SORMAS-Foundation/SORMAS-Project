/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.components;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.AefiChartData;
import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.AefiChartSeries;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.highcharts.HighChart;

public class AefiByVaccineDoseChart extends VerticalLayout {

	protected final HighChart chart;

	public AefiByVaccineDoseChart() {

		setMargin(false);
		setSpacing(false);
		setSizeFull();

		chart = new HighChart();
		chart.setSizeFull();

		addComponent(chart);
		setExpandRatio(chart, 1);
	}

	public void update(AefiChartData chartData) {

		StringBuilder hcjs = new StringBuilder();

		//@formatter:off
        hcjs.append(
                "var options = {" +
					"    chart: {" +
					"        type: 'bar'," +
					"		borderRadius: '8px'" +
					"    }," +
					"    title: {" +
					"        text: 'Adverse events by vaccine dose'," +
					"        align: 'left'," +
					"		style: {" +
					"        	fontSize: '15px'," +
					"        	fontWeight: 'bold'" +
					"        }" +
					"    }," +
					"    subtitle: {" +
					"        text: ''," +
					"        align: 'left'" +
					"    },"
        );
        //@formatter:on

		List<Object> xAxisCategories = chartData.getxAxisCategories();

		StringBuilder categoryBuilder = new StringBuilder();
		if (!xAxisCategories.isEmpty()) {
			categoryBuilder.append("[");
		}
		for (Object s : xAxisCategories) {
			if (xAxisCategories.indexOf(s) == xAxisCategories.size() - 1) {
				categoryBuilder.append("'" + I18nProperties.getCaption(String.valueOf(s)) + "']");
			} else {
				categoryBuilder.append("'" + I18nProperties.getCaption(String.valueOf(s)) + "', ");
			}
		}
		String categories = !StringUtils.isBlank(categoryBuilder.toString()) ? categoryBuilder.toString() : "[]";

		//@formatter:off
		hcjs.append("xAxis: { " +
				"		categories: " + categories + "," +
				"        title: {" +
				"           text: 'Vaccine dose'," +
				"			style: {" +
				"        		fontWeight: 'bold'" +
				"        	}" +
				"        }," +
				"},");
		//@formatter:on

		//@formatter:off
		hcjs.append("yAxis: {" +
				"        min: 0," +
				"        title: {" +
				"            text: 'No. of AEFI Reports'," +
				"            align: 'high'," +
				"				style: {" +
				"        			fontWeight: 'bold'" +
				"        		}" +
				"        }," +
				"        labels: {" +
				"            overflow: 'justify'" +
				"        }," +
				"        gridLineWidth: 0" +
				"    }," +
				"    tooltip: {" +
				"        valueSuffix: ''" +
				"    }," +
				"    plotOptions: {" +
				"        bar: {" +
				"            dataLabels: {" +
				"                enabled: true" +
				"            }," +
				"            groupPadding: 0.1" +
				"        }" +
				"    }," +
				"    credits: {" +
				"        enabled: false" +
				"    },");
		//@formatter:on

		hcjs.append("series: [");
		List<AefiChartSeries> chartSeries = chartData.getSeries();
		for (AefiChartSeries series : chartSeries) {
			hcjs.append("{")
				.append("name: '")
				.append(series.getName())
				.append("',")
				.append("color: '")
				.append(series.getColor())
				.append("',")
				.append("data: [")
				.append(String.join(",", series.getSeriesData()))
				.append("]")
				.append("}");

			if (chartSeries.indexOf(series) < chartSeries.size() - 1) {
				hcjs.append(",");
			}
		}
		hcjs.append("]");

		hcjs.append("};");

		chart.setHcjs(hcjs.toString());
	}
}
