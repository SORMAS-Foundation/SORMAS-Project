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
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.user.UserReferenceDto;

@Path("/samples")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class SampleResource {
	
	@GET
	@Path("/all/{since}")
	public List<SampleDto> getAllSamples(@Context SecurityContext sc, @PathParam("since") long since) {

		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<SampleDto> samples = FacadeProvider.getSampleFacade().getAllActiveSamplesAfter(new Date(since), userDto.getUuid());
		return samples;
	}
	
	@POST
	@Path("/query")
	public List<SampleDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {

		List<SampleDto> result = FacadeProvider.getSampleFacade().getByUuids(uuids); 
		return result;
	}
		
	@POST
	@Path("/push")
	public Integer postSamples(List<SampleDto> dtos) {
		SampleFacade sampleFacade = FacadeProvider.getSampleFacade();
		for (SampleDto dto : dtos) {
			sampleFacade.saveSample(dto);
		}
		
		return dtos.size();
	}
	
	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getSampleFacade().getAllActiveUuids(userDto.getUuid());
		return uuids;
	}
}
