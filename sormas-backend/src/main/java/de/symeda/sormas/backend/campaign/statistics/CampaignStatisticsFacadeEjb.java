package de.symeda.sormas.backend.campaign.statistics;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsCriteria;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsDto;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsFacade;
import de.symeda.sormas.api.utils.SortProperty;

@Stateless(name = "CampaignStatisticsFacade")
public class CampaignStatisticsFacadeEjb implements CampaignStatisticsFacade {

	@EJB
	private CampaignStatisticsService campaignStatisticsService;

	@Override
	public List<CampaignStatisticsDto> queryCampaignStatistics(
		CampaignStatisticsCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		return campaignStatisticsService.getCampaignStatistics(criteria, first, max, sortProperties);
	}

	@Override
	public long count(CampaignStatisticsCriteria criteria) {
		return campaignStatisticsService.count(criteria);
	}

	@LocalBean
	@Stateless
	public static class CampaignStatisticsFacadeEjbLocal extends CampaignStatisticsFacadeEjb {
	}
}
