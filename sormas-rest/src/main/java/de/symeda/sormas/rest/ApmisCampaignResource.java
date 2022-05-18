package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;


@Path("/v1")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"REST_USER" })
public class ApmisCampaignResource extends EntityDtoResource {
	
	@GET
	@Path("/campaigns") //return a list of all active campaigns 
	public List<CampaignDto> getAllCampaigns() {
		return FacadeProvider.getCampaignFacade().getAllActive();
	}
	 
	@GET
	@Path("/campaigns/{uuid}") //return a campaign by its UUID
	public CampaignDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getCampaignFacade().getByUuid(uuid); 
	}
	
	@GET
	@Path("/campaigns/{uuid}/forms") //return a campaign's forms //next: use the uuid of the form to get out all the data associated with that form and the campaign
	public Set<CampaignFormMetaReferenceDto> getCampaignForms(@PathParam("uuid") String uuid) {
		return FacadeProvider.getCampaignFacade().getByUuid(uuid).getCampaignFormMetas();
	}
	
	@GET
	@Path("/campaigns/{campaigns_uuid}/forms/{forms_uuid}") //should return the data for the specific form //I should write a custom query here that checks the campaign by meta and campaign
	public List<CampaignFormDataDto> getCampaignFormData(@PathParam("campaigns_uuid") String campaign_uuid, @PathParam("forms_uuid") String form_uuid) {
		return FacadeProvider.getCampaignFormDataFacade().getCampaignFormData(campaign_uuid, form_uuid);
	}
	

	
	
	
}
