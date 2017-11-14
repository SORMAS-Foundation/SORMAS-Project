package de.symeda.sormas.backend.region;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "DistrictFacade")
public class DistrictFacadeEjb implements DistrictFacade {
	
	@EJB
	private DistrictService service;
	@EJB
	private RegionService regionService;

	@Override
	public List<DistrictReferenceDto> getAllAsReference() {
		return service.getAll(District.NAME, true).stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}

	@Override
	public List<DistrictReferenceDto> getAllByRegion(String regionUuid) {
		
		Region region = regionService.getByUuid(regionUuid);
		
		return region.getDistricts().stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<DistrictDto> getAllAfter(Date date) {
		return service.getAllAfter(date, null).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public DistrictDto getDistrictByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
	
	@Override
	public DistrictReferenceDto getDistrictReferenceByUuid(String uuid) {
		return toReferenceDto(service.getByUuid(uuid));
	}
	
	public static DistrictReferenceDto toReferenceDto(District entity) {
		if (entity == null) {
			return null;
		}
		DistrictReferenceDto dto = new DistrictReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}
	
	private DistrictDto toDto(District entity) {
		if (entity == null) {
			return null;
		}
		DistrictDto dto = new DistrictDto();
		DtoHelper.fillReferenceDto(dto, entity);
		
		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));

		return dto;
	}
}
