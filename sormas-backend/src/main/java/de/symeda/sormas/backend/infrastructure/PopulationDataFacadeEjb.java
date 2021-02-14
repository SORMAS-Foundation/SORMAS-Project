package de.symeda.sormas.backend.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.PopulationDataFacade;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.region.Area;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "PopulationDataFacade")
public class PopulationDataFacadeEjb implements PopulationDataFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private PopulationDataService service;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;

	@Override
	public Integer getRegionPopulation(String regionUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);

		PopulationDataCriteria criteria =
			new PopulationDataCriteria().ageGroupIsNull(true)
				.sexIsNull(true)
				.districtIsNull(true)
				.region(new RegionReferenceDto(regionUuid, null, null));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		cq.where(filter);
		cq.select(root.get(PopulationData.POPULATION));

		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Integer getProjectedRegionPopulation(String regionUuid) {

		Float growthRate = regionService.getByUuid(regionUuid).getGrowthRate();

		if (growthRate == null || growthRate == 0) {
			return getRegionPopulation(regionUuid);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);

		PopulationDataCriteria criteria =
			new PopulationDataCriteria().ageGroupIsNull(true)
				.sexIsNull(true)
				.districtIsNull(true)
				.region(new RegionReferenceDto(regionUuid, null, null));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		cq.where(filter);

		try {
			PopulationData populationData = em.createQuery(cq).getSingleResult();
			return InfrastructureHelper.getProjectedPopulation(populationData.getPopulation(), populationData.getCollectionDate(), growthRate);
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	@Override
	public Integer getDistrictPopulation(String districtUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);

		PopulationDataCriteria criteria =
			new PopulationDataCriteria().ageGroupIsNull(true).sexIsNull(true).district(new DistrictReferenceDto(districtUuid, null, null));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		cq.where(filter);
		cq.select(root.get(PopulationData.POPULATION));

		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Integer getProjectedDistrictPopulation(String districtUuid) {

		Float growthRate = districtService.getByUuid(districtUuid).getGrowthRate();

		if (growthRate == null || growthRate == 0) {
			return getDistrictPopulation(districtUuid);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);

		PopulationDataCriteria criteria =
			new PopulationDataCriteria().ageGroupIsNull(true).sexIsNull(true).district(new DistrictReferenceDto(districtUuid, null, null));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		cq.where(filter);

		try {
			PopulationData populationData = em.createQuery(cq).getSingleResult();
			return InfrastructureHelper.getProjectedPopulation(populationData.getPopulation(), populationData.getCollectionDate(), growthRate);
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	@Override
	public void savePopulationData(List<PopulationDataDto> populationDataList) throws ValidationRuntimeException {

		for (PopulationDataDto populationData : populationDataList) {
			validate(populationData);
			PopulationData entity = fromDto(populationData, true);
			service.ensurePersisted(entity);
		}
	}

	@Override
	public List<PopulationDataDto> getPopulationData(PopulationDataCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PopulationData> cq = cb.createQuery(PopulationData.class);
		Root<PopulationData> root = cq.from(PopulationData.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		cq.where(filter);

		return em.createQuery(cq).getResultStream().map(populationData -> toDto(populationData)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getPopulationDataForExport() {

		//@formatter:off
		return em.createNativeQuery("SELECT " + Region.TABLE_NAME + "." + Region.NAME + " AS regionname, "
				+ District.TABLE_NAME + "." + District.NAME + " AS districtname, " + PopulationData.AGE_GROUP + ", " 
				+ PopulationData.SEX + ", " + PopulationData.POPULATION + " FROM " + PopulationData.TABLE_NAME
				+ " LEFT JOIN " + Region.TABLE_NAME + " ON " + PopulationData.REGION + "_id = "
				+ Region.TABLE_NAME + "." + Region.ID + " LEFT JOIN " + District.TABLE_NAME + " ON "
				+ PopulationData.DISTRICT + "_id = " + District.TABLE_NAME + "." + District.ID
				+ " ORDER BY regionname, districtname asc NULLS FIRST").getResultList();
		//@formatter:on
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getMissingPopulationDataForStatistics(
		StatisticsCaseCriteria criteria,
		boolean groupByRegion,
		boolean groupByDistrict,
		boolean groupBySex,
		boolean groupByAgeGroup) {

		StringBuilder regionsIn = new StringBuilder();
		StringBuilder districtsIn = new StringBuilder();
		StringBuilder sexesIn = new StringBuilder();
		StringBuilder ageGroupsIn = new StringBuilder();
		List<Object> parameters = new ArrayList<>();

		if (!CollectionUtils.isEmpty(criteria.getRegions()) && CollectionUtils.isEmpty(criteria.getDistricts())) {
			List<Long> regionIds = regionService.getIdsByReferenceDtos(criteria.getRegions());
			QueryHelper.appendInFilterValues(regionsIn, parameters, regionIds, entry -> entry);
		}
		if (!CollectionUtils.isEmpty(criteria.getDistricts())) {
			List<Long> districtIds = districtService.getIdsByReferenceDtos(criteria.getDistricts());
			QueryHelper.appendInFilterValues(districtsIn, parameters, districtIds, entry -> entry);
		}
		if (!CollectionUtils.isEmpty(criteria.getSexes())) {
			QueryHelper.appendInFilterValues(sexesIn, parameters, criteria.getSexes(), entry -> entry.name());
		}
		if (!CollectionUtils.isEmpty(criteria.getAgeGroups())) {
			QueryHelper.appendInFilterValues(ageGroupsIn, parameters, criteria.getAgeGroups(), entry -> entry.name());
		}

		StringBuilder queryBuilder = new StringBuilder();
		if (!groupByDistrict && CollectionUtils.isEmpty(criteria.getDistricts())) {
			queryBuilder.append("SELECT ").append(Region.ID).append(" FROM ").append(Region.TABLE_NAME);

			if (regionsIn.length() > 0) {
				queryBuilder.append(" WHERE ").append(Region.ID).append(" IN ").append(regionsIn);
			}

			//@formatter:off
			queryBuilder.append(" EXCEPT SELECT ").append(PopulationData.REGION).append("_id FROM ").append(PopulationData.TABLE_NAME)
			.append(" WHERE ").append(PopulationData.TABLE_NAME).append(".").append(PopulationData.DISTRICT).append("_id IS NULL").append(" AND ");
			//@formatter:on
		} else {
			queryBuilder.append("SELECT ").append(District.ID).append(" FROM ").append(District.TABLE_NAME);

			if (districtsIn.length() > 0) {
				queryBuilder.append(" WHERE ").append(District.ID).append(" IN ").append(districtsIn);
			}

			//@formatter:off
			queryBuilder.append(" EXCEPT SELECT ").append(PopulationData.DISTRICT).append("_id FROM ").append(PopulationData.TABLE_NAME).append(" WHERE ");
			//@formatter:on
		}

		queryBuilder.append(PopulationData.TABLE_NAME).append(".").append(PopulationData.SEX);
		if (sexesIn.length() > 0) {
			queryBuilder.append(" IN ").append(sexesIn);
		} else {
			queryBuilder.append(groupBySex ? " IS NOT NULL " : " IS NULL ");
		}

		queryBuilder.append(" AND ").append(PopulationData.TABLE_NAME).append(".").append(PopulationData.AGE_GROUP);
		if (ageGroupsIn.length() > 0) {
			queryBuilder.append(" IN ").append(ageGroupsIn);
		} else {
			queryBuilder.append(groupByAgeGroup ? " IS NOT NULL " : " IS NULL ");
		}

		Query query = em.createNativeQuery(queryBuilder.toString());
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(i + 1, parameters.get(i));
		}

		return query.getResultList();
	}

	public Integer getAreaPopulation(String areaUuid, AgeGroup ageGroup) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);
		Join<PopulationData, Region> regionJoin = root.join(PopulationData.REGION);
		Join<Region, Area> areaJoin = regionJoin.join(Region.AREA);

		Predicate areaFilter = cb.equal(areaJoin.get(Area.UUID), areaUuid);
		Predicate ageFilter = cb.and(cb.equal(root.get(PopulationData.AGE_GROUP), ageGroup));

		cq.where(areaFilter, ageFilter);
		cq.select(root.get(PopulationData.POPULATION));
		TypedQuery query = em.createQuery(cq);
		try {
			Integer totalPopulation = 0;
			for (Object i : query.getResultList()) {
				if (Objects.nonNull(i)) {
					totalPopulation = totalPopulation + (Integer) i;
				}
			}
			return totalPopulation;
		} catch (NoResultException e) {
			return null;
		}
	}

	private void validate(PopulationDataDto populationData) throws ValidationRuntimeException {

		if (populationData.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
	}

	public PopulationData fromDto(@NotNull PopulationDataDto source, boolean checkChangeDate) {

		PopulationData target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), PopulationData::new, checkChangeDate);

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setAgeGroup(source.getAgeGroup());
		target.setSex(source.getSex());
		target.setPopulation(source.getPopulation());
		target.setCollectionDate(source.getCollectionDate());

		return target;
	}

	public static PopulationDataDto toDto(PopulationData source) {

		if (source == null) {
			return null;
		}
		PopulationDataDto target = new PopulationDataDto();
		DtoHelper.fillDto(target, source);

		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setAgeGroup(source.getAgeGroup());
		target.setSex(source.getSex());
		target.setPopulation(source.getPopulation());
		target.setCollectionDate(source.getCollectionDate());

		return target;
	}

	@LocalBean
	@Stateless
	public static class PopulationDataFacadeEjbLocal extends PopulationDataFacadeEjb {

	}
}
