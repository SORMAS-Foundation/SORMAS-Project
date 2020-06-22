package de.symeda.sormas.api.campaign;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CampaignFacade {

	List<CampaignIndexDto> getIndexList(CampaignCriteria campaignCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(CampaignCriteria campaignCriteria);

	CampaignDto saveCampaign(CampaignDto dto);

	CampaignDto getByUuid(String uuid);

	boolean isArchived(String uuid);

	void deleteCampaign(String uuid);

	void archiveOrDearchiveCampaign(String campaignUuid, boolean archive);

}
