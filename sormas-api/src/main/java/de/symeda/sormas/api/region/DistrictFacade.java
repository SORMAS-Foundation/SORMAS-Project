package de.symeda.sormas.api.region;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface DistrictFacade {

    List<DistrictReferenceDto> getAllByRegion(String regionUuid);

	List<DistrictDto> getAllAfter(Date date);
	
	DistrictDto getDistrictByUuid(String uuid);
	
	DistrictReferenceDto getDistrictReferenceByUuid(String uuid);

	List<DistrictReferenceDto> getAllAsReference();

}
