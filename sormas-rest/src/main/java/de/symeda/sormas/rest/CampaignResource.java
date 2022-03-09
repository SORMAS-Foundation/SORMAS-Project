package de.symeda.sormas.rest;

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

@Path("/campaigns")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class CampaignResource {

	@GET
	@Path("/all/{since}")
	public List<CampaignDto> getAllCampaignFormData(@PathParam("since") long since) {
		return FacadeProvider.getCampaignFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<CampaignDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getCampaignFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getCampaignFacade().getAllActiveUuids();
	}
}
