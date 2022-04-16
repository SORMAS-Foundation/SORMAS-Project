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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureEjb;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.area.AreaService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "DistrictFacade")
public class DistrictFacadeEjb extends AbstractInfrastructureEjb<District, DistrictService> implements DistrictFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private UserService userService;
	@EJB
	private AreaService areaService;
	@EJB
	private RegionService regionService;
	@EJB
	private PopulationDataFacadeEjbLocal populationDataFacade;

	public DistrictFacadeEjb() {
	}

	@Inject
	protected DistrictFacadeEjb(DistrictService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(service, featureConfiguration);
	}

	@Override
	public List<DistrictReferenceDto> getAllActiveAsReference() {
		return service.getAllActive(District.NAME, true).stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<DistrictReferenceDto> getAllActiveByArea(String areaUuid) {

		Area area = areaService.getByUuid(areaUuid);
		return service.getAllActiveByArea(area).stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<DistrictReferenceDto> getAllActiveByRegion(String regionUuid) {

		Region region = regionService.getByUuid(regionUuid);
		return region.getDistricts().stream().filter(d -> !d.isArchived()).map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<DistrictDto> getAllAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DistrictDto> cq = cb.createQuery(DistrictDto.class);
		Root<District> district = cq.from(District.class);

		selectDtoFields(cq, district);

		Predicate filter = service.createChangeDateFilter(cb, district, date);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	// Need to be in the same order as in the constructor
	private void selectDtoFields(CriteriaQuery<DistrictDto> cq, Root<District> root) {

		Join<District, Region> region = root.join(District.REGION, JoinType.LEFT);

		cq.multiselect(
			root.get(District.CREATION_DATE),
			root.get(District.CHANGE_DATE),
			root.get(District.UUID),
			root.get(District.ARCHIVED),
			root.get(District.NAME),
			root.get(District.EPID_CODE),
			root.get(District.GROWTH_RATE),
			region.get(Region.UUID),
			region.get(Region.NAME),
			region.get(Region.EXTERNAL_ID),
			root.get(District.EXTERNAL_ID));
	}

	@Override
	public DistrictDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
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

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case District.NAME:
				case District.EPID_CODE:
				case District.GROWTH_RATE:
				case District.EXTERNAL_ID:
					expression = district.get(sortProperty.propertyName);
					break;
				case District.REGION:
					expression = region.get(Region.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(region.get(Region.NAME)), cb.asc(district.get(District.NAME)));
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
	public long count(DistrictCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<District> root = cq.from(District.class);

		Predicate filter = null;

		if (criteria != null) {
			filter = service.buildCriteriaFilter(criteria, cb, root);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getAllUuids();
	}

	@Override
	public int getCountByRegion(String regionUuid) {

		Region region = regionService.getByUuid(regionUuid);
		return service.getCountByRegion(region);
	}

	@Override
	public DistrictDto getDistrictByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public List<DistrictDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public DistrictReferenceDto getDistrictReferenceByUuid(String uuid) {
		return toReferenceDto(service.getByUuid(uuid));
	}

	@Override
	public DistrictReferenceDto getDistrictReferenceById(long id) {
		return toReferenceDto(service.getById(id));
	}

	@Override
	public Map<String, String> getRegionUuidsForDistricts(List<DistrictReferenceDto> districts) {

		if (districts.isEmpty()) {
			return new HashMap<>();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<District> root = cq.from(District.class);
		Join<District, Region> regionJoin = root.join(District.REGION, JoinType.LEFT);

		Predicate filter = root.get(District.UUID).in(districts.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));
		cq.where(filter);
		cq.multiselect(root.get(District.UUID), regionJoin.get(Region.UUID));

		return em.createQuery(cq).getResultList().stream().collect(Collectors.toMap(e -> (String) e[0], e -> (String) e[1]));
	}

	@Override
	public DistrictDto save(@Valid DistrictDto dto) throws ValidationRuntimeException {
		return save(dto, false);
	}

	@Override
	public DistrictDto save(@Valid DistrictDto dto, boolean allowMerge) throws ValidationRuntimeException {
		checkInfraDataLocked();

		if (dto.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}

		District district = service.getByUuid(dto.getUuid());

		if (district == null) {
			List<DistrictReferenceDto> duplicates = getByName(dto.getName(), dto.getRegion(), true);
			if (!duplicates.isEmpty()) {
				if (allowMerge) {
					String uuid = duplicates.get(0).getUuid();
					district = service.getByUuid(uuid);
					DistrictDto dtoToMerge = getDistrictByUuid(uuid);
					dto = DtoHelper.copyDtoValues(dtoToMerge, dto, true);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importDistrictAlreadyExists));
				}
			}
		}

		district = fillOrBuildEntity(dto, district, true);
		service.ensurePersisted(district);
		return toDto(district);
	}

	@Override
	public List<DistrictReferenceDto> getByName(String name, RegionReferenceDto regionRef, boolean includeArchivedEntities) {

		return service.getByName(name, regionService.getByReferenceDto(regionRef), includeArchivedEntities)
			.stream()
			.map(DistrictFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}
	
	@Override
	public List<DistrictReferenceDto> getByExternalID(Long ext_id, RegionReferenceDto regionRef, boolean includeArchivedEntities) {
		System.out.println("++++REGION IN USE TO QUERY DISTRICT b "+regionService.getByReferenceDto(regionRef));
		return service.getByExternalID(ext_id, regionService.getByReferenceDto(regionRef), includeArchivedEntities)
			.stream()
			.map(DistrictFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<DistrictReferenceDto> getByExternalId(Long externalId, boolean includeArchivedEntities) {

		return service.getByExternalId(externalId, includeArchivedEntities)
			.stream()
			.map(DistrictFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<DistrictReferenceDto> getReferencesByName(String name, boolean includeArchived) {
		return getByName(name, null, false);
	}

	@Override
	public List<String> getNamesByIds(List<Long> districtIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<District> root = cq.from(District.class);

		Predicate filter = root.get(District.ID).in(districtIds);
		cq.where(filter);
		cq.select(root.get(District.NAME));
		return em.createQuery(cq).getResultList();
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

		cq.where(cb.and(cb.isTrue(regionJoin.get(Region.ARCHIVED)), root.get(District.UUID).in(districtUuids)));

		cq.select(root.get(District.ID));

		return QueryHelper.getFirstResult(em, cq) != null;
	}

	public static DistrictReferenceDto toReferenceDto(District entity) {

		if (entity == null) {
			return null;
		}

		DistrictReferenceDto dto = new DistrictReferenceDto(entity.getUuid(), entity.toString(), entity.getExternalID());
		return dto;
	}

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
		dto.setArchived(entity.isArchived());
		System.out.println("ddddddddddddddddddddddddd=============dddddddddddddddd");
		dto.setExternalID(entity.getExternalID());

		return dto;
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

		return dto;
	}

	private District fillOrBuildEntity(@NotNull DistrictDto source, District target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, District::new, checkChangeDate);

		target.setName(source.getName());
		target.setEpidCode(source.getEpidCode());
		target.setGrowthRate(source.getGrowthRate());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setArchived(source.isArchived());
		target.setExternalID(source.getExternalID());

		return target;
	}

	@Override
	public String getFullEpidCodeForDistrict(String districtUuid) {

		District district = service.getByUuid(districtUuid);
		return getFullEpidCodeForDistrict(district);
	}

	private String getFullEpidCodeForDistrict(District district) {
		return (district.getRegion().getEpidCode() != null ? district.getRegion().getEpidCode() : "") + "-"
			+ (district.getEpidCode() != null ? district.getEpidCode() : "");
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
