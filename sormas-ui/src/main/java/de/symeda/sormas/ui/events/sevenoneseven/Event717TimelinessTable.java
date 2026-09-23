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

import java.util.EnumMap;
import java.util.Map;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.event.sevenoneseven.Event717IntervalResultDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessCalculator;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Shows the timeliness of the three 7-1-7 intervals as a table, so that it is also visible without the side panel, e.g. on
 * small screens.
 */
@SuppressWarnings({
	"serial",
	"java:S2160" })
public class Event717TimelinessTable extends VerticalLayout {

	private static final int INTERVAL_CELL_WIDTH = 90;

	private final Map<Event717Interval, Label> timelinessLabels = new EnumMap<>(Event717Interval.class);
	private final Map<Event717Interval, Label> metTargetLabels = new EnumMap<>(Event717Interval.class);

	private final GridLayout grid;

	public Event717TimelinessTable() {

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);
		addStyleName(CssStyles.VSPACE_3);

		Label heading = new Label(I18nProperties.getString(Strings.headingEvent717Timeliness));
		heading.addStyleName(CssStyles.H4);
		addComponent(heading);

		grid = new GridLayout(4, Event717Interval.values().length * 2 + 2);
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setSpacing(false);
		grid.setColumnExpandRatio(0, 0);
		grid.setColumnExpandRatio(1, 1);
		grid.setColumnExpandRatio(2, 1);
		grid.setColumnExpandRatio(3, 1);
		addComponent(grid);

		buildGrid();
	}

	private void buildGrid() {

		addHeaderCell(Captions.Event717Assessment_timelinessInterval, 0);
		addHeaderCell(Captions.Event717Assessment_timeliness, 1);
		addHeaderCell(Captions.Event717Assessment_target, 2);
		addHeaderCell(Captions.Event717Assessment_targetMet, 3);
		addDividerRow(1);

		Event717Interval[] intervals = Event717Interval.values();
		for (int i = 0; i < intervals.length; i++) {
			Event717Interval interval = intervals[i];
			int row = i * 2 + 2;

			Label intervalLabel = Event717IntervalColors.createIntervalLabel(interval);
			intervalLabel.setWidth(INTERVAL_CELL_WIDTH, Unit.PIXELS);
			intervalLabel.setHeight(100, Unit.PERCENTAGE);
			grid.addComponent(intervalLabel, 0, row);

			Label timelinessLabel = createValueLabel();
			timelinessLabels.put(interval, timelinessLabel);
			grid.addComponent(timelinessLabel, 1, row);

			Label targetLabel = createValueLabel();
			targetLabel.setValue(String.valueOf(interval.getTargetDays()));
			grid.addComponent(targetLabel, 2, row);

			Label metTargetLabel = createValueLabel();
			metTargetLabels.put(interval, metTargetLabel);
			grid.addComponent(metTargetLabel, 3, row);

			addDividerRow(row + 1);
		}
	}

	private void addHeaderCell(String captionKey, int column) {

		Label label = new Label(I18nProperties.getCaption(captionKey));
		CssStyles.style(label, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.LABEL_SMALL, CssStyles.HSPACE_LEFT_4);
		grid.addComponent(label, column, 0);
	}

	private Label createValueLabel() {

		Label label = new Label();
		CssStyles.style(label, CssStyles.HSPACE_LEFT_4, CssStyles.VSPACE_TOP_4);
		return label;
	}

	private void addDividerRow(int row) {

		Label divider = new Label();
		divider.setWidth(100, Unit.PERCENTAGE);
		divider.addStyleName(CssStyles.LABEL_BOTTOM_LINE);
		grid.addComponent(divider, 0, row, 3, row);
	}

	public void setValue(Event717TimelinessDto timeliness) {

		for (Event717Interval interval : Event717Interval.values()) {
			Event717IntervalResultDto result = timeliness.getResult(interval);
			timelinessLabels.get(interval).setValue(Event717TimelinessCalculator.formatDays(result.getDays()));

			Label metTargetLabel = metTargetLabels.get(interval);
			CssStyles
				.removeStyles(metTargetLabel, CssStyles.LABEL_POSITIVE, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_WARNING, CssStyles.LABEL_SECONDARY);

			Boolean targetMet = result.getTargetMet();
			if (targetMet != null) {
				metTargetLabel.setValue(I18nProperties.getEnumCaption(targetMet ? YesNoUnknown.YES : YesNoUnknown.NO));
				metTargetLabel.addStyleName(targetMet ? CssStyles.LABEL_POSITIVE : CssStyles.LABEL_CRITICAL);
			} else {
				// missing dates, an incomplete early response or a negative interval
				metTargetLabel.setValue(result.getStatus() != null ? result.getStatus().toString() : "");
				metTargetLabel
					.addStyleName(result.getStatus() == Event717TimelinessStatus.DATA_ERROR ? CssStyles.LABEL_WARNING : CssStyles.LABEL_SECONDARY);
			}
		}
	}
}
