package de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.builders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public abstract class SurveillanceEpiCurveBuilder {

	protected final EpiCurveGrouping epiCurveGrouping;
	protected final StringBuilder hcjs;

	public SurveillanceEpiCurveBuilder(EpiCurveGrouping epiCurveGrouping) {
		this.epiCurveGrouping = epiCurveGrouping;
		hcjs = new StringBuilder();
	}

	public String buildFrom(List<Date> filteredDates, DashboardCriteria dashboardCriteria) {
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

		List<String> newLabels = buildLabels(filteredDates);
		hcjs.append("xAxis: { categories: [");
		for (String s : newLabels) {
			if (newLabels.indexOf(s) == newLabels.size() - 1) {
				hcjs.append("'" + s + "']},");
			} else {
				hcjs.append("'" + s + "', ");
			}
		}

		//@formatter:off
		hcjs.append("yAxis: { min: 0, title: { text: '" + I18nProperties.getCaption(Captions.dashboardNumberOfCases) + "' }, allowDecimals: false, softMax: 10, "
			+ "stackLabels: { enabled: true, "
			+ "style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
			+ "legend: { verticalAlign: 'top', backgroundColor: 'transparent', align: 'left', "
			+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },"
			+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>" + I18nProperties.getCaption(Captions.dashboardTotal) + ": {point.stackTotal}'},"
			+ "plotOptions: { column: { borderWidth: 0, stacking: 'normal', groupPadding: 0, pointPadding: 0, dataLabels: {"
			+ "enabled: true, formatter: function() { if (this.y > 0) return this.y; },"
			+ "color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white' } } },");
		//@formatter:on

		List<EpiCurveSeriesElement> elements = buildEpiCurveSeriesElements(filteredDates, dashboardCriteria);
		buildSeries(elements);
		hcjs.append(", ");

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

	protected DashboardCriteria setNewCaseDatesInCaseCriteria(Date date, DashboardCriteria dashboardCriteria) {
		if (epiCurveGrouping == EpiCurveGrouping.DAY) {
			dashboardCriteria.dateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
		} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
			dashboardCriteria.dateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
		} else {
			dashboardCriteria.dateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
		}
		return dashboardCriteria;
	}

	protected void buildSeries(List<EpiCurveSeriesElement> elements) {
		hcjs.append("series: [");
		for (int i = 0; i < elements.size(); i++) {
			if (i > 0) {
				hcjs.append(", ");
			}
			buildSeriesElement(elements.get(i));
		}
		hcjs.append("]");
	}

	private void buildSeriesElement(EpiCurveSeriesElement element) {
		hcjs.append("{ name: '")
			.append(element.getCaption())
			.append("', color: '")
			.append(element.getColor())
			.append("', dataLabels: { allowOverlap: false },  data: [");
		buildDataObject(element.getValues());
		hcjs.append("]}");
	}

	private void buildDataObject(int[] values) {
		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				hcjs.append(", ");
			}
			hcjs.append(values[i]);
		}
	}

	private List<String> buildLabels(List<Date> filteredDates) {
		List<String> newLabels = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		for (Date date : filteredDates) {
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

	abstract List<EpiCurveSeriesElement> buildEpiCurveSeriesElements(List<Date> filteredDates, DashboardCriteria dashboardCriteria);
}
