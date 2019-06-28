package de.symeda.sormas.backend.infrastructure;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.infrastructure.PointOfEntryFacade;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;

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
	
}
