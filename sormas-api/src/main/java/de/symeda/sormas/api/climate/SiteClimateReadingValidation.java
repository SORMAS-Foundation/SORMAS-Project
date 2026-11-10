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

import static de.symeda.sormas.api.climate.ClimateObservationValidation.HUMIDITY_MAX_LIMIT;
import static de.symeda.sormas.api.climate.ClimateObservationValidation.RAINFALL_MAX_LIMIT;
import static de.symeda.sormas.api.climate.ClimateObservationValidation.TEMPERATURE_MAX_LIMIT;
import static de.symeda.sormas.api.climate.ClimateObservationValidation.TEMPERATURE_MIN_LIMIT;

import java.util.Date;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;

/**
 * The plausibility checks of a site reading: the same hard limits as manual entry of daily values.
 */
public final class SiteClimateReadingValidation {

	public static final int RAINFALL_PERIOD_MIN_HOURS = 1;
	public static final int RAINFALL_PERIOD_MAX_HOURS = 168;
	public static final int NOTES_MAX_LENGTH = 1024;

	private SiteClimateReadingValidation() {
	}

	/**
	 * @return the first problem with the reading, or null when it is valid
	 */
	public static String validate(SiteClimateReadingDto reading, Date now) {

		if (reading.getEnvironment() == null) {
			return I18nProperties.getValidationError(Validations.climateReadingEnvironment);
		}
		if (reading.getReadingDateTime() == null) {
			return I18nProperties.getValidationError(Validations.climateReadingDateTime);
		}
		if (reading.getReadingDateTime().after(now)) {
			return I18nProperties.getValidationError(Validations.climateReadingFuture);
		}
		if (reading.getAirTemperature() == null && reading.getRainfall() == null && reading.getRelativeHumidity() == null) {
			return I18nProperties.getValidationError(Validations.climateEntryNoValue);
		}
		if (!inRange(reading.getAirTemperature(), TEMPERATURE_MIN_LIMIT, TEMPERATURE_MAX_LIMIT)) {
			return I18nProperties.getValidationError(Validations.climateEntryTemperatureRange, TEMPERATURE_MIN_LIMIT, TEMPERATURE_MAX_LIMIT);
		}
		if (!inRange(reading.getRainfall(), 0, RAINFALL_MAX_LIMIT)) {
			return I18nProperties.getValidationError(Validations.climateReadingRainfallRange, RAINFALL_MAX_LIMIT);
		}
		Integer hours = reading.getRainfallPeriodHours();
		if (reading.getRainfall() != null && hours == null) {
			return I18nProperties.getValidationError(Validations.climateReadingRainfallPeriod, RAINFALL_PERIOD_MIN_HOURS, RAINFALL_PERIOD_MAX_HOURS);
		}
		if (hours != null && (hours < RAINFALL_PERIOD_MIN_HOURS || hours > RAINFALL_PERIOD_MAX_HOURS)) {
			return I18nProperties.getValidationError(Validations.climateReadingRainfallPeriod, RAINFALL_PERIOD_MIN_HOURS, RAINFALL_PERIOD_MAX_HOURS);
		}
		if (!inRange(reading.getRelativeHumidity(), 0, HUMIDITY_MAX_LIMIT)) {
			return I18nProperties.getValidationError(Validations.climateEntryHumidityRange);
		}
		if (reading.getNotes() != null && reading.getNotes().length() > NOTES_MAX_LENGTH) {
			return I18nProperties.getValidationError(Validations.climateReadingNotesTooLong, NOTES_MAX_LENGTH);
		}
		return null;
	}

	private static boolean inRange(Double value, double lowest, double highest) {
		return value == null || (!value.isNaN() && value >= lowest && value <= highest);
	}
}
