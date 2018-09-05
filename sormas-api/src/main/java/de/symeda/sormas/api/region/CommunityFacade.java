package de.symeda.sormas.api.region;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface CommunityFacade {
	
    List<CommunityReferenceDto> getAllByDistrict(String districtUuid);

	List<CommunityDto> getAllAfter(Date date);
	
	List<CommunityDto> getIndexList(CommunityCriteria criteria);
	
	CommunityDto getByUuid(String uuid);

	List<String> getAllUuids(String userUuid);

	CommunityReferenceDto getCommunityReferenceByUuid(String uuid);
	
	List<CommunityDto> getByUuids(List<String> uuids);
	
	void saveCommunity(CommunityDto dto);

}