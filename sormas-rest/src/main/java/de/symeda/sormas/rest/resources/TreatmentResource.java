package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/treatments")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Treatment resource", description = "Data about the treatment of diseases.\n\n" + "See also: **CaseResource**")
public class TreatmentResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all treatment data from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of all treatment data for the given interval.", useReturnTypeSchema = true)
	public List<TreatmentDto> getAllTreatments(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getTreatmentFacade().getAllActiveTreatmentsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of treatments that fulfill certain criteria.",
		description = "**-** tasks are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200", description = "Returns a list of treatments for the given interval.", useReturnTypeSchema = true)
	public List<TreatmentDto> getAllTreatments(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getTreatmentFacade().getAllActiveTreatmentsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get treatment data based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of treatment data by their UUIDs.", useReturnTypeSchema = true)
	public List<TreatmentDto> getByUuids(
		@RequestBody(description = "List of person UUIDs used to query treatment data entries.", required = true) List<String> uuids) {
		return FacadeProvider.getTreatmentFacade().getByUuids(uuids);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of treatment data entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded entry.",
		useReturnTypeSchema = true)
	public List<PushResult> postTreatments(
		@RequestBody(description = "List of TreatmentDtos to be added to the existing treatment data entries.",
			required = true) @Valid List<TreatmentDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getTreatmentFacade()::saveTreatment);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available treatment data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getTreatmentFacade().getAllActiveUuids();
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a list of treatment data based on TreatmentCriteria filter params.")
	@ApiResponse(description = "Returns a list of treatment data that met the filter criteria.", responseCode = "200", useReturnTypeSchema = true)
	public List<TreatmentIndexDto> getIndexList(
		@RequestBody(required = true,
			description = "Treatment-based query-filter criteria with sorting property.") CriteriaWithSorting<TreatmentCriteria> criteriaWithSorting) {
		return FacadeProvider.getTreatmentFacade().getIndexList(criteriaWithSorting.getCriteria());
	}
}
