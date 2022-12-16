package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/campaigns")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Campaign Resource",
	description = "Medical survey campaigns in a scientific context.\n\n" + "See also: **CampaignFormDataResource**, **CampaignFormMetaResource**.")
public class CampaignResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all campaign data from a date in the past until now.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of scientific survey campaigns for the given interval.",
		useReturnTypeSchema = true)
	public List<CampaignDto> getAllCampaignFormData(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getCampaignFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get campaign data entries based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of scientific survey campaigns by their UUIDs.", useReturnTypeSchema = true)
	public List<CampaignDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query conducted campaigns.", required = true) List<String> uuids) {
		return FacadeProvider.getCampaignFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available campaign data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getCampaignFacade().getAllActiveUuids();
	}
}
