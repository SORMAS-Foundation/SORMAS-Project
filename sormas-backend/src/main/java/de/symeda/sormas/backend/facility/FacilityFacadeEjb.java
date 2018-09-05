package de.symeda.sormas.backend.facility;

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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.facility.FacilityCriteria;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
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
	public FacilityReferenceDto getFacilityReferenceByUuid(String uuid) {
		return toReferenceDto(facilityService.getByUuid(uuid));
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
	public List<FacilityDto> getIndexList(String userUuid, FacilityCriteria facilityCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(Facility.class);
		Root<Facility> facility = cq.from(Facility.class);
		Join<Facility, Region> region = facility.join(Facility.REGION, JoinType.LEFT);
		Join<Facility, District> district = facility.join(Facility.DISTRICT, JoinType.LEFT);
		Join<Facility, Community> community = facility.join(Facility.COMMUNITY, JoinType.LEFT);

		User user = userService.getByUuid(userUuid);
		Predicate filter = facilityService.createUserFilter(cb, cq, facility, user);

		if (facilityCriteria != null) {
			Predicate criteriaFilter = facilityService.buildCriteriaFilter(facilityCriteria, cb, facility);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(facility);
		cq.orderBy(cb.asc(region.get(Region.NAME)), cb.asc(district.get(District.NAME)), cb.asc(community.get(Community.NAME)), cb.asc(facility.get(Facility.NAME)));
		cq.distinct(true);

		List<Facility> facilities = em.createQuery(cq).getResultList();
		return facilities.stream().map(f -> toDto(f)).collect(Collectors.toList());
	}

	@Override
	public void saveFacility(FacilityDto dto) {
		Facility facility = facilityService.getByUuid(dto.getUuid());

		facility = fillOrBuildEntity(dto, facility);

		if (facility.getRegion() == null) {
			throw new ValidationRuntimeException("You have to specify a valid region");
		}
		if (dto.getType() != FacilityType.LABORATORY) {
			if (facility.getDistrict() == null) {
				throw new ValidationRuntimeException("You have to specify a valid district");
			}
			if (facility.getCommunity() == null) {
				throw new ValidationRuntimeException("You have to specify a valid community");
			}
		}

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
