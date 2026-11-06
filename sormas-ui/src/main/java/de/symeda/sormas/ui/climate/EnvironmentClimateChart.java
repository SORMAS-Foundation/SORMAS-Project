/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.climate;

import static de.symeda.sormas.ui.climate.ClimateChartScript.html;
import static de.symeda.sormas.ui.climate.ClimateChartScript.number;
import static de.symeda.sormas.ui.climate.ClimateChartScript.string;
import static de.symeda.sormas.ui.climate.ClimateChartScript.x;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.climate.ClimateDailySeriesDto;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateDayDto;
import de.symeda.sormas.api.climate.ClimateObservationExportDto;
import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.climate.SiteClimateReadingDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * The stacked chart of the environment Climate data tab (decisions 90–94): rows for samples and events, then daily
 * rainfall, temperature and humidity panels with their normal bands and the site readings as diamonds, on one time axis.
 * Hovering a day shows all its values in one details line above the panels; missing days are gaps, never 0.
 */
public class EnvironmentClimateChart extends VerticalLayout {

	private static final long serialVersionUID = -7016937051284313205L;

	static final String SAMPLES = "samples";
	static final String EVENTS = "events";

	/** The chart's height in pixels; the rows are laid out in pixels inside it (decision 107). */
	static final int CHART_HEIGHT = 700;
	/** Room above the plot for the details line of the day hovered. */
	static final int PLOT_TOP = 100;
	/** Room below the plot for the date labels and the legend. */
	static final int MARGIN_BOTTOM = 130;
	static final int LANE_HEIGHT = 26;
	static final int LANE_GAP = 6;
	/** The band above each panel that holds its name. */
	static final int TITLE_BAND = 26;
	private static final String TITLE_STYLE = "{fontWeight:'normal',color:'#555555'}";

	/** How a sample is shown: positive when any pathogen test is positive (decision 92), else by its latest result. */
	enum SampleStatus {

		POSITIVE("circle", "#b03a2e", "#b03a2e", Strings.climateSamplePositive),
		NEGATIVE("circle", "#ffffff", "#2e7d32", Strings.climateSampleNegative),
		PENDING("triangle", "#7f8c8d", "#7f8c8d", Strings.climateSamplePending),
		OTHER("square", "#bdc3c7", "#7f8c8d", Strings.climateSampleOther);

		private final String symbol;
		private final String fill;
		private final String line;
		private final String caption;

		SampleStatus(String symbol, String fill, String line, String caption) {
			this.symbol = symbol;
			this.fill = fill;
			this.line = line;
			this.caption = caption;
		}

		static SampleStatus of(EnvironmentSampleIndexDto sample) {
			if (sample.getPositivePathogenTests() != null && !sample.getPositivePathogenTests().isEmpty()) {
				return POSITIVE;
			}
			if (sample.getLatestPathogenTestResult() == PathogenTestResultType.NEGATIVE) {
				return NEGATIVE;
			}
			if (sample.getLatestPathogenTestResult() == PathogenTestResultType.PENDING) {
				return PENDING;
			}
			return OTHER;
		}
	}

	private final Label dates = new Label();
	private final ClimateChart chart = new ClimateChart(this::pointClicked);
	private final Label credit = new Label(ClimateObservationExportDto.OPENWEATHER_CREDIT);

	private Map<Long, List<EnvironmentSampleIndexDto>> samplesByDay = Collections.emptyMap();
	private Map<Long, List<EventIndexDto>> eventsByDay = Collections.emptyMap();
	private String lastScript;

	public EnvironmentClimateChart() {

		setMargin(false);
		setSpacing(false);
		setWidthFull();
		// a heading of its own, so the chart does not read as part of the summary cards above it (decision 109)
		Label heading = new Label(I18nProperties.getCaption(Captions.climateDailyChart));
		heading.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_3);
		dates.addStyleNames(CssStyles.LABEL_SECONDARY, CssStyles.LABEL_SMALL);
		chart.setWidthFull();
		chart.setHeight(CHART_HEIGHT, Unit.PIXELS);
		credit.addStyleNames(CssStyles.LABEL_SECONDARY, CssStyles.LABEL_SMALL);
		addComponents(heading, dates, chart, credit);
	}

	/**
	 * @param samples
	 *            the environment's samples, or null when the user may not see them (the row is left out)
	 * @param events
	 *            the events linked to the environment, or null when the user may not see them
	 */
	public void update(
		ClimateDailySeriesDto series,
		LocalDate from,
		LocalDate to,
		Date reportDate,
		List<EnvironmentSampleIndexDto> samples,
		List<EventIndexDto> events,
		List<SiteClimateReadingDto> readings) {

		samplesByDay = samples != null ? byDay(samples, EnvironmentSampleIndexDto::getSampleDateTime, from, to) : null;
		eventsByDay = events != null ? byDay(events, EnvironmentClimateChart::eventDate, from, to) : null;
		Map<Long, List<SiteClimateReadingDto>> readingsByDay = byDay(readings, SiteClimateReadingDto::getReadingDateTime, from, to);

		dates.setValue(ClimateCaptions.date(from) + " – " + ClimateCaptions.date(to));
		credit.setVisible(series.getDays().stream().anyMatch(d -> d.getSource() == ClimateDataSource.OPENWEATHER));
		lastScript = script(series, from, to, reportDate, readingsByDay);
		chart.setHcjs(lastScript);
	}

	/**
	 * @return the Highcharts script of the last update
	 */
	String getScript() {
		return lastScript;
	}

	/**
	 * @return where the normal ranges come from, or how much history has built up before they exist; shown in the context
	 *         card (decision 108)
	 */
	static String status(ClimateDailySeriesDto series) {

		boolean anyNormal = series.getDays()
			.stream()
			.anyMatch(d -> d.getRainfallNormal() != null || d.getTemperatureNormal() != null || d.getHumidityNormal() != null);
		if (anyNormal) {
			return series.isDistrictNormals() && series.getDistrict() != null
				? String.format(
					I18nProperties.getString(Strings.climateNormalRangeDistrict),
					series.getFirstReferenceYear(),
					series.getLastReferenceYear(),
					series.getDistrict().getCaption())
				: String.format(I18nProperties.getString(Strings.climateNormalRange), series.getFirstReferenceYear(), series.getLastReferenceYear());
		}
		if (series.getHistoryFrom() == null) {
			return "";
		}
		Period held = Period.between(series.getHistoryFrom(), LocalDate.now());
		return String.format(I18nProperties.getString(Strings.climateHistoryBuilding), ClimateCaptions.period(held), series.getMinHistoryYears());
	}

	private String script(
		ClimateDailySeriesDto series,
		LocalDate from,
		LocalDate to,
		Date reportDate,
		Map<Long, List<SiteClimateReadingDto>> readingsByDay) {

		List<Row> rows = new ArrayList<>();
		if (samplesByDay != null) {
			rows.add(
				Row.lane(
					I18nProperties.getString(Strings.climateAxisSamples),
					samplesByDay.isEmpty() ? I18nProperties.getString(Strings.climateAxisSamplesNone) : null));
		}
		if (eventsByDay != null) {
			rows.add(
				Row.lane(
					I18nProperties.getString(Strings.climateAxisEvents),
					eventsByDay.isEmpty() ? I18nProperties.getString(Strings.climateAxisEventsNone) : null));
		}
		Row rain = Row.panel(I18nProperties.getString(Strings.climateAxisRainfall), 32);
		Row temperature = Row.panel(I18nProperties.getString(Strings.climateAxisTemperature), 36);
		Row humidity = Row.panel(I18nProperties.getString(Strings.climateAxisHumidity), 32);
		rows.add(rain);
		rows.add(temperature);
		rows.add(humidity);
		layoutRows(rows);

		StringBuilder js = new StringBuilder();
		js.append("var details = {");
		for (ClimateDayDto day : series.getDays()) {
			js.append(x(day.getDate()))
				.append(':')
				.append(string(details(day, readingsByDay.get(x(day.getDate())), samplesByDay, eventsByDay)))
				.append(',');
		}
		js.append("};\n");

		js.append("var options = {")
			.append("chart:{marginLeft:")
			.append(ClimateChartScript.MARGIN_LEFT)
			.append(",marginRight:")
			.append(ClimateChartScript.MARGIN_RIGHT)
			.append(",marginTop:")
			.append(PLOT_TOP)
			.append(",marginBottom:")
			.append(MARGIN_BOTTOM)
			.append(",spacingTop:4,animation:false,events:{render:function(){var ch=this;")
			.append("(ch.panelTitles||[]).forEach(function(t){t.destroy();});")
			.append("ch.panelTitles=ch.yAxis.filter(function(a){return a.options.panelTitle;}).map(function(a){")
			.append("return ch.renderer.text(a.options.panelTitle,ch.plotLeft,a.top-")
			.append(TITLE_BAND / 3)
			.append(").css({color:'#555555',fontSize:'12px'}).add();});")
			// a hint in the details area until a day is pointed at; the details box covers it
			.append("if(ch.hoverHint){ch.hoverHint.destroy();}ch.hoverHint=ch.renderer.text(")
			.append(string(I18nProperties.getString(Strings.climateChartHint)))
			.append(",ch.plotLeft,20).css({color:'#999999',fontSize:'12px',fontStyle:'italic'}).add();}}},")
			.append("title:{text:null},credits:{enabled:false},exporting:{enabled:false},time:{useUTC:true},")
			// the bottom margin is fixed so the rows can be laid out in pixels; a long legend pages instead of growing
			.append("legend:{enabled:true,maxHeight:")
			.append(MARGIN_BOTTOM - 30)
			.append(",itemStyle:{fontWeight:'normal'}},");

		// x axis: the days shown, the crosshair through every row and the report date
		js.append("xAxis:{type:'datetime',min:")
			.append(ClimateChartScript.axisMin(from))
			.append(",max:")
			.append(ClimateChartScript.axisMax(to))
			.append(",crosshair:{color:'#555555',dashStyle:'Dash',width:1},dateTimeLabelFormats:{day:'")
			.append(ClimateChartScript.dayMonthFormat())
			.append("',week:'")
			.append(ClimateChartScript.dayMonthFormat())
			.append("'},plotLines:[");
		if (reportDate != null) {
			LocalDate reportDay = ClimateCaptions.toLocalDate(reportDate);
			if (!reportDay.isBefore(from) && !reportDay.isAfter(to)) {
				js.append("{value:")
					.append(x(reportDay))
					.append(",color:'#555555',dashStyle:'Dash',width:1,zIndex:5,label:{text:")
					.append(string(I18nProperties.getString(Strings.climateReportDate)))
					.append(",rotation:0,y:-4,style:{color:'#555555'}}}");
			}
		}
		js.append("]},");

		js.append("yAxis:[");
		for (Row row : rows) {
			js.append("{top:").append(row.top).append(",height:").append(row.height).append(",offset:0,");
			if (row.lane) {
				// a lane: its name in the left margin, level with it, on a light background; a note when it is empty
				js.append("title:{text:")
					.append(string(row.title))
					.append(",rotation:0,align:'middle',textAlign:'right',offset:8,y:4,reserveSpace:false,style:")
					.append(TITLE_STYLE)
					.append("},lineWidth:0,min:-1,max:1,startOnTick:false,endOnTick:false,labels:{enabled:false},gridLineWidth:0,")
					.append("plotBands:[{from:-1,to:1,color:'#f4f6f7'");
				if (row.note != null) {
					js.append(",label:{text:")
						.append(string(html(row.note)))
						.append(",align:'left',verticalAlign:'middle',x:8,y:4,style:{color:'#888888',fontStyle:'italic'}}");
				}
				js.append("}]");
			} else {
				// a panel: its name is drawn in the band above it by the render event (an axis title moves with the
				// width of the axis labels)
				js.append("title:{text:null},panelTitle:")
					.append(string(row.title))
					.append(",lineWidth:1,labels:{x:-6},gridLineDashStyle:'Dot'");
				if (row == humidity) {
					js.append(",min:0,max:100,tickAmount:3");
				}
				if (row == rain) {
					js.append(",min:0");
				}
			}
			js.append("},");
		}
		js.append("],");

		js.append("tooltip:{shared:true,useHTML:true,shadow:false,borderWidth:0,backgroundColor:'rgba(255,255,255,0.96)',")
			.append("positioner:function(){return {x:this.chart.plotLeft,y:0};},")
			.append("formatter:function(){var d=details[this.x];var c=(this.points?this.points[0]:this).series.chart;return d?'<div style=\"white-space:normal;width:'+c.plotWidth+'px\">'+d+'</div>':false;}},");

		js.append("plotOptions:{series:{animation:false,states:{inactive:{opacity:1}},point:{events:{click:")
			.append(ClimateChart.clickHandler())
			.append("}}},column:{pointPadding:0.05,groupPadding:0,borderWidth:0},")
			.append("arearange:{lineWidth:0,enableMouseTracking:true,marker:{enabled:false}},")
			.append("line:{marker:{enabled:false}}},");

		js.append("series:[");
		int axis = 0;
		if (samplesByDay != null) {
			appendSampleSeries(js, axis++);
		}
		if (eventsByDay != null) {
			appendEventSeries(js, axis++);
		}
		int rainAxis = axis++;
		int temperatureAxis = axis++;
		int humidityAxis = axis;

		// normal bands: one legend entry for all three
		js.append(band("normal", I18nProperties.getString(Strings.climateSeriesNormalRange), series, ClimateDayDto::getRainfallNormal, rainAxis, null));
		js.append(column(I18nProperties.getString(Strings.climateSeriesRainfall), series, ClimateDayDto::getRainfall, rainAxis));
		js.append(band(null, null, series, ClimateDayDto::getTemperatureNormal, temperatureAxis, "normal"));
		js.append(minMax(series, temperatureAxis));
		js.append(line(I18nProperties.getString(Strings.climateSeriesTemperatureMean), "#d35400", series, ClimateDayDto::getTemperatureMean, temperatureAxis));
		js.append(band(null, null, series, ClimateDayDto::getHumidityNormal, humidityAxis, "normal"));
		js.append(line(I18nProperties.getString(Strings.climateSeriesHumidity), "#1f6f8b", series, ClimateDayDto::getRelativeHumidity, humidityAxis));

		js.append(readings(readingsByDay, SiteClimateReadingDto::getRainfall, rainAxis, "readings", true));
		js.append(readings(readingsByDay, SiteClimateReadingDto::getAirTemperature, temperatureAxis, null, false));
		js.append(readings(readingsByDay, SiteClimateReadingDto::getRelativeHumidity, humidityAxis, null, false));
		js.append("]};");
		return js.toString();
	}

	private void appendSampleSeries(StringBuilder js, int axis) {

		Map<SampleStatus, List<Long>> daysByStatus = new LinkedHashMap<>();
		for (SampleStatus sampleStatus : SampleStatus.values()) {
			daysByStatus.put(sampleStatus, new ArrayList<>());
		}
		// a day with several samples is shown once, with the strongest status: positive, negative, pending, other
		samplesByDay.forEach(
			(day, samples) -> daysByStatus.get(samples.stream().map(SampleStatus::of).min(Enum::compareTo).orElse(SampleStatus.OTHER)).add(day));

		daysByStatus.forEach((sampleStatus, days) -> {
			js.append("{id:'")
				.append(SAMPLES)
				.append('-')
				.append(sampleStatus.name().toLowerCase())
				.append("',type:'line',lineWidth:0,yAxis:")
				.append(axis)
				.append(",name:")
				.append(string(I18nProperties.getString(sampleStatus.caption)))
				.append(",showInLegend:")
				.append(!days.isEmpty())
				.append(",color:'")
				.append(sampleStatus.line)
				.append("',states:{hover:{lineWidthPlus:0}},marker:{enabled:true,radius:6,symbol:'")
				.append(sampleStatus.symbol)
				.append("',fillColor:'")
				.append(sampleStatus.fill)
				.append("',lineColor:'")
				.append(sampleStatus.line)
				.append("',lineWidth:2},data:[");
			days.stream().sorted().forEach(day -> js.append("{x:").append(day).append(",y:0,key:'").append(day).append("'},"));
			js.append("]},");
		});
	}

	private void appendEventSeries(StringBuilder js, int axis) {

		js.append("{id:'")
			.append(EVENTS)
			.append("',type:'line',lineWidth:0,yAxis:")
			.append(axis)
			.append(",name:")
			.append(string(I18nProperties.getString(Strings.climateSeriesEvents)))
			.append(",showInLegend:")
			.append(!eventsByDay.isEmpty())
			.append(",color:'#34495e',states:{hover:{lineWidthPlus:0}},marker:{enabled:true,radius:6,symbol:'square'},data:[");
		eventsByDay.keySet().stream().sorted().forEach(day -> js.append("{x:").append(day).append(",y:0,key:'").append(day).append("'},"));
		js.append("]},");
	}

	/**
	 * @param id
	 *            the band's id, so the other bands can link to its legend entry, or null
	 * @param linkedTo
	 *            the id of the band whose legend entry this one shares, or null
	 */
	private static String band(
		String id,
		String name,
		ClimateDailySeriesDto series,
		Function<ClimateDayDto, ClimateRangeDto> range,
		int axis,
		String linkedTo) {

		StringBuilder js = new StringBuilder("{type:'arearange',yAxis:").append(axis).append(",color:'#9ecae1',fillOpacity:0.45,zIndex:0,");
		if (id != null) {
			js.append("id:'").append(id).append("',name:").append(string(name)).append(',');
		}
		if (linkedTo != null) {
			js.append("linkedTo:'").append(linkedTo).append("',");
		}
		js.append("data:[");
		for (ClimateDayDto day : series.getDays()) {
			ClimateRangeDto normal = range.apply(day);
			js.append('[')
				.append(x(day.getDate()))
				.append(',')
				.append(normal != null ? number(normal.getP10()) : "null")
				.append(',')
				.append(normal != null ? number(normal.getP90()) : "null")
				.append("],");
		}
		return js.append("]},").toString();
	}

	private static String column(String name, ClimateDailySeriesDto series, Function<ClimateDayDto, Double> value, int axis) {
		StringBuilder js =
			new StringBuilder("{type:'column',name:").append(string(name)).append(",yAxis:").append(axis).append(",color:'#3c78b4',zIndex:1,data:[");
		series.getDays().forEach(day -> js.append('[').append(x(day.getDate())).append(',').append(number(value.apply(day))).append("],"));
		return js.append("]},").toString();
	}

	private static String minMax(ClimateDailySeriesDto series, int axis) {
		StringBuilder js = new StringBuilder("{type:'arearange',name:").append(string(I18nProperties.getString(Strings.climateSeriesTemperatureRange)))
			.append(",yAxis:")
			.append(axis)
			.append(",color:'#bdbdbd',fillOpacity:0.5,zIndex:1,data:[");
		series.getDays()
			.forEach(
				day -> js.append('[')
					.append(x(day.getDate()))
					.append(',')
					.append(number(day.getTemperatureMin()))
					.append(',')
					.append(number(day.getTemperatureMax()))
					.append("],"));
		return js.append("]},").toString();
	}

	private static String line(String name, String color, ClimateDailySeriesDto series, Function<ClimateDayDto, Double> value, int axis) {
		StringBuilder js = new StringBuilder("{type:'line',name:").append(string(name))
			.append(",yAxis:")
			.append(axis)
			.append(",color:'")
			.append(color)
			.append("',lineWidth:2,zIndex:2,data:[");
		series.getDays().forEach(day -> js.append('[').append(x(day.getDate())).append(',').append(number(value.apply(day))).append("],"));
		return js.append("]},").toString();
	}

	/**
	 * @return the site readings of one variable as diamonds on its panel; the first of the three carries the legend entry
	 */
	private static String readings(
		Map<Long, List<SiteClimateReadingDto>> readingsByDay,
		Function<SiteClimateReadingDto, Double> value,
		int axis,
		String id,
		boolean first) {

		StringBuilder js = new StringBuilder("{type:'line',lineWidth:0,yAxis:").append(axis)
			.append(",color:'#6c3483',zIndex:3,states:{hover:{lineWidthPlus:0}},marker:{enabled:true,symbol:'diamond',radius:5},");
		if (first) {
			js.append("id:'").append(id).append("',name:").append(string(I18nProperties.getString(Strings.climateSeriesSiteReading)));
			js.append(",showInLegend:").append(!readingsByDay.isEmpty()).append(',');
		} else {
			js.append("linkedTo:'readings',");
		}
		js.append("data:[");
		readingsByDay.keySet().stream().sorted().forEach(day -> {
			for (SiteClimateReadingDto reading : readingsByDay.get(day)) {
				if (value.apply(reading) != null) {
					js.append('[').append(day).append(',').append(number(value.apply(reading))).append("],");
				}
			}
		});
		return js.append("]},").toString();
	}

	/**
	 * @return the day's details line, as HTML: values with their normal ranges, source, site readings, samples and events
	 */
	static String details(
		ClimateDayDto day,
		List<SiteClimateReadingDto> readings,
		Map<Long, List<EnvironmentSampleIndexDto>> samplesByDay,
		Map<Long, List<EventIndexDto>> eventsByDay) {

		List<String> parts = new ArrayList<>();
		parts.add("<b>" + html(ClimateCaptions.date(day.getDate())) + "</b>");
		if (day.getSource() == null) {
			parts.add(I18nProperties.getString(Strings.climateNoData));
		} else {
			parts.add(
				String.format(I18nProperties.getString(Strings.climateDetailRainfall), value(day.getRainfall(), " mm"))
					+ normal(day.getRainfallNormal(), " mm"));
			String temperature = String.format(I18nProperties.getString(Strings.climateDetailTemperature), value(day.getTemperatureMean(), " °C"));
			if (day.getTemperatureMin() != null || day.getTemperatureMax() != null) {
				temperature += " ("
					+ String.format(
						I18nProperties.getString(Strings.climateDetailMinMax),
						value(day.getTemperatureMin(), ""),
						value(day.getTemperatureMax(), ""))
					+ ")";
			}
			parts.add(temperature + normal(day.getTemperatureNormal(), " °C"));
			String humidity = String.format(I18nProperties.getString(Strings.climateDetailHumidity), value(day.getRelativeHumidity(), " %"));
			if (day.getRelativeHumidity() != null && day.getHumidityMeasure() == HumidityMeasure.NOON_READING) {
				humidity += " " + I18nProperties.getString(Strings.climateDetailNoonReading);
			}
			parts.add(humidity + normal(day.getHumidityNormal(), " %"));
			parts.add(String.format(I18nProperties.getString(Strings.climateDetailSource), day.getSource()));
		}

		StringBuilder html = new StringBuilder(String.join(" · ", parts));
		if (readings != null) {
			for (SiteClimateReadingDto reading : readings) {
				html.append("<br/>").append(html(siteReading(reading)));
			}
		}
		long dayX = x(day.getDate());
		if (samplesByDay != null && samplesByDay.containsKey(dayX)) {
			html.append("<br/>")
				.append(
					html(
						String.format(
							I18nProperties.getString(Strings.climateDetailSamples),
							samplesByDay.get(dayX).stream().map(EnvironmentClimateChart::sample).collect(Collectors.joining(", ")))));
		}
		if (eventsByDay != null && eventsByDay.containsKey(dayX)) {
			html.append("<br/>")
				.append(
					html(
						String.format(
							I18nProperties.getString(Strings.climateDetailEvents),
							eventsByDay.get(dayX)
								.stream()
								.map(e -> StringUtils.defaultIfBlank(e.getEventTitle(), DataHelper.getShortUuid(e.getUuid())) + " (" + e.getEventStatus() + ")")
								.collect(Collectors.joining(", ")))));
		}
		return html.toString();
	}

	/**
	 * @return "Site reading 14:00: 27.5 °C · 12 mm in 24 h · 81 %"
	 */
	static String siteReading(SiteClimateReadingDto reading) {
		List<String> values = new ArrayList<>();
		if (reading.getAirTemperature() != null) {
			values.add(ClimateCaptions.oneDecimal(reading.getAirTemperature()) + " °C");
		}
		if (reading.getRainfall() != null) {
			values.add(
				String.format(
					I18nProperties.getString(Strings.climateReadingRainfall),
					ClimateCaptions.number(reading.getRainfall()),
					reading.getRainfallPeriodHours()));
		}
		if (reading.getRelativeHumidity() != null) {
			values.add(ClimateCaptions.number(reading.getRelativeHumidity()) + " %");
		}
		return String.format(
			I18nProperties.getString(Strings.climateDetailSiteReading),
			DateFormatHelper.formatLocalDateTime(reading.getReadingDateTime()),
			String.join(" · ", values));
	}

	private static String sample(EnvironmentSampleIndexDto sample) {
		String name = StringUtils.defaultIfBlank(sample.getFieldSampleId(), DataHelper.getShortUuid(sample.getUuid()));
		SampleStatus sampleStatus = SampleStatus.of(sample);
		if (sampleStatus == SampleStatus.POSITIVE) {
			return name + " "
				+ String.format(
					I18nProperties.getString(Strings.climateSamplePositiveFor),
					sample.getPositivePathogenTests().stream().map(Pathogen::getCaption).collect(Collectors.joining(", ")));
		}
		if (sampleStatus == SampleStatus.OTHER) {
			return name + " "
				+ (sample.getLatestPathogenTestResult() != null
					? sample.getLatestPathogenTestResult().toString()
					: I18nProperties.getString(Strings.climateSampleNotTested));
		}
		return name + " " + I18nProperties.getString(sampleStatus.caption).toLowerCase();
	}

	private static String value(Double value, String unit) {
		return value != null ? ClimateCaptions.oneDecimal(value) + unit : I18nProperties.getString(Strings.climateNoData);
	}

	private static String normal(ClimateRangeDto range, String unit) {
		if (range == null) {
			return "";
		}
		return " "
			+ String.format(
				I18nProperties.getString(Strings.climateDetailNormal),
				ClimateCaptions.oneDecimal(range.getP10()),
				ClimateCaptions.oneDecimal(range.getP90()) + unit);
	}

	private static Date eventDate(EventIndexDto event) {
		return event.getStartDate() != null ? event.getStartDate() : event.getReportDateTime();
	}

	/**
	 * @return the items by the x position of their calendar day, only days of the period
	 */
	static <T> Map<Long, List<T>> byDay(List<T> items, Function<T, Date> date, LocalDate from, LocalDate to) {
		Map<Long, List<T>> byDay = new LinkedHashMap<>();
		for (T item : items) {
			Date when = date.apply(item);
			if (when == null) {
				continue;
			}
			LocalDate day = ClimateCaptions.toLocalDate(when);
			if (!day.isBefore(from) && !day.isAfter(to)) {
				byDay.computeIfAbsent(x(day), d -> new ArrayList<>()).add(item);
			}
		}
		return byDay;
	}

	private void pointClicked(String seriesId, String key) {

		long day;
		try {
			day = Long.parseLong(key);
		} catch (NumberFormatException e) {
			return;
		}
		if (seriesId.startsWith(SAMPLES) && samplesByDay != null && samplesByDay.containsKey(day)) {
			List<EnvironmentSampleIndexDto> samples = samplesByDay.get(day);
			if (samples.size() == 1) {
				ControllerProvider.getEnvironmentSampleController().navigateToSample(samples.get(0).getUuid());
			} else {
				choose(samples, EnvironmentClimateChart::sample, s -> ControllerProvider.getEnvironmentSampleController().navigateToSample(s.getUuid()));
			}
		} else if (EVENTS.equals(seriesId) && eventsByDay != null && eventsByDay.containsKey(day)) {
			List<EventIndexDto> events = eventsByDay.get(day);
			if (events.size() == 1) {
				ControllerProvider.getEventController().navigateToData(events.get(0).getUuid());
			} else {
				choose(
					events,
					e -> StringUtils.defaultIfBlank(e.getEventTitle(), DataHelper.getShortUuid(e.getUuid())),
					e -> ControllerProvider.getEventController().navigateToData(e.getUuid()));
			}
		}
	}

	/**
	 * Opens a small list to choose one of several samples or events of the same day.
	 */
	private static <T> void choose(List<T> items, Function<T, String> caption, java.util.function.Consumer<T> open) {
		VerticalLayout list = new VerticalLayout();
		Window[] window = new Window[1];
		for (T item : items) {
			Button button = new Button(caption.apply(item), e -> {
				window[0].close();
				open.accept(item);
			});
			button.addStyleName(ValoTheme.BUTTON_LINK);
			list.addComponent(button);
		}
		window[0] = VaadinUiUtil.showPopupWindow(list, I18nProperties.getString(Strings.climateChooseOne));
	}

	/**
	 * Gives each row its top and height in pixels from the top of the chart. Lanes have a fixed height; each panel has a
	 * band above it for its name, and the panels share what is left by weight, the last one ending at the plot's bottom.
	 */
	static void layoutRows(List<Row> rows) {

		int plotHeight = CHART_HEIGHT - PLOT_TOP - MARGIN_BOTTOM;
		int fixed = 0;
		double weights = 0;
		for (Row row : rows) {
			if (row.lane) {
				fixed += LANE_HEIGHT + LANE_GAP;
			} else {
				fixed += TITLE_BAND;
				weights += row.weight;
			}
		}
		int available = plotHeight - fixed;
		int y = 0;
		Row last = rows.get(rows.size() - 1);
		for (Row row : rows) {
			if (row.lane) {
				row.top = PLOT_TOP + y;
				row.height = LANE_HEIGHT;
				y += LANE_HEIGHT + LANE_GAP;
			} else {
				y += TITLE_BAND;
				row.top = PLOT_TOP + y;
				row.height = row == last ? plotHeight - y : (int) Math.round(available * row.weight / weights);
				y += row.height;
			}
		}
	}

	static final class Row {

		private final String title;
		/** Shown inside an empty lane, or null. */
		private final String note;
		private final double weight;
		private final boolean lane;
		private int top;
		private int height;

		private Row(String title, String note, double weight, boolean lane) {
			this.title = title;
			this.note = note;
			this.weight = weight;
			this.lane = lane;
		}

		/** A row of markers (samples, events), with a note when it has none in the period. */
		static Row lane(String title, String note) {
			return new Row(title, note, 0, true);
		}

		/** A panel of daily values, sharing the height left by weight. */
		static Row panel(String title, double weight) {
			return new Row(title, null, weight, false);
		}

		int getTop() {
			return top;
		}

		int getHeight() {
			return height;
		}
	}
}
