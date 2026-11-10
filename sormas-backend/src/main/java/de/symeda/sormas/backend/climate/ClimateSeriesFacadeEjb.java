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

import de.symeda.sormas.api.climate.ClimateDailyExportDto;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateDailySeriesDto;
import de.symeda.sormas.api.climate.ClimateDayDto;
import de.symeda.sormas.api.climate.ClimateEnvironmentContextDto;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.ClimateSeriesCriteria;
import de.symeda.sormas.api.climate.ClimateSeriesFacade;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.ClimateWeekDto;
import de.symeda.sormas.api.climate.ClimateWeekValueDto;
import de.symeda.sormas.api.climate.ClimateWeeklySeriesDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentFacadeEjb;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.geo.GeoShapeProviderEjb.GeoShapeProviderEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.Location;
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
	@EJB
	private EnvironmentService environmentService;
	@EJB
	private ClimateLocationService climateLocationService;
	@EJB
	private ClimateJobService climateJobService;
	@EJB
	private GeoShapeProviderEjbLocal geoShapeProvider;

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

		WeeklyRun run = new WeeklyRun(region, districts, criteria.getFromWeek(), criteria.getToWeek(), Math.max(0, criteria.getLeadWeeks()));
		if (criteria.getDistrict() != null) {
			return run.districtSeries(districts.get(0));
		}
		ClimateWeeklySeriesDto regionSeries = run.regionSeries();
		if (criteria.isIncludeDistricts()) {
			districts.forEach(district -> regionSeries.getDistrictSeries().add(run.districtSeries(district)));
		}
		return regionSeries;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public ClimateDailySeriesDto getDailySeries(EnvironmentReferenceDto environmentRef, LocalDate from, LocalDate to) {

		if (environmentRef == null || from == null || to == null || from.isAfter(to)) {
			throw new IllegalArgumentException("A daily climate series needs an environment and a period");
		}

		Environment environment = environmentService.getByUuid(environmentRef.getUuid());
		District district = environment.getLocation() != null ? environment.getLocation().getDistrict() : null;
		ClimateLocation own = environment.getClimateLocation();
		ClimateLocation districtLocation = district != null ? district.getClimateLocation() : null;
		ClimateLocation location = own != null ? own : districtLocation;

		int currentYear = LocalDate.now().getYear();
		List<Integer> referenceYears = IntStream.range(currentYear - climateSettings.getHistoryYears(), currentYear).boxed().collect(Collectors.toList());
		int minYears = climateSettings.getMinHistoryYears();

		ClimateDailyNormals normals = location != null ? dailyNormals(location.getId(), referenceYears, minYears) : null;
		boolean districtNormals = own == null && location != null;
		if (own != null && districtLocation != null && !districtLocation.getId().equals(own.getId())) {
			ClimateDailyNormals districtsNormals = dailyNormals(districtLocation.getId(), referenceYears, minYears);
			if (countDaysWithNormals(districtsNormals, from, to) > countDaysWithNormals(normals, from, to)) {
				normals = districtsNormals;
				districtNormals = true;
			}
		}

		ClimateDailySeriesDto series = new ClimateDailySeriesDto(
			EnvironmentFacadeEjb.toReferenceDto(environment),
			district != null ? DistrictFacadeEjb.toReferenceDto(district) : null,
			own == null,
			districtNormals,
			location != null ? climateObservationService.getEarliestDay(location.getId()) : null,
			minYears,
			referenceYears.get(0),
			referenceYears.get(referenceYears.size() - 1));

		Map<LocalDate, ClimateObservation> rows =
			location != null ? climateObservationService.getByLocationAndPeriod(location, from, to) : Collections.emptyMap();
		for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
			series.getDays().add(day(date, rows.get(date), normals));
		}

		LocalDate lastDay = location != null ? climateObservationService.getLatestDay(location.getId(), LocalDate.now().minusDays(1)) : null;
		Map<LocalDate, ClimateDay> cardDays = lastDay != null
			? climateObservationService.getDays(Collections.singleton(location.getId()), lastDay.minusYears(1).minusDays(6), lastDay)
				.getOrDefault(location.getId(), Collections.emptyMap())
			: Collections.emptyMap();
		series.getCards().addAll(ClimateSummaryCards.build(lastDay, cardDays, normals));
		return series;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public ClimateEnvironmentContextDto getEnvironmentContext(EnvironmentReferenceDto environmentRef) {

		Environment environment = environmentService.getByUuid(environmentRef.getUuid());
		Location address = environment.getLocation();
		District district = address != null ? address.getDistrict() : null;
		ClimateLocation own = environment.getClimateLocation();
		ClimateLocation location = shownLocation(environment);

		ClimateEnvironmentContextDto context = new ClimateEnvironmentContextDto();
		context.setEnvironment(EnvironmentFacadeEjb.toReferenceDto(environment));
		context.setOwnLocation(own != null);
		context.setReportDate(environment.getReportDate());
		context.setArchived(environment.isArchived());
		context.setDeleted(environment.isDeleted());
		if (address != null) {
			context.setLatitude(address.getLatitude());
			context.setLongitude(address.getLongitude());
		}
		if (district != null) {
			context.setDistrict(DistrictFacadeEjb.toReferenceDto(district));
		}
		if (location == null) {
			return context;
		}

		context.setCellLatitude(location.getLatitude());
		context.setCellLongitude(location.getLongitude());
		context.setDistrictHasPoint(location.getLatitude() != null);
		context.setStillUpdated(climateLocationService.isInUse(location.getId()));
		context.setLatestDay(climateObservationService.getLatestDay(location.getId(), LocalDate.now().minusDays(1)));
		context.setHistoryFrom(climateObservationService.getEarliestDay(location.getId()));

		if (own != null) {
			context.getSharedDistricts()
				.addAll(climateLocationService.getDistrictNamesByLocation(Collections.singleton(own.getId())).getOrDefault(own.getId(), Collections.emptyList()));
			context.getSharedEnvironments().addAll(climateLocationService.getOtherEnvironmentNames(own.getId(), environment.getId()));
			if (district != null && address.getLatitude() != null && address.getLongitude() != null) {
				DistrictReferenceDto districtRef = DistrictFacadeEjb.toReferenceDto(district);
				if (geoShapeProvider.getDistrictShape(districtRef) != null) {
					DistrictReferenceDto atPoint = geoShapeProvider.getDistrictByCoord(new GeoLatLon(address.getLatitude(), address.getLongitude()));
					if (atPoint == null || !atPoint.getUuid().equals(district.getUuid())) {
						context.setOutsideDistrict(true);
						context.setDistrictAtPoint(atPoint);
					}
				}
			}
		}

		boolean districtLocation = own == null || !context.getSharedDistricts().isEmpty();
		for (ClimateJob job : climateJobService.getOpenJobs()) {
			if (fillsHistory(job, location, district, districtLocation)) {
				context.setLoadingJobType(job.getJobType());
				context.setLoadingLocationsDone(job.getLocationsDone());
				context.setLoadingLocationsTotal(job.getLocationsTotal());
				break;
			}
		}
		return context;
	}

	/**
	 * @return true when the open job fills the location's history: a top-up of the location, or a backfill or fetch now
	 *         whose scope holds the environment's district (environment-only locations only when the job includes
	 *         environments)
	 */
	private static boolean fillsHistory(ClimateJob job, ClimateLocation location, District district, boolean districtLocation) {

		if (job.getJobType() == ClimateJobType.TOP_UP) {
			return job.getClimateLocation() != null && job.getClimateLocation().getId().equals(location.getId());
		}
		if (job.getJobType() != ClimateJobType.BACKFILL && job.getJobType() != ClimateJobType.FETCH) {
			return false;
		}
		if (!districtLocation && !job.isIncludeEnvironments()) {
			return false;
		}
		if (job.getDistrict() != null) {
			return district != null && job.getDistrict().getId().equals(district.getId());
		}
		if (job.getRegion() != null) {
			return district != null && job.getRegion().getId().equals(district.getRegion().getId());
		}
		return true;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_EXPORT)
	public List<ClimateDailyExportDto> getDailyExportList(EnvironmentReferenceDto environmentRef, LocalDate from, LocalDate to) {

		ClimateDailySeriesDto series = getDailySeries(environmentRef, from, to);
		Environment environment = environmentService.getByUuid(environmentRef.getUuid());
		ClimateLocation location = shownLocation(environment);
		Map<LocalDate, ClimateObservation> rows =
			location != null ? climateObservationService.getByLocationAndPeriod(location, from, to) : Collections.emptyMap();
		String normalsFrom = series.isDistrictNormals() && series.getDistrict() != null
			? series.getDistrict().getCaption()
			: environment.getEnvironmentName();

		List<ClimateDailyExportDto> export = new ArrayList<>(series.getDays().size());
		for (ClimateDayDto day : series.getDays()) {
			ClimateDailyExportDto row = new ClimateDailyExportDto();
			row.setDate(ClimateCalendar.toDate(day.getDate()));
			row.setTemperatureMin(day.getTemperatureMin());
			row.setTemperatureMean(day.getTemperatureMean());
			row.setTemperatureMax(day.getTemperatureMax());
			row.setRainfall(day.getRainfall());
			row.setRelativeHumidity(day.getRelativeHumidity());
			row.setHumidityMeasure(day.getHumidityMeasure());
			row.setSource(day.getSource());
			ClimateObservation observation = rows.get(day.getDate());
			row.setSourceDetail(observation != null ? observation.getSourceDetail() : null);
			ClimateRangeDto rainfall = day.getRainfallNormal();
			if (rainfall != null) {
				row.setRainfallP10(rainfall.getP10());
				row.setRainfallP50(rainfall.getP50());
				row.setRainfallP90(rainfall.getP90());
			}
			ClimateRangeDto temperature = day.getTemperatureNormal();
			if (temperature != null) {
				row.setTemperatureP10(temperature.getP10());
				row.setTemperatureP50(temperature.getP50());
				row.setTemperatureP90(temperature.getP90());
			}
			ClimateRangeDto humidity = day.getHumidityNormal();
			if (humidity != null) {
				row.setHumidityP10(humidity.getP10());
				row.setHumidityP50(humidity.getP50());
				row.setHumidityP90(humidity.getP90());
			}
			if (rainfall != null || temperature != null || humidity != null) {
				row.setNormalsFrom(normalsFrom);
			}
			export.add(row);
		}
		return export;
	}

	/**
	 * @return the environment's own climate location, else its district's (no usable GPS), else null
	 */
	private static ClimateLocation shownLocation(Environment environment) {
		if (environment.getClimateLocation() != null) {
			return environment.getClimateLocation();
		}
		District district = environment.getLocation() != null ? environment.getLocation().getDistrict() : null;
		return district != null ? district.getClimateLocation() : null;
	}

	private ClimateDailyNormals dailyNormals(Long location, List<Integer> referenceYears, int minYears) {

		LocalDate referenceFrom = LocalDate.of(referenceYears.get(0), 1, 1);
		LocalDate referenceTo = LocalDate.of(referenceYears.get(referenceYears.size() - 1), 12, 31);
		return normalsCache.get(
			"D|L" + location + "|" + referenceYears + "|" + minYears,
			climateObservationService.getFingerprint(Collections.singleton(location), referenceFrom, referenceTo),
			() -> ClimateDailyNormals.compute(
				climateObservationService.getDays(Collections.singleton(location), referenceFrom, referenceTo)
					.getOrDefault(location, Collections.emptyMap()),
				referenceYears,
				minYears));
	}

	private static long countDaysWithNormals(ClimateDailyNormals normals, LocalDate from, LocalDate to) {
		long count = 0;
		for (LocalDate date = from; normals != null && !date.isAfter(to); date = date.plusDays(1)) {
			if (normals.hasAny(date)) {
				count++;
			}
		}
		return count;
	}

	private static ClimateDayDto day(LocalDate date, ClimateObservation row, ClimateDailyNormals normals) {

		ClimateDayDto day = new ClimateDayDto(date);
		HumidityMeasure measure = null;
		if (row != null) {
			day.setTemperatures(row.getTemperatureMin(), row.getTemperatureMean(), row.getTemperatureMax());
			day.setRainfall(row.getRainfall());
			day.setSource(row.getSource());
			if (row.getRelativeHumidity() != null) {
				measure = row.getHumidityMeasure();
			}
		}
		if (normals != null) {
			if (measure == null) {
				measure = normals.getHumidityMeasureForGap(date);
			}
			day.setRainfallNormal(normals.getRainfall(date));
			day.setTemperatureNormal(normals.getTemperature(date));
			day.setHumidityNormal(normals.getHumidity(date, measure));
		}
		day.setHumidity(row != null ? row.getRelativeHumidity() : null, measure);
		return day;
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
		/** Weeks before the first week shown, at the start of {@link #shownWeeks}, for the lag (decision 126). */
		private final int leadWeeks;
		private final LocalDate leadFrom;
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

		WeeklyRun(Region region, List<District> districts, EpiWeek fromWeek, EpiWeek toWeek, int leadWeeks) {

			this.region = region;
			this.districts = districts;
			this.leadWeeks = leadWeeks;
			leadFrom = ClimateStatistics.firstDay(fromWeek).minusWeeks(leadWeeks);
			shownWeeks = ClimateStatistics.weeksBetween(ClimateStatistics.epiWeekOf(leadFrom), toWeek);

			int currentYear = ClimateStatistics.epiWeekOf(LocalDate.now()).getYear();
			int historyYears = climateSettings.getHistoryYears();
			referenceYears = IntStream.range(currentYear - historyYears, currentYear).boxed().collect(Collectors.toList());
			minYears = climateSettings.getMinHistoryYears();
			rainfallMinMm = climateSettings.getRainfallMinMm();
			referenceFrom = ClimateStatistics.firstDayOfEpiYear(referenceYears.get(0));
			referenceTo = ClimateStatistics.firstDayOfEpiYear(currentYear).minusDays(1);

			shownFrom = ClimateStatistics.firstDay(fromWeek);
			shownTo = ClimateStatistics.firstDay(toWeek).plusDays(6);
			LocalDate loadFrom = leadFrom.minusWeeks(53);
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
			series.setLeadWeeks(leadWeeks);
			series.setOpenWeatherData(openWeatherData(location != null ? Collections.singleton(location) : Collections.<Long>emptySet()));
			Map<LocalDate, ClimateDay> days = location != null ? shownDays.getOrDefault(location, Collections.emptyMap()) : Collections.emptyMap();
			for (ClimateVariable variable : ClimateVariable.values()) {
				series.setCompleteness(variable, completeness(days, variable));
			}
			return series;
		}

		ClimateWeeklySeriesDto regionSeries() {

			Map<EpiWeek, ClimateWeekFigures> figures = combine(shownFigures, ClimateStatistics.weeksBetween(ClimateStatistics.epiWeekOf(leadFrom.minusWeeks(53)), shownWeeks.get(shownWeeks.size() - 1)));
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
			series.setLeadWeeks(leadWeeks);
			series.setOpenWeatherData(openWeatherData(locationIds()));
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

		/**
		 * @return true when a day returned (lead weeks included) comes from OpenWeather, so the screen shows its credit
		 */
		private boolean openWeatherData(Set<Long> locations) {
			return climateObservationService.hasSource(locations, leadFrom, shownTo, ClimateDataSource.OPENWEATHER);
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
