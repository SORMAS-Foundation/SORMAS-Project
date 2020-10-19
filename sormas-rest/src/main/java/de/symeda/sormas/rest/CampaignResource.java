package de.symeda.sormas.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignChangeDatesDto;
import de.symeda.sormas.api.campaign.CampaignSyncDto;

@Path("/campaigns")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class CampaignResource {

	@POST
	@Path("/sync")
	public CampaignSyncDto getCampaignSyncData(CampaignChangeDatesDto changeDates) {
		return FacadeProvider.getCampaignFacade().getCampaignSyncData(changeDates);
	}
}
