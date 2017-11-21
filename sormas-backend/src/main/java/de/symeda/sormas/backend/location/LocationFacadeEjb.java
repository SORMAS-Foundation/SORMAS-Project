package de.symeda.sormas.backend.location;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.location.LocationFacade;
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
	
	public Location fromDto(LocationDto source) {		
		if (source == null) {
			return null;
		}
		
		Location target = locationService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Location();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		} 
		DtoHelper.validateDto(source, target);
		
		target.setAddress(source.getAddress());
		target.setDetails(source.getDetails());
		target.setCity(source.getCity());
		
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setLatLonAccuracy(source.getLatLonAccuracy());

		return target;
	}
	
	public static LocationDto toDto(Location source) {
		
		if (source == null) {
			return null;
		}

		LocationDto target = new LocationDto();
		DtoHelper.fillDto(target, source);
		
		target.setAddress(source.getAddress());
		target.setDetails(source.getDetails());
		target.setCity(source.getCity());
		
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setLatLonAccuracy(source.getLatLonAccuracy());
		
		return target;
	}
	
	@LocalBean
	@Stateless
	public static class LocationFacadeEjbLocal extends LocationFacadeEjb {
	}
}
