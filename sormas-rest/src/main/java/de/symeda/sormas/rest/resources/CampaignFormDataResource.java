package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/campaignFormData")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Campaign Form Data Resource",
	description = "Medical survey data in a scientfic context.\n\n" + "See also: **CampainResource**, **CampaignFormMetaResource**.")
public class CampaignFormDataResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all data of filled campaign survey forms from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of campaign form data for the given interval.", useReturnTypeSchema = true)
	public List<CampaignFormDataDto> getAllCampaignFormData(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getCampaignFormDataFacade().getAllActiveAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get data of filled campaign survey forms based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of campaign form data by their UUIDs.", useReturnTypeSchema = true)
	public List<CampaignFormDataDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query campaign form data entries.", required = true) List<String> uuids) {
		return FacadeProvider.getCampaignFormDataFacade().getByUuids(uuids);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of campaign form data entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded entry.",
		useReturnTypeSchema = true)
	public List<PushResult> postCampaignFormData(
		@RequestBody(description = "List of CampainFormDataDtos to be added to the campaign's survey data entries.",
			required = true) @Valid List<CampaignFormDataDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getCampaignFormDataFacade()::saveCampaignFormData);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available campaign form data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getCampaignFormDataFacade().getAllActiveUuids();
	}
}
