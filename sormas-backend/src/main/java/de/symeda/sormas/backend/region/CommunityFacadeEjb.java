package de.symeda.sormas.backend.region;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "CommunityFacade")
public class CommunityFacadeEjb implements CommunityFacade {
	
	@EJB
	private CommunityService service;
	@EJB
	private DistrictService districtService;

	@Override
	public List<CommunityReferenceDto> getAllByDistrict(String districtUuid) {
		
		District district = districtService.getByUuid(districtUuid);
		
		return district.getCommunities().stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CommunityDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	public static CommunityReferenceDto toReferenceDto(Community entity) {
		if (entity == null) {
			return null;
		}
		CommunityReferenceDto dto = new CommunityReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}
	
	private CommunityDto toDto(Community entity) {
		if (entity == null) {
			return null;
		}
		CommunityDto dto = new CommunityDto();
		DtoHelper.fillReferenceDto(dto, entity);
		
		dto.setName(entity.getName());
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));

		return dto;
	}
}
