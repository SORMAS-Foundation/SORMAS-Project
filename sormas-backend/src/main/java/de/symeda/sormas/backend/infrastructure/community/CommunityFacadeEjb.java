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
package de.symeda.sormas.backend.infrastructure.community;

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
import de.symeda.sormas.api.infrastructure.community.CommunityCriteria;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.community.CommunityFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureEjb;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "CommunityFacade")
public class CommunityFacadeEjb extends AbstractInfrastructureEjb<Community, CommunityService> implements CommunityFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private UserService userService;
	@EJB
	private DistrictService districtService;

	public CommunityFacadeEjb() {
	}

	@Inject
	protected CommunityFacadeEjb(CommunityService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(service, featureConfiguration);
	}

	@Override
	public List<CommunityReferenceDto> getAllActiveByDistrict(String districtUuid) {

		District district = districtService.getByUuid(districtUuid);
		return district.getCommunities().stream().filter(c -> !c.isArchived()).map(CommunityFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public List<CommunityDto> getAllAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CommunityDto> cq = cb.createQuery(CommunityDto.class);
		Root<Community> community = cq.from(Community.class);

		selectDtoFields(cq, community);

		Predicate filter = service.createChangeDateFilter(cb, community, date);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	// Need to be in the same order as in the constructor
	private void selectDtoFields(CriteriaQuery<CommunityDto> cq, Root<Community> root) {

		Join<Community, District> district = root.join(Community.DISTRICT, JoinType.LEFT);
		Join<District, Region> region = district.join(District.REGION, JoinType.LEFT);

		cq.multiselect(
			root.get(Community.CREATION_DATE),
			root.get(Community.CHANGE_DATE),
			root.get(Community.UUID),
			root.get(Community.ARCHIVED),
			root.get(Community.NAME),
			root.get(Community.GROWTH_RATE),
			region.get(Region.UUID),
			region.get(Region.NAME),
			region.get(Region.EXTERNAL_ID),
			district.get(District.UUID),
			district.get(District.NAME),
			district.get(District.EXTERNAL_ID),
			root.get(Community.EXTERNAL_ID));
	}

	@Override
	public List<CommunityDto> getIndexList(CommunityCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Community> cq = cb.createQuery(Community.class);
		Root<Community> community = cq.from(Community.class);
		Join<Community, District> district = community.join(Community.DISTRICT, JoinType.LEFT);
		Join<District, Region> region = district.join(District.REGION, JoinType.LEFT);

		Predicate filter = null;
		if (criteria != null) {
			filter = service.buildCriteriaFilter(criteria, cb, community);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Community.NAME:
				case Community.GROWTH_RATE:
				case Community.EXTERNAL_ID:
					expression = community.get(sortProperty.propertyName);
					break;
				case District.REGION:
					expression = region.get(Region.NAME);
					break;
				case Community.DISTRICT:
					expression = district.get(District.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(region.get(Region.NAME)), cb.asc(district.get(District.NAME)), cb.asc(community.get(Community.NAME)));
		}

		cq.select(community);

		//		cq.multiselect(community.get(Community.CREATION_DATE), community.get(Community.CHANGE_DATE),
		//				community.get(Community.UUID), community.get(Community.NAME),
		//				region.get(Region.UUID), region.get(Region.NAME),
		//				district.get(District.UUID), district.get(District.NAME));

		return QueryHelper.getResultList(em, cq, first, max, this::toDto);
	}

	public Page<CommunityDto> getIndexPage(CommunityCriteria communityCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<CommunityDto> communityList = getIndexList(communityCriteria, offset, size, sortProperties);
		long totalElementCount = count(communityCriteria);
		return new Page<>(communityList, offset, size, totalElementCount);
	}

	@Override
	public long count(CommunityCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Community> root = cq.from(Community.class);

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
	public CommunityDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public List<CommunityDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public CommunityReferenceDto getCommunityReferenceByUuid(String uuid) {
		return toReferenceDto(service.getByUuid(uuid));
	}

	@Override
	public CommunityReferenceDto getCommunityReferenceById(long id) {
		return toReferenceDto(service.getById(id));
	}

	@Override
	public Map<String, String> getDistrictUuidsForCommunities(List<CommunityReferenceDto> communities) {

		if (communities.isEmpty()) {
			return new HashMap<>();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Community> root = cq.from(Community.class);
		Join<Community, District> districtJoin = root.join(Community.DISTRICT, JoinType.LEFT);

		Predicate filter = root.get(Community.UUID).in(communities.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));
		cq.where(filter);
		cq.multiselect(root.get(Community.UUID), districtJoin.get(District.UUID));

		return em.createQuery(cq).getResultList().stream().collect(Collectors.toMap(e -> (String) e[0], e -> (String) e[1]));
	}

	@Override
	public CommunityDto save(@Valid CommunityDto dto) throws ValidationRuntimeException {
		return save(dto, false);
	}

	@Override
	public CommunityDto save(@Valid CommunityDto dto, boolean allowMerge) throws ValidationRuntimeException {
		checkInfraDataLocked();

		if (dto.getDistrict() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
		}

		Community community = service.getByUuid(dto.getUuid());

		if (community == null) {
			List<CommunityReferenceDto> duplicates = getByName(dto.getName(), dto.getDistrict(), true);
			if (!duplicates.isEmpty()) {
				if (allowMerge) {
					String uuid = duplicates.get(0).getUuid();
					community = service.getByUuid(uuid);
					CommunityDto dtoToMerge = getByUuid(uuid);
					dto = DtoHelper.copyDtoValues(dtoToMerge, dto, true);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importCommunityAlreadyExists));
				}
			}
		}
		community = fillOrBuildEntity(dto, community, true);
		service.ensurePersisted(community);
		return toDto(community);
	}

	@Override
	public List<CommunityReferenceDto> getByName(String name, DistrictReferenceDto districtRef, boolean includeArchivedEntities) {

		return service.getByName(name, districtService.getByReferenceDto(districtRef), includeArchivedEntities)
			.stream()
			.map(CommunityFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}
	
	@Override
	public List<CommunityReferenceDto> getByExternalID(Long ext_id, DistrictReferenceDto districtRef, boolean includeArchivedEntities) {

		return service.getByExternalID(ext_id, districtService.getByReferenceDto(districtRef), includeArchivedEntities)
			.stream()
			.map(CommunityFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<CommunityReferenceDto> getByExternalId(Long externalId, boolean includeArchivedEntities) {

		return service.getByExternalId(externalId, includeArchivedEntities)
			.stream()
			.map(CommunityFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<CommunityReferenceDto> getReferencesByName(String name, boolean includeArchived) {
		return getByName(name, null, false);
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> communityUuids) {
		return service.isUsedInInfrastructureData(communityUuids, Facility.COMMUNITY, Facility.class);
	}

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> communityUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Community> root = cq.from(Community.class);
		Join<Community, District> districtJoin = root.join(Community.DISTRICT);
		Join<District, Region> regionJoin = districtJoin.join(District.REGION);

		cq.where(
			cb.and(
				cb.or(cb.isTrue(districtJoin.get(District.ARCHIVED)), cb.isTrue(regionJoin.get(Region.ARCHIVED))),
				root.get(Community.UUID).in(communityUuids)));

		cq.select(root.get(Community.ID));

		return QueryHelper.getFirstResult(em, cq) != null;
	}

	public static CommunityReferenceDto toReferenceDto(Community entity) {

		if (entity == null) {
			return null;
		}
		
		CommunityReferenceDto dto = new CommunityReferenceDto(entity.getUuid(), entity.toString(), entity.getExternalID());
		return dto;
	}

	private CommunityDto toDto(Community entity) {

		if (entity == null) {
			return null;
		}
		CommunityDto dto = new CommunityDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getDistrict().getRegion()));
		dto.setArchived(entity.isArchived());
		dto.setExternalID(entity.getExternalID());

		return dto;
	}

	private Community fillOrBuildEntity(@NotNull CommunityDto source, Community target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, Community::new, checkChangeDate);

		target.setName(source.getName());
		target.setGrowthRate(source.getGrowthRate());
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setArchived(source.isArchived());
		target.setExternalID(source.getExternalID());

		return target;
	}

	@LocalBean
	@Stateless
	public static class CommunityFacadeEjbLocal extends CommunityFacadeEjb {

		public CommunityFacadeEjbLocal() {
		}

		@Inject
		protected CommunityFacadeEjbLocal(CommunityService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}
