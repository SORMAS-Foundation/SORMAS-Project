/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.dashboard.adverseeventsfollowingimmunization;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiClassification;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDashboardFilterDateType;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationStatus;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.dashboard.AefiDashboardCriteria;
import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.AefiChartData;
import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.AefiChartSeries;
import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.MapAefiDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.AefiInvestigationService;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.AefiQueryContext;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.AefiService;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AdverseEvents;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.Aefi;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AefiJoins;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class AefiDashboardService {

	private final Logger logger = LoggerFactory.getLogger(AefiDashboardService.class);

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public static final String SERIOUS_SERIES_COLOR = "#E04A5B";
	public static final String NON_SERIOUS_SERIES_COLOR = "#7565B8";
	public static final String MALE_SERIES_COLOR = "#544FC5";
	public static final String FEMALE_SERIES_COLOR = "#2CAFFE";

	@EJB
	private AefiService aefiService;
	@EJB
	private AefiInvestigationService aefiInvestigationService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;

	public Map<AefiType, Long> getAefiCountsByType(AefiDashboardCriteria dashboardCriteria) {

		Map<AefiType, Long> result = new HashMap<>();

		Map<YesNoUnknown, Long> dataMap = getAefiCountsByEnumProperty(Aefi.SERIOUS, YesNoUnknown.class, dashboardCriteria, null);

		result.put(AefiType.SERIOUS, dataMap.getOrDefault(YesNoUnknown.YES, 0L));
		result.put(AefiType.NON_SERIOUS, dataMap.getOrDefault(YesNoUnknown.NO, 0L) + dataMap.getOrDefault(YesNoUnknown.UNKNOWN, 0L));

		return result;
	}

	public Map<AefiInvestigationStatus, Map<String, String>> getAefiInvestigationCountsByInvestigationStatus(
		AefiDashboardCriteria dashboardCriteria) {

		Map<AefiInvestigationStatus, Map<String, String>> countsByInvestigationStatus = new HashMap<>();

		Map<String, String> defaultValuesMap = new HashMap<>();
		defaultValuesMap.put("total", "0");
		defaultValuesMap.put("percent", "0");

		for (AefiInvestigationStatus investigationStatus : AefiInvestigationStatus.values()) {
			countsByInvestigationStatus.put(investigationStatus, new HashMap<>(defaultValuesMap));
		}

		Disease disease = dashboardCriteria.getDisease();
		RegionReferenceDto regionReference = dashboardCriteria.getRegion();
		DistrictReferenceDto districtReference = dashboardCriteria.getDistrict();

		String whereConditions = createAefiNativeQueryFilter(dashboardCriteria);
		if (StringUtils.isBlank(whereConditions)) {
			whereConditions = " aefininvestigation.investigationstatus is not null";
		} else {
			whereConditions = whereConditions + " AND aefininvestigation.investigationstatus is not null";
		}

		//@formatter:off
		String queryString = "select aefininvestigation.investigationstatus, count(*) as total_status "
				+ " from adverseeventsfollowingimmunizationinvestigation aefininvestigation"
				+ "         join adverseeventsfollowingimmunization aefi on aefininvestigation.adverseeventsfollowingimmunization_id = aefi.id"
				+ "         join immunization on aefi.immunization_id = immunization.id"
				+ " where " + whereConditions
				+ " group by aefininvestigation.investigationstatus";
		//@formatter:on

		Query dataQuery = em.createNativeQuery(queryString);

		if (disease != null) {
			dataQuery.setParameter("disease", disease.name());
		}

		if (regionReference != null) {
			Region region = regionService.getByReferenceDto(regionReference);
			dataQuery.setParameter("responsibleregion_id", region.getId());
		}

		if (districtReference != null) {
			District district = districtService.getByReferenceDto(districtReference);
			dataQuery.setParameter("responsibledistrict_id", district.getId());
		}

		if (dashboardCriteria.getDateFrom() != null && dashboardCriteria.getDateTo() != null) {
			Date dateFrom = DateHelper.getStartOfDay(dashboardCriteria.getDateFrom());
			Date dateTo = DateHelper.getEndOfDay(dashboardCriteria.getDateTo());

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateFromStr = DateHelper.formatLocalDate(dateFrom, simpleDateFormat);
			String dateToStr = DateHelper.formatLocalDate(dateTo, simpleDateFormat);

			dataQuery.setParameter("dateFrom", dateFromStr);
			dataQuery.setParameter("dateTo", dateToStr);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = dataQuery.getResultList();

		int totalInvestigations = 0;
		for (Object[] result : resultList) {
			totalInvestigations += ((BigInteger) result[1]).intValue();
		}

		int statusTotal;
		int statusPercent;
		for (Object[] result : resultList) {
			statusTotal = ((BigInteger) result[1]).intValue();
			statusPercent = (totalInvestigations == 0) ? 0 : ((int) ((statusTotal * 100.0f) / totalInvestigations));
			countsByInvestigationStatus.get(AefiInvestigationStatus.valueOf((String) result[0])).put("total", String.valueOf(statusTotal));
			countsByInvestigationStatus.get(AefiInvestigationStatus.valueOf((String) result[0])).put("percent", String.valueOf(statusPercent));
		}

		return countsByInvestigationStatus;
	}

	public Map<AefiClassification, Map<String, String>> getAefiInvestigationCountsByAefiClassification(AefiDashboardCriteria dashboardCriteria) {

		Map<AefiClassification, Map<String, String>> countsByAefiClassitication = new HashMap<>();

		Map<String, String> defaultValuesMap = new HashMap<>();
		defaultValuesMap.put("total", "0");
		defaultValuesMap.put("percent", "0");

		for (AefiClassification aefiClassification : AefiClassification.values()) {
			countsByAefiClassitication.put(aefiClassification, new HashMap<>(defaultValuesMap));
		}

		Disease disease = dashboardCriteria.getDisease();
		RegionReferenceDto regionReference = dashboardCriteria.getRegion();
		DistrictReferenceDto districtReference = dashboardCriteria.getDistrict();

		String whereConditions = createAefiNativeQueryFilter(dashboardCriteria);
		if (StringUtils.isBlank(whereConditions)) {
			whereConditions = " aefininvestigation.adverseeventfollowingimmunizationclassification is not null";
		} else {
			whereConditions = whereConditions + " AND aefininvestigation.adverseeventfollowingimmunizationclassification is not null";
		}

		//@formatter:off
		String queryString = "select aefininvestigation.adverseeventfollowingimmunizationclassification, count(*) as total_classification "
				+ " from adverseeventsfollowingimmunizationinvestigation aefininvestigation"
				+ "         join adverseeventsfollowingimmunization aefi on aefininvestigation.adverseeventsfollowingimmunization_id = aefi.id"
				+ "         join immunization on aefi.immunization_id = immunization.id"
				+ " where " + whereConditions
				+ " group by aefininvestigation.adverseeventfollowingimmunizationclassification";
		//@formatter:on

		Query dataQuery = em.createNativeQuery(queryString);

		if (disease != null) {
			dataQuery.setParameter("disease", disease.name());
		}

		if (regionReference != null) {
			Region region = regionService.getByReferenceDto(regionReference);
			dataQuery.setParameter("responsibleregion_id", region.getId());
		}

		if (districtReference != null) {
			District district = districtService.getByReferenceDto(districtReference);
			dataQuery.setParameter("responsibledistrict_id", district.getId());
		}

		if (dashboardCriteria.getDateFrom() != null && dashboardCriteria.getDateTo() != null) {
			Date dateFrom = DateHelper.getStartOfDay(dashboardCriteria.getDateFrom());
			Date dateTo = DateHelper.getEndOfDay(dashboardCriteria.getDateTo());

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateFromStr = DateHelper.formatLocalDate(dateFrom, simpleDateFormat);
			String dateToStr = DateHelper.formatLocalDate(dateTo, simpleDateFormat);

			dataQuery.setParameter("dateFrom", dateFromStr);
			dataQuery.setParameter("dateTo", dateToStr);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = dataQuery.getResultList();

		int totalInvestigations = 0;
		for (Object[] result : resultList) {
			totalInvestigations += ((BigInteger) result[1]).intValue();
		}

		int classificationTotal;
		int classificationPercent;
		for (Object[] result : resultList) {
			classificationTotal = ((BigInteger) result[1]).intValue();
			classificationPercent = (totalInvestigations == 0) ? 0 : ((int) ((classificationTotal * 100.0f) / totalInvestigations));
			countsByAefiClassitication.get(AefiClassification.valueOf((String) result[0])).put("total", String.valueOf(classificationTotal));
			countsByAefiClassitication.get(AefiClassification.valueOf((String) result[0])).put("percent", String.valueOf(classificationPercent));
		}

		return countsByAefiClassitication;
	}

	public Map<Vaccine, Map<AefiType, Long>> getAefiCountsByVaccine(AefiDashboardCriteria dashboardCriteria) {

		Map<Vaccine, Map<AefiType, Long>> countsByVaccine = new HashMap<>();

		Disease disease = dashboardCriteria.getDisease();
		RegionReferenceDto regionReference = dashboardCriteria.getRegion();
		DistrictReferenceDto districtReference = dashboardCriteria.getDistrict();

		String whereConditions = createAefiNativeQueryFilter(dashboardCriteria);
		if (StringUtils.isBlank(whereConditions)) {
			whereConditions = " primaryvaccine.vaccinename is not null";
		} else {
			whereConditions = whereConditions + " AND primaryvaccine.vaccinename is not null";
		}

		//@formatter:off
		String queryString = "select primaryvaccine.vaccinename as vaccine_name "
							+ "     , count(CASE WHEN aefi.serious = 'YES' THEN 1 END) as serious"
							+ "     , count(CASE WHEN aefi.serious <> 'YES' THEN 1 END) as non_serious"
							+ " from adverseeventsfollowingimmunization aefi"
							+ "         join immunization on aefi.immunization_id = immunization.id"
							+ "         join vaccination primaryvaccine on aefi.primarysuspectvaccine_id = primaryvaccine.id"
							+ " where " + whereConditions
							+ " group by primaryvaccine.vaccinename"
							+ " order by primaryvaccine.vaccinename";
		//@formatter:on

		Query dataQuery = em.createNativeQuery(queryString);

		if (disease != null) {
			dataQuery.setParameter("disease", disease.name());
		}

		if (regionReference != null) {
			Region region = regionService.getByReferenceDto(regionReference);
			dataQuery.setParameter("responsibleregion_id", region.getId());
		}

		if (districtReference != null) {
			District district = districtService.getByReferenceDto(districtReference);
			dataQuery.setParameter("responsibledistrict_id", district.getId());
		}

		if (dashboardCriteria.getDateFrom() != null && dashboardCriteria.getDateTo() != null) {
			Date dateFrom = DateHelper.getStartOfDay(dashboardCriteria.getDateFrom());
			Date dateTo = DateHelper.getEndOfDay(dashboardCriteria.getDateTo());

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateFromStr = DateHelper.formatLocalDate(dateFrom, simpleDateFormat);
			String dateToStr = DateHelper.formatLocalDate(dateTo, simpleDateFormat);

			dataQuery.setParameter("dateFrom", dateFromStr);
			dataQuery.setParameter("dateTo", dateToStr);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = dataQuery.getResultList();

		Map<AefiType, Long> aefiTypeDataMap;
		for (Object[] result : resultList) {
			aefiTypeDataMap = new HashMap<>();
			aefiTypeDataMap.put(AefiType.SERIOUS, ((BigInteger) result[1]).longValue());
			aefiTypeDataMap.put(AefiType.NON_SERIOUS, ((BigInteger) result[2]).longValue());

			countsByVaccine.put(Vaccine.valueOf((String) result[0]), aefiTypeDataMap);
		}

		return countsByVaccine;
	}

	public AefiChartData getAefiByVaccineDoseChartData(AefiDashboardCriteria dashboardCriteria) {

		AefiChartData chartData = new AefiChartData();

		AefiChartSeries seriousSeries = new AefiChartSeries(AefiType.SERIOUS);
		seriousSeries.setColor(SERIOUS_SERIES_COLOR);
		AefiChartSeries nonSeriousSeries = new AefiChartSeries(AefiType.NON_SERIOUS);
		nonSeriousSeries.setColor(NON_SERIOUS_SERIES_COLOR);

		chartData.addSeries(seriousSeries);
		chartData.addSeries(nonSeriousSeries);

		Disease disease = dashboardCriteria.getDisease();
		RegionReferenceDto regionReference = dashboardCriteria.getRegion();
		DistrictReferenceDto districtReference = dashboardCriteria.getDistrict();

		String whereConditions = createAefiNativeQueryFilter(dashboardCriteria);
		if (StringUtils.isBlank(whereConditions)) {
			whereConditions = " primaryvaccine.vaccinedose is not null";
		} else {
			whereConditions = whereConditions + " AND primaryvaccine.vaccinedose is not null";
		}

		//@formatter:off
		String queryString = "select primaryvaccine.vaccinedose as vaccine_dose "
				+ "     , count(CASE WHEN aefi.serious = 'YES' THEN 1 END) as serious"
				+ "     , count(CASE WHEN aefi.serious <> 'YES' THEN 1 END) as non_serious"
				+ " from adverseeventsfollowingimmunization aefi"
				+ "         join immunization on aefi.immunization_id = immunization.id"
				+ "         join vaccination primaryvaccine on aefi.primarysuspectvaccine_id = primaryvaccine.id"
				+ " where " + whereConditions
				+ " group by primaryvaccine.vaccinedose"
				+ " order by primaryvaccine.vaccinedose";
		//@formatter:on

		Query dataQuery = em.createNativeQuery(queryString);

		if (disease != null) {
			dataQuery.setParameter("disease", disease.name());
		}

		if (regionReference != null) {
			Region region = regionService.getByReferenceDto(regionReference);
			dataQuery.setParameter("responsibleregion_id", region.getId());
		}

		if (districtReference != null) {
			District district = districtService.getByReferenceDto(districtReference);
			dataQuery.setParameter("responsibledistrict_id", district.getId());
		}

		if (dashboardCriteria.getDateFrom() != null && dashboardCriteria.getDateTo() != null) {
			Date dateFrom = DateHelper.getStartOfDay(dashboardCriteria.getDateFrom());
			Date dateTo = DateHelper.getEndOfDay(dashboardCriteria.getDateTo());

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateFromStr = DateHelper.formatLocalDate(dateFrom, simpleDateFormat);
			String dateToStr = DateHelper.formatLocalDate(dateTo, simpleDateFormat);

			dataQuery.setParameter("dateFrom", dateFromStr);
			dataQuery.setParameter("dateTo", dateToStr);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = dataQuery.getResultList();

		for (Object[] result : resultList) {
			chartData.addXAxisCategory(result[0]);
			seriousSeries.addData(result[1].toString());
			nonSeriousSeries.addData(result[2].toString());
		}

		return chartData;
	}

	public AefiChartData getAefiEventsByGenderChartData(AefiDashboardCriteria dashboardCriteria) {

		AefiChartData chartData = new AefiChartData();

		AefiChartSeries maleSeries = new AefiChartSeries(Sex.MALE);
		maleSeries.setColor(MALE_SERIES_COLOR);
		AefiChartSeries femaleSeries = new AefiChartSeries(Sex.FEMALE);
		femaleSeries.setColor(FEMALE_SERIES_COLOR);

		chartData.addSeries(maleSeries);
		chartData.addSeries(femaleSeries);

		Disease disease = dashboardCriteria.getDisease();
		RegionReferenceDto regionReference = dashboardCriteria.getRegion();
		DistrictReferenceDto districtReference = dashboardCriteria.getDistrict();

		List<String> adverseEventsList = new ArrayList<>();
		adverseEventsList.add(AdverseEvents.SEVERE_LOCAL_REACTION);
		adverseEventsList.add(AdverseEvents.SEIZURES);
		adverseEventsList.add(AdverseEvents.ABSCESS);
		adverseEventsList.add(AdverseEvents.SEPSIS);
		adverseEventsList.add(AdverseEvents.ENCEPHALOPATHY);
		adverseEventsList.add(AdverseEvents.TOXIC_SHOCK_SYNDROME);
		adverseEventsList.add(AdverseEvents.THROMBOCYTOPENIA);
		adverseEventsList.add(AdverseEvents.ANAPHYLAXIS);
		adverseEventsList.add(AdverseEvents.FEVERISH_FEELING);

		String queryString;
		String queryFilter = createAefiNativeQueryFilter(dashboardCriteria);
		String whereConditions;
		String adverseEventCondition;
		String lowerCaseAdverseEvent;

		for (String adverseEvent : adverseEventsList) {

			whereConditions = queryFilter;
			lowerCaseAdverseEvent = adverseEvent.toLowerCase();
			adverseEventCondition = " adverseevents." + lowerCaseAdverseEvent + " = 'YES'";
			if (StringUtils.isBlank(whereConditions)) {
				whereConditions = adverseEventCondition;
			} else {
				whereConditions = whereConditions + " AND " + adverseEventCondition;
			}

			//@formatter:off
			queryString = "select adverseevents." + lowerCaseAdverseEvent + " as " + lowerCaseAdverseEvent
					+ "     , count(CASE WHEN person.sex = 'MALE' THEN 1 END) as total_male"
					+ "     , count(CASE WHEN person.sex = 'FEMALE' THEN 1 END) as total_female"
					+ " from adverseeventsfollowingimmunization aefi"
					+ "         join immunization on aefi.immunization_id = immunization.id"
					+ "         join person on immunization.person_id = person.id"
					+ "         join adverseevents on aefi.adverseevents_id = adverseevents.id"
					+ " where " + whereConditions
					+ " group by adverseevents." + lowerCaseAdverseEvent;
			//@formatter:on

			Query dataQuery = em.createNativeQuery(queryString);

			if (disease != null) {
				dataQuery.setParameter("disease", disease.name());
			}

			if (regionReference != null) {
				Region region = regionService.getByReferenceDto(regionReference);
				dataQuery.setParameter("responsibleregion_id", region.getId());
			}

			if (districtReference != null) {
				District district = districtService.getByReferenceDto(districtReference);
				dataQuery.setParameter("responsibledistrict_id", district.getId());
			}

			if (dashboardCriteria.getDateFrom() != null && dashboardCriteria.getDateTo() != null) {
				Date dateFrom = DateHelper.getStartOfDay(dashboardCriteria.getDateFrom());
				Date dateTo = DateHelper.getEndOfDay(dashboardCriteria.getDateTo());

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String dateFromStr = DateHelper.formatLocalDate(dateFrom, simpleDateFormat);
				String dateToStr = DateHelper.formatLocalDate(dateTo, simpleDateFormat);

				dataQuery.setParameter("dateFrom", dateFromStr);
				dataQuery.setParameter("dateTo", dateToStr);
			}

			chartData.addXAxisCategory(adverseEvent);

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = dataQuery.getResultList();

			if (!resultList.isEmpty()) {
				Object[] firstResult = resultList.get(0);
				maleSeries.addData("-" + firstResult[1].toString());
				femaleSeries.addData(firstResult[2].toString());
			} else {
				maleSeries.addData("0");
				femaleSeries.addData("0");
			}
		}

		return chartData;
	}

	private <T extends Enum<?>> Map<T, Long> getAefiCountsByEnumProperty(
		String property,
		Class<T> propertyType,
		AefiDashboardCriteria dashboardCriteria,
		BiFunction<CriteriaBuilder, Root<Aefi>, Predicate> additionalFilters) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<Aefi> aefiRoot = cq.from(Aefi.class);
		final Path<Object> groupingProperty = aefiRoot.get(property);

		cq.multiselect(groupingProperty, cb.count(aefiRoot));

		final Predicate criteriaFilter = createAefiFilter(new AefiQueryContext(cb, cq, aefiRoot), dashboardCriteria);
		cq.where(CriteriaBuilderHelper.and(cb, criteriaFilter, additionalFilters != null ? additionalFilters.apply(cb, aefiRoot) : null));

		cq.groupBy(groupingProperty);

		return QueryHelper.getResultList(em, cq, null, null, Function.identity())
			.stream()
			.collect(Collectors.toMap(t -> propertyType.cast(t.get(0)), t -> (Long) t.get(1)));
	}

	public Long countAefiForMap(AefiDashboardCriteria criteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<Aefi> aefiRoot = cq.from(Aefi.class);

		AefiQueryContext aefiQueryContext = new AefiQueryContext(cb, cq, aefiRoot);
		AefiJoins joins = aefiQueryContext.getJoins();

		cq.select(cb.count(aefiRoot));

		final Predicate criteriaFilter = createAefiFilter(aefiQueryContext, criteria);

		cq.where(CriteriaBuilderHelper.and(cb, criteriaFilter));

		return QueryHelper.getSingleResult(em, cq);
	}

	public List<MapAefiDto> getAefiForMap(AefiDashboardCriteria criteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<MapAefiDto> cq = cb.createQuery(MapAefiDto.class);
		final Root<Aefi> aefiRoot = cq.from(Aefi.class);

		AefiQueryContext aefiQueryContext = new AefiQueryContext(cb, cq, aefiRoot);
		AefiJoins joins = aefiQueryContext.getJoins();

		Join<Aefi, Facility> aefiFacility = joins.getHealthFacility();
		Join<Immunization, Facility> immunizationFacility = joins.getImmunizationJoins().getHealthFacility();
		Join<Person, Location> immunizationPersonAddress = joins.getImmunizationJoins().getPersonJoins().getAddress();

		cq.multiselect(
			aefiFacility.get(Facility.LATITUDE),
			aefiFacility.get(Facility.LONGITUDE),
			immunizationFacility.get(Facility.LATITUDE),
			immunizationFacility.get(Facility.LONGITUDE),
			immunizationPersonAddress.get(Facility.LATITUDE),
			immunizationPersonAddress.get(Facility.LONGITUDE),
			aefiRoot.get(Aefi.SERIOUS));

		final Predicate criteriaFilter = createAefiFilter(aefiQueryContext, criteria);

		cq.where(CriteriaBuilderHelper.and(cb, criteriaFilter));

		return QueryHelper.getResultList(em, cq, null, null);
	}

	private <T extends AbstractDomainObject> Predicate createAefiFilter(AefiQueryContext queryContext, AefiDashboardCriteria criteria) {
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = queryContext.getQuery();
		From<?, Aefi> aefiRoot = queryContext.getRoot();
		AefiJoins joins = queryContext.getJoins();

		Predicate filter = aefiService.createUserFilter(queryContext);

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			aefiService.buildCriteriaFilter(
				new AefiCriteria().disease(criteria.getDisease())
					.region(criteria.getRegion())
					.district(criteria.getDistrict())
					.aefiType(criteria.getAefiType()),
				queryContext));

		if (criteria.getDateFrom() != null && criteria.getDateTo() != null) {
			final Predicate dateFilter;
			Date dateFrom = DateHelper.getStartOfDay(criteria.getDateFrom());
			Date dateTo = DateHelper.getEndOfDay(criteria.getDateTo());

			AefiDashboardFilterDateType aefiDashboardFilterDateType = criteria.getAefiDashboardFilterDateType() != null
				? criteria.getAefiDashboardFilterDateType()
				: AefiDashboardFilterDateType.REPORT_DATE;

			switch (aefiDashboardFilterDateType) {
			case REPORT_DATE:
				dateFilter = cb.between(aefiRoot.get(Aefi.REPORT_DATE), dateFrom, dateTo);
				break;
			case START_DATE:
				dateFilter = cb.between(aefiRoot.get(Aefi.START_DATE_TIME), dateFrom, dateTo);
				break;
			default:
				throw new RuntimeException("Unhandled date type [" + aefiDashboardFilterDateType + "]");
			}

			filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		}

		return CriteriaBuilderHelper.and(
			cb,
			filter,
			// Exclude deleted adverse events. Archived adverse events should stay included
			cb.isFalse(aefiRoot.get(Aefi.DELETED)));
	}

	private String createAefiNativeQueryFilter(AefiDashboardCriteria criteria) {

		List<String> whereConditions = new ArrayList<>();

		if (criteria.getDisease() != null) {
			whereConditions.add("immunization.disease = :disease");
		}

		if (criteria.getRegion() != null) {
			whereConditions.add("immunization.responsibleregion_id = :responsibleregion_id");
		}

		if (criteria.getDistrict() != null) {
			whereConditions.add("immunization.responsibledistrict_id = :responsibledistrict_id");
		}

		if (criteria.getDateFrom() != null && criteria.getDateTo() != null) {
			AefiDashboardFilterDateType aefiDashboardFilterDateType = criteria.getAefiDashboardFilterDateType() != null
				? criteria.getAefiDashboardFilterDateType()
				: AefiDashboardFilterDateType.REPORT_DATE;

			switch (aefiDashboardFilterDateType) {
			case REPORT_DATE:
				whereConditions
					.add("(cast(aefi.reportdate as date) >= cast(:dateFrom as date) and cast(aefi.reportdate as date) <= cast(:dateTo as date))");
				break;
			case START_DATE:
				whereConditions.add(
					"(cast(aefi.startdatetime as date) >= cast(:dateFrom as date) and cast(aefi.startdatetime as date) <= cast(:dateTo as date))");
				break;
			default:
				throw new RuntimeException("Unhandled date type [" + aefiDashboardFilterDateType + "]");
			}
		}

		return String.join(" AND ", whereConditions);
	}
}
