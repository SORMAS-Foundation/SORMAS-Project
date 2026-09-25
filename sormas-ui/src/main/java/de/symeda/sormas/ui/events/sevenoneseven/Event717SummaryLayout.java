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
package de.symeda.sormas.ui.events.sevenoneseven;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.text.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.sevenoneseven.Event717EarlyResponseAction;
import de.symeda.sormas.api.event.sevenoneseven.Event717IndexDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.event.sevenoneseven.Event717OutcomeCountsDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717SummaryDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;

/**
 * 7-1-7 performance of the assessed events matching the filters of the 7-1-7 summary, like the summary reports of the "Assess 7-1-7
 * results" sheet of the 7-1-7 data consolidation spreadsheet. Styles are defined in the dashboard view theme.
 * <p>
 * Percentages only count evaluable events (meeting or not meeting the target); the other results are shown next to them.
 */
@SuppressWarnings("serial")
public class Event717SummaryLayout extends VerticalLayout {

	private static final String NO_VALUE = "–";
	private static final String ERROR_ICON = "<span class=\"event717-error-icon\">!</span>";

	private final Label scopeLabel;
	private final Label cardsLabel;
	private final HighChart chart;
	private final Grid<Event717EarlyResponseAction> actionsGrid;

	private Event717SummaryDto summary = new Event717SummaryDto(0);

	public Event717SummaryLayout() {

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		// the gaps are set on the components, the layout spacing would add to them
		setSpacing(false);
		addStyleName("event717-summary");

		Label titleLabel = new Label(I18nProperties.getString(Strings.headingEvent717Performance));
		titleLabel.addStyleName("event717-panel-title");
		scopeLabel = new Label("", ContentMode.HTML);
		scopeLabel.setWidth(100, Unit.PERCENTAGE);
		VerticalLayout header = new VerticalLayout(titleLabel, scopeLabel);
		header.setMargin(false);
		header.setSpacing(false);
		addComponent(header);

		cardsLabel = new Label("", ContentMode.HTML);
		cardsLabel.setWidth(100, Unit.PERCENTAGE);
		cardsLabel.addStyleName(CssStyles.VSPACE_TOP_3);
		addComponent(cardsLabel);

		chart = new HighChart();
		chart.setWidth(100, Unit.PERCENTAGE);
		chart.setHeight(300, Unit.PIXELS);
		VerticalLayout chartPanel = createPanel(
			I18nProperties.getString(Strings.headingEvent717PercentMeetingTargets),
			I18nProperties.getString(Strings.infoEvent717EvaluableOnly),
			chart);
		chartPanel.addStyleName("event717-panel-chart");

		actionsGrid = createActionsGrid();
		VerticalLayout tablePanel = createPanel(
			I18nProperties.getString(Strings.headingEvent717EarlyResponseActionsPerformance),
			String.format(I18nProperties.getString(Strings.infoEvent717ActionsMeasured), Event717Interval.RESPONSE.getTargetDays()),
			actionsGrid);
		tablePanel.addStyleName("event717-panel-table");

		// a css layout, so that the panels wrap below each other on small screens
		CssLayout panels = new CssLayout(chartPanel, tablePanel);
		panels.setWidth(100, Unit.PERCENTAGE);
		panels.addStyleNames("event717-panels", CssStyles.VSPACE_TOP_3);
		addComponent(panels);
	}

	private static VerticalLayout createPanel(String title, String subtitle, com.vaadin.ui.Component content) {

		Label titleLabel = new Label(title);
		titleLabel.addStyleName("event717-panel-title");
		Label subtitleLabel = new Label(subtitle);
		subtitleLabel.setWidth(100, Unit.PERCENTAGE);
		subtitleLabel.addStyleNames("event717-panel-subtitle", CssStyles.VSPACE_3);

		VerticalLayout panel = new VerticalLayout(titleLabel, subtitleLabel, content);
		panel.setMargin(false);
		panel.setSpacing(false);
		panel.addStyleName("event717-panel");
		return panel;
	}

	/**
	 * Loads the summary of the assessed events matching the criteria.
	 */
	public void refresh(EventCriteria criteria) {

		summary = FacadeProvider.getEvent717AssessmentFacade().getSummary(criteria);

		scopeLabel.setValue(buildScopeHtml());
		cardsLabel.setValue(buildCardsHtml());
		chart.setHcjs(buildChartJs());
		actionsGrid.getDataProvider().refreshAll();
	}

	/**
	 * The number of assessed events and the legend shared by all cards.
	 */
	private String buildScopeHtml() {

		StringBuilder html = new StringBuilder("<div class=\"event717-scope\"><span class=\"event717-scope-count\">");
		html.append(
			String.format(
				escape(I18nProperties.getString(Strings.infoEvent717AssessedEvents)),
				"<span class=\"event717-scope-value\">" + summary.getAssessedEvents() + "</span>"))
			.append("</span>");
		html.append("<span class=\"event717-scope-legend\">")
			.append(legendEntry(swatch("event717-within-target"), Event717TimelinessStatus.WITHIN_TARGET.toString()))
			.append(legendEntry(swatch("event717-over-target"), I18nProperties.getCaption(Captions.event717DoesNotMeet)))
			.append(legendEntry(swatch("event717-missing"), I18nProperties.getCaption(Captions.event717MissingOrIncomplete)))
			.append(legendEntry(ERROR_ICON, Event717TimelinessStatus.DATA_ERROR.toString()))
			.append("</span>");
		return html.append("</div>").toString();
	}

	private static String legendEntry(String marker, String caption) {
		return "<span class=\"event717-legend-entry\">" + marker + escape(caption) + "</span>";
	}

	private String buildCardsHtml() {

		StringBuilder html = new StringBuilder("<div class=\"event717-cards\">");
		for (Event717Interval interval : Event717Interval.values()) {
			String target = String.format(
				I18nProperties.getString(interval.getTargetDays() == 1 ? Strings.infoEvent717TargetDay : Strings.infoEvent717TargetDays),
				interval.getTargetDays());
			// only the response can be incomplete; the other intervals lack a date
			Event717TimelinessStatus missingStatus =
				interval == Event717Interval.RESPONSE ? Event717TimelinessStatus.INCOMPLETE : Event717TimelinessStatus.MISSING;
			html.append(
				card(
					interval.toString(),
					target,
					Event717IntervalColors.getColor(interval),
					Event717IntervalColors.getBackgroundColor(interval),
					Event717IntervalColors.getColor(interval),
					summary.getInterval(interval),
					Event717TimelinessStatus.WITHIN_TARGET.toString(),
					missingStatus.toString()));
		}
		html.append(
			card(
				I18nProperties.getCaption(Captions.event717AllTargets),
				I18nProperties.getString(Strings.infoEvent717AllTargets),
				Event717IntervalColors.COLOR_ALL_TARGETS,
				null,
				null,
				summary.getAllTargets(),
				I18nProperties.getCaption(Captions.event717MeetsAll),
				Event717TimelinessStatus.INCOMPLETE.toString()));
		return html.append("</div>").toString();
	}

	/**
	 * @param backgroundColor
	 *            The background of the card, white if null.
	 * @param valueColor
	 *            The color of the percentage, the default text color if null.
	 */
	private static String card(
		String title,
		String target,
		String color,
		String backgroundColor,
		String valueColor,
		Event717OutcomeCountsDto counts,
		String withinTargetCaption,
		String missingCaption) {

		Integer percentage = counts.getPercentageWithinTarget();
		int missing = counts.getMissing() + counts.getIncomplete();

		StringBuilder html = new StringBuilder();
		html.append("<div class=\"event717-card\" style=\"border-top-color:").append(color).append(";");
		if (backgroundColor != null) {
			html.append("background-color:").append(backgroundColor).append(";");
		}
		html.append("\">");
		html.append("<div class=\"event717-card-header\"><span class=\"event717-card-title\">")
			.append(escape(title))
			.append("</span><span class=\"event717-card-target\">")
			.append(escape(target))
			.append("</span></div>");
		html.append("<div class=\"event717-card-value\"").append(valueColor != null ? " style=\"color:" + valueColor + ";\"" : "").append(">")
			.append(percentage != null ? percentage + "<span class=\"event717-unit\">%</span>" : NO_VALUE)
			.append("</div>");
		html.append("<div class=\"event717-card-evaluable\">")
			.append(
				percentage != null
					? String.format(
						escape(I18nProperties.getString(Strings.infoEvent717Evaluable)),
						"<b>"
							+ escape(
								String.format(
									I18nProperties.getString(Strings.infoEvent717EvaluableCount),
									counts.getWithinTarget(),
									counts.getEvaluable()))
							+ "</b>")
					: escape(I18nProperties.getString(Strings.infoEvent717NoEvaluable)))
			.append("</div>");

		String overTargetCaption = I18nProperties.getCaption(Captions.event717DoesNotMeet);
		String dataErrorCaption = Event717TimelinessStatus.DATA_ERROR.toString();

		// share of each result among all assessed events
		html.append("<div class=\"event717-bar\">")
			.append(barSegment(counts.getWithinTarget(), "event717-within-target", withinTargetCaption))
			.append(barSegment(counts.getOverTarget(), "event717-over-target", overTargetCaption))
			.append(barSegment(missing, "event717-missing", missingCaption))
			.append(barSegment(counts.getDataError(), "event717-data-error", dataErrorCaption))
			.append("</div>");

		// the results are named in the legend above the cards
		html.append("<div class=\"event717-statuses\">")
			.append(status(swatch("event717-within-target"), withinTargetCaption, counts.getWithinTarget()))
			.append(status(swatch("event717-over-target"), overTargetCaption, counts.getOverTarget()))
			.append(status(swatch("event717-missing"), missingCaption, missing))
			.append(status(ERROR_ICON, dataErrorCaption, counts.getDataError()))
			.append("</div>");

		return html.append("</div>").toString();
	}

	private static String barSegment(int count, String styleName, String caption) {
		return count > 0
			? "<div class=\"" + styleName + "\" style=\"flex:" + count + " 1 0;\" data-tooltip=\"" + escape(caption + ": " + count) + "\"></div>"
			: "";
	}

	private static String status(String marker, String caption, int count) {
		return "<span class=\"event717-status\" data-tooltip=\"" + escape(caption) + "\">" + marker + "<span class=\"event717-count\">" + count
			+ "</span></span>";
	}

	private static String swatch(String styleName) {
		return "<span class=\"event717-swatch " + styleName + "\"></span>";
	}

	private String buildChartJs() {

		StringBuilder categories = new StringBuilder();
		StringBuilder data = new StringBuilder();
		for (Event717Interval interval : Event717Interval.values()) {
			appendPoint(
				categories,
				data,
				interval.toString(),
				Event717IntervalColors.getColor(interval),
				summary.getInterval(interval),
				Strings.infoEvent717MeetTarget);
		}
		appendPoint(
			categories,
			data,
			I18nProperties.getCaption(Captions.event717AllTargets),
			Event717IntervalColors.COLOR_ALL_TARGETS,
			summary.getAllTargets(),
			Strings.infoEvent717MeetAllTargets);

		//@formatter:off
		return "var options = {"
			+ "chart: { type: 'column', backgroundColor: 'transparent', style: { fontFamily: 'Open Sans, sans-serif' } },"
			+ "title: { text: '' },"
			+ "credits: { enabled: false },"
			+ "legend: { enabled: false },"
			+ "exporting: { enabled: false },"
			+ "xAxis: { categories: [" + categories + "], labels: { useHTML: true, style: { textAlign: 'center' } } },"
			+ "yAxis: { min: 0, max: 100, tickInterval: 25, title: { text: '' }, labels: { format: '{value}%' } },"
			+ "tooltip: { formatter: function() { return '<b>' + this.point.caption + '</b><br/>' + this.point.detail; } },"
			+ "plotOptions: { column: { maxPointWidth: 70, dataLabels: { enabled: true, format: '{y}%', style: { fontSize: '11px' } } } },"
			+ "series: [{ name: '" + escapeJs(I18nProperties.getCaption(Captions.event717PercentWithinTarget)) + "', data: [" + data + "] }]"
			+ "};";
		//@formatter:on
	}

	/**
	 * @param detailString
	 *            The tooltip text of the bar, with the events meeting the target and the evaluable events as parameters.
	 */
	private static void appendPoint(
		StringBuilder categories,
		StringBuilder data,
		String caption,
		String color,
		Event717OutcomeCountsDto counts,
		String detailString) {

		if (categories.length() > 0) {
			categories.append(",");
			data.append(",");
		}
		String label = escape(caption) + "<br/><span style=\"font-size:11px;color:#666666;\">"
			+ escape(String.format(I18nProperties.getString(Strings.infoEvent717SampleSize), counts.getEvaluable())) + "</span>";
		categories.append("'").append(escapeJs(label)).append("'");
		String detail = String.format(I18nProperties.getString(detailString), counts.getWithinTarget(), counts.getEvaluable());
		Integer percentage = counts.getPercentageWithinTarget();
		data.append("{ y: ")
			.append(percentage != null ? percentage : "null")
			.append(", color: '")
			.append(color)
			.append("', caption: '")
			.append(escapeJs(escape(caption)))
			.append("', detail: '")
			.append(escapeJs(escape(detail)))
			.append("' }");
	}

	private Grid<Event717EarlyResponseAction> createActionsGrid() {

		Grid<Event717EarlyResponseAction> grid = new Grid<>();
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.setItems(Arrays.asList(Event717EarlyResponseAction.values()));
		grid.setHeightByRows(Event717EarlyResponseAction.values().length);

		grid.addColumn(action -> I18nProperties.getPrefixCaption(Event717IndexDto.I18N_PREFIX, Event717IndexDto.getEarlyResponseActionDaysProperty(action)))
			.setCaption(I18nProperties.getCaption(Captions.Action))
			.setDescriptionGenerator(Event717EarlyResponseAction::toString)
			.setWidth(160);
		grid.addColumn(action -> {
			Integer percentage = counts(action).getPercentageWithinTarget();
			return percentage != null ? percentage + "%" : NO_VALUE;
		}).setCaption(I18nProperties.getCaption(Captions.event717PercentWithinTarget)).setWidth(100);
		addCountColumn(grid, Event717TimelinessStatus.WITHIN_TARGET.toString(), Event717OutcomeCountsDto::getWithinTarget, 92);
		addCountColumn(grid, I18nProperties.getCaption(Captions.event717DoesNotMeet), Event717OutcomeCountsDto::getOverTarget, 98);
		addCountColumn(grid, Event717TimelinessStatus.MISSING.toString(), c -> c.getMissing() + c.getIncomplete(), 62);
		addCountColumn(grid, I18nProperties.getCaption(Captions.event717NotApplicableShort), Event717OutcomeCountsDto::getNotApplicable, 46);
		// the last column takes the remaining width, so that the other columns stay next to the actions on wide screens
		grid.addColumn(action -> counts(action).getDataError())
			.setCaption(Event717TimelinessStatus.DATA_ERROR.toString())
			.setMinimumWidth(100)
			.setExpandRatio(1);

		grid.getColumns().forEach(column -> column.setSortable(false));
		return grid;
	}

	private void addCountColumn(
		Grid<Event717EarlyResponseAction> grid,
		String caption,
		Function<Event717OutcomeCountsDto, Integer> count,
		double width) {
		grid.addColumn(action -> count.apply(counts(action))).setCaption(caption).setWidth(width);
	}

	private Event717OutcomeCountsDto counts(Event717EarlyResponseAction action) {
		return summary.getEarlyResponseAction(action);
	}

	/**
	 * @return A CSV export of the figures shown in the summary, for the criteria at the time of the download.
	 */
	public static StreamResource createExportResource(Supplier<EventCriteria> criteria) {

		return new StreamResource(() -> {
			Event717SummaryDto summary = FacadeProvider.getEvent717AssessmentFacade().getSummary(criteria.get());

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try (CSVWriter writer = CSVUtils
				.createCSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), FacadeProvider.getConfigFacade().getCsvSeparator())) {

				writer.writeNext(
					new String[] {
						I18nProperties.getCaption(Captions.event717AssessedEvents),
						String.valueOf(summary.getAssessedEvents()) });
				writer.writeNext(
					new String[] {
						I18nProperties.getCaption(Captions.event717Indicator),
						I18nProperties.getCaption(Captions.event717PercentWithinTarget),
						I18nProperties.getCaption(Captions.event717Evaluable),
						Event717TimelinessStatus.WITHIN_TARGET.toString(),
						Event717TimelinessStatus.OVER_TARGET.toString(),
						Event717TimelinessStatus.MISSING.toString(),
						Event717TimelinessStatus.INCOMPLETE.toString(),
						Event717TimelinessStatus.DATA_ERROR.toString(),
						I18nProperties.getCaption(Captions.event717NotApplicableShort) });
				for (Event717Interval interval : Event717Interval.values()) {
					writer.writeNext(exportRow(interval.toString(), summary.getInterval(interval)));
				}
				writer.writeNext(exportRow(I18nProperties.getCaption(Captions.event717AllTargets), summary.getAllTargets()));
				for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
					writer.writeNext(exportRow(action.toString(), summary.getEarlyResponseAction(action)));
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			return new ByteArrayInputStream(out.toByteArray());
		}, DownloadUtil.createFileNameWithCurrentDate(ExportEntityName.EVENT_717_SUMMARY, ".csv"));
	}

	private static String[] exportRow(String indicator, Event717OutcomeCountsDto counts) {

		Integer percentage = counts.getPercentageWithinTarget();
		return new String[] {
			indicator,
			percentage != null ? percentage + "%" : "",
			String.valueOf(counts.getEvaluable()),
			String.valueOf(counts.getWithinTarget()),
			String.valueOf(counts.getOverTarget()),
			String.valueOf(counts.getMissing()),
			String.valueOf(counts.getIncomplete()),
			String.valueOf(counts.getDataError()),
			String.valueOf(counts.getNotApplicable()) };
	}

	private static String escape(String text) {
		return StringEscapeUtils.escapeHtml4(text);
	}

	private static String escapeJs(String text) {
		return StringEscapeUtils.escapeEcmaScript(text);
	}
}
