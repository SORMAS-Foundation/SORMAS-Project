package de.symeda.sormas.api.campaign.statistics;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface CampaignStatisticsFacade {

	List<CampaignStatisticsDto> getCampaignStatistics(CampaignStatisticsCriteria criteria);
}
