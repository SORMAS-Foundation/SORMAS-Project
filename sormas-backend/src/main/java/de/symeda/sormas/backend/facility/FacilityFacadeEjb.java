package de.symeda.sormas.backend.facility;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "FacilityFacade")
public class FacilityFacadeEjb implements FacilityFacade {
	
	@EJB
	private FacilityService fs;

	@Override
	public List<ReferenceDto> getAllAsReference() {
		return fs.getAll().stream()
				.map(f -> DtoHelper.toReferenceDto(f))
				.collect(Collectors.toList());
	}
}
