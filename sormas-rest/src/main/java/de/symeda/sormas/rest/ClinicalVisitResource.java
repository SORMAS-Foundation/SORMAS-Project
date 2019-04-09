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
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.user.UserReferenceDto;

@Path("/clinicalvisits")
@Produces({ MediaType.APPLICATION_JSON + "; charset=UTF-8" })
@Consumes({ MediaType.APPLICATION_JSON + "; charset=UTF-8" })
@RolesAllowed("USER")
public class ClinicalVisitResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<ClinicalVisitDto> getAllVisits(@Context SecurityContext sc, @PathParam("since") long since) {

		UserReferenceDto userDto = FacadeProvider.getUserFacade()
				.getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<ClinicalVisitDto> result = FacadeProvider.getClinicalVisitFacade().getAllActiveClinicalVisitsAfter(new Date(since),
				userDto.getUuid());
		return result;
	}

	@POST
	@Path("/query")
	public List<ClinicalVisitDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {

		List<ClinicalVisitDto> result = FacadeProvider.getClinicalVisitFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	public List<PushResult> postVisits(List<ClinicalVisitDto> dtos) {

		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getClinicalVisitFacade()::saveClinicalVisit);
		return result;
	}

	@GET	
	@Path("/uuids")
	public List<String> getAllActiveUuids(@Context SecurityContext sc) {

		UserReferenceDto userDto = FacadeProvider.getUserFacade()
				.getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getClinicalVisitFacade().getAllActiveUuids(userDto.getUuid());
		return uuids;
	}
	
}
