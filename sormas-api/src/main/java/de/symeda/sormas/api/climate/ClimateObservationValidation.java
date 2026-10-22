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

package de.symeda.sormas.api.climate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;

/**
 * The checks every incoming climate value passes, whether it comes by CSV import, push or manual entry. The limits reject
 * physically impossible values only, not unusual ones: an import can only accept or reject a row, so a tight limit would
 * throw away real extremes.
 */
public final class ClimateObservationValidation {

	public static final double TEMPERATURE_MIN_LIMIT = -60;
	public static final double TEMPERATURE_MAX_LIMIT = 60;
	public static final double RAINFALL_MAX_LIMIT = 1000;
	public static final double HUMIDITY_MAX_LIMIT = 100;

	/** Agencies round minimum, mean and maximum separately, so their order may be off by this much. */
	public static final double TEMPERATURE_ORDER_TOLERANCE = 0.1;

	private ClimateObservationValidation() {
	}

	/**
	 * @return the day, or null when the text is not a date written {@code yyyy-mm-dd}
	 */
	public static LocalDate parseDate(String date) {
		if (StringUtils.isBlank(date)) {
			return null;
		}
		try {
			return LocalDate.parse(date.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	/**
	 * @return the first problem with the entry, or null when it is valid
	 * @param today
	 *            values are accepted up to the day before: today's totals are not complete yet
	 */
	public static String validate(ClimateObservationEntryDto entry, LocalDate today) {

		long keys = Stream.of(entry.getDistrictUuid(), entry.getDistrictExternalId(), entry.getEnvironmentUuid(), entry.getClimateLocationUuid())
			.filter(StringUtils::isNotBlank)
			.count();
		if (keys != 1) {
			return I18nProperties.getValidationError(Validations.climateEntryLocationKey);
		}

		LocalDate day = parseDate(entry.getDate());
		if (day == null) {
			return I18nProperties.getValidationError(Validations.climateEntryDate);
		}
		if (!day.isBefore(today)) {
			return I18nProperties.getValidationError(Validations.climateEntryDateNotEnded);
		}

		Double min = entry.getTemperatureMin();
		Double mean = entry.getTemperatureMean();
		Double max = entry.getTemperatureMax();
		if (min == null && mean == null && max == null && entry.getRainfall() == null && entry.getRelativeHumidity() == null) {
			return I18nProperties.getValidationError(Validations.climateEntryNoValue);
		}

		if (!inRange(min, TEMPERATURE_MIN_LIMIT, TEMPERATURE_MAX_LIMIT)
			|| !inRange(mean, TEMPERATURE_MIN_LIMIT, TEMPERATURE_MAX_LIMIT)
			|| !inRange(max, TEMPERATURE_MIN_LIMIT, TEMPERATURE_MAX_LIMIT)) {
			return I18nProperties.getValidationError(Validations.climateEntryTemperatureRange, TEMPERATURE_MIN_LIMIT, TEMPERATURE_MAX_LIMIT);
		}
		if (outOfOrder(min, mean) || outOfOrder(mean, max) || outOfOrder(min, max)) {
			return I18nProperties.getValidationError(Validations.climateEntryTemperatureOrder);
		}
		if (!inRange(entry.getRainfall(), 0, RAINFALL_MAX_LIMIT)) {
			return I18nProperties.getValidationError(Validations.climateEntryRainfallRange, RAINFALL_MAX_LIMIT);
		}
		if (!inRange(entry.getRelativeHumidity(), 0, HUMIDITY_MAX_LIMIT)) {
			return I18nProperties.getValidationError(Validations.climateEntryHumidityRange);
		}
		if (entry.getRelativeHumidity() != null && entry.getHumidityMeasure() == null) {
			return I18nProperties.getValidationError(Validations.climateEntryHumidityMeasure);
		}
		return null;
	}

	/**
	 * Fills an empty mean temperature with (min + max) ÷ 2 when both are given, the usual practice in climate statistics.
	 * Many met agencies report only the minimum and maximum, and the weekly temperature is built from daily means.
	 */
	public static void fillMeanTemperature(ClimateObservationEntryDto entry) {
		if (entry.getTemperatureMean() == null && entry.getTemperatureMin() != null && entry.getTemperatureMax() != null) {
			entry.setTemperatureMean(Math.round((entry.getTemperatureMin() + entry.getTemperatureMax()) * 50) / 100d);
		}
	}

	private static boolean inRange(Double value, double lowest, double highest) {
		return value == null || (!value.isNaN() && value >= lowest && value <= highest);
	}

	private static boolean outOfOrder(Double lower, Double higher) {
		return lower != null && higher != null && lower > higher + TEMPERATURE_ORDER_TOLERANCE;
	}
}
