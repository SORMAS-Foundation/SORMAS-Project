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
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/campaignFormMeta")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Campaign Form Meta Resource",
	description = "Meta information corresponding to medical survey data in a scientfic context.\n\n"
		+ "See also: **CampaignResource**, **CampaignFormDataResource**.")
public class CampaignFormMetaResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all meta information entries on campaign survey forms from a date in the past until now.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of campaign form meta information for the given interval.",
		useReturnTypeSchema = true)
	public List<CampaignFormMetaDto> getAllCampaignFormMeta(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getCampaignFormMetaFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get meta information entries on campaign survey forms based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of campaign form meta information by their UUIDs.", useReturnTypeSchema = true)
	public List<CampaignFormMetaDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query campaign form meta information entries.", required = true) List<String> uuids) {
		return FacadeProvider.getCampaignFormMetaFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available campaign form meta information entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getCampaignFormMetaFacade().getAllUuids();
	}
}
