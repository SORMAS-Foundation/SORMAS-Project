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
import de.symeda.sormas.api.report.AggregateCaseCountDto;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/aggregatereports")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Aggregate Report Resource", description = "Management of aggregated reports generated for an epi week.")
public class AggregateReportResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all aggregated reports from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of aggregated reports for the given interval.", useReturnTypeSchema = true)
	public List<AggregateReportDto> getAllAggregateReports(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getAggregateReportFacade().getAllAggregateReportsAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get aggregated report data entries based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of aggregated reports by their UUIDs.", useReturnTypeSchema = true)
	public List<AggregateReportDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query aggregated report entries.", required = true) List<String> uuids) {
		List<AggregateReportDto> result = FacadeProvider.getAggregateReportFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of aggregated reports to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded aggregated report.",
		useReturnTypeSchema = true)
	public List<PushResult> postAggregateReports(
		@RequestBody(description = "List of AggregatedReportDtos to be added to the server.", required = true) @Valid List<AggregateReportDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getAggregateReportFacade()::saveAggregateReport);
		return result;
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a list of AggregatedCaseCounts based on AggregatedReportCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of aggregated case counts that have met the filter criteria.",
		useReturnTypeSchema = true)
	public List<AggregateCaseCountDto> getIndexList(
		@RequestBody(description = "Aggregated report based query-filter.", required = true) AggregateReportCriteria criteria) {
		return FacadeProvider.getAggregateReportFacade().getIndexList(criteria);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available aggregated reports.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getAggregateReportFacade().getAllUuids();
	}
}
