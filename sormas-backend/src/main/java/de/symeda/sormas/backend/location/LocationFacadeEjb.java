package de.symeda.sormas.backend.location;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

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
		
	public Location fromLocationDto(LocationDto dto) {		
		if (dto == null) {
			return null;
		}
		
		Location location = locationService.getByUuid(dto.getUuid());
		if (location == null) {
			location = new Location();
			location.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				location.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		} 
		
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
