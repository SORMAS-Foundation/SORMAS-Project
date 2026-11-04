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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.symeda.sormas.api.climate.ClimateHistoryCoverageDto;
import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateFormatHelper;

/**
 * How the Climate data screen names places and values.
 */
public final class ClimateCaptions {

	private ClimateCaptions() {
	}

	/**
	 * @return the place a row belongs to: "Ikeja", "Well A (environment)", "Ikeja + 2 more" when the location is shared, or
	 *         "Not linked (6.60, 3.35)" for a location nothing uses any more
	 */
	public static String place(ClimateObservationIndexDto observation) {

		List<String> names = placeNames(observation);
		if (names.isEmpty()) {
			return observation.getLatitude() != null
				? String.format(I18nProperties.getString(Strings.climatePlaceNotLinked), observation.getLatitude(), observation.getLongitude())
				: I18nProperties.getString(Strings.climatePlaceNotLinkedNoPoint);
		}
		if (names.size() > 1) {
			return String.format(I18nProperties.getString(Strings.climatePlaceShared), names.get(0), names.size() - 1);
		}
		return names.get(0);
	}

	/**
	 * @return every district and environment using the row's location, environments marked as such
	 */
	public static List<String> placeNames(ClimateObservationIndexDto observation) {

		List<String> names = new ArrayList<>(observation.getDistrictNames());
		observation.getEnvironmentNames()
			.forEach(environment -> names.add(String.format(I18nProperties.getString(Strings.climatePlaceEnvironment), environment)));
		return names;
	}

	/**
	 * @return "84 (noon reading)", or empty without a value
	 */
	public static String humidity(ClimateObservationIndexDto observation) {

		if (observation.getRelativeHumidity() == null) {
			return "";
		}
		String value = number(observation.getRelativeHumidity());
		return observation.getHumidityMeasure() != null ? value + " (" + observation.getHumidityMeasure() + ")" : value;
	}

	/**
	 * @return a row's values on one line, "Import: 23.8 / 26.4 / 29.6 °C · 18.2 mm · 84 % (Daily mean)", with "–" for a
	 *         missing value
	 */
	public static String values(ClimateObservationIndexDto observation) {

		return String.format(
			I18nProperties.getString(Strings.climateValuesLine),
			observation.getSource(),
			orDash(observation.getTemperatureMin()),
			orDash(observation.getTemperatureMean()),
			orDash(observation.getTemperatureMax()),
			orDash(observation.getRainfall()),
			observation.getRelativeHumidity() != null ? humidity(observation) : "–");
	}

	private static String orDash(Double value) {
		return value != null ? number(value) : "–";
	}

	/**
	 * @return "1 year 2 months", "3 years", "4 months"
	 */
	public static String period(Period period) {

		List<String> parts = new ArrayList<>();
		if (period.getYears() > 0) {
			parts.add(
				period.getYears() == 1
					? I18nProperties.getString(Strings.climateOneYear)
					: String.format(I18nProperties.getString(Strings.climateYears), period.getYears()));
		}
		if (period.getMonths() > 0 || parts.isEmpty()) {
			parts.add(
				period.getMonths() == 1
					? I18nProperties.getString(Strings.climateOneMonth)
					: String.format(I18nProperties.getString(Strings.climateMonths), period.getMonths()));
		}
		return String.join(" ", parts);
	}

	/**
	 * @return the years held and their sources: "2021–2023 Import · 2024 Import 300 days, OpenWeather 66 days · 2025–2026
	 *         OpenWeather"
	 */
	public static String sources(ClimateHistoryCoverageDto coverage) {

		List<String> spans = new ArrayList<>();
		for (ClimateHistoryCoverageDto.SourceSpan span : coverage.getSourceSpans()) {
			String years = span.getFromYear() == span.getToYear() ? String.valueOf(span.getFromYear()) : span.getFromYear() + "–" + span.getToYear();
			if (span.isSingleSource()) {
				spans.add(years + " " + span.getDaysBySource().keySet().iterator().next());
			} else {
				List<String> sources = new ArrayList<>();
				span.getDaysBySource()
					.forEach((source, days) -> sources.add(String.format(I18nProperties.getString(Strings.climateSourceDays), source, days)));
				spans.add(years + " " + String.join(", ", sources));
			}
		}
		return String.join(" · ", spans);
	}

	/**
	 * @return the value without trailing zeros (26.4, 0), or empty for no value; a missing value is never shown as 0
	 */
	public static String number(Double value) {

		if (value == null) {
			return "";
		}
		return value == Math.rint(value) ? String.valueOf(value.longValue()) : String.valueOf(Math.round(value * 100) / 100.0);
	}

	/**
	 * @return the value with one decimal (26.4, 0.0), or empty for no value: for chart hovers and cards, where a fixed
	 *         number of decimals reads better
	 */
	public static String oneDecimal(Double value) {
		return value != null ? String.format(Locale.ROOT, "%.1f", value) : "";
	}

	/**
	 * @return a GPS coordinate with up to 5 decimals (about 1 m), or empty
	 */
	public static String coordinate(Double value) {
		if (value == null) {
			return "";
		}
		return BigDecimal.valueOf(value).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
	}

	/**
	 * @return the day in the user's date format
	 */
	public static String date(LocalDate day) {
		return day != null ? DateFormatHelper.formatDate(toDate(day)) : "";
	}

	/**
	 * @return the start of the day in the server's time zone
	 */
	public static Date toDate(LocalDate day) {
		return Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * @return the calendar day of the date in the server's time zone
	 */
	public static LocalDate toLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * @return "A", "A and B", "A, B and C"
	 */
	public static String joinAnd(List<String> parts) {
		if (parts.size() <= 1) {
			return parts.isEmpty() ? "" : parts.get(0);
		}
		String head = String.join(", ", parts.subList(0, parts.size() - 1));
		return String.format(I18nProperties.getString(Strings.climateAnd), head, parts.get(parts.size() - 1));
	}
}
