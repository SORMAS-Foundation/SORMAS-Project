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

import com.vaadin.server.Sizeable;
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
 * Shows the timeliness of the three 7-1-7 intervals and whether their targets have been met.
 */
@SuppressWarnings({
	"serial",
	"java:S2160" })
public class Event717TimelinessPanel extends VerticalLayout {

	private final Map<Event717Interval, Label> daysLabels = new EnumMap<>(Event717Interval.class);
	private final Map<Event717Interval, Label> statusLabels = new EnumMap<>(Event717Interval.class);
	private final Label completionLabel;
	private final Label overallLabel;

	public Event717TimelinessPanel(boolean showHeading) {

		setWidth(100, Sizeable.Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		if (showHeading) {
			Label heading = new Label(I18nProperties.getString(Strings.headingEvent717Timeliness));
			heading.addStyleName(CssStyles.H3);
			addComponent(heading);
		}

		for (Event717Interval interval : Event717Interval.values()) {
			HorizontalLayout row = new HorizontalLayout();
			row.setWidth(100, Sizeable.Unit.PERCENTAGE);
			row.setMargin(false);
			row.addStyleName(CssStyles.VSPACE_4);

			Label intervalLabel = new Label(interval.toString());
			CssStyles.style(intervalLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			Label daysLabel = new Label();
			Label statusLabel = new Label();
			CssStyles.style(statusLabel, CssStyles.LABEL_BOLD, CssStyles.ALIGN_RIGHT);

			row.addComponents(intervalLabel, daysLabel, statusLabel);
			row.setExpandRatio(intervalLabel, 1);
			row.setExpandRatio(daysLabel, 1);
			row.setExpandRatio(statusLabel, 1);
			addComponent(row);

			daysLabels.put(interval, daysLabel);
			statusLabels.put(interval, statusLabel);
		}

		completionLabel = new Label();
		completionLabel.addStyleName(CssStyles.VSPACE_TOP_4);
		addComponent(completionLabel);

		overallLabel = new Label();
		CssStyles.style(overallLabel, CssStyles.LABEL_BOLD, CssStyles.VSPACE_TOP_4);
		addComponent(overallLabel);
	}

	public void setValue(Event717TimelinessDto timeliness) {

		for (Event717Interval interval : Event717Interval.values()) {
			Event717IntervalResultDto result = timeliness.getResult(interval);
			daysLabels.get(interval)
				.setValue(
					String.format(
						I18nProperties.getString(Strings.infoEvent717TimelinessDays),
						result.getDays() != null ? Event717TimelinessCalculator.formatDays(result.getDays()) : "-",
						interval.getTargetDays()));
			styleStatusLabel(statusLabels.get(interval), result.getStatus());
		}

		completionLabel.setValue(
			I18nProperties.getString(Strings.infoEvent717EarlyResponseCompletion) + ": "
				+ (timeliness.isEarlyResponseIncomplete()
					? Event717TimelinessStatus.INCOMPLETE.toString()
					: DateFormatHelper.formatDate(timeliness.getEarlyResponseCompletionDate())));

		CssStyles.removeStyles(overallLabel, CssStyles.LABEL_POSITIVE, CssStyles.LABEL_CRITICAL);
		if (timeliness.getAllTargetsMet() == null) {
			overallLabel.setValue("");
		} else if (timeliness.getAllTargetsMet()) {
			overallLabel.setValue(I18nProperties.getString(Strings.infoEvent717AllTargetsMet));
			overallLabel.addStyleName(CssStyles.LABEL_POSITIVE);
		} else {
			overallLabel.setValue(I18nProperties.getString(Strings.infoEvent717NotAllTargetsMet));
			overallLabel.addStyleName(CssStyles.LABEL_CRITICAL);
		}
	}

	/**
	 * Sets the caption of the status and colors the label: green for met, red for not met, orange for data errors.
	 */
	public static void styleStatusLabel(Label label, Event717TimelinessStatus status) {

		CssStyles.removeStyles(label, CssStyles.LABEL_POSITIVE, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_WARNING, CssStyles.LABEL_SECONDARY);
		label.setValue(status != null ? status.toString() : "");
		if (status == null) {
			return;
		}

		switch (status) {
		case MET:
			label.addStyleName(CssStyles.LABEL_POSITIVE);
			break;
		case NOT_MET:
			label.addStyleName(CssStyles.LABEL_CRITICAL);
			break;
		case DATA_ERROR:
			label.addStyleName(CssStyles.LABEL_WARNING);
			break;
		default:
			label.addStyleName(CssStyles.LABEL_SECONDARY);
		}
	}
}
