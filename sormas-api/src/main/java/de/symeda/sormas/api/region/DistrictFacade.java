package de.symeda.sormas.api.region;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface DistrictFacade {

    List<DistrictReferenceDto> getAllByRegion(String regionUuid);
	
	int getCountByRegion(String regionUuid);

	List<DistrictDto> getAllAfter(Date date);
	
	List<DistrictDto> getIndexList(DistrictCriteria criteria);
	
	DistrictDto getDistrictByUuid(String uuid);
	
	DistrictReferenceDto getDistrictReferenceByUuid(String uuid);
	
	DistrictReferenceDto getDistrictReferenceById(int id);

	List<DistrictReferenceDto> getAllAsReference();

	List<String> getAllUuids(String userUuid);
	
	List<String> getAllUuids();
	
	List<DistrictDto> getByUuids(List<String> uuids);
	
	void saveDistrict(DistrictDto dto) throws ValidationRuntimeException;
	
	List<DistrictReferenceDto> getByName(String name, RegionReferenceDto regionRef);
	
}
