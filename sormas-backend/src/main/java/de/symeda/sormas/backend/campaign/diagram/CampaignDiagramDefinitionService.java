package de.symeda.sormas.backend.campaign.diagram;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class CampaignDiagramDefinitionService extends AbstractAdoService<CampaignDiagramDefinition> {

	public CampaignDiagramDefinitionService() {
		super(CampaignDiagramDefinition.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CampaignDiagramDefinition> from) {
		return null;
	}
}
