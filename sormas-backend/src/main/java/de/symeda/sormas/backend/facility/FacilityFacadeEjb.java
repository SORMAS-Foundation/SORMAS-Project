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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.facility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

import de.symeda.sormas.api.facility.FacilityCriteria;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "FacilityFacade")
public class FacilityFacadeEjb implements FacilityFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;
	@EJB
	private CommunityService communityService;
	@EJB
	private DistrictService districtService;
	@EJB
	private RegionService regionService;

	@Override
	public List<FacilityReferenceDto> getActiveHealthFacilitiesByCommunity(CommunityReferenceDto communityRef, boolean includeStaticFacilities) {
		Community community = communityService.getByUuid(communityRef.getUuid());
		List<Facility> facilities = facilityService.getActiveHealthFacilitiesByCommunity(community, includeStaticFacilities);

		return facilities.stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<FacilityReferenceDto> getActiveHealthFacilitiesByDistrict(DistrictReferenceDto districtRef, boolean includeStaticFacilities) {
		District district = districtService.getByUuid(districtRef.getUuid());
		List<Facility> facilities = facilityService.getActiveHealthFacilitiesByDistrict(district, includeStaticFacilities);

		return facilities.stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<FacilityReferenceDto> getAllActiveLaboratories(boolean includeOtherLaboratory) {
		List<Facility> laboratories = facilityService.getAllActiveLaboratories(includeOtherLaboratory);

		return laboratories.stream().map(l -> toReferenceDto(l)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return facilityService.getAllUuids();
	}

	@Override
	public List<FacilityDto> getAllByRegionAfter(String regionUuid, Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FacilityDto> cq = cb.createQuery(FacilityDto.class);
		Root<Facility> facility = cq.from(Facility.class);

		selectDtoFields(cq, facility);

		Predicate filter = facilityService.createChangeDateFilter(cb, facility, date);

		if (regionUuid != null) {
			Predicate regionFilter = cb.equal(facility.get(Facility.REGION), regionService.getByUuid(regionUuid));
			filter = AbstractAdoService.and(cb, filter, regionFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	@Override
	public List<FacilityDto> getAllWithoutRegionAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FacilityDto> cq = cb.createQuery(FacilityDto.class);
		Root<Facility> facility = cq.from(Facility.class);

		selectDtoFields(cq, facility);

		Predicate filter = facilityService.createChangeDateFilter(cb, facility, date);

		Predicate regionFilter = cb.isNull(facility.get(Facility.REGION));
		filter = AbstractAdoService.and(cb, filter, regionFilter);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	// Need to be in the same order as in the constructor
	private void selectDtoFields(CriteriaQuery<FacilityDto> cq, Root<Facility> root) {
		Join<Facility, Community> community = root.join(Facility.COMMUNITY, JoinType.LEFT);
		Join<Facility, District> district = root.join(Facility.DISTRICT, JoinType.LEFT);
		Join<Facility, Region> region = root.join(Facility.REGION, JoinType.LEFT);

		cq.multiselect(root.get(Facility.CREATION_DATE), root.get(Facility.CHANGE_DATE), root.get(Facility.UUID), root.get(Facility.ARCHIVED),
				root.get(Facility.NAME), region.get(Region.UUID), region.get(Region.NAME), district.get(District.UUID),
				district.get(District.NAME), community.get(Community.UUID), community.get(Community.NAME),
				root.get(Facility.CITY), root.get(Facility.LATITUDE), root.get(Facility.LONGITUDE),
				root.get(Facility.TYPE), root.get(Facility.PUBLIC_OWNERSHIP), root.get(Facility.EXTERNAL_ID));
	}

	@Override
	public FacilityDto getByUuid(String uuid) {
		return toDto(facilityService.getByUuid(uuid));
	}

	@Override
	public List<FacilityDto> getByUuids(List<String> uuids) {
		return facilityService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public FacilityReferenceDto getFacilityReferenceByUuid(String uuid) {
		return toReferenceDto(facilityService.getByUuid(uuid));
	}

	@Override
	public List<FacilityReferenceDto> getByName(String name, DistrictReferenceDto districtRef,
			CommunityReferenceDto communityRef, boolean includeArchivedEntities) {
		return facilityService
				.getHealthFacilitiesByName(name, districtService.getByReferenceDto(districtRef),
						communityService.getByReferenceDto(communityRef), includeArchivedEntities)
				.stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<FacilityReferenceDto> getLaboratoriesByName(String name, boolean includeArchivedEntities) {
		return facilityService.getLaboratoriesByName(name, includeArchivedEntities).stream().map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}

	@Override
	public void archive(String facilityUuid) {
		Facility facility = facilityService.getByUuid(facilityUuid);
		facility.setArchived(true);
		facilityService.ensurePersisted(facility);
	}

	@Override
	public void dearchive(String facilityUuid) {
		Facility facility = facilityService.getByUuid(facilityUuid);		
		facility.setArchived(false);
		facilityService.ensurePersisted(facility);
	}

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> facilityUuids) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Facility> root = cq.from(Facility.class);
		Join<Facility, Community> communityJoin = root.join(Facility.COMMUNITY);
		Join<Facility, District> districtJoin = root.join(Facility.DISTRICT);
		Join<Facility, Region> regionJoin = root.join(Facility.REGION);

		cq.where(
				cb.and(
						cb.or(
								cb.isTrue(communityJoin.get(Community.ARCHIVED)),
								cb.isTrue(districtJoin.get(District.ARCHIVED)),
								cb.isTrue(regionJoin.get(Region.ARCHIVED))
								),
						root.get(Facility.UUID).in(facilityUuids)
						)
				);

		cq.select(root.get(Facility.ID));

		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
	}

	public static FacilityReferenceDto toReferenceDto(Facility entity) {
		if (entity == null) {
			return null;
		}
		FacilityReferenceDto dto = new FacilityReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	private FacilityDto toDto(Facility entity) {
		if (entity == null) {
			return null;
		}
		FacilityDto dto = new FacilityDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setType(entity.getType());
		dto.setPublicOwnership(entity.isPublicOwnership());
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));
		dto.setCommunity(CommunityFacadeEjb.toReferenceDto(entity.getCommunity()));
		dto.setCity(entity.getCity());
		dto.setLatitude(entity.getLatitude());
		dto.setLongitude(entity.getLongitude());
		dto.setArchived(entity.isArchived());
		dto.setExternalID(entity.getExternalID());

		return dto;
	}

	@LocalBean
	@Stateless
	public static class FacilityFacadeEjbLocal extends FacilityFacadeEjb {
	}

	@Override
	public List<FacilityDto> getIndexList(FacilityCriteria facilityCriteria, Integer first, Integer max,
			List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(Facility.class);
		Root<Facility> facility = cq.from(Facility.class);
		Join<Facility, Region> region = facility.join(Facility.REGION, JoinType.LEFT);
		Join<Facility, District> district = facility.join(Facility.DISTRICT, JoinType.LEFT);
		Join<Facility, Community> community = facility.join(Facility.COMMUNITY, JoinType.LEFT);

		Predicate filter = facilityService.buildCriteriaFilter(facilityCriteria, cb, facility);
		Predicate excludeFilter = cb.and(cb.notEqual(facility.get(Facility.UUID), FacilityDto.OTHER_FACILITY_UUID),
				cb.notEqual(facility.get(Facility.UUID), FacilityDto.NONE_FACILITY_UUID),
				cb.notEqual(facility.get(Facility.UUID), FacilityDto.OTHER_LABORATORY_UUID));
		if (filter != null) {
			filter = AbstractAdoService.and(cb, filter, excludeFilter);
		} else {
			filter = excludeFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Facility.NAME:
				case Facility.CITY:
				case Facility.LATITUDE:
				case Facility.LONGITUDE:
				case Facility.EXTERNAL_ID:
					expression = facility.get(sortProperty.propertyName);
					break;
				case Facility.REGION:
					expression = region.get(Region.NAME);
					break;
				case Facility.DISTRICT:
					expression = district.get(District.NAME);
					break;
				case Facility.COMMUNITY:
					expression = community.get(Community.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(region.get(Region.NAME)), cb.asc(district.get(District.NAME)),
					cb.asc(community.get(Community.NAME)), cb.asc(facility.get(Facility.NAME)));
		}

		cq.select(facility);

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList().stream().map(f -> toDto(f)).collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(f -> toDto(f)).collect(Collectors.toList());
		}
	}

	@Override
	public long count(FacilityCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Facility> root = cq.from(Facility.class);

		Predicate filter = facilityService.buildCriteriaFilter(criteria, cb, root);
		Predicate excludeFilter = cb.and(cb.notEqual(root.get(Facility.UUID), FacilityDto.OTHER_FACILITY_UUID),
				cb.notEqual(root.get(Facility.UUID), FacilityDto.NONE_FACILITY_UUID),
				cb.notEqual(root.get(Facility.UUID), FacilityDto.OTHER_LABORATORY_UUID));
		if (filter != null) {
			filter = AbstractAdoService.and(cb, filter, excludeFilter);
		} else {
			filter = excludeFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public void saveFacility(FacilityDto dto) throws ValidationRuntimeException {
		Facility facility = facilityService.getByUuid(dto.getUuid());
		
		if (facility == null) {
			if (FacilityType.LABORATORY.equals(dto.getType()) && !getLaboratoriesByName(dto.getName(), true).isEmpty()) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importLaboratoryAlreadyExists));
			} else if (!getByName(dto.getName(), dto.getDistrict(), dto.getCommunity(), true).isEmpty()) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importFacilityAlreadyExists));
			}
		}

		if (!FacilityType.LABORATORY.equals(dto.getType())) {
			if (dto.getRegion() == null) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
			}
			if (dto.getDistrict() == null) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
			}
		}

		facility = fillOrBuildEntity(dto, facility);
		facilityService.ensurePersisted(facility);
	}

	private Facility fillOrBuildEntity(@NotNull FacilityDto source, Facility target) {
		if (target == null) {
			target = new Facility();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target);

		target.setName(source.getName());

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));

		target.setCity(source.getCity());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());

		target.setType(source.getType());
		target.setArchived(source.isArchived());
		target.setExternalID(source.getExternalID());

		return target;
	}

}
