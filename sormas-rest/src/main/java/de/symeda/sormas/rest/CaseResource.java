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
import de.symeda.sormas.api.user.UserReferenceDto;

@Path("/cases")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class CaseResource {

	@GET
	@Path("/all/{since}")
	public List<CaseDataDto> getAllCases(@Context SecurityContext sc, @PathParam("since") long since) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<CaseDataDto> cases = FacadeProvider.getCaseFacade().getAllCasesAfter(new Date(since), userDto.getUuid());
		return cases;
	}
	
	@POST 
	@Path("/push")
	public Integer postCases(List<CaseDataDto> dtos) {
		
		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		for (CaseDataDto dto : dtos) {
			caseFacade.saveCase(dto);
		}
		
		return dtos.size();
	}
	
	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getCaseFacade().getAllUuids(userDto.getUuid());
		return uuids;
	}
}
