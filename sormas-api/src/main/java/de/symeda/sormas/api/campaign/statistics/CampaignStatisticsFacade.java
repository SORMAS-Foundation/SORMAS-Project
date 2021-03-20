package de.symeda.sormas.api.campaign.statistics;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CampaignStatisticsFacade {

	List<CampaignStatisticsDto> queryCampaignStatistics(
		CampaignFormDataCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties);

	long count(CampaignFormDataCriteria criteria);
}
