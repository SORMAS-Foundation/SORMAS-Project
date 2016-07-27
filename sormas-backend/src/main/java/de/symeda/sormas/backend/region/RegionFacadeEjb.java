package de.symeda.sormas.backend.region;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "RegionFacade")
public class RegionFacadeEjb implements RegionFacade {
	
	@EJB
	private RegionService service;

	@Override
	public List<ReferenceDto> getAllAsReference() {
		return service.getAll().stream()
				.map(f -> DtoHelper.toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<RegionDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	private RegionDto toDto(Region entity) {
		RegionDto dto = new RegionDto();
		dto.setUuid(entity.getUuid());
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		
		dto.setName(entity.getName());

		return dto;
	}
}
