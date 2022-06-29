package de.symeda.sormas.backend.report;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.AggregateReportFacade;
import de.symeda.sormas.api.report.AggregateReportGroupingLevel;
import de.symeda.sormas.api.report.AggregatedCaseCountDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AgeGroupUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import org.docx4j.wml.P;

@Stateless(name = "AggregateReportFacade")
@RolesAllowed(UserRight._AGGREGATE_REPORT_VIEW)
public class AggregateReportFacadeEjb implements AggregateReportFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private AggregateReportService service;
	@EJB
	private UserService userService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;

	@Override
	public List<AggregateReportDto> getAllAggregateReportsAfter(Date date) {

		return service.getAllAfter(date).stream().map(r -> toDto(r)).collect(Collectors.toList());
	}

	@Override
	public List<AggregateReportDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(r -> toDto(r)).collect(Collectors.toList());
	}

	@Override
	@RolesAllowed(UserRight._AGGREGATE_REPORT_EDIT)
	public AggregateReportDto saveAggregateReport(@Valid AggregateReportDto dto) {

		if (dto.getAgeGroup() != null && dto.getAgeGroup().isEmpty()) {
			AgeGroupUtils.validateAgeGroup(dto.getAgeGroup());
		}
		AggregateReport report = fromDto(dto, true);
		service.ensurePersisted(report);
		return toDto(report);
	}

	@Override
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getAllUuids();
	}

	public static AggregateReportDto toDto(AggregateReport source) {

		if (source == null) {
			return null;
		}

		AggregateReportDto target = new AggregateReportDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setPointOfEntry(PointOfEntryFacadeEjb.toReferenceDto(source.getPointOfEntry()));
		target.setNewCases(source.getNewCases());
		target.setLabConfirmations(source.getLabConfirmations());
		target.setDeaths(source.getDeaths());
		target.setAgeGroup(source.getAgeGroup());

		return target;
	}

	@Override
	public List<AggregatedCaseCountDto> getIndexList(AggregateReportCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AggregatedCaseCountDto> cq = cb.createQuery(AggregatedCaseCountDto.class);
		Root<AggregateReport> root = cq.from(AggregateReport.class);

		Predicate filter = service.createUserFilter(cb, cq, root);
		if (criteria != null) {
			Predicate criteriaFilter = service.createCriteriaFilter(criteria, cb, cq, root);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		List<Selection<?>> selectionList = new ArrayList<>(
			Arrays.asList(
				root.get(AggregateReport.DISEASE),
				cb.sum(root.get(AggregateReport.NEW_CASES)),
				cb.sum(root.get(AggregateReport.LAB_CONFIRMATIONS)),
				cb.sum(root.get(AggregateReport.DEATHS)),
				root.get(AggregateReport.YEAR),
				root.get(AggregateReport.EPI_WEEK),
				root.get(AggregateReport.AGE_GROUP)));

		List<Expression<?>> expressions = new ArrayList<>(
			Arrays.asList(
				root.get(AggregateReport.DISEASE),
				root.get(AggregateReport.YEAR),
				root.get(AggregateReport.EPI_WEEK),
				root.get(AggregateReport.AGE_GROUP)));

		AggregateReportGroupingLevel groupingLevel = null;

		if (criteria != null && criteria.getAggregateReportGroupingLevel() != null) {
			groupingLevel = criteria.getAggregateReportGroupingLevel();

			if (groupingLevel.equals(AggregateReportGroupingLevel.REGION)) {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(
						cb.isNotNull(root.get(AggregateReport.REGION)),
						cb.isNotNull(root.get(AggregateReport.DISTRICT)),
						cb.isNotNull(root.get(AggregateReport.HEALTH_FACILITY)),
						cb.isNotNull(root.get(AggregateReport.POINT_OF_ENTRY))));
			}
			if (groupingLevel.equals(AggregateReportGroupingLevel.DISTRICT)) {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(
						cb.isNotNull(root.get(AggregateReport.DISTRICT)),
						cb.isNotNull(root.get(AggregateReport.HEALTH_FACILITY)),
						cb.isNotNull(root.get(AggregateReport.POINT_OF_ENTRY))));
			}
			if (groupingLevel.equals(AggregateReportGroupingLevel.HEALTH_FACILITY)) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.isNotNull(root.get(AggregateReport.HEALTH_FACILITY))));
			}
			if (groupingLevel.equals(AggregateReportGroupingLevel.POINT_OF_ENTRY)) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.isNotNull(root.get(AggregateReport.POINT_OF_ENTRY))));
			}

//			if (shouldIncludeRegion(groupingLevel)) {
				Join<AggregateReport, Region> regionJoin = root.join(AggregateReport.REGION, JoinType.LEFT);
				List<Path<Object>> regionPath = Arrays.asList(regionJoin.get(Region.NAME), regionJoin.get(Region.ID));
				expressions.addAll(regionPath);
				selectionList.addAll(regionPath);
//			}

//			if (shouldIncludeDistrict(groupingLevel)) {
				Join<AggregateReport, District> districtJoin = root.join(AggregateReport.DISTRICT, JoinType.LEFT);
				List<Path<Object>> districtPath = Arrays.asList(districtJoin.get(District.NAME), districtJoin.get(District.ID));
				expressions.addAll(districtPath);
				selectionList.addAll(districtPath);
//			}

//			if (shouldIncludeHealthFacility(groupingLevel)) {
				Join<AggregateReport, Facility> facilityJoin = root.join(AggregateReport.HEALTH_FACILITY, JoinType.LEFT);
				List<Path<Object>> facilityPath = Arrays.asList(facilityJoin.get(Facility.NAME), facilityJoin.get(Facility.ID));
				expressions.addAll(facilityPath);
				selectionList.addAll(facilityPath);
//			}

//			if (shouldIncludePointOfEntry(groupingLevel)) {
				Join<AggregateReport, PointOfEntry> pointOfEntryJoin = root.join(AggregateReport.POINT_OF_ENTRY, JoinType.LEFT);
				List<Path<Object>> pointOfEntryPath = Arrays.asList(pointOfEntryJoin.get(PointOfEntry.ID), pointOfEntryJoin.get(PointOfEntry.NAME));
				expressions.addAll(pointOfEntryPath);
				selectionList.addAll(pointOfEntryPath);
//			}
		}

		selectionList.add(root.get(AggregateReport.CHANGE_DATE));
		expressions.add(root.get(AggregateReport.CHANGE_DATE));
		cq.multiselect(selectionList);

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(expressions);

		List<AggregatedCaseCountDto> queryResult = em.createQuery(cq).getResultList();
		Map<Disease, AggregatedCaseCountDto> reportSet = new HashMap<>();

		List<AggregatedCaseCountDto> resultList = new ArrayList<>();
		Map<AggregatedCaseCountDto, List<AggregatedCaseCountDto>> subJurisdictionsToBeMerged = new HashMap<>();

		final AggregateReportGroupingLevel finalGroupingLevel = groupingLevel;
		for (AggregatedCaseCountDto aggregatedCaseCountDto : queryResult) {

			Optional<AggregatedCaseCountDto> similarOptional =
				resultList.stream().filter(a -> aggregatedCaseCountDto.similar(a, finalGroupingLevel)).findAny();
			if (similarOptional.isPresent()) {
				AggregatedCaseCountDto similar = similarOptional.get();
				if (aggregatedCaseCountDto.equalJurisdiction(similar)){
					if (aggregatedCaseCountDto.getChangeDate().getTime() > similar.getChangeDate().getTime()) {
						resultList.remove(similar);
						resultList.add(aggregatedCaseCountDto);
					}
				} else {
					if (aggregatedCaseCountDto.higherJurisdictionLevel(similar)) {
						resultList.remove(similar);
						resultList.add(aggregatedCaseCountDto);
					} else if (aggregatedCaseCountDto.sameJurisdictionLevel(similar)){
						if (subJurisdictionsToBeMerged.containsKey(similar)) {
							List<AggregatedCaseCountDto> mergeListDtos = subJurisdictionsToBeMerged.get(similar);
							Optional<AggregatedCaseCountDto> mergeDtoEqualJurisdictionOptional =
								mergeListDtos.stream().filter(a -> a.equalJurisdiction(aggregatedCaseCountDto)).findAny();
							if (mergeDtoEqualJurisdictionOptional.isPresent()){
								AggregatedCaseCountDto mergeDuplicate = mergeDtoEqualJurisdictionOptional.get();
								if (aggregatedCaseCountDto.getChangeDate().getTime() > mergeDuplicate.getChangeDate().getTime()) {
									mergeListDtos.remove(mergeDuplicate);
									mergeListDtos.add(aggregatedCaseCountDto);
								}
							} else {
								mergeListDtos.add(aggregatedCaseCountDto);
							}
						} else {
							List<AggregatedCaseCountDto> arrayList = new ArrayList();
							arrayList.add(aggregatedCaseCountDto);
							subJurisdictionsToBeMerged.put(similar, arrayList);
						}
					}
				}
			} else {
				resultList.add(aggregatedCaseCountDto);
			}
		}

		subJurisdictionsToBeMerged.forEach((aggregatedCaseCountDto, aggregatedCaseCountDtos) -> {
			resultList.remove(aggregatedCaseCountDto);
			aggregatedCaseCountDto.setNewCases(aggregatedCaseCountDtos.stream().mapToLong(a->a.getNewCases()).sum() + aggregatedCaseCountDto.getNewCases());
			aggregatedCaseCountDto.setDeaths(aggregatedCaseCountDtos.stream().mapToLong(a->a.getDeaths()).sum() + aggregatedCaseCountDto.getDeaths());
			aggregatedCaseCountDto.setLabConfirmations(aggregatedCaseCountDtos.stream().mapToLong(a->a.getLabConfirmations()).sum() + aggregatedCaseCountDto.getLabConfirmations());
			if (aggregatedCaseCountDto.getHealthFacilityId() != null || aggregatedCaseCountDto.getPointOfEntryId() != null) {
				aggregatedCaseCountDto.setHealthFacilityId(null);
				aggregatedCaseCountDto.setHealthFacilityName(null);
				aggregatedCaseCountDto.setPointOfEntryId(null);
				aggregatedCaseCountDto.setPointOfEntryName(null);
			} else if (aggregatedCaseCountDto.getDistrictId() != null && aggregatedCaseCountDto.getPointOfEntryId() == null && aggregatedCaseCountDto.getHealthFacilityId() == null) {
				aggregatedCaseCountDto.setDistrictId(null);
				aggregatedCaseCountDto.setDistrictName(null);
			}
			resultList.add(aggregatedCaseCountDto);
		});

		for (AggregatedCaseCountDto result : resultList) {
			reportSet.put(result.getDisease(), result);
		}

		Region selectedRegion = null;
		District selectedDistrict = null;
		Facility selectedFacility = null;
		PointOfEntry selectedPoindOfEntry = null;

		if (criteria != null) {
			selectedRegion = criteria.getRegion() != null ? regionService.getByUuid(criteria.getRegion().getUuid()) : null;
			selectedDistrict = criteria.getDistrict() != null ? districtService.getByUuid(criteria.getDistrict().getUuid()) : null;
			selectedFacility = criteria.getHealthFacility() != null ? facilityService.getByUuid(criteria.getHealthFacility().getUuid()) : null;
			selectedPoindOfEntry = criteria.getPointOfEntry() != null ? pointOfEntryService.getByUuid(criteria.getPointOfEntry().getUuid()) : null;
		}

		if (criteria != null && criteria.getShowZeroRowsForGrouping()) {
			List<EpiWeek> epiWeekList = DateHelper.createEpiWeekListFromInterval(criteria.getEpiWeekFrom(), criteria.getEpiWeekTo());
			if (criteria.getDisease() == null) {
				for (Disease disease : diseaseConfigurationFacade.getAllDiseases(true, false, false)) {
					if (!reportSet.containsKey(disease)) {
						for (EpiWeek epiWeek : epiWeekList) {
							addZeroRowToList(resultList, selectedRegion, selectedDistrict, selectedFacility, selectedPoindOfEntry, disease, epiWeek);
						}
					} else {
						for (EpiWeek epiWeek : epiWeekList) {
							Optional<AggregatedCaseCountDto> resultForEpiWeekAndDisease = resultList.stream()
								.filter(
									report -> report.getDisease().equals(disease)
										&& report.getYear() == epiWeek.getYear()
										&& report.getEpiWeek().equals(epiWeek.getWeek()))
								.findAny();
							if (!resultForEpiWeekAndDisease.isPresent()) {
								addZeroRowToList(resultList, selectedRegion, selectedDistrict, selectedFacility, selectedPoindOfEntry, disease, epiWeek);
							}
						}
					}
				}
			} else {
				if (!reportSet.containsKey(criteria.getDisease())) {
					for (EpiWeek epiWeek : epiWeekList) {
						addZeroRowToList(
							resultList,
							selectedRegion,
							selectedDistrict,
							selectedFacility,
							selectedPoindOfEntry,
							criteria.getDisease(),
							epiWeek);
					}
				} else {
					for (EpiWeek epiWeek : epiWeekList) {
						Optional<AggregatedCaseCountDto> resultForEpiWeekAndDisease = resultList.stream()
							.filter(
								report -> report.getDisease().equals(criteria.getDisease())
									&& report.getYear() == epiWeek.getYear()
									&& report.getEpiWeek().equals(epiWeek.getWeek()))
							.findAny();
						if (!resultForEpiWeekAndDisease.isPresent()) {
							addZeroRowToList(
								resultList,
								selectedRegion,
								selectedDistrict,
								selectedFacility,
								selectedPoindOfEntry,
								criteria.getDisease(),
								epiWeek);
						}
					}
				}
			}
		}

		resultList.sort(
			Comparator.comparing(AggregatedCaseCountDto::getDisease, Comparator.nullsFirst(Comparator.comparing(Disease::toString)))
				.thenComparing(AggregatedCaseCountDto::getYear, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(AggregatedCaseCountDto::getEpiWeek, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(AggregatedCaseCountDto::getRegionName, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(AggregatedCaseCountDto::getDistrictName, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(AggregatedCaseCountDto::getHealthFacilityName, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(AggregatedCaseCountDto::getPointOfEntryName, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(
					r -> r.getAgeGroup() != null
						? r.getAgeGroup().split("_")[0].replaceAll("[^a-zA-Z]", StringUtils.EMPTY).toUpperCase()
						: StringUtils.EMPTY)
				.thenComparing(
					r -> r.getAgeGroup() != null ? Integer.parseInt(r.getAgeGroup().split("_")[0].replaceAll("[^0-9]", StringUtils.EMPTY)) : 0));
		return resultList;
	}

	@Override
	public List<AggregateReportDto> getAggregateReports(AggregateReportCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AggregateReport> cq = cb.createQuery(AggregateReport.class);
		Root<AggregateReport> root = cq.from(AggregateReport.class);

		Predicate filter = service.createUserFilter(cb, cq, root);
		if (criteria != null) {
			Predicate criteriaFilter = service.createCriteriaFilter(criteria, cb, cq, root);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		List<AggregateReport> resultList = em.createQuery(cq).getResultList();

		List<AggregateReportDto> aggregateReportDtoList =
			resultList.stream().map(aggregateReport -> toDto(aggregateReport)).collect(Collectors.toList());

		Set<AggregateReportDto> onlyDuplicatesReturnList = new HashSet<>();

		aggregateReportDtoList.forEach(r -> {
			List<AggregateReportDto> duplicateReports =
				aggregateReportDtoList.stream().filter(r2 -> !r.equals(r2) && isDuplicate(r, r2)).collect(Collectors.toList());
			if (duplicateReports.stream().anyMatch(r2 -> r.getChangeDate().before(r2.getChangeDate()))) {
				r.setDuplicate(true);
				onlyDuplicatesReturnList.add(r);
			}
			onlyDuplicatesReturnList.addAll(duplicateReports);
		});

		if (criteria != null && criteria.getShowOnlyDuplicates()) {
			List<AggregateReportDto> aggregateReportDtos = new ArrayList<>(onlyDuplicatesReturnList);
			aggregateReportDtos.sort(getAggregateReportsComparator());
			return aggregateReportDtos;
		}

		aggregateReportDtoList.sort(getAggregateReportsComparator());
		return aggregateReportDtoList;
	}

	private Comparator<AggregateReportDto> getAggregateReportsComparator() {
		return Comparator.comparing(AggregateReportDto::getDisease, Comparator.nullsFirst(Comparator.comparing(Disease::toString)))
			.thenComparing(AggregateReportDto::getYear, Comparator.nullsFirst(Comparator.naturalOrder()))
			.thenComparing(AggregateReportDto::getEpiWeek, Comparator.nullsFirst(Comparator.naturalOrder()))
			.thenComparing(
				r -> r.getAgeGroup() != null
					? r.getAgeGroup().split("_")[0].replaceAll("[^a-zA-Z]", StringUtils.EMPTY).toUpperCase()
					: StringUtils.EMPTY)
			.thenComparing(r -> r.getAgeGroup() != null ? Integer.parseInt(r.getAgeGroup().split("_")[0].replaceAll("[^0-9]", StringUtils.EMPTY)) : 0)
			.thenComparing(AggregateReportDto::getRegion, Comparator.nullsFirst(Comparator.naturalOrder()))
			.thenComparing(AggregateReportDto::getDistrict, Comparator.nullsFirst(Comparator.naturalOrder()))
			.thenComparing(AggregateReportDto::getHealthFacility, Comparator.nullsFirst(Comparator.naturalOrder()))
			.thenComparing(AggregateReportDto::getPointOfEntry, Comparator.nullsFirst(Comparator.naturalOrder()))
			.thenComparing(AggregateReportDto::getChangeDate);
	}

	private boolean isDuplicate(AggregateReportDto aggregateReportDto, AggregateReportDto duplicateCandidate) {

		return Stream
			.<Function<AggregateReportDto, Object>> of(
				AggregateReportDto::getDisease,
				AggregateReportDto::getRegion,
				AggregateReportDto::getDistrict,
				AggregateReportDto::getHealthFacility,
				AggregateReportDto::getPointOfEntry,
				AggregateReportDto::getYear,
				AggregateReportDto::getEpiWeek,
				AggregateReportDto::getAgeGroup)
			.allMatch(m -> DataHelper.equal(m.apply(aggregateReportDto), m.apply(duplicateCandidate)));

	}

	private void addZeroRowToList(
		List<AggregatedCaseCountDto> resultList,
		Region selectedRegion,
		District selectedDistrict,
		Facility selectedFacility,
		PointOfEntry selectedPoindOfEntry,
		Disease disease,
		EpiWeek epiWeek) {

		String regionName = selectedRegion != null ? selectedRegion.getName() : null;
		String districtName = selectedDistrict != null ? selectedDistrict.getName() : null;
		String healthFacilityName = selectedFacility != null ? selectedFacility.getName() : null;
		String pointOfEntryName = selectedPoindOfEntry != null ? selectedPoindOfEntry.getName() : null;

		resultList.add(
			new AggregatedCaseCountDto(
				disease,
				0L,
				0L,
				0L,
				epiWeek.getYear(),
				epiWeek.getWeek(),
				"",
				regionName,
				districtName,
				healthFacilityName,
				pointOfEntryName, new Date()));
	}

	private boolean shouldIncludeRegion(AggregateReportGroupingLevel groupingLevel) {
		return AggregateReportGroupingLevel.REGION.equals(groupingLevel)
			|| AggregateReportGroupingLevel.DISTRICT.equals(groupingLevel)
			|| AggregateReportGroupingLevel.HEALTH_FACILITY.equals(groupingLevel)
			|| AggregateReportGroupingLevel.POINT_OF_ENTRY.equals(groupingLevel);
	}

	private boolean shouldIncludeDistrict(AggregateReportGroupingLevel groupingLevel) {
		return AggregateReportGroupingLevel.DISTRICT.equals(groupingLevel)
			|| AggregateReportGroupingLevel.HEALTH_FACILITY.equals(groupingLevel)
			|| AggregateReportGroupingLevel.POINT_OF_ENTRY.equals(groupingLevel);
	}

	private boolean shouldIncludeHealthFacility(AggregateReportGroupingLevel groupingLevel) {
		return AggregateReportGroupingLevel.HEALTH_FACILITY.equals(groupingLevel);
	}

	private boolean shouldIncludePointOfEntry(AggregateReportGroupingLevel groupingLevel) {
		return AggregateReportGroupingLevel.POINT_OF_ENTRY.equals(groupingLevel);
	}

	@Override
	public List<AggregateReportDto> getList(AggregateReportCriteria criteria) {

		User user = userService.getCurrentUser();
		return service.findBy(criteria, user).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	public AggregateReport fromDto(@NotNull AggregateReportDto source, boolean checkChangeDate) {

		AggregateReport target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), AggregateReport::new, checkChangeDate);

		target.setDisease(source.getDisease());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setPointOfEntry(pointOfEntryService.getByReferenceDto(source.getPointOfEntry()));
		target.setNewCases(source.getNewCases());
		target.setLabConfirmations(source.getLabConfirmations());
		target.setDeaths(source.getDeaths());
		target.setAgeGroup(source.getAgeGroup());

		return target;
	}

	@Override
	@RolesAllowed(UserRight._AGGREGATE_REPORT_EDIT)
	public void deleteReport(String reportUuid) {

		if (!userService.hasRight(UserRight.AGGREGATE_REPORT_EDIT)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to edit aggregate reports.");
		}

		AggregateReport aggregateReport = service.getByUuid(reportUuid);
		service.deletePermanent(aggregateReport);
	}

	@Override
	public long countWithCriteria(AggregateReportCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AggregateReport> root = cq.from(AggregateReport.class);

		Predicate filter = service.createUserFilter(cb, cq, root);
		if (criteria != null) {
			Predicate criteriaFilter = service.createCriteriaFilter(criteria, cb, cq, root);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));

		return em.createQuery(cq).getSingleResult();
	}

	@LocalBean
	@Stateless
	public static class AggregateReportFacadeEjbLocal extends AggregateReportFacadeEjb {

	}
}
