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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections4.ListUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.climate.ClimateCaseCountSource;
import de.symeda.sormas.api.climate.ClimateCaseCriteria;
import de.symeda.sormas.api.climate.ClimateCaseFacade;
import de.symeda.sormas.api.climate.ClimateCaseSeriesDto;
import de.symeda.sormas.api.climate.ClimateCaseWeekDto;
import de.symeda.sormas.api.climate.ClimateDashboardExportDto;
import de.symeda.sormas.api.climate.ClimateNearbyCasesDto;
import de.symeda.sormas.api.climate.ClimateNearbyCasesWeekDto;
import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.ClimateSeriesCriteria;
import de.symeda.sormas.api.climate.ClimateUsualRangeMissing;
import de.symeda.sormas.api.climate.ClimateWeekDto;
import de.symeda.sormas.api.climate.ClimateWeeklySeriesDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentFacadeEjb;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.infrastructure.PopulationData;
import de.symeda.sormas.backend.infrastructure.PopulationDataService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.report.AggregateReport;
import de.symeda.sormas.backend.report.AggregateReportService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ClimateCaseFacade")
public class ClimateCaseFacadeEjb implements ClimateCaseFacade {

	private static final double PER_100000 = 100_000;
	/** Mean Earth radius. */
	private static final double EARTH_RADIUS_KM = 6371.0088;
	private static final double KM_PER_DEGREE_LATITUDE = 111.32;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ClimateSettings climateSettings;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CaseService caseService;
	@EJB
	private AggregateReportService aggregateReportService;
	@EJB
	private CurrentUserService currentUserService;
	@EJB
	private EnvironmentService environmentService;
	@EJB
	private PopulationDataService populationDataService;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private ClimateSeriesFacadeEjb.ClimateSeriesFacadeEjbLocal climateSeriesFacade;

	@Override
	@RightsAllowed(UserRight._DASHBOARD_CLIMATE_VIEW)
	public List<ClimateCaseSeriesDto> getCaseSeries(ClimateCaseCriteria criteria) {

		if (criteria.getRegion() == null
			|| criteria.getFromWeek() == null
			|| criteria.getToWeek() == null
			|| ClimateStatistics.firstDay(criteria.getFromWeek()).isAfter(ClimateStatistics.firstDay(criteria.getToWeek()))) {
			throw new IllegalArgumentException("A case series needs a region and a period");
		}

		Region region = regionService.getByUuid(criteria.getRegion().getUuid());
		List<District> districts;
		if (criteria.getDistrict() != null) {
			districts = Collections.singletonList(districtService.getByUuid(criteria.getDistrict().getUuid()));
		} else {
			districts = new ArrayList<>(districtService.getAllActiveByRegion(region));
			districts.sort(Comparator.comparing(District::getName, String.CASE_INSENSITIVE_ORDER));
		}

		List<EpiWeek> shownWeeks = ClimateStatistics.weeksBetween(criteria.getFromWeek(), criteria.getToWeek());
		int currentYear = ClimateStatistics.epiWeekOf(LocalDate.now()).getYear();
		List<Integer> referenceYears =
			IntStream.range(currentYear - climateSettings.getHistoryYears(), currentYear).boxed().collect(Collectors.toList());
		LocalDate referenceFrom = ClimateStatistics.firstDayOfEpiYear(referenceYears.get(0));
		LocalDate referenceTo = ClimateStatistics.firstDayOfEpiYear(currentYear).minusDays(1);
		LocalDate shownFrom = ClimateStatistics.firstDay(criteria.getFromWeek());
		LocalDate shownTo = ClimateStatistics.firstDay(criteria.getToWeek()).plusDays(6);
		LocalDate loadFrom = shownFrom.isBefore(referenceFrom) ? shownFrom : referenceFrom;
		LocalDate loadTo = shownTo.isAfter(referenceTo) ? shownTo : referenceTo;

		Map<District, Integer> populations = new HashMap<>();
		districts.forEach(district -> populations.put(district, getProjectedPopulation(district)));
		List<DistrictReferenceDto> withoutPopulation = districts.stream()
			.filter(district -> populations.get(district) == null)
			.map(DistrictFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
		Integer regionPopulation =
			withoutPopulation.isEmpty() && !districts.isEmpty() ? districts.stream().mapToInt(populations::get).sum() : null;

		SeriesSettings settings = new SeriesSettings(
			shownWeeks,
			referenceYears,
			climateSettings.getCaseMinHistoryYears(),
			climateSettings.getCaseMinWeeklyCases());

		List<ClimateCaseSeriesDto> result = new ArrayList<>();
		for (Disease disease : criteria.getDiseases()) {
			// a disease with case surveillance is counted from its cases, even with aggregate reporting on; otherwise from the
			// lab-confirmed counts of its aggregate reports, where a week without a report has no value (decision 147)
			ClimateCaseCountSource source = diseaseConfigurationFacade.isCaseSurveillanceEnabled(disease)
				? ClimateCaseCountSource.CASES
				: ClimateCaseCountSource.AGGREGATE_LAB_CONFIRMED;
			Map<Long, Map<EpiWeek, int[]>> counts;
			Map<EpiWeek, int[]> regionCounts;
			if (source == ClimateCaseCountSource.CASES) {
				counts = countCases(disease, districts, criteria.getClassification().getClassifications(), loadFrom, loadTo);
				Map<EpiWeek, int[]> sums = new HashMap<>();
				for (Map<EpiWeek, int[]> byWeek : counts.values()) {
					byWeek.forEach((week, count) -> {
						int[] sum = sums.computeIfAbsent(week, w -> new int[2]);
						sum[0] += count[0];
						sum[1] += count[1];
					});
				}
				regionCounts = sums;
			} else {
				AggregateCounts aggregate = countAggregateReports(disease, region, districts, loadFrom, loadTo);
				counts = aggregate.byDistrict;
				regionCounts = aggregate.region;
			}
			Set<Integer> excludedYears = diseaseConfigurationFacade.getUsualRangeExcludedYears(disease);
			List<Integer> yearsWithCases = referenceYears.stream()
				.filter(year -> !excludedYears.contains(year))
				.filter(year -> source == ClimateCaseCountSource.CASES ? hasCases(disease, year) : hasAggregateReports(disease, year))
				.collect(Collectors.toList());
			DiseaseYears years = new DiseaseYears(disease, source, excludedYears, yearsWithCases);

			if (criteria.getDistrict() != null) {
				District district = districts.get(0);
				result.add(
					series(
						years,
						RegionFacadeEjb.toReferenceDto(district.getRegion()),
						DistrictFacadeEjb.toReferenceDto(district),
						counts.getOrDefault(district.getId(), Collections.emptyMap()),
						populations.get(district),
						withoutPopulation,
						settings));
				continue;
			}

			ClimateCaseSeriesDto regionSeries = series(
				years,
				RegionFacadeEjb.toReferenceDto(region),
				null,
				regionCounts,
				regionPopulation,
				withoutPopulation,
				settings);
			if (criteria.isIncludeDistricts()) {
				for (District district : districts) {
					regionSeries.getDistrictSeries()
						.add(
							series(
								years,
								RegionFacadeEjb.toReferenceDto(region),
								DistrictFacadeEjb.toReferenceDto(district),
								counts.getOrDefault(district.getId(), Collections.emptyMap()),
								populations.get(district),
								populations.get(district) == null
									? Collections.singletonList(DistrictFacadeEjb.toReferenceDto(district))
									: Collections.emptyList(),
								settings));
				}
			}
			result.add(regionSeries);
		}
		return result;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_EXPORT)
	public List<ClimateDashboardExportDto> getDashboardExportList(ClimateCaseCriteria criteria) {

		// the export holds the dashboard's case counts, so it needs the dashboard's right as well (decision 128)
		if (!currentUserService.hasUserRight(UserRight.DASHBOARD_CLIMATE_VIEW)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		boolean includeDistricts = criteria.getDistrict() == null;
		ClimateWeeklySeriesDto climate = climateSeriesFacade.getWeeklySeries(
			new ClimateSeriesCriteria().region(criteria.getRegion())
				.district(criteria.getDistrict())
				.weeks(criteria.getFromWeek(), criteria.getToWeek())
				.includeDistricts(includeDistricts));
		List<ClimateCaseSeriesDto> cases = getCaseSeries(
			new ClimateCaseCriteria().region(criteria.getRegion())
				.district(criteria.getDistrict())
				.weeks(criteria.getFromWeek(), criteria.getToWeek())
				.diseases(criteria.getDiseases())
				.classification(criteria.getClassification())
				.includeDistricts(includeDistricts));

		List<ClimateDashboardExportDto> rows = new ArrayList<>(exportRows(climate, cases));
		if (includeDistricts) {
			for (ClimateWeeklySeriesDto districtClimate : climate.getDistrictSeries()) {
				List<ClimateCaseSeriesDto> districtCases = cases.stream()
					.map(
						series -> series.getDistrictSeries()
							.stream()
							.filter(s -> districtClimate.getDistrict().equals(s.getDistrict()))
							.findFirst()
							.orElse(null))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
				rows.addAll(exportRows(districtClimate, districtCases));
			}
		}
		return rows;
	}

	/**
	 * @return one row per week of the place's climate series, with the cases of the same week
	 */
	private static List<ClimateDashboardExportDto> exportRows(ClimateWeeklySeriesDto climate, List<ClimateCaseSeriesDto> cases) {

		Integer population = cases.isEmpty() ? null : cases.get(0).getPopulation();
		List<ClimateDashboardExportDto> rows = new ArrayList<>();
		for (ClimateWeekDto week : climate.getShownWeeks()) {
			ClimateDashboardExportDto row =
				new ClimateDashboardExportDto(climate.getRegion(), climate.getDistrict(), week, climate.isOpenWeatherData(), population);
			for (ClimateCaseSeriesDto series : cases) {
				series.getWeeks()
					.stream()
					.filter(w -> w.getWeek().equals(week.getWeek()))
					.findFirst()
					.ifPresent(w -> row.putCases(series.getDisease(), series.getCountSource(), w));
			}
			rows.add(row);
		}
		return rows;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public ClimateNearbyCasesDto getNearbyCases(EnvironmentReferenceDto environmentRef, Disease disease, double radiusKm, LocalDate from, LocalDate to) {

		if (environmentRef == null || from == null || to == null || from.isAfter(to) || radiusKm <= 0) {
			throw new IllegalArgumentException("Nearby cases need an environment, a radius and a period");
		}
		if (!currentUserService.hasUserRight(UserRight.CASE_VIEW)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		Environment environment = environmentService.getByUuid(environmentRef.getUuid());
		Location site = environment.getLocation();
		boolean byCommunityOnly = site == null || !isUsablePoint(site.getLatitude(), site.getLongitude());
		Long communityId = site != null && site.getCommunity() != null ? site.getCommunity().getId() : null;

		ClimateNearbyCasesDto nearby =
			new ClimateNearbyCasesDto(EnvironmentFacadeEjb.toReferenceDto(environment), disease, radiusKm, byCommunityOnly);
		Map<EpiWeek, ClimateNearbyCasesWeekDto> weeks = new HashMap<>();
		for (EpiWeek week : ClimateStatistics.weeksBetween(ClimateStatistics.epiWeekOf(from), ClimateStatistics.epiWeekOf(to))) {
			ClimateNearbyCasesWeekDto weekDto = new ClimateNearbyCasesWeekDto(week);
			weeks.put(week, weekDto);
			nearby.getWeeks().add(weekDto);
		}
		if (byCommunityOnly && communityId == null) {
			return nearby;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		CaseQueryContext queryContext = new CaseQueryContext(cb, cq, caze);
		CaseJoins joins = queryContext.getJoins();
		Join<Person, Location> address = joins.getPersonAddress();
		Expression<Date> onsetDate = joins.getSymptoms().get(Symptoms.ONSET_DATE);
		Expression<Date> caseDate = cb.coalesce(onsetDate, caze.get(Case.REPORT_DATE));
		Expression<Double> addressLatitude = address.get(Location.LATITUDE);
		Expression<Double> addressLongitude = address.get(Location.LONGITUDE);
		Expression<Long> epiDataId = caze.get(Case.EPI_DATA).get(EpiData.ID);
		Expression<Long> caseCommunity =
			cb.coalesce(address.join(Location.COMMUNITY, JoinType.LEFT).get(Community.ID), joins.getResponsibleCommunity().get(Community.ID));

		Predicate addressPoint = cb.and(cb.isNotNull(addressLatitude), cb.isNotNull(addressLongitude));
		Predicate sameCommunity = communityId != null ? cb.equal(caseCommunity, communityId) : cb.disjunction();
		Predicate place;
		if (byCommunityOnly) {
			place = sameCommunity;
		} else {
			// a latitude/longitude box around the site narrows the cases; the exact distance is checked below, on the point
			// the case is placed at
			double latitude = site.getLatitude();
			double longitude = site.getLongitude();
			double latitudeDelta = radiusKm / KM_PER_DEGREE_LATITUDE;
			double longitudeDelta = radiusKm / (KM_PER_DEGREE_LATITUDE * Math.max(Math.cos(Math.toRadians(latitude)), 0.01));

			Subquery<Long> infectionInBox = cq.subquery(Long.class);
			Root<Exposure> exposure = infectionInBox.from(Exposure.class);
			Join<Exposure, Location> exposureLocation = exposure.join(Exposure.LOCATION);
			infectionInBox.select(exposure.get(Exposure.ID))
				.where(
					cb.equal(exposure.get(Exposure.EPI_DATA), caze.get(Case.EPI_DATA)),
					cb.isTrue(exposure.get(ExposureDto.PROBABLE_INFECTION_ENVIRONMENT)),
					cb.between(exposureLocation.get(Location.LATITUDE), latitude - latitudeDelta, latitude + latitudeDelta),
					cb.between(exposureLocation.get(Location.LONGITUDE), longitude - longitudeDelta, longitude + longitudeDelta));

			place = cb.or(
				cb.and(
					addressPoint,
					cb.between(addressLatitude, latitude - latitudeDelta, latitude + latitudeDelta),
					cb.between(addressLongitude, longitude - longitudeDelta, longitude + longitudeDelta)),
				cb.exists(infectionInBox),
				cb.and(cb.not(addressPoint), sameCommunity));
		}

		Predicate filter = cb.and(
			cb.isFalse(caze.get(Case.DELETED)),
			cb.notEqual(caze.get(Case.CASE_CLASSIFICATION), CaseClassification.NO_CASE),
			cb.greaterThanOrEqualTo(caseDate, ClimateCalendar.toDate(from)),
			cb.lessThan(caseDate, ClimateCalendar.toDate(to.plusDays(1))),
			place);
		if (disease != null) {
			filter = cb.and(filter, cb.equal(caze.get(Case.DISEASE), disease));
		}
		filter = CriteriaBuilderHelper.and(cb, filter, caseService.createUserFilter(queryContext));
		cq.multiselect(caze.get(Case.UUID), onsetDate, caze.get(Case.REPORT_DATE), addressLatitude, addressLongitude, epiDataId);
		cq.where(filter);
		List<Object[]> rows = em.createQuery(cq).getResultList();

		Map<Long, double[]> infectionPoints =
			byCommunityOnly ? Collections.emptyMap() : getInfectionPoints(rows.stream().map(row -> (Long) row[5]).collect(Collectors.toList()));
		for (Object[] row : rows) {
			Date day = (Date) (row[1] != null ? row[1] : row[2]);
			ClimateNearbyCasesWeekDto week = weeks.get(ClimateStatistics.epiWeekOf(ClimateCalendar.toLocalDate(day)));
			if (week == null) {
				continue;
			}
			String uuid = (String) row[0];
			if (byCommunityOnly) {
				week.getCommunityCaseUuids().add(uuid);
				continue;
			}
			// the probable infection environment first, else the home address (decision 82); never the report point or the
			// facility, which say where the case was seen, not where the person was exposed
			double[] infection = infectionPoints.get((Long) row[5]);
			double[] point = infection != null
				? infection
				: isUsablePoint((Double) row[3], (Double) row[4]) ? new double[] {
					(Double) row[3],
					(Double) row[4] } : null;
			if (point == null) {
				week.getCommunityCaseUuids().add(uuid);
			} else if (distanceKm(site.getLatitude(), site.getLongitude(), point[0], point[1]) <= radiusKm) {
				week.getCaseUuids().add(uuid);
				if (infection != null) {
					week.getExposureCaseUuids().add(uuid);
				}
			}
		}
		return nearby;
	}

	/**
	 * @return by epi data id, the point of the exposure marked as the probable infection environment, when it has usable
	 *         GPS; of several, the one that started last
	 */
	private Map<Long, double[]> getInfectionPoints(List<Long> epiDataIds) {

		Map<Long, double[]> points = new HashMap<>();
		Map<Long, Date> startDates = new HashMap<>();
		for (List<Long> batch : ListUtils.partition(epiDataIds, ModelConstants.PARAMETER_LIMIT)) {
			if (batch.isEmpty()) {
				continue;
			}
			List<Object[]> exposures = em
				.createQuery(
					"SELECT e." + Exposure.EPI_DATA + ".id, e." + ExposureDto.START_DATE + ", l." + Location.LATITUDE + ", l." + Location.LONGITUDE
						+ " FROM " + Exposure.TABLE_NAME + " e JOIN e." + Exposure.LOCATION + " l WHERE e." + Exposure.EPI_DATA + ".id IN :epiData AND e."
						+ ExposureDto.PROBABLE_INFECTION_ENVIRONMENT + " = true ORDER BY e.id",
					Object[].class)
				.setParameter("epiData", batch)
				.getResultList();
			for (Object[] exposure : exposures) {
				Long epiData = (Long) exposure[0];
				Date start = (Date) exposure[1];
				if (!isUsablePoint((Double) exposure[2], (Double) exposure[3])) {
					continue;
				}
				Date latest = startDates.get(epiData);
				if (!points.containsKey(epiData) || (start != null && (latest == null || !start.before(latest)))) {
					points.put(epiData, new double[] {
						(Double) exposure[2],
						(Double) exposure[3] });
					startDates.put(epiData, start);
				}
			}
		}
		return points;
	}

	/**
	 * @return true when the point can be used: latitude −90 to 90, longitude −180 to 180, and not 0, 0
	 */
	static boolean isUsablePoint(Double latitude, Double longitude) {
		return latitude != null
			&& longitude != null
			&& latitude >= -90
			&& latitude <= 90
			&& longitude >= -180
			&& longitude <= 180
			&& !(latitude == 0 && longitude == 0);
	}

	/**
	 * @return the great-circle distance between two points, in km (haversine)
	 */
	static double distanceKm(double latitude1, double longitude1, double latitude2, double longitude2) {
		double dLatitude = Math.toRadians(latitude2 - latitude1);
		double dLongitude = Math.toRadians(longitude2 - longitude1);
		double a = Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2)
			+ Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) * Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2);
		return 2 * EARTH_RADIUS_KM * Math.asin(Math.min(1, Math.sqrt(a)));
	}

	private static ClimateCaseSeriesDto series(
		DiseaseYears years,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		Map<EpiWeek, int[]> counts,
		Integer population,
		List<DistrictReferenceDto> withoutPopulation,
		SeriesSettings settings) {

		Double perCase = population != null && population > 0 ? PER_100000 / population : null;
		Map<Integer, ClimateRangeDto> ranges = new HashMap<>();
		Map<Integer, ClimateUsualRangeMissing> missing = new HashMap<>();
		List<ClimateCaseWeekDto> weeks = new ArrayList<>();
		for (EpiWeek week : settings.shownWeeks) {
			int number = week.getWeek();
			if (!ranges.containsKey(number) && !missing.containsKey(number)) {
				usualRange(number, counts, years, settings, ranges, missing);
			}
			// cases: a week without cases is 0; aggregate reports: a week without a report has no value (decision 147)
			boolean reported = years.source == ClimateCaseCountSource.CASES || counts.containsKey(week);
			int[] count = counts.getOrDefault(week, new int[2]);
			ClimateRangeDto range = ranges.get(number);
			weeks.add(
				new ClimateCaseWeekDto(
					week,
					count[0],
					count[1],
					reported && perCase != null ? count[0] * perCase : null,
					range,
					range != null && perCase != null ? range.scaled(perCase) : null,
					missing.get(number),
					reported));
		}

		ClimateCaseSeriesDto series = new ClimateCaseSeriesDto(
			years.disease,
			years.source,
			region,
			district,
			population,
			withoutPopulation,
			settings.referenceYears.get(0),
			settings.referenceYears.get(settings.referenceYears.size() - 1),
			years.excludedYears,
			years.yearsWithCases,
			weeks);

		LocalDate today = LocalDate.now();
		List<ClimateCaseWeekDto> complete = weeks.stream()
			.filter(week -> ClimateStatistics.firstDay(week.getWeek()).plusDays(6).isBefore(today))
			.collect(Collectors.toList());
		List<ClimateCaseWeekDto> recent = complete.subList(Math.max(0, complete.size() - ClimateCaseSeriesDto.RECENT_WEEKS), complete.size());
		series.setRecentWeeks(recent.size(), (int) recent.stream().filter(ClimateCaseWeekDto::isAboveUsualRange).count());
		return series;
	}

	/**
	 * The usual range for a week number (decision 75): weekly counts of the week and its neighbours in each reference year
	 * with cases of the disease (a week without cases counts as 0), when there are enough such years and their median is
	 * at least the minimum weekly count. For aggregate reports a week without a report is left out, and a year counts only
	 * when its window has at least one report (decision 147).
	 */
	private static void usualRange(
		int week,
		Map<EpiWeek, int[]> counts,
		DiseaseYears years,
		SeriesSettings settings,
		Map<Integer, ClimateRangeDto> ranges,
		Map<Integer, ClimateUsualRangeMissing> missing) {

		if (years.yearsWithCases.size() < settings.minYears) {
			missing.put(week, ClimateUsualRangeMissing.NOT_ENOUGH_HISTORY);
			return;
		}
		boolean cases = years.source == ClimateCaseCountSource.CASES;
		ClimateStatistics.PooledValues pooled = ClimateStatistics.poolWeeks(
			week,
			years.yearsWithCases,
			epiWeek -> {
				if (counts.containsKey(epiWeek)) {
					return Double.valueOf(counts.get(epiWeek)[0]);
				}
				return cases ? Double.valueOf(0) : null;
			});
		ClimateRangeDto range = pooled.toRange(settings.minYears);
		if (range == null) {
			missing.put(week, ClimateUsualRangeMissing.NOT_ENOUGH_HISTORY);
		} else if (ClimateStatistics.percentile(pooled.getValues(), 0.5) < settings.minWeeklyCases) {
			missing.put(week, ClimateUsualRangeMissing.TOO_FEW_CASES);
		} else {
			ranges.put(week, range);
		}
	}

	/**
	 * Counts the cases of the districts by district and epi week, within the user's jurisdiction: index 0 holds all cases,
	 * index 1 those placed by report date because their onset date is missing. The cases' dates are read and put into epi
	 * weeks in Java, so no database-specific week function is needed.
	 */
	private Map<Long, Map<EpiWeek, int[]>> countCases(
		Disease disease,
		Collection<District> districts,
		Collection<?> classifications,
		LocalDate from,
		LocalDate to) {

		Map<Long, Map<EpiWeek, int[]>> counts = new HashMap<>();
		if (districts.isEmpty()) {
			return counts;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		CaseQueryContext queryContext = new CaseQueryContext(cb, cq, caze);
		Expression<Date> onsetDate = queryContext.getJoins().getSymptoms().get(Symptoms.ONSET_DATE);
		Expression<Date> reportDate = caze.get(Case.REPORT_DATE);
		Expression<Date> caseDate = cb.coalesce(onsetDate, reportDate);
		Expression<Long> districtId = queryContext.getJoins().getResponsibleDistrict().get(District.ID);

		Predicate filter = cb.and(
			cb.equal(caze.get(Case.DISEASE), disease),
			cb.isFalse(caze.get(Case.DELETED)),
			caze.get(Case.CASE_CLASSIFICATION).in(classifications),
			districtId.in(districts.stream().map(District::getId).collect(Collectors.toList())),
			cb.greaterThanOrEqualTo(caseDate, ClimateCalendar.toDate(from)),
			cb.lessThan(caseDate, ClimateCalendar.toDate(to.plusDays(1))));
		filter = CriteriaBuilderHelper.and(cb, filter, caseService.createUserFilter(queryContext));
		cq.multiselect(districtId, onsetDate, reportDate);
		cq.where(filter);

		for (Object[] row : em.createQuery(cq).getResultList()) {
			boolean byReportDate = row[1] == null;
			EpiWeek week = ClimateStatistics.epiWeekOf(ClimateCalendar.toLocalDate((Date) (byReportDate ? row[2] : row[1])));
			int[] count = counts.computeIfAbsent((Long) row[0], id -> new HashMap<>()).computeIfAbsent(week, w -> new int[2]);
			count[0]++;
			if (byReportDate) {
				count[1]++;
			}
		}
		return counts;
	}

	/**
	 * @return true when the deployment holds at least one case of the disease in the epi year, anywhere and of any
	 *         classification: the year counts for the usual range, so a year before SORMAS was used for the disease is not
	 *         read as a year without cases
	 */
	private boolean hasCases(Disease disease, int year) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> caze = cq.from(Case.class);
		CaseQueryContext queryContext = new CaseQueryContext(cb, cq, caze);
		Expression<Date> caseDate = cb.coalesce(queryContext.getJoins().getSymptoms().get(Symptoms.ONSET_DATE), caze.get(Case.REPORT_DATE));
		cq.select(caze.get(Case.ID));
		cq.where(
			cb.equal(caze.get(Case.DISEASE), disease),
			cb.isFalse(caze.get(Case.DELETED)),
			cb.greaterThanOrEqualTo(caseDate, ClimateCalendar.toDate(ClimateStatistics.firstDayOfEpiYear(year))),
			cb.lessThan(caseDate, ClimateCalendar.toDate(ClimateStatistics.firstDayOfEpiYear(year + 1))));
		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
	}

	/**
	 * Counts the lab-confirmed aggregate reports of a disease without case surveillance (decision 147), within the user's
	 * jurisdiction, by district and epi week, and for the region. They are added up as SORMAS's aggregate report screen
	 * does: of several reports for the same place, week and age group the latest counts; a report for a whole district
	 * replaces its facilities' and points of entry's for that week and age group, and a report for the whole region its
	 * districts'; reports of the same level are added. A region-level report counts only for the region. A week without
	 * any report is left out of the maps: it has no value, not 0.
	 */
	private AggregateCounts countAggregateReports(Disease disease, Region region, Collection<District> districts, LocalDate from, LocalDate to) {

		AggregateCounts result = new AggregateCounts();
		if (districts.isEmpty()) {
			return result;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<AggregateReport> report = cq.from(AggregateReport.class);
		Join<AggregateReport, District> district = report.join(AggregateReport.DISTRICT, JoinType.LEFT);
		Join<AggregateReport, Facility> facility = report.join(AggregateReport.HEALTH_FACILITY, JoinType.LEFT);
		Join<AggregateReport, PointOfEntry> pointOfEntry = report.join(AggregateReport.POINT_OF_ENTRY, JoinType.LEFT);
		// epi years can start in the calendar year before, so one year more on each side; the weeks are checked below
		Predicate filter = cb.and(
			cb.equal(report.get(AggregateReport.DISEASE), disease),
			cb.equal(report.get(AggregateReport.REGION), region),
			cb.between(report.get(AggregateReport.YEAR), from.getYear() - 1, to.getYear() + 1));
		filter = CriteriaBuilderHelper.and(cb, filter, aggregateReportService.createUserFilter(cb, cq, report));
		cq.multiselect(
			district.get(District.ID),
			facility.get(Facility.ID),
			pointOfEntry.get(PointOfEntry.ID),
			report.get(AggregateReport.YEAR),
			report.get(AggregateReport.EPI_WEEK),
			report.get(AggregateReport.AGE_GROUP),
			report.get(AggregateReport.LAB_CONFIRMATIONS),
			report.get(AggregateReport.CHANGE_DATE));
		cq.where(filter);

		// the latest report per place, week and age group
		Map<List<Object>, Object[]> latest = new HashMap<>();
		for (Object[] row : em.createQuery(cq).getResultList()) {
			if (row[3] == null || row[4] == null) {
				continue;
			}
			EpiWeek week = new EpiWeek((Integer) row[3], (Integer) row[4]);
			LocalDate firstDay = ClimateStatistics.firstDay(week);
			if (firstDay.isBefore(from) || firstDay.isAfter(to)) {
				continue;
			}
			List<Object> key = Arrays.asList(row[0], row[1], row[2], week, row[5]);
			Object[] known = latest.get(key);
			if (known == null || ((Date) row[7]).after((Date) known[7])) {
				latest.put(key, row);
			}
		}

		// per district, week and age group: the district's own report, else the sum of its facilities' and points of entry's
		Map<Long, Map<EpiWeek, Map<String, LevelCount>>> byDistrict = new HashMap<>();
		Map<EpiWeek, Map<String, Integer>> regionLevel = new HashMap<>();
		latest.forEach((key, row) -> {
			EpiWeek week = (EpiWeek) key.get(3);
			String ageGroup = (String) row[5];
			int labConfirmed = row[6] != null ? (Integer) row[6] : 0;
			if (row[0] == null) {
				regionLevel.computeIfAbsent(week, w -> new HashMap<>()).merge(ageGroup, labConfirmed, Integer::sum);
				return;
			}
			LevelCount count = byDistrict.computeIfAbsent((Long) row[0], id -> new HashMap<>())
				.computeIfAbsent(week, w -> new HashMap<>())
				.computeIfAbsent(ageGroup, g -> new LevelCount());
			if (row[1] == null && row[2] == null) {
				count.own = (count.own != null ? count.own : 0) + labConfirmed;
			} else {
				count.lower += labConfirmed;
			}
		});

		Set<Long> districtIds = districts.stream().map(District::getId).collect(Collectors.toSet());
		Map<EpiWeek, Map<String, Integer>> districtsByAgeGroup = new HashMap<>();
		byDistrict.forEach((districtId, weeks) -> {
			if (!districtIds.contains(districtId)) {
				return;
			}
			weeks.forEach((week, ageGroups) -> {
				int sum = 0;
				for (Map.Entry<String, LevelCount> ageGroup : ageGroups.entrySet()) {
					int value = ageGroup.getValue().value();
					sum += value;
					districtsByAgeGroup.computeIfAbsent(week, w -> new HashMap<>()).merge(ageGroup.getKey(), value, Integer::sum);
				}
				result.byDistrict.computeIfAbsent(districtId, id -> new HashMap<>()).put(week, new int[] {
					sum,
					0 });
			});
		});

		// the region: its own report per age group, else the sum of its districts'
		Set<EpiWeek> weeks = new HashSet<>(regionLevel.keySet());
		weeks.addAll(districtsByAgeGroup.keySet());
		for (EpiWeek week : weeks) {
			Map<String, Integer> own = regionLevel.getOrDefault(week, Collections.emptyMap());
			Map<String, Integer> fromDistricts = districtsByAgeGroup.getOrDefault(week, Collections.emptyMap());
			Set<String> ageGroups = new HashSet<>(own.keySet());
			ageGroups.addAll(fromDistricts.keySet());
			int sum = 0;
			for (String ageGroup : ageGroups) {
				sum += own.containsKey(ageGroup) ? own.get(ageGroup) : fromDistricts.get(ageGroup);
			}
			result.region.put(week, new int[] {
				sum,
				0 });
		}
		return result;
	}

	/**
	 * @return true when the deployment holds at least one aggregate report of the disease in the epi year, anywhere: the
	 *         year counts for the usual range, as {@link #hasCases(Disease, int)} for cases
	 */
	private boolean hasAggregateReports(Disease disease, int year) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AggregateReport> report = cq.from(AggregateReport.class);
		cq.select(report.get(AggregateReport.ID));
		cq.where(cb.equal(report.get(AggregateReport.DISEASE), disease), cb.equal(report.get(AggregateReport.YEAR), year));
		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
	}

	/**
	 * @return the district's population projected with its growth rate, or null when it has no population data
	 */
	private Integer getProjectedPopulation(District district) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		PopulationDataCriteria criteria = new PopulationDataCriteria().ageGroupIsNull(true)
			.sexIsNull(true)
			.communityIsNull(true)
			.district(DistrictFacadeEjb.toReferenceDto(district));
		cq.where(populationDataService.buildCriteriaFilter(criteria, cb, root));
		List<PopulationData> rows = em.createQuery(cq).getResultList();
		if (rows.size() != 1 || rows.get(0).getPopulation() == null) {
			return null;
		}
		PopulationData row = rows.get(0);
		Float growthRate = district.getGrowthRate();
		return growthRate == null || growthRate == 0
			? row.getPopulation()
			: InfrastructureHelper.getProjectedPopulation(row.getPopulation(), row.getCollectionDate(), growthRate);
	}

	private static final class SeriesSettings {

		private final List<EpiWeek> shownWeeks;
		private final List<Integer> referenceYears;
		private final int minYears;
		private final int minWeeklyCases;

		private SeriesSettings(List<EpiWeek> shownWeeks, List<Integer> referenceYears, int minYears, int minWeeklyCases) {
			this.shownWeeks = shownWeeks;
			this.referenceYears = referenceYears;
			this.minYears = minYears;
			this.minWeeklyCases = minWeeklyCases;
		}
	}

	/**
	 * Aggregate counts by district and week, and the region's; index 0 of each array is the lab-confirmed count, index 1 is
	 * always 0 (aggregate reports have no report-date fallback).
	 */
	private static final class AggregateCounts {

		private final Map<Long, Map<EpiWeek, int[]>> byDistrict = new HashMap<>();
		private final Map<EpiWeek, int[]> region = new HashMap<>();
	}

	/**
	 * One district's aggregate count for a week and age group: its own report, if any, else the sum of its facilities' and
	 * points of entry's.
	 */
	private static final class LevelCount {

		private Integer own;
		private int lower;

		private int value() {
			return own != null ? own : lower;
		}
	}

	private static final class DiseaseYears {

		private final Disease disease;
		private final ClimateCaseCountSource source;
		private final Set<Integer> excludedYears;
		private final List<Integer> yearsWithCases;

		private DiseaseYears(Disease disease, ClimateCaseCountSource source, Set<Integer> excludedYears, List<Integer> yearsWithCases) {
			this.disease = disease;
			this.source = source;
			this.excludedYears = excludedYears;
			this.yearsWithCases = yearsWithCases;
		}
	}

	@LocalBean
	@Stateless
	public static class ClimateCaseFacadeEjbLocal extends ClimateCaseFacadeEjb {
	}
}
