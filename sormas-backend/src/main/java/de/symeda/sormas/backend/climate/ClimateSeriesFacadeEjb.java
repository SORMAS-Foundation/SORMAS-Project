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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.ClimateSeriesCriteria;
import de.symeda.sormas.api.climate.ClimateSeriesFacade;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.ClimateWeekDto;
import de.symeda.sormas.api.climate.ClimateWeekValueDto;
import de.symeda.sormas.api.climate.ClimateWeeklySeriesDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ClimateSeriesFacade")
public class ClimateSeriesFacadeEjb implements ClimateSeriesFacade {

	@EJB
	private ClimateSettings climateSettings;
	@EJB
	private ClimateObservationService climateObservationService;
	@EJB
	private ClimateNormalsCache normalsCache;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_CLIMATE_VIEW,
		UserRight._CLIMATE_VIEW })
	public ClimateWeeklySeriesDto getWeeklySeries(ClimateSeriesCriteria criteria) {

		if (criteria.getRegion() == null
			|| criteria.getFromWeek() == null
			|| criteria.getToWeek() == null
			|| ClimateStatistics.firstDay(criteria.getFromWeek()).isAfter(ClimateStatistics.firstDay(criteria.getToWeek()))) {
			throw new IllegalArgumentException("A weekly climate series needs a region and a period");
		}

		Region region = regionService.getByUuid(criteria.getRegion().getUuid());
		List<District> districts;
		if (criteria.getDistrict() != null) {
			districts = Collections.singletonList(districtService.getByUuid(criteria.getDistrict().getUuid()));
		} else {
			districts = new ArrayList<>(districtService.getAllActiveByRegion(region));
			districts.sort(Comparator.comparing(District::getName, String.CASE_INSENSITIVE_ORDER));
		}

		WeeklyRun run = new WeeklyRun(region, districts, criteria.getFromWeek(), criteria.getToWeek());
		if (criteria.getDistrict() != null) {
			return run.districtSeries(districts.get(0));
		}
		ClimateWeeklySeriesDto regionSeries = run.regionSeries();
		if (criteria.isIncludeDistricts()) {
			districts.forEach(district -> regionSeries.getDistrictSeries().add(run.districtSeries(district)));
		}
		return regionSeries;
	}

	private static Long locationId(District district) {
		return district.getClimateLocation() != null ? district.getClimateLocation().getId() : null;
	}

	private static boolean hasPoint(District district) {
		return district.getClimateLocation() != null && district.getClimateLocation().getLatitude() != null;
	}

	/**
	 * One request: the region's districts, the weeks shown (and a year before them, for last year's values) and the
	 * reference years, loaded once and shared by the region's series and its districts' series.
	 */
	private class WeeklyRun {

		private final Region region;
		private final List<District> districts;
		private final List<EpiWeek> shownWeeks;
		private final List<Integer> referenceYears;
		private final int minYears;
		private final double rainfallMinMm;
		private final LocalDate referenceFrom;
		private final LocalDate referenceTo;
		private final LocalDate shownFrom;
		private final LocalDate shownTo;
		private final Map<Long, Map<LocalDate, ClimateDay>> shownDays;
		private final Map<Long, Map<EpiWeek, ClimateWeekFigures>> shownFigures = new HashMap<>();
		private Map<Long, Map<EpiWeek, ClimateWeekFigures>> referenceFigures;
		private final Map<Long, ClimateWeeklyNormals> locationNormals = new HashMap<>();

		WeeklyRun(Region region, List<District> districts, EpiWeek fromWeek, EpiWeek toWeek) {

			this.region = region;
			this.districts = districts;
			shownWeeks = ClimateStatistics.weeksBetween(fromWeek, toWeek);

			int currentYear = ClimateStatistics.epiWeekOf(LocalDate.now()).getYear();
			int historyYears = climateSettings.getHistoryYears();
			referenceYears = IntStream.range(currentYear - historyYears, currentYear).boxed().collect(Collectors.toList());
			minYears = climateSettings.getMinHistoryYears();
			rainfallMinMm = climateSettings.getRainfallMinMm();
			referenceFrom = ClimateStatistics.firstDayOfEpiYear(referenceYears.get(0));
			referenceTo = ClimateStatistics.firstDayOfEpiYear(currentYear).minusDays(1);

			shownFrom = ClimateStatistics.firstDay(fromWeek);
			shownTo = ClimateStatistics.firstDay(toWeek).plusDays(6);
			LocalDate loadFrom = shownFrom.minusWeeks(53);
			shownDays = climateObservationService.getDays(locationIds(), loadFrom, shownTo);
			List<EpiWeek> loadedWeeks = ClimateStatistics.weeksBetween(ClimateStatistics.epiWeekOf(loadFrom), toWeek);
			shownDays.forEach((location, days) -> shownFigures.put(location, rollUp(days, loadedWeeks)));
		}

		ClimateWeeklySeriesDto districtSeries(District district) {

			Long location = locationId(district);
			Map<EpiWeek, ClimateWeekFigures> figures = location != null ? shownFigures.getOrDefault(location, Collections.emptyMap()) : Collections.emptyMap();
			ClimateWeeklyNormals normals = location != null ? districtNormals(location) : ClimateWeeklyNormals.compute(Collections.emptyMap(), referenceYears, minYears);
			ClimateWeeklySeriesDto series = new ClimateWeeklySeriesDto(
				RegionFacadeEjb.toReferenceDto(district.getRegion()),
				DistrictFacadeEjb.toReferenceDto(district),
				hasPoint(district),
				location != null ? climateObservationService.getEarliestDay(location) : null,
				minYears,
				referenceYears.get(0),
				referenceYears.get(referenceYears.size() - 1),
				weeks(figures, normals, false));
			Map<LocalDate, ClimateDay> days = location != null ? shownDays.getOrDefault(location, Collections.emptyMap()) : Collections.emptyMap();
			for (ClimateVariable variable : ClimateVariable.values()) {
				series.setCompleteness(variable, completeness(days, variable));
			}
			return series;
		}

		ClimateWeeklySeriesDto regionSeries() {

			Map<EpiWeek, ClimateWeekFigures> figures = combine(shownFigures, ClimateStatistics.weeksBetween(ClimateStatistics.epiWeekOf(shownFrom.minusWeeks(53)), shownWeeks.get(shownWeeks.size() - 1)));
			String key = "W|R|" + districts.stream().map(district -> String.valueOf(locationId(district))).collect(Collectors.joining(",")) + "|"
				+ referenceYears + "|" + minYears;
			ClimateWeeklyNormals normals = normalsCache.get(
				key,
				climateObservationService.getFingerprint(locationIds(), referenceFrom, referenceTo),
				() -> ClimateWeeklyNormals.compute(combine(referenceFigures(), referenceWeeks()), referenceYears, minYears));

			LocalDate historyFrom = null;
			boolean allHaveHistory = !districts.isEmpty();
			for (District district : districts) {
				LocalDate earliest = locationId(district) != null ? climateObservationService.getEarliestDay(locationId(district)) : null;
				if (earliest == null) {
					allHaveHistory = false;
				} else if (historyFrom == null || earliest.isAfter(historyFrom)) {
					historyFrom = earliest;
				}
			}

			ClimateWeeklySeriesDto series = new ClimateWeeklySeriesDto(
				RegionFacadeEjb.toReferenceDto(region),
				null,
				districts.stream().anyMatch(ClimateSeriesFacadeEjb::hasPoint),
				allHaveHistory ? historyFrom : null,
				minYears,
				referenceYears.get(0),
				referenceYears.get(referenceYears.size() - 1),
				weeks(figures, normals, true));
			for (ClimateVariable variable : ClimateVariable.values()) {
				List<Double> shares = districts.stream().map(district -> {
					Long location = locationId(district);
					return completeness(location != null ? shownDays.getOrDefault(location, Collections.emptyMap()) : Collections.emptyMap(), variable);
				}).filter(Objects::nonNull).collect(Collectors.toList());
				series.setCompleteness(variable, shares.isEmpty() || shares.size() < districts.size() ? null : ClimateStatistics.mean(shares));
			}
			return series;
		}

		private ClimateWeeklyNormals districtNormals(Long location) {
			return locationNormals.computeIfAbsent(
				location,
				id -> normalsCache.get(
					"W|L" + id + "|" + referenceYears + "|" + minYears,
					climateObservationService.getFingerprint(Collections.singleton(id), referenceFrom, referenceTo),
					() -> ClimateWeeklyNormals.compute(referenceFigures().getOrDefault(id, Collections.emptyMap()), referenceYears, minYears)));
		}

		private Map<Long, Map<EpiWeek, ClimateWeekFigures>> referenceFigures() {
			if (referenceFigures == null) {
				referenceFigures = new HashMap<>();
				List<EpiWeek> weeks = referenceWeeks();
				climateObservationService.getDays(locationIds(), referenceFrom, referenceTo)
					.forEach((location, days) -> referenceFigures.put(location, rollUp(days, weeks)));
			}
			return referenceFigures;
		}

		private List<EpiWeek> referenceWeeks() {
			int lastYear = referenceYears.get(referenceYears.size() - 1);
			return ClimateStatistics
				.weeksBetween(new EpiWeek(referenceYears.get(0), 1), new EpiWeek(lastYear, ClimateStatistics.weeksInEpiYear(lastYear)));
		}

		private Set<Long> locationIds() {
			return districts.stream().map(ClimateSeriesFacadeEjb::locationId).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
		}

		private Map<EpiWeek, ClimateWeekFigures> combine(Map<Long, Map<EpiWeek, ClimateWeekFigures>> figuresByLocation, List<EpiWeek> weeks) {

			Map<EpiWeek, ClimateWeekFigures> combined = new HashMap<>();
			for (EpiWeek week : weeks) {
				List<ClimateWeekFigures> districtWeeks = new ArrayList<>();
				for (District district : districts) {
					Map<EpiWeek, ClimateWeekFigures> figures = figuresByLocation.get(locationId(district));
					if (figures != null && figures.containsKey(week)) {
						districtWeeks.add(figures.get(week));
					}
				}
				combined.put(week, ClimateStatistics.combine(week, districtWeeks, districts.size()));
			}
			return combined;
		}

		/**
		 * @return the share of days from the first day shown to the last, but at most yesterday, that have a value of the
		 *         variable; null when no day shown has passed yet
		 */
		private Double completeness(Map<LocalDate, ClimateDay> days, ClimateVariable variable) {

			LocalDate last = shownTo.isBefore(LocalDate.now()) ? shownTo : LocalDate.now().minusDays(1);
			if (last.isBefore(shownFrom)) {
				return null;
			}
			Function<ClimateDay, Double> value = valueOf(variable);
			long withData = days.values()
				.stream()
				.filter(day -> !day.getDate().isBefore(shownFrom) && !day.getDate().isAfter(last))
				.filter(day -> value.apply(day) != null)
				.count();
			return withData / (double) (last.toEpochDay() - shownFrom.toEpochDay() + 1);
		}

		private List<ClimateWeekDto> weeks(Map<EpiWeek, ClimateWeekFigures> figures, ClimateWeeklyNormals normals, boolean region) {

			List<ClimateWeekDto> weeks = new ArrayList<>();
			for (EpiWeek week : shownWeeks) {
				ClimateWeekFigures current = figures.get(week);
				EpiWeek lastYearWeek = ClimateStatistics.sameWeekLastYear(week);
				ClimateWeekFigures lastYear = lastYearWeek != null ? figures.get(lastYearWeek) : null;
				int number = week.getWeek();

				HumidityMeasure measure = current != null && current.getHumidityMeasure() != null
					? current.getHumidityMeasure()
					: normals.getHumidityMeasureForGap(number);
				Double lastYearHumidity = lastYear != null && lastYear.getHumidityMeasure() == measure ? lastYear.getHumidity() : null;

				ClimateRangeDto rainfallNormal = normals.getRainfall(number);
				ClimateRangeDto temperatureNormal = normals.getTemperature(number);
				ClimateRangeDto humidityNormal = measure != null ? normals.getHumidity(number, measure) : null;

				Double rainfall = current != null ? current.getRainfall() : null;
				Double temperature = current != null ? current.getTemperatureMean() : null;
				Double humidity = current != null ? current.getHumidity() : null;

				weeks.add(
					new ClimateWeekDto(
						week,
						value(
							rainfall,
							current != null ? current.getRainfallDays() : 0,
							region,
							rainfallNormal,
							lastYear != null ? lastYear.getRainfall() : null,
							isAbove(rainfall, rainfallNormal) && rainfall >= rainfallMinMm),
						value(
							temperature,
							current != null ? current.getTemperatureDays() : 0,
							region,
							temperatureNormal,
							lastYear != null ? lastYear.getTemperatureMean() : null,
							isAbove(temperature, temperatureNormal)),
						value(
							humidity,
							current != null ? current.getHumidityDays() : 0,
							region,
							humidityNormal,
							lastYearHumidity,
							isAbove(humidity, humidityNormal)),
						current != null ? current.getTemperatureMin() : null,
						current != null ? current.getTemperatureMax() : null,
						measure));
			}
			return weeks;
		}
	}

	private static Map<EpiWeek, ClimateWeekFigures> rollUp(Map<LocalDate, ClimateDay> days, Collection<EpiWeek> weeks) {
		Map<EpiWeek, ClimateWeekFigures> figures = new HashMap<>();
		weeks.forEach(week -> figures.put(week, ClimateStatistics.rollUp(week, days)));
		return figures;
	}

	private static ClimateWeekValueDto value(Double value, int count, boolean region, ClimateRangeDto normal, Double lastYear, boolean unusual) {
		return new ClimateWeekValueDto(value, region ? null : count, region ? count : null, normal, value != null ? lastYear : null, unusual);
	}

	private static boolean isAbove(Double value, ClimateRangeDto normal) {
		return value != null && normal != null && normal.isAbove(value);
	}

	static Function<ClimateDay, Double> valueOf(ClimateVariable variable) {
		switch (variable) {
		case RAINFALL:
			return ClimateDay::getRainfall;
		case TEMPERATURE:
			return ClimateDay::getTemperatureMean;
		case HUMIDITY:
			return ClimateDay::getRelativeHumidity;
		default:
			throw new IllegalArgumentException(variable.name());
		}
	}

	@LocalBean
	@Stateless
	public static class ClimateSeriesFacadeEjbLocal extends ClimateSeriesFacadeEjb {
	}
}
