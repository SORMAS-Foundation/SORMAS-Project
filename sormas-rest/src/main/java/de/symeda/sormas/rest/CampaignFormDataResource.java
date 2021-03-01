package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
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

@Path("/campaignFormData")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class CampaignFormDataResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<CampaignFormDataDto> getAllCampaignFormData(@PathParam("since") long since) {
		return FacadeProvider.getCampaignFormDataFacade().getAllActiveAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<CampaignFormDataDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getCampaignFormDataFacade().getByUuids(uuids);
	}

	@POST
	@Path("/push")
	public List<PushResult> postCampaignFormData(@Valid List<CampaignFormDataDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getCampaignFormDataFacade()::saveCampaignFormData);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getCampaignFormDataFacade().getAllActiveUuids();
	}
}
