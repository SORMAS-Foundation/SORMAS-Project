/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.infrastructure.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.backend.infrastructure.InfrastructureAdo;
import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionCriteria;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.infrastructure.region.RegionIndexDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.DefaultInfrastructureCache;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.area.AreaFacadeEjb;
import de.symeda.sormas.backend.infrastructure.area.AreaService;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "RegionFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class RegionFacadeEjb
	extends AbstractInfrastructureFacadeEjb<Region, RegionDto, RegionIndexDto, RegionReferenceDto, RegionService, RegionCriteria>
	implements RegionFacade {

	@EJB
	private PopulationDataFacadeEjbLocal populationDataFacade;
	@EJB
	private AreaService areaService;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CountryService countryService;
	@EJB
	private CountryFacadeEjbLocal countryFacade;
	@EJB
	private DefaultInfrastructureCache defaultInfrastructureCache;

	public RegionFacadeEjb() {
	}

	@Inject
	protected RegionFacadeEjb(RegionService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			Region.class,
			RegionDto.class,
			service,
			featureConfiguration,
			Validations.importRegionAlreadyExists,
			Strings.messageRegionArchivingNotPossible,
			null);
	}

	@Override
	@PermitAll
	public List<RegionReferenceDto> getAllActiveByServerCountry() {
		CountryReferenceDto serverCountry = countryFacade.getServerCountry();

		return getAllActiveByPredicate((cb, root) -> {
			if (serverCountry != null) {
				Path<Object> countryUuid = root.join(Region.COUNTRY, JoinType.LEFT).get(AbstractDomainObject.UUID);
				return CriteriaBuilderHelper.or(cb, cb.isNull(countryUuid), cb.equal(countryUuid, serverCountry.getUuid()));
			}

			return null;
		});
	}

	@Override
	@PermitAll
	public List<RegionReferenceDto> getAllActiveByCountry(String countryUuid) {
		return getAllActiveByPredicate((cb, root) -> cb.equal(root.get(Region.COUNTRY).get(AbstractDomainObject.UUID), countryUuid));
	}

	@Override
	@PermitAll
	public List<RegionReferenceDto> getAllActiveByArea(String areaUuid) {
		return getAllActiveByPredicate((cb, root) -> cb.equal(root.get(Region.AREA).get(AbstractDomainObject.UUID), areaUuid));
	}

	@Override
	@PermitAll
	public List<RegionReferenceDto> getAllActiveAsReference() {
		return toRefDtos(service.getAllActive(Region.NAME, true).stream());
	}

	@Override
	public List<RegionIndexDto> getIndexList(RegionCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(Region.class);
		Root<Region> region = cq.from(Region.class);
		Join<Region, Area> area = region.join(Region.AREA, JoinType.LEFT);
		Join<Region, Country> country = region.join(Region.COUNTRY, JoinType.LEFT);

		Predicate filter = null;
		if (criteria != null) {
			filter = service.buildCriteriaFilter(criteria, cb, region);
		}
		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Region.NAME:
				case Region.EPID_CODE:
				case Region.EXTERNAL_ID:
					expression = cb.lower(region.get(sortProperty.propertyName));
					break;
				case Region.GROWTH_RATE:
				case Region.DEFAULT_INFRASTRUCTURE:
					expression = region.get(sortProperty.propertyName);
					break;
				case Region.AREA:
					expression = cb.lower(area.get(Area.NAME));
					break;
				case RegionIndexDto.COUNTRY:
					expression = cb.lower(country.get(Country.DEFAULT_NAME));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(cb.lower(region.get(Region.NAME))));
		}

		cq.select(region);

		return QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
	}

	public Page<RegionIndexDto> getIndexPage(RegionCriteria regionCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<RegionIndexDto> regionIndexList = getIndexList(regionCriteria, offset, size, sortProperties);
		long totalElementCount = count(regionCriteria);
		return new Page<>(regionIndexList, offset, size, totalElementCount);
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public RegionReferenceDto getRegionReferenceById(int id) {
		return toReferenceDto(service.getById(id));
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public List<String> getNamesByIds(List<Long> regionIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Region> root = cq.from(Region.class);

		Predicate filter = root.get(AbstractDomainObject.ID).in(regionIds);
		cq.where(filter);
		cq.select(root.get(Region.NAME));
		return em.createQuery(cq).getResultList();
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> regionUuids) {
		return service.isUsedInInfrastructureData(regionUuids, District.REGION, District.class)
			|| service.isUsedInInfrastructureData(regionUuids, Facility.REGION, Facility.class)
			|| service.isUsedInInfrastructureData(regionUuids, PointOfEntry.REGION, PointOfEntry.class);
	}

	public static RegionReferenceDto toReferenceDto(Region entity) {
		if (entity == null) {
			return null;
		}
		return new RegionReferenceDto(entity.getUuid(), entity.getName(), entity.getExternalID());
	}

	public RegionDto toDto(Region entity) {
		if (entity == null) {
			return null;
		}
		RegionDto dto = new RegionDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setArchived(entity.isArchived());
		dto.setExternalID(entity.getExternalID());
		dto.setArea(AreaFacadeEjb.toReferenceDto(entity.getArea()));
		dto.setCountry(CountryFacadeEjb.toReferenceDto(entity.getCountry()));
		dto.setCentrallyManaged(entity.isCentrallyManaged());
		dto.setDefaultInfrastructure(entity.isDefaultInfrastructure());

		return dto;
	}

	@Override
	protected RegionReferenceDto toRefDto(Region region) {
		return toReferenceDto(region);
	}

	public RegionIndexDto toIndexDto(Region entity) {
		if (entity == null) {
			return null;
		}
		RegionIndexDto dto = new RegionIndexDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setPopulation(populationDataFacade.getRegionPopulation(dto.getUuid()));
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setExternalID(entity.getExternalID());
		dto.setArea(AreaFacadeEjb.toReferenceDto(entity.getArea()));
		dto.setCountry(CountryFacadeEjb.toReferenceDto(entity.getCountry()));
		dto.setDefaultInfrastructure(entity.isDefaultInfrastructure());

		return dto;
	}

	@Override
	protected List<Region> findDuplicates(RegionDto dto, boolean includeArchived) {
		return service.getByName(dto.getName(), includeArchived);
	}

	@Override
	@PermitAll
	public List<RegionReferenceDto> getReferencesByName(String name, boolean includeArchivedEntities) {
		return toRefDtos(service.getByName(name, includeArchivedEntities).stream());
	}

	@PermitAll
	public List<RegionDto> getByName(String name, boolean includeArchivedEntities) {
		return toDtos(service.getByName(name, includeArchivedEntities).stream());
	}

	@Override
	@PermitAll
	public List<RegionReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchivedEntities) {
		return toRefDtos(service.getByExternalId(externalId, includeArchivedEntities).stream());
	}

	private List<RegionReferenceDto> getAllActiveByPredicate(BiFunction<CriteriaBuilder, Root<Region>, Predicate> buildPredicate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(Region.class);
		Root<Region> root = cq.from(Region.class);

		Predicate basicFilter = service.createBasicFilter(cb, root);
		cq.where(CriteriaBuilderHelper.and(cb, basicFilter, buildPredicate.apply(cb, root)));

		cq.orderBy(cb.asc(root.get(Region.NAME)));

		return toRefDtos(em.createQuery(cq).getResultList().stream());
	}

	@Override
	protected Region fillOrBuildEntity(@NotNull RegionDto source, Region target, boolean checkChangeDate, boolean allowUuidOverwrite) {
		target = DtoHelper.fillOrBuildEntity(source, target, Region::new, checkChangeDate, allowUuidOverwrite);

		target.setName(source.getName());
		target.setEpidCode(source.getEpidCode());
		target.setGrowthRate(source.getGrowthRate());
		target.setArchived(source.isArchived());
		target.setExternalID(source.getExternalID());
		target.setArea(areaService.getByReferenceDto(source.getArea()));
		target.setCountry(countryService.getByReferenceDto(source.getCountry()));
		target.setCentrallyManaged(source.isCentrallyManaged());
		target.setDefaultInfrastructure(source.isDefaultInfrastructure());

		return target;
	}

	@Override
	protected boolean checkDefaultRemovalAllowed(RegionDto dto) {
		return districtFacade.getDefaultInfrastructureReference() == null;
	}

	@Override
	protected Region getDefaultInfrastructure() {
		return defaultInfrastructureCache.getDefaultRegion();
	}

	@Override
	protected void resetDefaultInfrastructure() {
		defaultInfrastructureCache.resetDefaultRegion();
	}

	@Override
	public List<RegionDto> getAllRegion() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RegionDto> cq = cb.createQuery(RegionDto.class);
		Root<Region> region = cq.from(Region.class);

		selectDtoFields(cq, region);

		return em.createQuery(cq).getResultList();
	}

	// Need to be in the same order as in the constructor
//	private void selectDtoFields(CriteriaQuery<RegionDto> cq, Root<Region> root) {
//
//		cq.multiselect(
//				root.get(Region.CREATION_DATE),
//				root.get(Region.CHANGE_DATE),
//				root.get(Region.UUID),
//				root.get(Region.ARCHIVED),
//				root.get(Region.NAME),
//				root.get(Region.EPID_CODE),
//				root.get(Region.GROWTH_RATE),
//				root.get(Region.EXTERNAL_ID));
//	}


	protected void selectDtoFields(CriteriaQuery<RegionDto> cq, Root<Region> root) {

		Join<Region, Country> country = root.join(Region.COUNTRY, JoinType.LEFT);
		Join<Region, Area> area = root.join(Region.AREA, JoinType.LEFT);
		// Needs to be in the same order as in the constructor
		cq.multiselect(
				root.get(AbstractDomainObject.CREATION_DATE),
				root.get(AbstractDomainObject.CHANGE_DATE),
				root.get(AbstractDomainObject.UUID),
				root.get(InfrastructureAdo.ARCHIVED),
				root.get(Region.NAME),
				root.get(Region.EPID_CODE),
				root.get(Region.GROWTH_RATE),
				root.get(Region.EXTERNAL_ID),
				//root.get(Region.ID),
				country.get(AbstractDomainObject.UUID),
				country.get(Country.DEFAULT_NAME),
				country.get(Country.ISO_CODE),
				area.get(AbstractDomainObject.UUID));
	}

	@LocalBean
	@Stateless
	public static class RegionFacadeEjbLocal extends RegionFacadeEjb {

		public RegionFacadeEjbLocal() {
		}

		@Inject
		protected RegionFacadeEjbLocal(RegionService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}
