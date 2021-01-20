package de.symeda.sormas.backend.campaign.diagram;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;

@Stateless
@LocalBean
public class CampaignDiagramDefinitionService extends AdoServiceWithUserFilter<CampaignDiagramDefinition> {

	public CampaignDiagramDefinitionService() {
		super(CampaignDiagramDefinition.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CampaignDiagramDefinition> from) {
		return null;
	}

	public boolean diagramExists(@NotNull String diagramId) {
		return exists((cb, root) -> cb.equal(root.get(CampaignDiagramDefinition.DIAGRAM_ID), diagramId));
	}

	public CampaignDiagramDefinition getByDiagramId(@NotNull String diagramId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> param = cb.parameter(String.class, CampaignDiagramDefinition.DIAGRAM_ID);
		CriteriaQuery<CampaignDiagramDefinition> cq = cb.createQuery(getElementClass());
		Root<CampaignDiagramDefinition> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(CampaignDiagramDefinition.DIAGRAM_ID), param));
		TypedQuery<CampaignDiagramDefinition> q = em.createQuery(cq).setParameter(param, diagramId);
		CampaignDiagramDefinition entity = q.getResultList().stream().findFirst().orElse(null);
		return entity;
	}
}
