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
package de.symeda.sormas.backend.infrastructure.district;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.DefaultInfrastructureCache;
import de.symeda.sormas.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.area.AreaService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "DistrictFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class DistrictFacadeEjb
	extends AbstractInfrastructureFacadeEjb<District, DistrictDto, DistrictIndexDto, DistrictReferenceDto, DistrictService, DistrictCriteria>
	implements DistrictFacade {

	@EJB
	private AreaService areaService;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private PopulationDataFacadeEjbLocal populationDataFacade;
	@EJB
	private DefaultInfrastructureCache defaultInfrastructureCache;

	public DistrictFacadeEjb() {
	}

	@Inject
	protected DistrictFacadeEjb(DistrictService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			District.class,
			DistrictDto.class,
			service,
			featureConfiguration,
			Validations.importDistrictAlreadyExists,
			Strings.messageDistrictArchivingNotPossible,
			Strings.messageDistrictDearchivingNotPossible);
	}

	@Override
	@PermitAll
	public List<DistrictReferenceDto> getAllActiveAsReference() {
		return service.getAllActive(District.NAME, true).stream().map(DistrictFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<DistrictReferenceDto> getAllActiveByArea(String areaUuid) {

		Area area = areaService.getByUuid(areaUuid);
		return service.getAllActiveByArea(area).stream().map(DistrictFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<DistrictReferenceDto> getAllActiveByRegion(String regionUuid) {
		Region region = regionService.getByUuid(regionUuid);
		return region.getDistricts().stream().filter(d -> !d.isArchived()).map(DistrictFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public List<DistrictIndexDto> getIndexList(DistrictCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<District> cq = cb.createQuery(District.class);
		Root<District> district = cq.from(District.class);
		Join<District, Region> region = district.join(District.REGION, JoinType.LEFT);

		Predicate filter = null;
		if (criteria != null) {
			filter = service.buildCriteriaFilter(criteria, cb, district);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case District.NAME:
				case District.EPID_CODE:
				case District.EXTERNAL_ID:
					expression = cb.lower(district.get(sortProperty.propertyName));
					break;
				case District.GROWTH_RATE:
				case District.DEFAULT_INFRASTRUCTURE:
					expression = district.get(sortProperty.propertyName);
					break;
				case District.REGION:
					expression = cb.lower(region.get(Region.NAME));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(cb.lower(region.get(Region.NAME))), cb.asc(cb.lower(district.get(District.NAME))));
		}

		cq.select(district);

		return QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
	}

	public Page<DistrictIndexDto> getIndexPage(DistrictCriteria districtCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<DistrictIndexDto> districtIndexList = getIndexList(districtCriteria, offset, size, sortProperties);
		long totalElementCount = count(districtCriteria);
		return new Page<>(districtIndexList, offset, size, totalElementCount);
	}

	@Override
	@PermitAll
	public int getCountByRegion(String regionUuid) {
		Region region = regionService.getByUuid(regionUuid);
		return service.getCountByRegion(region);
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public DistrictReferenceDto getDistrictReferenceById(long id) {
		return toReferenceDto(service.getById(id));
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public Map<String, String> getRegionUuidsForDistricts(List<DistrictReferenceDto> districts) {

		if (districts.isEmpty()) {
			return new HashMap<>();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<District> root = cq.from(District.class);
		Join<District, Region> regionJoin = root.join(District.REGION, JoinType.LEFT);

		Predicate filter = root.get(AbstractDomainObject.UUID).in(districts.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));
		cq.where(filter);
		cq.multiselect(root.get(AbstractDomainObject.UUID), regionJoin.get(AbstractDomainObject.UUID));

		return em.createQuery(cq).getResultList().stream().collect(Collectors.toMap(e -> (String) e[0], e -> (String) e[1]));
	}

	@Override
	protected List<District> findDuplicates(DistrictDto dto, boolean includeArchived) {
		return service.getByName(dto.getName(), regionService.getByReferenceDto(dto.getRegion()), includeArchived);
	}

	@Override
	@PermitAll
	public List<DistrictReferenceDto> getByName(String name, RegionReferenceDto regionRef, boolean includeArchivedEntities) {

		return service.getByName(name, regionService.getByReferenceDto(regionRef), includeArchivedEntities)
			.stream()
			.map(DistrictFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<DistrictReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchivedEntities) {

		return service.getByExternalId(externalId, includeArchivedEntities)
			.stream()
			.map(DistrictFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@PermitAll
	public List<DistrictDto> getByExternalId(String externalId, boolean includeArchivedEntities) {
		return toDtos(service.getByExternalId(externalId, includeArchivedEntities).stream());
	}

	@Override
	@PermitAll
	public List<DistrictReferenceDto> getReferencesByName(String name, boolean includeArchived) {
		return getByName(name, null, false);
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> districtUuids) {

		return service.isUsedInInfrastructureData(districtUuids, Community.DISTRICT, Community.class)
			|| service.isUsedInInfrastructureData(districtUuids, Facility.DISTRICT, Facility.class)
			|| service.isUsedInInfrastructureData(districtUuids, PointOfEntry.DISTRICT, PointOfEntry.class);
	}

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> districtUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<District> root = cq.from(District.class);
		Join<District, Region> regionJoin = root.join(District.REGION);

		cq.where(cb.and(cb.isTrue(regionJoin.get(InfrastructureAdo.ARCHIVED)), root.get(AbstractDomainObject.UUID).in(districtUuids)));

		cq.select(root.get(AbstractDomainObject.ID));

		return QueryHelper.getFirstResult(em, cq) != null;
	}

	public static DistrictReferenceDto toReferenceDto(District entity) {

		if (entity == null) {
			return null;
		}

		return new DistrictReferenceDto(entity.getUuid(), entity.getName(), entity.getExternalID());
	}

	@Override
	public DistrictDto toDto(District entity) {

		if (entity == null) {
			return null;
		}

		DistrictDto dto = new DistrictDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setExternalID(entity.getExternalID());
		applyToDtoInheritance(dto, entity);

		return dto;
	}

	@Override
	protected DistrictReferenceDto toRefDto(District district) {
		return toReferenceDto(district);
	}

	public DistrictIndexDto toIndexDto(District entity) {

		if (entity == null) {
			return null;
		}

		DistrictIndexDto dto = new DistrictIndexDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setPopulation(populationDataFacade.getDistrictPopulation(dto.getUuid()));
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setExternalID(entity.getExternalID());
		dto.setDefaultInfrastructure(entity.isDefaultInfrastructure());

		return dto;
	}

	@Override
	protected District fillOrBuildEntity(@NotNull DistrictDto source, District target, boolean checkChangeDate, boolean allowUuidOverwrite) {

		target = DtoHelper.fillOrBuildEntity(source, target, District::new, checkChangeDate, allowUuidOverwrite);

		target.setName(source.getName());
		target.setEpidCode(source.getEpidCode());
		target.setGrowthRate(source.getGrowthRate());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setExternalID(source.getExternalID());
		applyFillOrBuildEntityInheritance(target, source);

		return target;
	}

	@Override
	@PermitAll
	public String getFullEpidCodeForDistrict(String districtUuid) {

		District district = service.getByUuid(districtUuid);

		return (district.getRegion().getEpidCode() != null ? district.getRegion().getEpidCode() : "") + "-"
			+ (district.getEpidCode() != null ? district.getEpidCode() : "");
	}

	@Override
	protected boolean checkDefaultEligible(DistrictDto dto) {
		return dto.getRegion().equals(regionFacade.getDefaultInfrastructureReference());
	}

	@Override
	protected boolean checkDefaultRemovalAllowed(DistrictDto dto) {
		return communityFacade.getDefaultInfrastructureReference() == null;
	}

	@Override
	protected District getDefaultInfrastructure() {
		return defaultInfrastructureCache.getDefaultDistrict();
	}

	@Override
	protected void resetDefaultInfrastructure() {
		defaultInfrastructureCache.resetDefaultDistrict();
	}

	@LocalBean
	@Stateless
	public static class DistrictFacadeEjbLocal extends DistrictFacadeEjb {

		public DistrictFacadeEjbLocal() {
		}

		@Inject
		protected DistrictFacadeEjbLocal(DistrictService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}
