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

import de.symeda.sormas.ui.highcharts.HighChart;

/**
 * A Highcharts chart whose points can call back to the server. The chart code calls
 * {@code connector.onPointClick(seriesId, key)}, for example from {@code plotOptions.series.point.events.click}; the
 * listener gets the series id and the point's key (a sample, event or week). Uses {@link HighChart}'s connector, which
 * Vaadin finds through the superclass.
 */
public class ClimateChart extends HighChart {

	private static final long serialVersionUID = -3518096046624390214L;

	/** The JavaScript call that reports a click on a point, with the series id and the point's key. */
	public static final String CLICK_CALL = "connector.onPointClick";

	@FunctionalInterface
	public interface PointClickListener {

		void pointClicked(String seriesId, String key);
	}

	public ClimateChart(PointClickListener listener) {
		addFunction("onPointClick", arguments -> {
			if (arguments.length() >= 2) {
				listener.pointClicked(arguments.getString(0), arguments.getString(1));
			}
		});
	}

	/**
	 * @return the JavaScript click handler for {@code point.events.click}: it reports the clicked point's series id and its
	 *         {@code key} option
	 */
	public static String clickHandler() {
		return "function () { if (this.options.key) { " + CLICK_CALL + "(this.series.options.id || '', String(this.options.key)); } }";
	}
}
