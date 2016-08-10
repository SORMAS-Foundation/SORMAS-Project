package de.symeda.sormas.backend.location;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.location.LocationFacade;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "LocationFacade")
@LocalBean
public class LocationFacadeEjb implements LocationFacade {
	
	@EJB
	private LocationService locationService;

	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	
	
	public Location fromLocationDto(@NotNull LocationDto dto) {		
		if (dto == null) {
			return null;
		}
		
		Location location;
		if (dto.getChangeDate() == null) {
			location = new Location();
		} else {
			location = locationService.getByUuid(dto.getUuid());
		}

		location.setUuid(dto.getUuid());
		
		location.setAddress(dto.getAddress());
		location.setDetails(dto.getDetails());
		location.setCity(dto.getCity());
		
		location.setRegion(DtoHelper.fromReferenceDto(dto.getRegion(), regionService));
		location.setDistrict(DtoHelper.fromReferenceDto(dto.getDistrict(), districtService));
		location.setCommunity(DtoHelper.fromReferenceDto(dto.getCommunity(), communityService));
		
		location.setLatitude(dto.getLatitude());
		location.setLongitude(dto.getLongitude());
		
		return location;
	}
	
	public static LocationDto toLocationDto(Location location) {
		
		if (location == null) {
			return null;
		}

		LocationDto dto = new LocationDto();
		
		dto.setCreationDate(location.getCreationDate());
		dto.setChangeDate(location.getChangeDate());
		dto.setUuid(location.getUuid());
		
		dto.setAddress(location.getAddress());
		dto.setDetails(location.getDetails());
		dto.setCity(location.getCity());
		
		dto.setRegion(DtoHelper.toReferenceDto(location.getRegion()));
		dto.setDistrict(DtoHelper.toReferenceDto(location.getDistrict()));
		dto.setCommunity(DtoHelper.toReferenceDto(location.getCommunity()));
		
		dto.setLatitude(location.getLatitude());
		dto.setLongitude(location.getLongitude());
		
		return dto;
	}
}
