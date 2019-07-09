package de.symeda.sormas.backend.infrastructure;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryFacade;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "PointOfEntryFacade")
public class PointOfEntryFacadeEjb implements PointOfEntryFacade {

	@EJB
	private PointOfEntryService service;
	@EJB
	private DistrictService districtService;
	
	public static PointOfEntryReferenceDto toReferenceDto(PointOfEntry entity) {
		if (entity == null) {
			return null;
		}
		
		PointOfEntryReferenceDto ref = new PointOfEntryReferenceDto(entity.getUuid(), entity.toString());
		return ref;
	}
	
	@Override
	public List<PointOfEntryReferenceDto> getAllByDistrict(String districtUuid, boolean includeOthers) {
		District district = districtService.getByUuid(districtUuid);
		return service.getAllByDistrict(district, includeOthers).stream()
				.map(p -> toReferenceDto(p))
				.collect(Collectors.toList());
	}

	@Override
	public PointOfEntryDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
	
	private PointOfEntryDto toDto(PointOfEntry entity) {
		if (entity == null) {
			return null;
		}
		PointOfEntryDto dto = new PointOfEntryDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setPointOfEntryType(entity.getPointOfEntryType());
		dto.setActive(entity.isActive());
		dto.setLatitude(entity.getLatitude());
		dto.setLongitude(entity.getLongitude());
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));

		return dto;
	}
	
}
