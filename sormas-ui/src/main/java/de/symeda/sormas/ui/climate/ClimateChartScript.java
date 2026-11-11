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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;

import de.symeda.sormas.api.utils.DateFormatHelper;

/**
 * Small helpers for writing Highcharts options as JavaScript: day positions on a datetime axis, numbers, escaped text.
 * Charts on the climate screens share these, so their axes line up.
 */
public final class ClimateChartScript {

	/** Left and right margins shared by the stacked charts, so their time axes line up. */
	public static final int MARGIN_LEFT = 64;
	public static final int MARGIN_RIGHT = 24;

	private static final long HALF_DAY = 12L * 60 * 60 * 1000;

	private ClimateChartScript() {
	}

	/**
	 * @return the day's position on a Highcharts datetime axis: midnight UTC of the calendar day, so the axis shows the day
	 *         itself in every time zone
	 */
	public static long x(LocalDate day) {
		return day.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
	}

	/**
	 * @return the axis start: half a day before the first day, so its column is whole
	 */
	public static long axisMin(LocalDate from) {
		return x(from) - HALF_DAY;
	}

	public static long axisMax(LocalDate to) {
		return x(to) + HALF_DAY;
	}

	/**
	 * @return the number for JavaScript, or {@code null} for a missing value (a gap, never 0)
	 */
	public static String number(Double value) {
		return value != null ? String.format(Locale.ROOT, "%.2f", value) : "null";
	}

	/**
	 * @return the text as a JavaScript string literal in single quotes
	 */
	public static String string(String text) {
		if (text == null) {
			return "null";
		}
		StringBuilder literal = new StringBuilder("'");
		for (char c : text.toCharArray()) {
			switch (c) {
			case '\\':
				literal.append("\\\\");
				break;
			case '\'':
				literal.append("\\'");
				break;
			case '\n':
				literal.append("\\n");
				break;
			case '\r':
				break;
			case '<':
				// keeps "</script>" and the like harmless; the text is HTML-escaped already where shown as HTML
				literal.append("\\u003c");
				break;
			default:
				literal.append(c);
			}
		}
		return literal.append('\'').toString();
	}

	/**
	 * @return the text with HTML special characters escaped, for user content inside a hover written as HTML
	 */
	public static String html(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
	}

	/**
	 * @return the Highcharts label format of a day without the year, in the user's day/month order
	 */
	public static String dayMonthFormat() {
		String pattern = DateFormatHelper.getDateFormatPattern();
		return pattern.indexOf('M') < pattern.indexOf('d') ? "%m/%d" : "%d/%m";
	}
}
