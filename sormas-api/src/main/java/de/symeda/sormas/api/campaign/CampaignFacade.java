package de.symeda.sormas.api.campaign;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.CoreBaseFacade;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CampaignFacade extends CoreBaseFacade<CampaignDto, CampaignIndexDto, CampaignReferenceDto, CampaignCriteria> {

	List<CampaignReferenceDto> getAllActiveCampaignsAsReference();

	CampaignReferenceDto getLastStartedCampaign();

	List<CampaignDashboardElement> getCampaignDashboardElements(String campaignUuid);

	boolean isArchived(String uuid);

	void deleteCampaign(String uuid);

	List<String> getAllActiveUuids();

	void validate(CampaignReferenceDto campaignReferenceDto);
}
