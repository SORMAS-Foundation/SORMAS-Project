package de.symeda.sormas.api.campaign;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.utils.SortProperty;

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

	CampaignReferenceDto getReferenceByUuid(String uuid);

	boolean exists(String uuid);

	CampaignSyncDto getCampaignSyncData(CampaignChangeDatesDto changeDates);

	List<CampaignDto> getAllAfter(Date campaignChangeDate);
}
