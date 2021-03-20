package de.symeda.sormas.backend.campaign.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsFacade;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsIndexDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CampaignStatisticsFacade")
public class CampaignStatisticsFacadeEjb implements CampaignStatisticsFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CampaignStatisticsService campaignStatisticsService;

	@Override
	public List<CampaignStatisticsIndexDto> getIndexList(
		CampaignFormDataCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		return new ArrayList<>();
	}

	@Override
	public long count(CampaignFormDataCriteria criteria) {
		return 0;
	}

	@LocalBean
	@Stateless
	public static class CampaignStatisticsFacadeEjbLocal extends CampaignStatisticsFacadeEjb {
	}
}
