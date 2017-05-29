package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;

@Path("/cases")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class CaseResource {

	@GET
	@Path("/all/{user}/{since}")
	public List<CaseDataDto> getAllCases(@Context SecurityContext sc, @PathParam("user") String userUuid, @PathParam("since") long since) {
		
		List<CaseDataDto> cases = FacadeProvider.getCaseFacade().getAllCasesAfter(new Date(since), userUuid);
		return cases;
	}
	
	@POST 
	@Path("/push")
	public Long postCases(List<CaseDataDto> dtos) {
		
		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		for (CaseDataDto dto : dtos) {
			caseFacade.saveCase(dto);
		}
		
		return new Date().getTime();
	}
}
