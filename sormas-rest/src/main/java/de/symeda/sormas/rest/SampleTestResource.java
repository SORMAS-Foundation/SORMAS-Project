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
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestFacade;
import de.symeda.sormas.api.user.UserReferenceDto;

@Path("/sampletests")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class SampleTestResource {
	
	@GET
	@Path("/all/{since}")
	public List<SampleTestDto> getAllSampleTests(@Context SecurityContext sc, @PathParam("since") long since) {

		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<SampleTestDto> sampleTests = FacadeProvider.getSampleTestFacade().getAllActiveSampleTestsAfter(new Date(since), userDto.getUuid());
		return sampleTests;
	}
	
	@POST
	@Path("/query")
	public List<SampleTestDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {

		List<SampleTestDto> result = FacadeProvider.getSampleTestFacade().getByUuids(uuids); 
		return result;
	}
	
	@POST
	@Path("/push")
	public Integer postSampleTests(List<SampleTestDto> dtos) {
		SampleTestFacade sampleTestFacade = FacadeProvider.getSampleTestFacade();
		for (SampleTestDto dto : dtos) {
			sampleTestFacade.saveSampleTest(dto);
		}
		
		return dtos.size();
	}

	
	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getSampleTestFacade().getAllActiveUuids(userDto.getUuid());
		return uuids;
	}
}
