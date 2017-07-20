package de.symeda.sormas.api.facility;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;

@Remote
public interface FacilityFacade {

	List<FacilityReferenceDto> getAll();
	
    List<FacilityReferenceDto> getAllByCommunity(CommunityReferenceDto community, boolean includeOthers);
    List<FacilityReferenceDto> getAllByDistrict(DistrictReferenceDto district, boolean includeOthers);
    List<FacilityReferenceDto> getAllLaboratories();

	List<FacilityDto> getAllByRegionAfter(String regionUuid, Date date);
	
	FacilityDto getByUuid(String uuid);
}
