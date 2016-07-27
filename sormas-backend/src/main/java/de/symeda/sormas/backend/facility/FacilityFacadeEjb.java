package de.symeda.sormas.backend.facility;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "FacilityFacade")
public class FacilityFacadeEjb implements FacilityFacade {
	
	@EJB
	private FacilityService service;

	@Override
	public List<ReferenceDto> getAllAsReference() {
		return service.getAll().stream()
				.map(f -> DtoHelper.toReferenceDto(f))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<FacilityDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	private FacilityDto toDto(Facility entity) {
		FacilityDto dto = new FacilityDto();
		dto.setUuid(entity.getUuid());
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		
		dto.setName(entity.getName());
		dto.setType(entity.getType());
		dto.setPublicOwnership(entity.isPublicOwnership());
		dto.setLocation(LocationFacadeEjb.toLocationDto(entity.getLocation()));

		return dto;
	}
}
