package de.symeda.sormas.backend.region;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
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
}
