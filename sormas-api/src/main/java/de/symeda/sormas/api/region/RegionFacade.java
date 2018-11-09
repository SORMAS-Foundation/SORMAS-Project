package de.symeda.sormas.api.region;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface RegionFacade {

    List<RegionReferenceDto> getAllAsReference();

	List<RegionDto> getAllAfter(Date date);
	
	List<RegionDto> getIndexList();
	
	RegionDto getRegionByUuid(String uuid);
	
	RegionReferenceDto getRegionReferenceByUuid(String uuid);

	RegionReferenceDto getRegionReferenceById(int id);
	
	List<String> getAllUuids(String userUuid);
	
	List<String> getAllUuids();
	
	List<RegionDto> getByUuids(List<String> uuids);
	
	void saveRegion(RegionDto dto);
	
	List<RegionReferenceDto> getByName(String name);
	
}
