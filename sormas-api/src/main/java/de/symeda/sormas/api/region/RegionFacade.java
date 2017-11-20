package de.symeda.sormas.api.region;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface RegionFacade {

    List<RegionReferenceDto> getAllAsReference();

	List<RegionDto> getAllAfter(Date date);
	
	RegionDto getRegionByUuid(String uuid);
	
	RegionReferenceDto getRegionReferenceByUuid(String uuid);
	
	List<RegionDataDto> getAllData();
}
