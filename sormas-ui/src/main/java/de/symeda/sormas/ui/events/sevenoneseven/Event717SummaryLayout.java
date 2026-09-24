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
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
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
 * Summary of the 7-1-7 view of the event directory: the 7-1-7 performance of the assessed events matching the event filters, like
 * the summary reports of the "Assess 7-1-7 results" sheet of the 7-1-7 data consolidation spreadsheet.
 * <p>
 * Percentages only count evaluable events (meeting or not meeting the target); the other results are always listed next to them.
 */
@SuppressWarnings("serial")
public class Event717SummaryLayout extends VerticalLayout {

	private static final String NO_VALUE = "–";

	private final Label cardsLabel;
	private final HighChart chart;
	private final Grid<Event717EarlyResponseAction> actionsGrid;

	private Event717SummaryDto summary = new Event717SummaryDto(0);

	public Event717SummaryLayout() {

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(true);

		addComponent(createHeading(I18nProperties.getString(Strings.headingEvent717OverallPerformance)));
		cardsLabel = new Label("", ContentMode.HTML);
		cardsLabel.setWidth(100, Unit.PERCENTAGE);
		addComponent(cardsLabel);

		addComponent(createHeading(I18nProperties.getString(Strings.headingEvent717PercentMeetingTargets)));
		chart = new HighChart();
		chart.setWidth(100, Unit.PERCENTAGE);
		chart.setHeight(300, Unit.PIXELS);
		addComponent(chart);

		addComponent(createHeading(I18nProperties.getString(Strings.headingEvent717EarlyResponseActionsPerformance)));
		actionsGrid = createActionsGrid();
		addComponent(actionsGrid);

		Label denominatorInfo =
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoEvent717SummaryDenominator), ContentMode.HTML);
		denominatorInfo.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(denominatorInfo, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		addComponent(denominatorInfo);
	}

	private static Label createHeading(String caption) {

		Label heading = new Label(caption);
		CssStyles.style(heading, CssStyles.H3, CssStyles.VSPACE_TOP_3);
		return heading;
	}

	/**
	 * Loads the summary of the assessed events matching the criteria.
	 *
	 * @return The number of assessed events.
	 */
	public int refresh(EventCriteria criteria) {

		summary = FacadeProvider.getEvent717AssessmentFacade().getSummary(criteria);

		cardsLabel.setValue(buildCardsHtml());
		chart.setHcjs(buildChartJs());
		actionsGrid.getDataProvider().refreshAll();

		return summary.getAssessedEvents();
	}

	private String buildCardsHtml() {

		StringBuilder html = new StringBuilder("<div style=\"display:flex;flex-wrap:wrap;gap:12px;\">");
		html.append(
			card(
				I18nProperties.getString(Strings.headingEvent717AssessedEvents),
				"#6E7A87",
				String.valueOf(summary.getAssessedEvents()),
				"",
				""));
		for (Event717Interval interval : Event717Interval.values()) {
			html.append(outcomeCard(interval.toString(), Event717IntervalColors.getColor(interval), summary.getInterval(interval)));
		}
		html.append(
			outcomeCard(I18nProperties.getCaption(Captions.event717AllTargets), Event717IntervalColors.COLOR_ALL_TARGETS, summary.getAllTargets()));
		return html.append("</div>").toString();
	}

	private static String outcomeCard(String caption, String color, Event717OutcomeCountsDto counts) {

		Integer percentage = counts.getPercentageWithinTarget();
		String evaluable = percentage != null
			? String.format(I18nProperties.getString(Strings.infoEvent717Evaluable), counts.getWithinTarget(), counts.getEvaluable())
			: I18nProperties.getString(Strings.infoEvent717NoEvaluable);
		String details = String.format(
			I18nProperties.getString(Strings.infoEvent717OutcomeCounts),
			counts.getOverTarget(),
			counts.getMissing() + counts.getIncomplete(),
			counts.getDataError());

		return card(caption, color, percentage != null ? percentage + "%" : NO_VALUE, evaluable, details);
	}

	private static String card(String caption, String color, String value, String subtitle, String details) {

		return "<div style=\"flex:1 1 180px;min-width:180px;border:1px solid #DDDDDD;border-radius:4px;overflow:hidden;\">"
			+ "<div style=\"background-color:" + color + ";color:#FFFFFF;font-weight:bold;padding:6px 10px;\">" + escape(caption) + "</div>"
			+ "<div style=\"padding:8px 10px;\">"
			+ "<div style=\"font-size:32px;font-weight:bold;line-height:1.2;color:" + color + ";\">" + escape(value) + "</div>"
			+ "<div style=\"color:#555555;\">" + escape(subtitle) + "</div>"
			+ "<div style=\"color:#888888;font-size:12px;white-space:normal;\">" + escape(details) + "</div>"
			+ "</div></div>";
	}

	private String buildChartJs() {

		StringBuilder categories = new StringBuilder();
		StringBuilder data = new StringBuilder();
		for (Event717Interval interval : Event717Interval.values()) {
			appendPoint(categories, data, interval.toString(), Event717IntervalColors.getColor(interval), summary.getInterval(interval));
		}
		appendPoint(
			categories,
			data,
			I18nProperties.getCaption(Captions.event717AllTargets),
			Event717IntervalColors.COLOR_ALL_TARGETS,
			summary.getAllTargets());

		//@formatter:off
		return "var options = {"
			+ "chart: { type: 'column', backgroundColor: 'transparent' },"
			+ "title: { text: '' },"
			+ "credits: { enabled: false },"
			+ "legend: { enabled: false },"
			+ "exporting: { enabled: false },"
			+ "xAxis: { categories: [" + categories + "] },"
			+ "yAxis: { min: 0, max: 100, title: { text: '' }, labels: { format: '{value}%' } },"
			+ "tooltip: { pointFormat: '<b>{point.y}%</b>' },"
			+ "plotOptions: { column: { dataLabels: { enabled: true, format: '{y}%' } } },"
			+ "series: [{ name: '" + escapeJs(I18nProperties.getCaption(Captions.event717PercentWithinTarget)) + "', data: [" + data + "] }]"
			+ "};";
		//@formatter:on
	}

	private static void appendPoint(StringBuilder categories, StringBuilder data, String caption, String color, Event717OutcomeCountsDto counts) {

		if (categories.length() > 0) {
			categories.append(",");
			data.append(",");
		}
		categories.append("'").append(escapeJs(caption)).append("'");
		Integer percentage = counts.getPercentageWithinTarget();
		data.append("{ y: ").append(percentage != null ? percentage : "null").append(", color: '").append(color).append("' }");
	}

	private Grid<Event717EarlyResponseAction> createActionsGrid() {

		Grid<Event717EarlyResponseAction> grid = new Grid<>();
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.setItems(Arrays.asList(Event717EarlyResponseAction.values()));
		grid.setHeightByRows(Event717EarlyResponseAction.values().length);

		grid.addColumn(action -> I18nProperties.getPrefixCaption(Event717IndexDto.I18N_PREFIX, Event717IndexDto.getEarlyResponseActionDaysProperty(action)))
			.setCaption(I18nProperties.getCaption(Captions.Event717EarlyResponseAction))
			.setDescriptionGenerator(Event717EarlyResponseAction::toString)
			.setExpandRatio(1);
		grid.addColumn(action -> {
			Integer percentage = counts(action).getPercentageWithinTarget();
			return percentage != null ? percentage + "%" : NO_VALUE;
		}).setCaption(I18nProperties.getCaption(Captions.event717PercentWithinTarget));
		addCountColumn(grid, Event717TimelinessStatus.WITHIN_TARGET.toString(), Event717OutcomeCountsDto::getWithinTarget);
		addCountColumn(grid, Event717TimelinessStatus.OVER_TARGET.toString(), Event717OutcomeCountsDto::getOverTarget);
		addCountColumn(grid, Event717TimelinessStatus.MISSING.toString(), c -> c.getMissing() + c.getIncomplete());
		addCountColumn(grid, I18nProperties.getCaption(Captions.event717NotApplicableShort), Event717OutcomeCountsDto::getNotApplicable);
		addCountColumn(grid, Event717TimelinessStatus.DATA_ERROR.toString(), Event717OutcomeCountsDto::getDataError);

		grid.getColumns().forEach(column -> column.setSortable(false));
		return grid;
	}

	private void addCountColumn(Grid<Event717EarlyResponseAction> grid, String caption, Function<Event717OutcomeCountsDto, Integer> count) {
		grid.addColumn(action -> count.apply(counts(action))).setCaption(caption);
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
