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

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;

/**
 * Colors of the three 7-1-7 intervals, as used by the 7-1-7 assessment tool.
 */
public final class Event717IntervalColors {

	private static final String COLOR_DETECTION = "#E7503C";
	private static final String COLOR_NOTIFICATION = "#F49234";
	private static final String COLOR_RESPONSE = "#43A047";

	private Event717IntervalColors() {
		// Hide Utility Class Constructor
	}

	public static String getColor(Event717Interval interval) {

		switch (interval) {
		case DETECTION:
			return COLOR_DETECTION;
		case NOTIFICATION:
			return COLOR_NOTIFICATION;
		case RESPONSE:
			return COLOR_RESPONSE;
		default:
			throw new IllegalArgumentException(interval.name());
		}
	}

	/**
	 * @return A label showing the caption of the interval on its color.
	 */
	public static Label createIntervalLabel(Event717Interval interval) {

		return new Label(
			"<div style=\"background-color:" + getColor(interval)
				+ ";color:#ffffff;font-weight:bold;height:100%;padding:8px;display:flex;align-items:center;\">" + interval + "</div>",
			ContentMode.HTML);
	}
}
