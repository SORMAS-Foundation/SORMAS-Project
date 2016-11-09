package de.symeda.sormas.api.facility;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;

@Remote
public interface FacilityFacade {

	List<ReferenceDto> getAll();
	
    List<ReferenceDto> getAllByCommunity(String communityUuid);

	List<FacilityDto> getAllAfter(Date date);
}
