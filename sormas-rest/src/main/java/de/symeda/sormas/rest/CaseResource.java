package de.symeda.sormas.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.person.CasePersonDto;

/**
 * @see https://jersey.java.net/documentation/latest/
 *
 */
@Path("/cases")
@Produces({
	MediaType.APPLICATION_JSON + "; charset=UTF-8"
	})
public class CaseResource implements CaseFacade {

	@GET
	@Path("/{uuid}")
	@Override
	public CaseDataDto getCaseDataByUuid(@PathParam("uuid") String uuid) {
		
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(uuid);
		return caze;
	}

	@Override
	public List<CaseDataDto> getAllCases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDataDto saveCase(CaseDataDto dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDataDto createCase(String personUuid, CaseDataDto caseDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
