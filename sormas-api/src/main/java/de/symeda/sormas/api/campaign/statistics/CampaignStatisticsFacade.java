package de.symeda.sormas.api.campaign.statistics;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CampaignStatisticsFacade {

	List<CampaignStatisticsDto> getCampaignStatistics(CampaignStatisticsCriteria criteria, List<SortProperty> sortProperties);

	long count(CampaignStatisticsCriteria criteria);
}
