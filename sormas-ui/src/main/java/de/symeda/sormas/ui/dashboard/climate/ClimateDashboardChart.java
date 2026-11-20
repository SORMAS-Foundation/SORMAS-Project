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

package de.symeda.sormas.ui.dashboard.climate;

import static de.symeda.sormas.ui.climate.ClimateChartScript.html;
import static de.symeda.sormas.ui.climate.ClimateChartScript.number;
import static de.symeda.sormas.ui.climate.ClimateChartScript.string;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.climate.ClimateCaseCountSource;
import de.symeda.sormas.api.climate.ClimateCaseSeriesDto;
import de.symeda.sormas.api.climate.ClimateCaseWeekDto;
import de.symeda.sormas.api.climate.ClimateObservationExportDto;
import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.ClimateUsualRangeMissing;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.ClimateWeekDto;
import de.symeda.sormas.api.climate.ClimateWeekValueDto;
import de.symeda.sormas.api.climate.ClimateWeeklySeriesDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.climate.ClimateCaptions;
import de.symeda.sormas.ui.climate.ClimateChart;
import de.symeda.sormas.ui.climate.ClimateChartScript;
import de.symeda.sormas.ui.climate.ClimateIcon;
import de.symeda.sormas.ui.dashboard.climate.ClimateDashboardLayout.Measure;
import de.symeda.sormas.ui.dashboard.climate.ClimateDashboardLayout.Selection;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * The Climate dashboard's stacked chart (decisions 122–126): one panel per disease, then one per climate variable, on
 * one epi-week axis, with the usual-range and normal bands, unusual-week shading through every panel and one details line
 * for the week pointed at. The climate panels can be shifted by a lag; missing weeks are gaps, never 0.
 */
public class ClimateDashboardChart extends VerticalLayout {

	private static final long serialVersionUID = 6290233816312585069L;

	/** Room above the plot for the details line of the week pointed at: up to about 90 px when it wraps in 8 columns. */
	static final int PLOT_TOP = 100;
	/** Room below the plot for the week labels and the legend (two rows). */
	static final int MARGIN_BOTTOM = 80;
	/**
	 * The relative heights of a disease and a climate panel; the panels share the window's height in this proportion
	 * (decision 136).
	 */
	static final int DISEASE_PANEL = 140;
	static final int CLIMATE_PANEL = 130;
	/** No panel is drawn lower than this; with many panels the chart is then taller than the window. */
	static final int MIN_PANEL = 70;
	/** What the chart leaves of the browser window's height, so its bottom edge shows. */
	static final int WINDOW_MARGIN = 16;
	/** The window height assumed when the browser's is not known (tests). */
	static final int DEFAULT_WINDOW_HEIGHT = 800;
	/** The band above each panel that holds its name. */
	static final int TITLE_BAND = 22;
	static final int PANEL_GAP = 8;
	/** The height of one disease row in the heatmap. */
	static final int HEATMAP_ROW = 26;

	/** Disease colours: none of them is the rain blue, the temperature orange or the shading amber. */
	static final String[] DISEASE_COLORS = {
		"#7d3c98",
		"#117a65",
		"#2e4053",
		"#935116",
		"#1a5276",
		"#6e2c00",
		"#4a235a",
		"#0e6251",
		"#515a5a",
		"#7b241c" };
	static final String SHADING_COLOR = "rgba(243,156,18,0.18)";
	/** The heatmap outline of an unusual week. */
	static final String OUTLINE_COLOR = "#e67e22";

	private final Label dates = new Label();
	private final Label status = new Label();
	private final ClimateChart chart = new ClimateChart((seriesId, key) -> {
	});
	private final Label credit = new Label(ClimateObservationExportDto.OPENWEATHER_CREDIT);

	private String lastScript;
	private int chartHeight;
	private int windowHeight;

	public ClimateDashboardChart() {

		setMargin(false);
		setSpacing(false);
		setWidthFull();
		Label heading = new Label(I18nProperties.getCaption(Captions.climateDashboardChart));
		heading.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		dates.addStyleName(CssStyles.CLIMATE_CHART_DATES);
		status.addStyleName(CssStyles.CLIMATE_CHART_STATUS);
		// the period and the history status on one line under the heading (decision 135)
		Label separator = new Label(" · ");
		separator.addStyleName(CssStyles.CLIMATE_CHART_STATUS);
		HorizontalLayout subtitle = new HorizontalLayout(dates, separator, status);
		subtitle.setMargin(false);
		subtitle.setSpacing(false);
		chart.setWidthFull();
		credit.setWidthFull();
		credit.addStyleNames(CssStyles.LABEL_SMALL, CssStyles.ALIGN_CENTER);
		addComponents(heading, subtitle, chart, credit);
	}

	/**
	 * @param shading
	 *            the variable whose unusual weeks are shaded, or null for none
	 * @param lag
	 *            how many weeks earlier the climate panels show (decision 126)
	 * @param heatmap
	 *            true for one heatmap row per disease instead of a panel per disease (decision 129)
	 * @param windowHeight
	 *            the browser window's height, which the chart fits (decision 136)
	 */
	public void update(
		Selection selection,
		ClimateWeeklySeriesDto climate,
		List<ClimateCaseSeriesDto> cases,
		Set<ClimateVariable> variables,
		ClimateVariable shading,
		Measure measure,
		int lag,
		boolean heatmap,
		int windowHeight) {

		this.windowHeight = windowHeight;

		dates.setValue(String.format(I18nProperties.getString(Strings.climateDashboardDates), weekLabel(selection.getFrom()), weekLabel(selection.getTo())));
		status.setValue(status(climate));
		// the layout draws only with at least one disease chosen (decision 137), so there is always a panel
		credit.setVisible(!variables.isEmpty() && climate.isOpenWeatherData());
		lastScript = script(selection, climate, cases, variables, shading, measure, lag, heatmap);
		chart.setHeight(chartHeight, Unit.PIXELS);
		chart.setHcjs(lastScript);
	}

	/**
	 * @return the Highcharts script of the last update, or null before the first
	 */
	String getScript() {
		return lastScript;
	}

	int getChartHeight() {
		return chartHeight;
	}

	Label getStatus() {
		return status;
	}

	Label getCredit() {
		return credit;
	}

	/**
	 * @return where the normal ranges come from, or how much climate history has built up before they exist (decision 124)
	 */
	static String status(ClimateWeeklySeriesDto climate) {

		if (Arrays.stream(ClimateVariable.values()).anyMatch(climate::hasNormals)) {
			return String.format(I18nProperties.getString(Strings.climateNormalRange), climate.getFirstReferenceYear(), climate.getLastReferenceYear());
		}
		if (climate.getHistoryFrom() == null) {
			return climate.getDistrict() == null && !climate.getDistrictSeries().isEmpty()
				&& climate.getDistrictSeries().stream().anyMatch(d -> d.getHistoryFrom() != null)
					? I18nProperties.getString(Strings.climateDashboardHistoryIncomplete)
					: I18nProperties.getString(Strings.climateHistoryNone);
		}
		Period held = climate.getHeldPeriod(LocalDate.now());
		return String.format(I18nProperties.getString(Strings.climateHistoryBuilding), ClimateCaptions.period(held), climate.getMinHistoryYears());
	}

	/**
	 * @return the height the panels share, name bands and gaps included, so the whole chart with its details strip,
	 *         axis and legend fits the window (decision 136)
	 */
	static int fillHeight(int windowHeight) {
		return (windowHeight > 0 ? windowHeight : DEFAULT_WINDOW_HEIGHT) - WINDOW_MARGIN - PLOT_TOP - MARGIN_BOTTOM;
	}

	/**
	 * @param heatmapHeight
	 *            the height of the heatmap's rows, or 0 without a heatmap
	 * @param fillHeight
	 *            the height the panels share, name bands and gaps included ({@link #fillHeight(int)})
	 * @return the height of each panel, top to bottom: the disease panels or the heatmap, then the climate panels
	 *         (decision 136). Disease and climate panels share what the name bands, gaps and heatmap leave, in the
	 *         proportion of {@link #DISEASE_PANEL} to {@link #CLIMATE_PANEL}, but none is lower than {@link #MIN_PANEL};
	 *         the heatmap keeps its rows
	 */
	static int[] panelHeights(int diseasePanels, int heatmapHeight, int climatePanels, int fillHeight) {

		int panels = diseasePanels + (heatmapHeight > 0 ? 1 : 0) + climatePanels;
		if (panels == 0) {
			return new int[0];
		}
		int fixed = panels * TITLE_BAND + (panels - 1) * PANEL_GAP + heatmapHeight;
		int base = diseasePanels * DISEASE_PANEL + climatePanels * CLIMATE_PANEL;
		double scale = base == 0 ? 1 : (double) (fillHeight - fixed) / base;

		int[] heights = new int[panels];
		int i = 0;
		if (heatmapHeight > 0) {
			heights[i++] = heatmapHeight;
		}
		for (int d = 0; d < diseasePanels; d++) {
			heights[i++] = Math.max(MIN_PANEL, (int) Math.round(DISEASE_PANEL * scale));
		}
		for (int c = 0; c < climatePanels; c++) {
			heights[i++] = Math.max(MIN_PANEL, (int) Math.round(CLIMATE_PANEL * scale));
		}
		return heights;
	}

	static String weekLabel(EpiWeek week) {
		return week.getWeek() + "/" + week.getYear();
	}

	/**
	 * @return the climate week shown above the case week at {@code index} of the period, with the lag; null when it falls
	 *         before the loaded weeks
	 */
	static ClimateWeekDto shiftedWeek(ClimateWeeklySeriesDto climate, int index, int lag) {
		int position = climate.getLeadWeeks() + index - lag;
		return position >= 0 && position < climate.getWeeks().size() ? climate.getWeeks().get(position) : null;
	}

	private String script(
		Selection selection,
		ClimateWeeklySeriesDto climate,
		List<ClimateCaseSeriesDto> cases,
		Set<ClimateVariable> variables,
		ClimateVariable shading,
		Measure measure,
		int lag,
		boolean heatmap) {

		List<EpiWeek> weeks = climate.getShownWeeks().stream().map(ClimateWeekDto::getWeek).collect(Collectors.toList());
		int count = weeks.size();

		// rows in pixels: a name band above each panel, then the panel (decision 122), filling the chart (decision 135)
		List<int[]> rows = new ArrayList<>();
		int y = PLOT_TOP;
		for (int height : panelHeights(
			heatmap ? 0 : cases.size(),
			heatmap ? HEATMAP_ROW * cases.size() : 0,
			variables.size(),
			fillHeight(windowHeight))) {
			y += TITLE_BAND;
			rows.add(new int[] {
				y,
				height });
			y += height + PANEL_GAP;
		}
		chartHeight = y - PANEL_GAP + MARGIN_BOTTOM;

		StringBuilder js = new StringBuilder();
		js.append("var details = [");
		for (int i = 0; i < count; i++) {
			js.append(string(details(weeks.get(i), i, climate, cases, variables, measure, lag))).append(',');
		}
		js.append("];\n");

		js.append("var options = {chart:{marginLeft:")
			.append(heatmap ? heatmapMargin(cases) : ClimateChartScript.MARGIN_LEFT)
			.append(",marginRight:")
			.append(ClimateChartScript.MARGIN_RIGHT)
			.append(",marginTop:")
			.append(PLOT_TOP)
			.append(",marginBottom:")
			.append(MARGIN_BOTTOM)
			// where the pointer is, for the details line below
			.append(",spacingTop:4,animation:false,events:{load:function(){var ch=this;")
			.append("ch.container.addEventListener('mousemove',function(e){ch.pointerY=e.clientY-ch.container.getBoundingClientRect().top;});},")
			.append("render:function(){var ch=this;")
			.append("(ch.panelTitles||[]).forEach(function(t){t.destroy();});")
			.append("ch.panelTitles=ch.yAxis.filter(function(a){return a.options.panelTitle;}).map(function(a){")
			.append("return ch.renderer.text(a.options.panelTitle,ch.plotLeft,a.top-")
			.append(TITLE_BAND / 3)
			.append(",true).css({color:'#333333',fontSize:'12px'}).add();});")
			.append("if(ch.hoverHint){ch.hoverHint.destroy();}ch.hoverHint=ch.renderer.text(")
			.append(string(I18nProperties.getString(Strings.climateDashboardHint)))
			.append(",ch.plotLeft,20).css({color:'#999999',fontSize:'12px',fontStyle:'italic'}).add();}}},")
			.append("title:{text:null},credits:{enabled:false},exporting:{enabled:false},")
			.append("legend:{enabled:true,maxHeight:")
			.append(MARGIN_BOTTOM - 40)
			.append(",itemStyle:{fontWeight:'normal'}},");

		// x axis: one category per week, the hover line through every panel and the shading
		js.append("xAxis:{categories:[");
		weeks.forEach(week -> js.append(string(weekLabel(week))).append(','));
		js.append("],crosshair:{color:'#555555',dashStyle:'Dash',width:1},labels:{autoRotation:false,step:")
			.append(Math.max(1, (int) Math.ceil(count / 13.0)))
			.append("},plotBands:[");
		boolean anyShaded = false;
		if (shading != null) {
			for (int i = 0; i < count; i++) {
				ClimateWeekDto shifted = shiftedWeek(climate, i, lag);
				if (shifted != null && shifted.get(shading).isUnusual()) {
					anyShaded = true;
					js.append("{from:").append(i - 0.5).append(",to:").append(i + 0.5).append(",color:'").append(SHADING_COLOR).append("',zIndex:0},");
				}
			}
		}
		js.append("]},");

		js.append("yAxis:[");
		int row = 0;
		if (heatmap) {
			if (!cases.isEmpty()) {
				StringBuilder categories = new StringBuilder(",reversed:true,gridLineWidth:0,lineWidth:0,categories:[");
				cases.forEach(series -> categories.append(string(series.getDisease().toString())).append(','));
				categories.append("],labels:{x:-6,style:{fontSize:'12px',color:'#333333'}}");
				appendAxis(js, rows.get(row++), heatmapTitle(measure), categories.toString());
			}
		} else {
			for (ClimateCaseSeriesDto series : cases) {
				appendAxis(js, rows.get(row++), diseaseTitle(series, measure), ",min:0");
			}
		}
		for (ClimateVariable variable : orderedVariables(variables)) {
			String title = climateTitle(variable, lag) + climateNote(selection, climate, variable);
			appendAxis(js, rows.get(row++), title, variable == ClimateVariable.HUMIDITY ? ",min:0,max:100,tickAmount:3" : variable == ClimateVariable.RAINFALL ? ",min:0" : "");
		}
		js.append("],");

		js.append("tooltip:{shared:true,useHTML:true,shadow:false,animation:false,borderWidth:0,backgroundColor:'rgba(255,255,255,0.96)',")
			// the details line sits at the top of the part of the chart in view, so it stays visible when the page is
			// scrolled; when the pointer is right under it, it moves to the bottom of that part (decision 136)
			.append("positioner:function(w,h){var c=this.chart;var r=c.container.getBoundingClientRect();")
			.append("var top=Math.max(0,-r.top);var bottom=Math.min(c.chartHeight,(window.innerHeight||document.documentElement.clientHeight)-r.top);")
			.append("var y=top;if(c.pointerY!==undefined&&c.pointerY<top+h+8){y=Math.max(top,bottom-h);}")
			.append("return {x:c.plotLeft,y:y};},")
			.append("formatter:function(){var p=this.points?this.points[0]:this;var c=p.series.chart;var d=details[p.point.x];")
			.append("return d?'<div style=\"white-space:normal;width:'+c.plotWidth+'px\">'+d+'</div>':false;}},");

		if (heatmap) {
			// the colour scale of the heatmap only; the other series keep their own colours
			js.append("colorAxis:{min:0,max:1,minColor:'#f4ecf7',maxColor:'#4a235a',labels:{formatter:function(){return Math.round(this.value*100)+'%';}}},");
		}
		// on hover, a circle on each line and no marker on the bands, so no shape suggests a meaning (decision 143)
		js.append("plotOptions:{series:{animation:false,states:{inactive:{opacity:1}}"
			+ (heatmap ? ",colorAxis:false" : "")
			+ "},column:{pointPadding:0.05,groupPadding:0,borderWidth:0},")
			.append("arearange:{lineWidth:0,marker:{enabled:false,states:{hover:{enabled:false}}}},line:{marker:{enabled:false,symbol:'circle'}}},");

		js.append("series:[");
		int axis = heatmap ? appendHeatmap(js, climate, cases, shading, measure, lag, count) : appendDiseasePanels(js, cases, measure, count);

		boolean normalLegend = true;
		boolean lastYearLegend = true;
		for (ClimateVariable variable : orderedVariables(variables)) {
			Function<Integer, ClimateWeekValueDto> value = i -> {
				ClimateWeekDto week = shiftedWeek(climate, i, lag);
				return week != null ? week.get(variable) : null;
			};
			boolean hasNormal = false;
			for (int i = 0; i < count; i++) {
				hasNormal |= value.apply(i) != null && value.apply(i).getNormal() != null;
			}
			if (hasNormal) {
				js.append("{type:'arearange',yAxis:").append(axis).append(",color:'#9ecae1',fillOpacity:0.45,zIndex:0,");
				if (normalLegend) {
					js.append("id:'normal',name:").append(string(I18nProperties.getString(Strings.climateSeriesNormalRange))).append(',');
					normalLegend = false;
				} else {
					js.append("linkedTo:'normal',");
				}
				js.append("data:[");
				for (int i = 0; i < count; i++) {
					ClimateWeekValueDto week = value.apply(i);
					ClimateRangeDto normal = week != null ? week.getNormal() : null;
					js.append('[')
						.append(i)
						.append(',')
						.append(normal != null ? number(normal.getP10()) : "null")
						.append(',')
						.append(normal != null ? number(normal.getP90()) : "null")
						.append("],");
				}
				js.append("]},");
			}

			String color = color(variable);
			js.append("{type:'")
				.append(variable == ClimateVariable.RAINFALL ? "column" : "line")
				.append("',name:")
				.append(string(seriesName(variable)))
				.append(",yAxis:")
				.append(axis)
				.append(",color:'")
				.append(color)
				.append("',lineWidth:2,zIndex:1,data:[");
			List<Double> values = new ArrayList<>();
			for (int i = 0; i < count; i++) {
				ClimateWeekValueDto week = value.apply(i);
				values.add(week != null ? week.getValue() : null);
			}
			// a week between two weeks without data gets a dot on a line, or it would not be seen (decision 148)
			js.append(variable == ClimateVariable.RAINFALL ? columnData(values) : lineData(values));
			js.append("]},");

			// before normal ranges exist: the same week last year as a dashed grey line (decision 124)
			boolean anyLastYear = false;
			List<Double> lastYear = new ArrayList<>();
			for (int i = 0; i < count; i++) {
				ClimateWeekValueDto week = value.apply(i);
				Double previous = week != null && week.getNormal() == null ? week.getLastYear() : null;
				anyLastYear |= previous != null;
				lastYear.add(previous);
			}
			if (anyLastYear) {
				js.append("{type:'line',yAxis:").append(axis).append(",color:'#888888',dashStyle:'Dash',lineWidth:1.5,zIndex:2,");
				if (lastYearLegend) {
					js.append("id:'lastYear',name:").append(string(I18nProperties.getString(Strings.climateDashboardSeriesLastYear))).append(',');
					lastYearLegend = false;
				} else {
					js.append("linkedTo:'lastYear',");
				}
				js.append("data:[").append(lineData(lastYear)).append("]},");
			}
			axis++;
		}

		if (anyShaded) {
			// the shading is a plot band, which has no legend entry of its own
			js.append("{type:'area',name:")
				.append(string(String.format(I18nProperties.getString(Strings.climateDashboardSeriesUnusual), shading.toString().toLowerCase())))
				.append(",color:'")
				.append(SHADING_COLOR)
				.append("',yAxis:0,data:[],enableMouseTracking:false},");
		}
		js.append("]};");
		return js.toString();
	}

	/**
	 * @return the values of a line, a lone value (no value on either side) with a dot, since the lines have no markers
	 *         (decisions 143, 148)
	 */
	static String lineData(List<Double> values) {
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			if (ClimateChartScript.isLone(values, i)) {
				data.append("{y:").append(number(values.get(i))).append(',').append(ClimateChartScript.LONE_MARKER).append("},");
			} else {
				data.append(number(values.get(i))).append(',');
			}
		}
		return data.toString();
	}

	private static String columnData(List<Double> values) {
		return values.stream().map(v -> number(v) + ",").collect(Collectors.joining());
	}

	/**
	 * Adds a column panel per disease over its usual range band (decision 123).
	 *
	 * @return the number of y axes used
	 */
	private static int appendDiseasePanels(StringBuilder js, List<ClimateCaseSeriesDto> cases, Measure measure, int count) {

		int axis = 0;
		boolean usualLegend = true;
		for (int d = 0; d < cases.size(); d++) {
			ClimateCaseSeriesDto series = cases.get(d);
			boolean hasRange = series.countWeeksWithUsualRange() > 0;
			if (hasRange) {
				js.append("{type:'arearange',yAxis:").append(axis).append(",color:'#bdbdbd',fillOpacity:0.55,zIndex:0,");
				if (usualLegend) {
					js.append("id:'usual',name:").append(string(usualRangeName(series))).append(',');
					usualLegend = false;
				} else {
					js.append("linkedTo:'usual',");
				}
				js.append("data:[");
				for (int i = 0; i < count; i++) {
					ClimateRangeDto range = usualRange(series.getWeeks().get(i), measure);
					js.append('[')
						.append(i)
						.append(',')
						.append(range != null ? number(range.getP10()) : "null")
						.append(',')
						.append(range != null ? number(range.getP90()) : "null")
						.append("],");
				}
				js.append("]},");
			}
			js.append("{type:'column',name:")
				.append(string(series.getDisease().toString()))
				.append(",yAxis:")
				.append(axis)
				.append(",color:'")
				.append(DISEASE_COLORS[d % DISEASE_COLORS.length])
				.append("',zIndex:1,data:[");
			for (int i = 0; i < count; i++) {
				js.append(number(caseValue(series.getWeeks().get(i), measure))).append(',');
			}
			js.append("]},");
			axis++;
		}
		return axis;
	}

	/**
	 * Adds the heatmap (decision 129): one row per disease, each cell coloured by its share of that disease's busiest
	 * week in the period; an amber outline on unusual weeks of the shaded variable (with the lag); a dot where the week
	 * is above the usual range.
	 *
	 * @return the number of y axes used
	 */
	private static int appendHeatmap(
		StringBuilder js,
		ClimateWeeklySeriesDto climate,
		List<ClimateCaseSeriesDto> cases,
		ClimateVariable shading,
		Measure measure,
		int lag,
		int count) {

		if (cases.isEmpty()) {
			return 0;
		}
		js.append("{type:'heatmap',yAxis:0,colorAxis:0,name:")
			.append(string(I18nProperties.getString(Strings.climateDashboardSeriesHeatmap)))
			.append(",borderWidth:1,borderColor:'#ffffff',colsize:1,showInLegend:false,data:[");
		StringBuilder above = new StringBuilder();
		StringBuilder outlines = new StringBuilder();
		for (int d = 0; d < cases.size(); d++) {
			ClimateCaseSeriesDto series = cases.get(d);
			double peak = 0;
			for (int i = 0; i < count; i++) {
				Double value = caseValue(series.getWeeks().get(i), measure);
				peak = Math.max(peak, value != null ? value : 0);
			}
			for (int i = 0; i < count; i++) {
				ClimateCaseWeekDto week = series.getWeeks().get(i);
				Double value = caseValue(week, measure);
				// a week without an aggregate report has no value: an empty cell, not a pale one (decision 147)
				Double share = value == null ? null : peak > 0 ? value / peak : 0;
				ClimateWeekDto shifted = shading != null ? shiftedWeek(climate, i, lag) : null;
				js.append("{x:").append(i).append(",y:").append(d).append(",value:").append(number(share)).append("},");
				if (shifted != null && shifted.get(shading).isUnusual()) {
					outlines.append("{x:").append(i).append(",y:").append(d).append(",value:0,color:'rgba(0,0,0,0)'},");
				}
				if (week.isAboveUsualRange()) {
					above.append('[').append(i).append(',').append(d).append("],");
				}
			}
		}
		js.append("]},");
		if (outlines.length() > 0) {
			// unusual weeks as amber outlines over the cells, not fills (UI spec); the v8 heatmap ignores borders per cell
			js.append("{type:'heatmap',yAxis:0,colorAxis:false,showInLegend:false,enableMouseTracking:false,colsize:1,zIndex:2,")
				.append("borderColor:'")
				.append(OUTLINE_COLOR)
				.append("',borderWidth:2,data:[")
				.append(outlines)
				.append("]},");
		}
		if (above.length() > 0) {
			js.append("{type:'scatter',yAxis:0,name:")
				.append(string(I18nProperties.getString(Strings.climateDashboardAboveUsual)))
				.append(",color:'#ffffff',marker:{symbol:'circle',radius:3,lineColor:'#333333',lineWidth:1},zIndex:3,data:[")
				.append(above)
				.append("]},");
		}
		return 1;
	}

	/**
	 * @return the left margin of the heatmap view: wide enough for the longest disease name beside its row
	 */
	static int heatmapMargin(List<ClimateCaseSeriesDto> cases) {
		int longest = cases.stream().mapToInt(series -> series.getDisease().toString().length()).max().orElse(0);
		return Math.max(ClimateChartScript.MARGIN_LEFT, Math.min(240, 7 * longest + 20));
	}

	static String heatmapTitle(Measure measure) {
		return html(
			String.format(
				I18nProperties.getString(Strings.climateDashboardHeatmapTitle),
				I18nProperties.getCaption(measure == Measure.INCIDENCE ? Captions.climateDashboardMeasureIncidence : Captions.climateDashboardMeasureCount)
					.toLowerCase()));
	}

	private static void appendAxis(StringBuilder js, int[] row, String title, String extra) {
		js.append("{top:")
			.append(row[0])
			.append(",height:")
			.append(row[1])
			.append(",offset:0,title:{text:null},panelTitle:")
			.append(string(title))
			.append(",lineWidth:1,labels:{x:-6},gridLineDashStyle:'Dot'")
			.append(extra)
			.append("},");
	}

	static List<ClimateVariable> orderedVariables(Set<ClimateVariable> variables) {
		return Arrays.stream(ClimateVariable.values()).filter(variables::contains).collect(Collectors.toList());
	}

	/**
	 * @return the disease panel's name as HTML, with the reason when it has no usual range (decision 123)
	 */
	static String diseaseTitle(ClimateCaseSeriesDto series, Measure measure) {
		// a disease without case surveillance shows its lab-confirmed aggregate reports, and says so (decision 147)
		boolean aggregate = series.getCountSource() == ClimateCaseCountSource.AGGREGATE_LAB_CONFIRMED;
		String format = measure == Measure.INCIDENCE
			? aggregate ? Strings.climateDashboardPanelLabConfirmedIncidence : Strings.climateDashboardPanelIncidence
			: aggregate ? Strings.climateDashboardPanelLabConfirmed : Strings.climateDashboardPanelCount;
		String title = "<b>" + html(String.format(I18nProperties.getString(format), series.getDisease().toString())) + "</b>";
		if (series.countWeeksWithUsualRange() == 0) {
			Set<ClimateUsualRangeMissing> reasons = series.getWeeks()
				.stream()
				.map(ClimateCaseWeekDto::getUsualRangeMissing)
				.filter(java.util.Objects::nonNull)
				.collect(Collectors.toCollection(LinkedHashSet::new));
			if (!reasons.isEmpty()) {
				title += " <span style=\"color:#888888\">· "
					+ html(reasons.stream().sorted().map(ClimateUsualRangeMissing::toString).collect(Collectors.joining("; ")))
					+ "</span>";
			}
		}
		return title;
	}

	static String usualRangeName(ClimateCaseSeriesDto series) {
		if (series.getExcludedYears().isEmpty()) {
			return String.format(I18nProperties.getString(Strings.climateDashboardUsualRange), series.getFirstReferenceYear(), series.getLastReferenceYear());
		}
		return String.format(
			I18nProperties.getString(Strings.climateDashboardUsualRangeExcluded),
			series.getFirstReferenceYear(),
			series.getLastReferenceYear(),
			series.getExcludedYears().stream().sorted().map(String::valueOf).collect(Collectors.joining(", ")));
	}

	/**
	 * @return the climate panel's name as HTML: its icon and name, and the shift when there is one
	 */
	static String climateTitle(ClimateVariable variable, int lag) {
		String title = ClimateIcon.of(variable).html() + " <b>" + html(I18nProperties.getString(axisName(variable))) + "</b>";
		if (lag == 1) {
			title += " " + html(I18nProperties.getString(Strings.climateDashboardPanelShiftedOne));
		} else if (lag > 1) {
			title += " " + html(String.format(I18nProperties.getString(Strings.climateDashboardPanelShifted), lag));
		}
		return title;
	}

	/**
	 * @return the note after a climate panel's name when the period has no value of the variable at all (decision 124)
	 */
	static String climateNote(Selection selection, ClimateWeeklySeriesDto climate, ClimateVariable variable) {
		if (climate.getWeeks().stream().anyMatch(week -> week.get(variable).getValue() != null)) {
			return "";
		}
		String note = !climate.isHasPoint() && selection.getDistrict() != null
			? String.format(I18nProperties.getString(Strings.climateDashboardNoPoint), selection.getPlaceName())
			: String.format(I18nProperties.getString(Strings.climateDashboardNoClimateData), selection.getPlaceName());
		return " <span style=\"color:#888888\">· " + html(note) + "</span>";
	}

	private static String axisName(ClimateVariable variable) {
		switch (variable) {
		case RAINFALL:
			return Strings.climateAxisRainfall;
		case TEMPERATURE:
			return Strings.climateAxisTemperature;
		default:
			return Strings.climateAxisHumidity;
		}
	}

	private static String seriesName(ClimateVariable variable) {
		switch (variable) {
		case RAINFALL:
			return I18nProperties.getString(Strings.climateDashboardSeriesRainfall);
		case TEMPERATURE:
			return I18nProperties.getString(Strings.climateDashboardSeriesTemperature);
		default:
			return I18nProperties.getString(Strings.climateSeriesHumidity);
		}
	}

	static String color(ClimateVariable variable) {
		switch (variable) {
		case RAINFALL:
			return "#3c78b4";
		case TEMPERATURE:
			return "#d35400";
		default:
			return "#1f6f8b";
		}
	}

	/**
	 * @return the week's count or incidence, or null for a week without an aggregate report: a gap, not 0 (decision 147)
	 */
	static Double caseValue(ClimateCaseWeekDto week, Measure measure) {
		if (!week.isReported()) {
			return null;
		}
		return measure == Measure.INCIDENCE ? week.getIncidence() : Double.valueOf(week.getCount());
	}

	static ClimateRangeDto usualRange(ClimateCaseWeekDto week, Measure measure) {
		return measure == Measure.INCIDENCE ? week.getUsualRangeIncidence() : week.getUsualRange();
	}

	/**
	 * @return the details line of the week at {@code index}, as HTML: each disease's figures, then the climate of the
	 *         shifted week (decision 124's hover)
	 */
	static String details(
		EpiWeek week,
		int index,
		ClimateWeeklySeriesDto climate,
		List<ClimateCaseSeriesDto> cases,
		Set<ClimateVariable> variables,
		Measure measure,
		int lag) {

		StringBuilder html = new StringBuilder("<b>").append(html(weekTitle(week))).append("</b>");
		List<String> diseaseParts = new ArrayList<>();
		for (ClimateCaseSeriesDto series : cases) {
			diseaseParts.add(caseDetail(series, series.getWeeks().get(index), measure));
		}
		if (!diseaseParts.isEmpty()) {
			html.append(" · ").append(String.join(" · ", diseaseParts));
		}

		if (!variables.isEmpty()) {
			html.append("<br/>");
			ClimateWeekDto shifted = shiftedWeek(climate, index, lag);
			if (lag > 0) {
				EpiWeek shiftedWeek = shifted != null ? shifted.getWeek() : null;
				String prefix = shiftedWeek == null
					? ""
					: lag == 1
						? String.format(I18nProperties.getString(Strings.climateDashboardShiftedOne), shiftedWeek.getWeek(), shiftedWeek.getYear())
						: String.format(I18nProperties.getString(Strings.climateDashboardShifted), shiftedWeek.getWeek(), shiftedWeek.getYear(), lag);
				if (!prefix.isEmpty()) {
					html.append(html(prefix)).append(' ');
				}
			}
			if (shifted == null) {
				html.append(html(I18nProperties.getString(Strings.climateNoData)));
			} else {
				List<String> climateParts = new ArrayList<>();
				for (ClimateVariable variable : orderedVariables(variables)) {
					climateParts.add(climateDetail(climate, shifted, variable));
				}
				html.append(String.join(" · ", climateParts));
			}
		}
		return html.toString();
	}

	static String weekTitle(EpiWeek week) {
		LocalDate start = ClimateCaptions.toLocalDate(DateHelper.getEpiWeekStart(week));
		return String.format(
			I18nProperties.getString(Strings.climateDashboardWeek),
			week.getWeek(),
			week.getYear(),
			ClimateCaptions.date(start),
			ClimateCaptions.date(start.plusDays(6)));
	}

	static String caseDetail(ClimateCaseSeriesDto series, ClimateCaseWeekDto week, Measure measure) {

		String disease = series.getDisease().toString();
		String detail;
		if (series.getCountSource() == ClimateCaseCountSource.AGGREGATE_LAB_CONFIRMED) {
			// lab-confirmed aggregate reports, or that the week has none (decision 147)
			if (!week.isReported()) {
				return html(String.format(I18nProperties.getString(Strings.climateDashboardDetailNoReport), disease));
			}
			detail = measure == Measure.INCIDENCE && week.getIncidence() != null
				? String.format(
					I18nProperties.getString(Strings.climateDashboardDetailLabConfirmedIncidence),
					disease,
					ClimateCaptions.oneDecimal(week.getIncidence()),
					week.getCount())
				: String.format(I18nProperties.getString(Strings.climateDashboardDetailLabConfirmed), disease, week.getCount());
		} else {
			detail = measure == Measure.INCIDENCE && week.getIncidence() != null
				? String.format(
					I18nProperties.getString(week.getCount() == 1 ? Strings.climateDashboardDetailIncidenceOne : Strings.climateDashboardDetailIncidence),
					disease,
					ClimateCaptions.oneDecimal(week.getIncidence()),
					week.getCount())
				: String.format(
					I18nProperties.getString(week.getCount() == 1 ? Strings.climateDashboardDetailCountOne : Strings.climateDashboardDetailCount),
					disease,
					week.getCount());
			if (week.getReportDateCount() > 0) {
				detail += " " + String.format(I18nProperties.getString(Strings.climateDashboardDetailReportDate), week.getReportDateCount());
			}
		}
		ClimateRangeDto range = usualRange(week, measure);
		if (range != null) {
			detail += " " + String.format(
				I18nProperties.getString(Strings.climateDashboardDetailUsual),
				ClimateCaptions.oneDecimal(range.getP10()),
				ClimateCaptions.oneDecimal(range.getP90()));
			if (week.isAboveUsualRange()) {
				detail += " · " + I18nProperties.getString(Strings.climateDashboardAboveUsual);
			}
		} else if (week.getUsualRangeMissing() != null && series.countWeeksWithUsualRange() > 0) {
			detail += " (" + week.getUsualRangeMissing().toString() + ")";
		}
		return html(detail);
	}

	static String climateDetail(ClimateWeeklySeriesDto climate, ClimateWeekDto week, ClimateVariable variable) {

		ClimateWeekValueDto value = week.get(variable);
		String unit = variable == ClimateVariable.RAINFALL ? " mm" : variable == ClimateVariable.TEMPERATURE ? " °C" : " %";
		String format = variable == ClimateVariable.RAINFALL
			? Strings.climateDetailRainfall
			: variable == ClimateVariable.TEMPERATURE ? Strings.climateDetailTemperature : Strings.climateDetailHumidity;

		if (value.getValue() == null) {
			String gap = value.getDays() != null
				? String.format(I18nProperties.getString(Strings.climateDashboardDetailDays), value.getDays())
				: climate.getDistrict() == null
					? I18nProperties.getString(Strings.climateDashboardDetailNoDistricts)
					: I18nProperties.getString(Strings.climateNoData);
			return ClimateIcon.of(variable).html() + " " + html(String.format(I18nProperties.getString(format), gap));
		}

		String text = String.format(I18nProperties.getString(format), ClimateCaptions.oneDecimal(value.getValue()) + unit);
		if (variable == ClimateVariable.TEMPERATURE && (week.getTemperatureMin() != null || week.getTemperatureMax() != null)) {
			text += " ("
				+ String.format(
					I18nProperties.getString(Strings.climateDetailMinMax),
					week.getTemperatureMin() != null ? ClimateCaptions.oneDecimal(week.getTemperatureMin()) : "–",
					week.getTemperatureMax() != null ? ClimateCaptions.oneDecimal(week.getTemperatureMax()) : "–")
				+ ")";
		}
		if (variable == ClimateVariable.HUMIDITY && week.getHumidityMeasure() == HumidityMeasure.NOON_READING) {
			text += " " + I18nProperties.getString(Strings.climateDetailNoonReading);
		}
		if (value.getNormal() != null) {
			text += " "
				+ String.format(
					I18nProperties.getString(Strings.climateDetailNormal),
					ClimateCaptions.oneDecimal(value.getNormal().getP10()),
					ClimateCaptions.oneDecimal(value.getNormal().getP90()) + unit);
			if (value.isUnusual()) {
				text += " · " + I18nProperties.getString(Strings.climateDashboardAboveNormal);
			}
		} else if (value.getLastYear() != null) {
			text += " " + String.format(I18nProperties.getString(Strings.climateDashboardDetailLastYear), ClimateCaptions.oneDecimal(value.getLastYear()) + unit);
		}
		return ClimateIcon.of(variable).html() + " " + html(text);
	}
}
