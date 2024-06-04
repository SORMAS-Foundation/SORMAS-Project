package de.symeda.sormas.api.campaign;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;

@Remote
public interface CampaignFacade extends CoreFacade<CampaignDto, CampaignIndexDto, CampaignReferenceDto, CampaignCriteria> {

	List<CampaignReferenceDto> getAllActiveCampaignsAsReference();

	CampaignReferenceDto getLastStartedCampaign();

	CampaignDto getCampaignByUuid(String uuid);

	List<CampaignDashboardElement> getCampaignDashboardElements(String campaignUuid);

	List<String> getAllActiveUuids();

	void validate(CampaignReferenceDto campaignReferenceDto);

}
