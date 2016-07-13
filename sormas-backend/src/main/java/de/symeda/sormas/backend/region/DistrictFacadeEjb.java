package de.symeda.sormas.backend.region;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "DistrictFacade")
public class DistrictFacadeEjb implements DistrictFacade {
	
	@EJB
	private DistrictService service;
	@EJB
	private RegionService regionService;

	@Override
	public List<ReferenceDto> getAllAsReference(String regionUuid) {
		
		Region region = regionService.getByUuid(regionUuid);
		
		return region.getDistricts().stream()
				.map(f -> DtoHelper.toReferenceDto(f))
				.collect(Collectors.toList());
	}
}
