package de.symeda.sormas.api.facility;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface FacilityFacade {

	List<FacilityReferenceDto> getAll();
	
	List<FacilityDto> getIndexList(String userUuid, FacilityCriteria facilityCriteria);
	
    List<FacilityReferenceDto> getHealthFacilitiesByCommunity(CommunityReferenceDto community, boolean includeStaticFacilities);
    List<FacilityReferenceDto> getHealthFacilitiesByDistrict(DistrictReferenceDto district, boolean includeStaticFacilities);
    List<FacilityReferenceDto> getHealthFacilitiesByRegion(RegionReferenceDto region, boolean includeStaticFacilities);
    List<FacilityReferenceDto> getAllLaboratories(boolean includeOtherLaboratory);

	List<FacilityDto> getAllByRegionAfter(String regionUuid, Date date);
	List<FacilityDto> getAllWithoutRegionAfter(Date date);
	
	FacilityReferenceDto getFacilityReferenceByUuid(String uuid);
	
	FacilityDto getByUuid(String uuid);
	
	List<FacilityDto> getByUuids(List<String> uuids);

	List<String> getAllUuids(String userUuid);

	void saveFacility(FacilityDto value) throws ValidationRuntimeException;

}
