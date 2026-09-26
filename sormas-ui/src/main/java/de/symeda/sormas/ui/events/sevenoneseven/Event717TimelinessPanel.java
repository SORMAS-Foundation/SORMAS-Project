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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.event.sevenoneseven.Event717IntervalResultDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessCalculator;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

/**
 * Compact summary of the 7-1-7 timeliness of an event: the overall result, the three intervals with their timeliness against
 * the target and the completion of the early response.
 */
@SuppressWarnings({
	"serial",
	"java:S2160" })
public class Event717TimelinessPanel extends VerticalLayout {

	private static final int DOT_SIZE = 10;
	private static final int INTERVAL_LABEL_WIDTH = 120;

	private final Label verdictLabel;
	private final Map<Event717Interval, Label> daysLabels = new EnumMap<>(Event717Interval.class);
	private final Map<Event717Interval, Label> statusLabels = new EnumMap<>(Event717Interval.class);
	private final Label completionLabel;

	public Event717TimelinessPanel(boolean showHeading) {

		setWidth(100, Sizeable.Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		if (showHeading) {
			Label heading = new Label(I18nProperties.getString(Strings.headingEvent717Summary));
			heading.addStyleName(CssStyles.H3);
			addComponent(heading);
		}

		verdictLabel = new Label("", ContentMode.HTML);
		CssStyles.style(verdictLabel, CssStyles.LABEL_BOLD, CssStyles.VSPACE_3);
		addComponent(verdictLabel);

		for (Event717Interval interval : Event717Interval.values()) {
			HorizontalLayout row = new HorizontalLayout();
			row.setWidth(100, Sizeable.Unit.PERCENTAGE);
			row.setMargin(false);
			row.setSpacing(false);
			row.addStyleName(CssStyles.VSPACE_4);

			Label intervalLabel = new Label(createDot(interval) + " " + interval, ContentMode.HTML);
			intervalLabel.setWidth(INTERVAL_LABEL_WIDTH, Sizeable.Unit.PIXELS);
			Label daysLabel = new Label();
			daysLabel.setWidthUndefined();
			daysLabel.addStyleName(CssStyles.LABEL_BOLD);
			Label statusLabel = new Label("", ContentMode.HTML);
			statusLabel.setWidthUndefined();
			statusLabel.addStyleName(CssStyles.HSPACE_LEFT_3);
			// pushes the values next to the interval names instead of to the right edge
			Label spacer = new Label();

			row.addComponents(intervalLabel, daysLabel, statusLabel, spacer);
			row.setExpandRatio(intervalLabel, 0);
			row.setExpandRatio(daysLabel, 0);
			row.setExpandRatio(statusLabel, 0);
			row.setExpandRatio(spacer, 1);
			addComponent(row);

			daysLabels.put(interval, daysLabel);
			statusLabels.put(interval, statusLabel);
		}

		completionLabel = new Label();
		CssStyles.style(completionLabel, CssStyles.LABEL_SECONDARY, CssStyles.VSPACE_TOP_4);
		addComponent(completionLabel);
	}

	public void setValue(Event717TimelinessDto timeliness) {

		int metTargets = 0;
		for (Event717Interval interval : Event717Interval.values()) {
			Event717IntervalResultDto result = timeliness.getResult(interval);

			// e.g. 3 / 7, where 7 is the target of the interval
			daysLabels.get(interval)
				.setValue(
					(result.getDays() != null ? Event717TimelinessCalculator.formatDays(result.getDays()) : "-") + " / " + interval.getTargetDays());

			Label statusLabel = statusLabels.get(interval);
			statusLabel.setValue(createStatusIcon(result.getStatus()));
			statusLabel.setDescription(result.getStatus() != null ? result.getStatus().toString() : null);

			if (Boolean.TRUE.equals(result.getTargetMet())) {
				metTargets++;
			}
		}

		updateVerdict(timeliness, metTargets);

		if (timeliness.isEarlyResponseIncomplete()) {
			completionLabel.setValue(I18nProperties.getString(Strings.infoEvent717EarlyResponseIncomplete));
		} else {
			completionLabel.setValue(
				I18nProperties.getString(Strings.infoEvent717EarlyResponseCompletion) + ": "
					+ DateFormatHelper.formatDate(timeliness.getEarlyResponseCompletionDate()));
		}
	}

	private void updateVerdict(Event717TimelinessDto timeliness, int metTargets) {

		CssStyles.removeStyles(verdictLabel, CssStyles.LABEL_POSITIVE, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_SECONDARY);

		if (Boolean.TRUE.equals(timeliness.getAllTargetsMet())) {
			verdictLabel
				.setValue(VaadinIcons.CHECK_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoEvent717AllTargetsMet));
			verdictLabel.addStyleName(CssStyles.LABEL_POSITIVE);
		} else if (Boolean.FALSE.equals(timeliness.getAllTargetsMet())) {
			verdictLabel.setValue(
				VaadinIcons.CLOSE_CIRCLE.getHtml() + " "
					+ String.format(I18nProperties.getString(Strings.infoEvent717TargetsMet), metTargets, Event717Interval.values().length));
			verdictLabel.addStyleName(CssStyles.LABEL_CRITICAL);
		} else {
			// at least one interval can not be evaluated yet
			verdictLabel.setValue(
				String.format(I18nProperties.getString(Strings.infoEvent717TargetsMet), metTargets, Event717Interval.values().length));
			verdictLabel.addStyleName(CssStyles.LABEL_SECONDARY);
		}
	}

	private static String createDot(Event717Interval interval) {

		return "<span style=\"display:inline-block;width:" + DOT_SIZE + "px;height:" + DOT_SIZE + "px;border-radius:50%;background-color:"
			+ Event717IntervalColors.getColor(interval) + ";\"></span>";
	}

	private static String createStatusIcon(Event717TimelinessStatus status) {

		if (status == null) {
			return "";
		}

		switch (status) {
		case WITHIN_TARGET:
			return colored(VaadinIcons.CHECK.getHtml(), "#43A047");
		case OVER_TARGET:
			return colored(VaadinIcons.CLOSE.getHtml(), "#E7503C");
		case DATA_ERROR:
			return colored(VaadinIcons.WARNING.getHtml(), "#F49234");
		default:
			return colored(VaadinIcons.MINUS.getHtml(), "#999999");
		}
	}

	private static String colored(String icon, String color) {
		return "<span style=\"color:" + color + ";\">" + icon + "</span>";
	}
}
