package de.symeda.sormas.backend.location;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.location.LocationFacade;
import de.symeda.sormas.api.location.LocationReferenceDto;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "LocationFacade")
public class LocationFacadeEjb implements LocationFacade {
	
	@EJB
	private LocationService locationService;

	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
		
	public static LocationReferenceDto toReferenceDto(Location entity) {
		if(entity == null) {
			return null;
		}
		
		LocationReferenceDto dto = new LocationReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}
	
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
		
		location.setRegion(regionService.getByReferenceDto(dto.getRegion()));
		location.setDistrict(districtService.getByReferenceDto(dto.getDistrict()));
		location.setCommunity(communityService.getByReferenceDto(dto.getCommunity()));
		
		location.setLatitude(dto.getLatitude());
		location.setLongitude(dto.getLongitude());
		
		return location;
	}
	
	public static LocationDto toLocationDto(Location entity) {
		
		if (entity == null) {
			return null;
		}

		LocationDto dto = new LocationDto();
		DtoHelper.fillDto(dto, entity);
		
		dto.setAddress(entity.getAddress());
		dto.setDetails(entity.getDetails());
		dto.setCity(entity.getCity());
		
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));
		dto.setCommunity(CommunityFacadeEjb.toReferenceDto(entity.getCommunity()));
		
		dto.setLatitude(entity.getLatitude());
		dto.setLongitude(entity.getLongitude());
		
		return dto;
	}
	
	@LocalBean
	@Stateless
	public static class LocationFacadeEjbLocal extends LocationFacadeEjb {
	}
}
