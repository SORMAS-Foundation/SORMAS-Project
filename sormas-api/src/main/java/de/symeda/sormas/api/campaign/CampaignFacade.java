package de.symeda.sormas.api.campaign;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;

@Remote
public interface CampaignFacade extends CoreFacade<CampaignDto, CampaignIndexDto, CampaignReferenceDto, CampaignCriteria> {

	List<CampaignReferenceDto> getAllActiveCampaignsAsReference();

	CampaignReferenceDto getLastStartedCampaign();

	List<CampaignDashboardElement> getCampaignDashboardElements(String campaignUuid);

	List<String> getAllActiveUuids();

	void validate(CampaignReferenceDto campaignReferenceDto);

	boolean isCampaignEditAllowed(String caseUuid, boolean withArchive);
}
