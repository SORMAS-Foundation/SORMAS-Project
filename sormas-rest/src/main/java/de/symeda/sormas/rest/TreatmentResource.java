package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/treatments")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class TreatmentResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<TreatmentDto> getAllTreatments(@PathParam("since") long since) {
		return FacadeProvider.getTreatmentFacade().getAllActiveTreatmentsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<TreatmentDto> getAllTreatments(@PathParam("since") long since, @PathParam("size") int size, @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getTreatmentFacade().getAllActiveTreatmentsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	public List<TreatmentDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getTreatmentFacade().getByUuids(uuids);
	}

	@POST
	@Path("/push")
	public List<PushResult> postTreatments(@Valid List<TreatmentDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getTreatmentFacade()::saveTreatment);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getTreatmentFacade().getAllActiveUuids();
	}

	@POST
	@Path("/indexList")
	public List<TreatmentIndexDto> getIndexList(@RequestBody CriteriaWithSorting<TreatmentCriteria> criteriaWithSorting) {
		return FacadeProvider.getTreatmentFacade().getIndexList(criteriaWithSorting.getCriteria());
	}
}
