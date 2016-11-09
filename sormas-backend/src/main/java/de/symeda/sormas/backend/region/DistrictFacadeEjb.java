package de.symeda.sormas.backend.region;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "DistrictFacade")
public class DistrictFacadeEjb implements DistrictFacade {
	
	@EJB
	private DistrictService service;
	@EJB
	private RegionService regionService;

	@Override
	public List<ReferenceDto> getAllByRegion(String regionUuid) {
		
		Region region = regionService.getByUuid(regionUuid);
		
		return region.getDistricts().stream()
				.map(f -> DtoHelper.toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<DistrictDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	private DistrictDto toDto(District entity) {
		DistrictDto dto = new DistrictDto();
		dto.setUuid(entity.getUuid());
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		
		dto.setName(entity.getName());
		dto.setRegion(DtoHelper.toReferenceDto(entity.getRegion()));

		return dto;
	}
}
