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
import de.symeda.sormas.api.region.RegionReferenceDto;
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
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * @author Christopher Riedel
 *
 */
@Stateless(name = "FacilityFacade")
public class FacilityFacadeEjb implements FacilityFacade {

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

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@Override
	public List<FacilityReferenceDto> getHealthFacilitiesByCommunity(CommunityReferenceDto communityRef,
			boolean includeStaticFacilities) {

		Community community = communityService.getByUuid(communityRef.getUuid());
		List<Facility> facilities = facilityService.getHealthFacilitiesByCommunity(community, includeStaticFacilities);

		return facilities.stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<FacilityReferenceDto> getHealthFacilitiesByDistrict(DistrictReferenceDto districtRef,
			boolean includeStaticFacilities) {
		District district = districtService.getByUuid(districtRef.getUuid());
		List<Facility> facilities = facilityService.getHealthFacilitiesByDistrict(district, includeStaticFacilities);

		return facilities.stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<FacilityReferenceDto> getHealthFacilitiesByRegion(RegionReferenceDto regionRef,
			boolean includeStaticFacilities) {
		Region region = regionService.getByReferenceDto(regionRef);
		List<Facility> facilities = facilityService.getHealthFacilitiesByRegion(region, includeStaticFacilities);

		return facilities.stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<FacilityReferenceDto> getAllLaboratories(boolean includeOtherLaboratory) {
		List<Facility> laboratories = facilityService.getAllLaboratories(includeOtherLaboratory);

		return laboratories.stream().map(l -> toReferenceDto(l)).collect(Collectors.toList());
	}

	@Override
	public List<FacilityReferenceDto> getAll() {
		return facilityService.getAll().stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids(String userUuid) {

		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return facilityService.getAllUuids(user);
	}

	@Override
	public List<FacilityDto> getAllByRegionAfter(String regionUuid, Date date) {
		Region region = null;
		if (regionUuid != null) {
			region = regionService.getByUuid(regionUuid);
		}

		List<Facility> facilities = facilityService.getAllByRegionAfter(region, date);
		return facilities.stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<FacilityDto> getAllWithoutRegionAfter(Date date) {
		List<Facility> facilities = facilityService.getAllWithoutRegionAfter(date);
		return facilities.stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public FacilityDto getByUuid(String uuid) {
		return toDto(facilityService.getByUuid(uuid));
	}

	@Override
	public List<FacilityDto> getByUuids(List<String> uuids) {
		return facilityService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public FacilityReferenceDto getFacilityReferenceByUuid(String uuid) {
		return toReferenceDto(facilityService.getByUuid(uuid));
	}

	@Override
	public List<FacilityReferenceDto> getByName(String name, DistrictReferenceDto districtRef, CommunityReferenceDto communityRef) {
		return facilityService.getHealthFacilitiesByName(name, districtService.getByReferenceDto(districtRef), communityService.getByReferenceDto(communityRef))
				.stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
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

		return dto;
	}

	@LocalBean
	@Stateless
	public static class FacilityFacadeEjbLocal extends FacilityFacadeEjb {
	}

	@Override
	public List<FacilityDto> getIndexList(FacilityCriteria facilityCriteria, int first, int max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(Facility.class);
		Root<Facility> facility = cq.from(Facility.class);
		Join<Facility, Region> region = facility.join(Facility.REGION, JoinType.LEFT);
		Join<Facility, District> district = facility.join(Facility.DISTRICT, JoinType.LEFT);
		Join<Facility, Community> community = facility.join(Facility.COMMUNITY, JoinType.LEFT);

		Predicate filter = facilityService.buildCriteriaFilter(facilityCriteria, cb, facility);
		Predicate excludeFilter = cb.and(
				cb.notEqual(facility.get(Facility.UUID), FacilityDto.OTHER_FACILITY_UUID),
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
			cq.orderBy(cb.asc(region.get(Region.NAME)), cb.asc(district.get(District.NAME)), cb.asc(community.get(Community.NAME)), cb.asc(facility.get(Facility.NAME)));
		}

		cq.select(facility);
		
		List<Facility> facilities = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		return facilities.stream().map(f -> toDto(f)).collect(Collectors.toList());
	}
	
	@Override
	public long count(FacilityCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Facility> root = cq.from(Facility.class);
		
		Predicate filter = facilityService.buildCriteriaFilter(criteria, cb, root);
		Predicate excludeFilter = cb.and(
				cb.notEqual(root.get(Facility.UUID), FacilityDto.OTHER_FACILITY_UUID),
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

		if (dto.getType() != FacilityType.LABORATORY) {
			if (dto.getRegion() == null) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
			}
			if (dto.getDistrict() == null) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
			}
			if (dto.getCommunity() == null) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validCommunity));
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

		return target;
	}

}
