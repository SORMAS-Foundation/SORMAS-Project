package de.symeda.sormas.backend.infrastructure;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.PopulationDataFacade;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "PopulationDataFacade")
public class PopulationDataFacadeEjb implements PopulationDataFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

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

		PopulationDataCriteria criteria = new PopulationDataCriteria()
				.ageGroupIsNull(true)
				.sexIsNull(true)
				.districtIsNull(true)
				.region(new RegionReferenceDto(regionUuid));
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
		
		PopulationDataCriteria criteria = new PopulationDataCriteria()
				.ageGroupIsNull(true)
				.sexIsNull(true)
				.districtIsNull(true)
				.region(new RegionReferenceDto(regionUuid));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		cq.where(filter);
		
		try {
			PopulationData populationData = em.createQuery(cq).getSingleResult();
			return getProjectedPopulationData(populationData, growthRate);
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	@Override
	public Integer getDistrictPopulation(String districtUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<PopulationData> root = cq.from(PopulationData.class);

		PopulationDataCriteria criteria = new PopulationDataCriteria()
				.ageGroupIsNull(true)
				.sexIsNull(true)
				.district(new DistrictReferenceDto(districtUuid));
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
		
		PopulationDataCriteria criteria = new PopulationDataCriteria()
				.ageGroupIsNull(true)
				.sexIsNull(true)
				.district(new DistrictReferenceDto(districtUuid));
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		cq.where(filter);

		try {
			PopulationData populationData = em.createQuery(cq).getSingleResult();
			return getProjectedPopulationData(populationData, growthRate);
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	@Override
	public void savePopulationData(List<PopulationDataDto> populationDatas) throws ValidationRuntimeException {
		
		for (PopulationDataDto populationData : populationDatas) {
			validate(populationData);
			PopulationData entity = fromDto(populationData);
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
		return em.createNativeQuery("SELECT " + Region.TABLE_NAME + "." + Region.NAME + " AS regionname, "
				+ District.TABLE_NAME + "." + District.NAME + " AS districtname, " + PopulationData.AGE_GROUP + ", " 
				+ PopulationData.SEX + ", " + PopulationData.POPULATION + " FROM " + PopulationData.TABLE_NAME
				+ " LEFT JOIN " + Region.TABLE_NAME + " ON " + PopulationData.REGION + "_id = "
				+ Region.TABLE_NAME + "." + Region.ID + " LEFT JOIN " + District.TABLE_NAME + " ON "
				+ PopulationData.DISTRICT + "_id = " + District.TABLE_NAME + "." + District.ID
				+ " ORDER BY regionname, districtname asc NULLS FIRST").getResultList();
	}

	private void validate(PopulationDataDto populationData) throws ValidationRuntimeException {
		if (populationData.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
	}

	public PopulationData fromDto(@NotNull PopulationDataDto source) {
		PopulationData target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new PopulationData();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

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
	
	private Integer getProjectedPopulationData(PopulationData populationData, Float growthRate) {
		Integer population = populationData.getPopulation();
		
		if (population != null && population > 0) {
			Integer yearsBetween = DateHelper.getYearsBetween(populationData.getCollectionDate(), new Date());
			int calculatedYears = 0;
			while (calculatedYears < yearsBetween) {
				population += (int) (population / 100 * growthRate);
				calculatedYears++;
			}
		}
		
		return population;
	}

	@LocalBean
	@Stateless
	public static class PopulationDataFacadeEjbLocal extends PopulationDataFacadeEjb {
	}

}
