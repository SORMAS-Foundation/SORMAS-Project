package de.symeda.sormas.api.campaign;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.utils.SortProperty;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface CampaignFacade {

	List<CampaignIndexDto> getIndexList(CampaignCriteria campaignCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	List<CampaignReferenceDto> getAllCampaignsAsReference();

	CampaignReferenceDto getLastStartedCampaign();

	long count(CampaignCriteria campaignCriteria);

	CampaignDto saveCampaign(CampaignDto dto);

	CampaignDto getByUuid(String uuid);

	List<CampaignDashboardElement> getCampaignDashboardElements(String campaignUuid);

	boolean isArchived(String uuid);

	void deleteCampaign(String uuid);

	void archiveOrDearchiveCampaign(String campaignUuid, boolean archive);
}
