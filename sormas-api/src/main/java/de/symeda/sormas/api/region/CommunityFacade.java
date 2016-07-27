package de.symeda.sormas.api.region;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;

@Remote
public interface CommunityFacade {

    List<ReferenceDto> getAllAsReference(String districtUuid);

	List<CommunityDto> getAllAfter(Date date);
}
