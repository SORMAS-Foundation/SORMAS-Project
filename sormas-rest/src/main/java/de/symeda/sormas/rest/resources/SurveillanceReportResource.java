package de.symeda.sormas.rest.resources;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/surveillancereports")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Surveillance Report Resource", description = "Management of surveillance reports generated in relation to unique cases")
public class SurveillanceReportResource extends EntityDtoResource {

	@POST
	@Path("/query/cases")
	@Operation(summary = "Get surveillance reports based on the unique IDs (UUIDs) of the cases the reports are about.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of surveillance reports based on the UUIDs of associated cases.",
		useReturnTypeSchema = true)
	public List<SurveillanceReportDto> getByCaseUuids(
		@RequestBody(description = "List of case UUIDs used to query surveillance reports.", required = true) List<String> uuids) {
		List<SurveillanceReportDto> result = FacadeProvider.getSurveillanceReportFacade().getByCaseUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of surveillance reports to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded entry.",
		useReturnTypeSchema = true)
	public List<PushResult> postCaseReports(
		@RequestBody(description = "List of SurveillanceReports to be added to the existing surveillance report data entries.",
			required = true) @Valid List<SurveillanceReportDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getSurveillanceReportFacade()::saveSurveillanceReport);
	}
}
