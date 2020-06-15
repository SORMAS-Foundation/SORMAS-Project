/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.region;

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
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.CommunityCriteria;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CommunityFacade")
public class CommunityFacadeEjb implements CommunityFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CommunityService communityService;
	@EJB
	private UserService userService;
	@EJB
	private DistrictService districtService;

	@Override
	public List<CommunityReferenceDto> getAllActiveByDistrict(String districtUuid) {

		District district = districtService.getByUuid(districtUuid);
		return district.getCommunities().stream().filter(c -> !c.isArchived()).map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<CommunityDto> getAllAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CommunityDto> cq = cb.createQuery(CommunityDto.class);
		Root<Community> community = cq.from(Community.class);

		selectDtoFields(cq, community);

		Predicate filter = communityService.createChangeDateFilter(cb, community, date);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	@Override
	public void archive(String communityUuid) {

		Community community = communityService.getByUuid(communityUuid);
		community.setArchived(true);
		communityService.ensurePersisted(community);
	}

	@Override
	public void dearchive(String communityUuid) {

		Community community = communityService.getByUuid(communityUuid);
		community.setArchived(false);
		communityService.ensurePersisted(community);
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
			region.get(Region.UUID),
			region.get(Region.NAME),
			district.get(District.UUID),
			district.get(District.NAME),
			root.get(Community.EXTERNAL_ID));
	}

	@Override
	public List<CommunityDto> getIndexList(CommunityCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Community> cq = cb.createQuery(Community.class);
		Root<Community> community = cq.from(Community.class);
		Join<Community, District> district = community.join(Community.DISTRICT, JoinType.LEFT);
		Join<District, Region> region = district.join(District.REGION, JoinType.LEFT);

		Predicate filter = communityService.buildCriteriaFilter(criteria, cb, community);

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Community.NAME:
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

		if (first != null && max != null) {
			return em.createQuery(cq)
				.setFirstResult(first)
				.setMaxResults(max)
				.getResultList()
				.stream()
				.map(f -> toDto(f))
				.collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(f -> toDto(f)).collect(Collectors.toList());
		}
	}

	@Override
	public long count(CommunityCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Community> root = cq.from(Community.class);

		Predicate filter = communityService.buildCriteriaFilter(criteria, cb, root);

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

		return communityService.getAllUuids();
	}

	@Override
	public CommunityDto getByUuid(String uuid) {
		return toDto(communityService.getByUuid(uuid));
	}

	@Override
	public List<CommunityDto> getByUuids(List<String> uuids) {
		return communityService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public CommunityReferenceDto getCommunityReferenceByUuid(String uuid) {
		return toReferenceDto(communityService.getByUuid(uuid));
	}

	@Override
	public CommunityReferenceDto getCommunityReferenceById(long id) {
		return toReferenceDto(communityService.getById(id));
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
	public void saveCommunity(CommunityDto dto) throws ValidationRuntimeException {

		Community community = communityService.getByUuid(dto.getUuid());

		if (community == null && !getByName(dto.getName(), dto.getDistrict(), true).isEmpty()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importCommunityAlreadyExists));
		}

		if (dto.getDistrict() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
		}

		community = fillOrBuildEntity(dto, community);
		communityService.ensurePersisted(community);
	}

	@Override
	public List<CommunityReferenceDto> getByName(String name, DistrictReferenceDto districtRef, boolean includeArchivedEntities) {

		return communityService.getByName(name, districtService.getByReferenceDto(districtRef), includeArchivedEntities)
			.stream()
			.map(c -> toReferenceDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> communityUuids) {
		return communityService.isUsedInInfrastructureData(communityUuids, Facility.COMMUNITY, Facility.class);
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

		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
	}

	public static CommunityReferenceDto toReferenceDto(Community entity) {

		if (entity == null) {
			return null;
		}
		CommunityReferenceDto dto = new CommunityReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	private CommunityDto toDto(Community entity) {

		if (entity == null) {
			return null;
		}
		CommunityDto dto = new CommunityDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getDistrict().getRegion()));
		dto.setArchived(entity.isArchived());
		dto.setExternalID(entity.getExternalID());

		return dto;
	}

	private Community fillOrBuildEntity(@NotNull CommunityDto source, Community target) {

		if (target == null) {
			target = new Community();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target);

		target.setName(source.getName());
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setArchived(source.isArchived());
		target.setExternalID(source.getExternalID());

		return target;
	}

	@LocalBean
	@Stateless
	public static class CommunityFacadeEjbLocal extends CommunityFacadeEjb {

	}
}
