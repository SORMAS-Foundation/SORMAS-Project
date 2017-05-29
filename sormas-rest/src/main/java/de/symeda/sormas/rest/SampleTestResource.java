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
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestFacade;

@Path("/sampletests")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class SampleTestResource {
	
	@GET
	@Path("/all/{user}/{since}")
	public List<SampleTestDto> getAllSampleTests(@PathParam("user") String userUuid, @PathParam("since") long since) {
		List<SampleTestDto> sampleTests = FacadeProvider.getSampleTestFacade().getAllSampleTestsAfter(new Date(since), userUuid);
		return sampleTests;
	}
	
	@POST
	@Path("/push")
	public Long postSampleTests(List<SampleTestDto> dtos) {
		SampleTestFacade sampleTestFacade = FacadeProvider.getSampleTestFacade();
		for (SampleTestDto dto : dtos) {
			sampleTestFacade.saveSampleTest(dto);
		}
		
		return new Date().getTime();
	}

}
