package de.symeda.sormas.backend.feature;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.feature.FeatureConfigurationCriteria;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "FeatureConfigurationFacade")
public class FeatureConfigurationFacadeEjb implements FeatureConfigurationFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	
	@EJB
	private FeatureConfigurationService service;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	
	@Override
	public List<FeatureConfigurationIndexDto> getFeatureConfigurations(FeatureConfigurationCriteria criteria, boolean includeInactive) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FeatureConfigurationIndexDto> cq = cb.createQuery(FeatureConfigurationIndexDto.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);
		Join<FeatureConfiguration, Region> regionJoin = root.join(FeatureConfiguration.REGION, JoinType.LEFT);
		Join<FeatureConfiguration, District> districtJoin = root.join(FeatureConfiguration.DISTRICT, JoinType.LEFT);
		
		cq.multiselect(root.get(FeatureConfiguration.UUID), regionJoin.get(Region.UUID), districtJoin.get(District.UUID), 
				districtJoin.get(District.NAME), root.get(FeatureConfiguration.DISEASE), root.get(FeatureConfiguration.END_DATE));
		
		if (criteria != null) {
			Predicate filter = service.createCriteriaFilter(criteria, cb, cq, root);
			if (filter != null) {
				cq.where(filter);
			}
		}
		
		List<FeatureConfigurationIndexDto> resultList = em.createQuery(cq).getResultList();
		resultList.stream().forEach(config -> config.setActive(true));
		
		if (includeInactive) {
			if (criteria.getDistrict() == null) {
				List<District> districts = null;
				if (criteria.getRegion() != null) {
					Region region = regionService.getByUuid(criteria.getRegion().getUuid());
					districts = districtService.getAllByRegion(region);
				} else {
					districts = districtService.getAll();
				}
				
				List<String> activeUuids = resultList.stream().map(config -> config.getDistrictUuid()).collect(Collectors.toList());
				districts = districts.stream().filter(district -> !activeUuids.contains(district.getUuid())).collect(Collectors.toList());
				
				for (District district : districts) {
					resultList.add(new FeatureConfigurationIndexDto(null, criteria.getRegion() != null ? criteria.getRegion().getUuid() : null, 
							district.getUuid(), district.getName(), criteria.getDisease(), null));
				}
			}
		}
		
		resultList.sort((c1, c2) -> c1.getDistrictName().compareTo(c2.getDistrictName()));
		return resultList;
	}
	
	public static FeatureConfigurationDto toDto(FeatureConfiguration source) {
		if (source == null) {
			return null;
		}
		
		FeatureConfigurationDto target = new FeatureConfigurationDto();
		DtoHelper.fillDto(target, source);
		
		target.setFeatureType(source.getFeatureType());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setDisease(source.getDisease());
		target.setEndDate(source.getEndDate());
		
		return target;
	}
	
	public FeatureConfiguration fromDto(@NotNull FeatureConfigurationDto source) {
		FeatureConfiguration target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new FeatureConfiguration();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);
		
		target.setFeatureType(source.getFeatureType());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setDisease(source.getDisease());
		target.setEndDate(source.getEndDate());
		
		return target;
	}
	
}
