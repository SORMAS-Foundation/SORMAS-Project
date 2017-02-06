package de.symeda.sormas.backend.facility;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "FacilityFacade")
public class FacilityFacadeEjb implements FacilityFacade {
	
	@EJB
	private FacilityService service;
	@EJB
	private CommunityService communityService;
	@EJB
	private DistrictService districtService;

	
	@Override
	public List<FacilityReferenceDto> getAllByCommunity(CommunityReferenceDto communityRef) {
		
		Community community = communityService.getByUuid(communityRef.getUuid());
		List<Facility> facilities = service.getAllByCommunity(community);
		
		return facilities.stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<FacilityReferenceDto> getAllByDistrict(DistrictReferenceDto districtRef) {
    	District district = districtService.getByUuid(districtRef.getUuid());
		List<Facility> facilities = service.getAllByDistrict(district);
		
		return facilities.stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}

	@Override
	public List<FacilityReferenceDto> getAllLaboratories() {
		List<Facility> laboratories = service.getAllByFacilityType(FacilityType.LABORATORY);
		
		return laboratories.stream()
				.map(l -> toReferenceDto(l))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<FacilityReferenceDto> getAll() {
		return service.getAll().stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<FacilityDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public FacilityDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
	
	public static FacilityReferenceDto toReferenceDto(Facility entity) {
		if (entity == null) {
			return null;
		}
		FacilityReferenceDto dto = new FacilityReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}
	
	private FacilityDto toDto(Facility entity) {
		if (entity == null) {
			return null;
		}
		FacilityDto dto = new FacilityDto();
		DtoHelper.fillReferenceDto(dto, entity);
		
		dto.setName(entity.getName());
		dto.setType(entity.getType());
		dto.setPublicOwnership(entity.isPublicOwnership());
		dto.setLocation(LocationFacadeEjb.toLocationDto(entity.getLocation()));

		return dto;
	}
}
