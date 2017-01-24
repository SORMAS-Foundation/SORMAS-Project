package de.symeda.sormas.api.facility;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface FacilityFacade {

	List<FacilityReferenceDto> getAll();
	
    List<FacilityReferenceDto> getAllByCommunity(String communityUuid);

	List<FacilityDto> getAllAfter(Date date);
	
	FacilityDto getByUuid(String uuid);
}
