package de.symeda.sormas.backend.region;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "CommunityFacade")
public class CommunityFacadeEjb implements CommunityFacade {
	
	@EJB
	private CommunityService service;
	@EJB
	private DistrictService districtService;

	@Override
	public List<ReferenceDto> getAllAsReference(String districtUuid) {
		
		District district = districtService.getByUuid(districtUuid);
		
		return district.getCommunities().stream()
				.map(f -> DtoHelper.toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CommunityDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	private CommunityDto toDto(Community entity) {
		CommunityDto dto = new CommunityDto();
		dto.setUuid(entity.getUuid());
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		
		dto.setName(entity.getName());
		dto.setDistrict(DtoHelper.toReferenceDto(entity.getDistrict()));

		return dto;
	}
}
