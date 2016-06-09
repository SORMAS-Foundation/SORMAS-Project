package de.symeda.sormas.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDto;
import de.symeda.sormas.api.caze.CaseFacade;

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
	public CaseDto getByUuid(@PathParam("uuid") String uuid) {
		
		CaseDto caze = FacadeProvider.getCaseFacade().getByUuid(uuid);
		return caze;
	}

	@Override
	public List<CaseDto> getAllCases() {
		// TODO Auto-generated method stub
		return null;
	}
}
