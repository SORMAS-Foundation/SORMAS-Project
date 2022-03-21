package de.symeda.sormas.api.campaign;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CampaignFacade {

	List<CampaignIndexDto> getIndexList(CampaignCriteria campaignCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	List<CampaignReferenceDto> getAllActiveCampaignsAsReference();
	
	CampaignReferenceDto getLastStartedCampaign();

	long count(CampaignCriteria campaignCriteria);

	CampaignDto saveCampaign(@Valid CampaignDto dto);

	CampaignDto getByUuid(String uuid);

	List<CampaignDashboardElement> getCampaignDashboardElements(String campaignUuid);

	boolean isArchived(String uuid);

	void deleteCampaign(String uuid);
	
	void cloneCampaign(String uuid);

	void archiveOrDearchiveCampaign(String campaignUuid, boolean archive);

	CampaignReferenceDto getReferenceByUuid(String uuid);

	boolean exists(String uuid);

	List<CampaignDto> getAllAfter(Date campaignChangeDate);

	List<CampaignDto> getByUuids(List<String> uuids);

	List<String> getAllActiveUuids();

	void validate(CampaignReferenceDto campaignReferenceDto);
}
