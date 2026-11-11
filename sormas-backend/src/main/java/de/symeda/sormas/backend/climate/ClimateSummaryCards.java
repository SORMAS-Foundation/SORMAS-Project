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

package de.symeda.sormas.backend.climate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.symeda.sormas.api.climate.ClimateComparison;
import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.ClimateRangePosition;
import de.symeda.sormas.api.climate.ClimateSummaryCardDto;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.HumidityMeasure;

/**
 * The summary cards of the environment Climate data tab (decision 71): the 7 days ending on the latest day with data,
 * against the sums (rainfall) or means of the same days' daily normals; before normals exist, against the same days a
 * year earlier; otherwise values only. Each needs at least 6 of the 7 days.
 */
public final class ClimateSummaryCards {

	private static final int CARD_DAYS = 7;

	private ClimateSummaryCards() {
	}

	/**
	 * @param lastDay
	 *            the latest day with data, or null when the place holds none
	 * @param days
	 *            the place's rows of the 7 days and of the same days a year earlier
	 * @param normals
	 *            the daily normals, or null when there are none
	 * @return rainfall, temperature and humidity
	 */
	public static List<ClimateSummaryCardDto> build(LocalDate lastDay, Map<LocalDate, ClimateDay> days, ClimateDailyNormals normals) {

		List<ClimateSummaryCardDto> cards = new ArrayList<>();
		for (ClimateVariable variable : ClimateVariable.values()) {
			cards.add(lastDay != null ? card(variable, lastDay, days, normals) : new ClimateSummaryCardDto(variable, null, null));
		}
		return cards;
	}

	private static ClimateSummaryCardDto card(ClimateVariable variable, LocalDate lastDay, Map<LocalDate, ClimateDay> days, ClimateDailyNormals normals) {

		LocalDate firstDay = lastDay.minusDays(CARD_DAYS - 1);
		ClimateSummaryCardDto card = new ClimateSummaryCardDto(variable, firstDay, lastDay);

		HumidityMeasure measure = variable == ClimateVariable.HUMIDITY ? humidityMeasure(days, firstDay) : null;
		Function<ClimateDay, Double> value = valueOf(variable, measure);
		List<LocalDate> dates = new ArrayList<>();
		List<Double> values = new ArrayList<>();
		for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
			Double dayValue = valueOn(days, date, value);
			if (dayValue != null) {
				dates.add(date);
				values.add(dayValue);
			}
		}
		card.setDays(values.size());
		card.setHumidityMeasure(measure);
		if (values.size() < ClimateStatistics.MIN_DAYS_PER_WEEK) {
			return card;
		}
		double current = aggregate(variable, values);
		card.setValue(current);

		List<ClimateRangeDto> ranges = new ArrayList<>();
		if (normals != null) {
			for (LocalDate date : dates) {
				ClimateRangeDto range = normal(normals, variable, date, measure);
				if (range != null) {
					ranges.add(range);
				}
			}
		}
		if (!ranges.isEmpty() && ranges.size() == dates.size()) {
			ClimateRangeDto normal = new ClimateRangeDto(
				aggregate(variable, map(ranges, ClimateRangeDto::getP10)),
				aggregate(variable, map(ranges, ClimateRangeDto::getP50)),
				aggregate(variable, map(ranges, ClimateRangeDto::getP90)));
			card.setComparison(ClimateComparison.NORMAL);
			card.setNormal(normal);
			card.setPosition(
				current > normal.getP90() ? ClimateRangePosition.ABOVE : current < normal.getP10() ? ClimateRangePosition.BELOW : ClimateRangePosition.WITHIN);
			compare(card, variable, current, normal.getP50());
			return card;
		}

		List<Double> thisYear = new ArrayList<>();
		List<Double> lastYear = new ArrayList<>();
		for (int i = 0; i < dates.size(); i++) {
			Double earlier = valueOn(days, dates.get(i).minusYears(1), value);
			if (earlier != null) {
				thisYear.add(values.get(i));
				lastYear.add(earlier);
			}
		}
		if (lastYear.size() >= ClimateStatistics.MIN_DAYS_PER_WEEK) {
			double earlier = aggregate(variable, lastYear);
			card.setComparison(ClimateComparison.LAST_YEAR);
			card.setLastYear(earlier);
			compare(card, variable, aggregate(variable, thisYear), earlier);
		} else {
			card.setComparison(ClimateComparison.VALUES_ONLY);
		}
		return card;
	}

	private static void compare(ClimateSummaryCardDto card, ClimateVariable variable, double value, double reference) {
		if (variable == ClimateVariable.RAINFALL) {
			card.setNormallyDry(card.getComparison() == ClimateComparison.NORMAL && reference == 0);
			card.setPercentDifference(reference != 0 ? (value - reference) / reference * 100 : null);
		} else {
			card.setDifference(value - reference);
		}
	}

	/**
	 * @return daily means when at least 6 of the 7 days have them, else noon readings when at least 6 do, else null
	 */
	private static HumidityMeasure humidityMeasure(Map<LocalDate, ClimateDay> days, LocalDate firstDay) {
		for (HumidityMeasure measure : new HumidityMeasure[] {
			HumidityMeasure.DAILY_MEAN,
			HumidityMeasure.NOON_READING }) {
			int count = 0;
			for (int i = 0; i < CARD_DAYS; i++) {
				if (valueOn(days, firstDay.plusDays(i), valueOf(ClimateVariable.HUMIDITY, measure)) != null) {
					count++;
				}
			}
			if (count >= ClimateStatistics.MIN_DAYS_PER_WEEK) {
				return measure;
			}
		}
		return null;
	}

	private static Function<ClimateDay, Double> valueOf(ClimateVariable variable, HumidityMeasure measure) {
		switch (variable) {
		case RAINFALL:
			return ClimateDay::getRainfall;
		case TEMPERATURE:
			return ClimateDay::getTemperatureMean;
		case HUMIDITY:
			return day -> measure != null && day.getHumidityMeasure() == measure ? day.getRelativeHumidity() : null;
		default:
			throw new IllegalArgumentException(variable.name());
		}
	}

	private static ClimateRangeDto normal(ClimateDailyNormals normals, ClimateVariable variable, LocalDate date, HumidityMeasure measure) {
		switch (variable) {
		case RAINFALL:
			return normals.getRainfall(date);
		case TEMPERATURE:
			return normals.getTemperature(date);
		case HUMIDITY:
			return normals.getHumidity(date, measure);
		default:
			throw new IllegalArgumentException(variable.name());
		}
	}

	private static Double valueOn(Map<LocalDate, ClimateDay> days, LocalDate date, Function<ClimateDay, Double> value) {
		ClimateDay day = days.get(date);
		return day != null ? value.apply(day) : null;
	}

	/**
	 * @return the total for rainfall, the mean otherwise
	 */
	private static double aggregate(ClimateVariable variable, List<Double> values) {
		return variable == ClimateVariable.RAINFALL ? ClimateStatistics.sum(values) : ClimateStatistics.mean(values);
	}

	private static List<Double> map(List<ClimateRangeDto> ranges, Function<ClimateRangeDto, Double> percentile) {
		List<Double> values = new ArrayList<>();
		ranges.forEach(range -> values.add(percentile.apply(range)));
		return values;
	}
}
