package de.symeda.sormas.backend.campaign.form;

import de.symeda.sormas.backend.common.AbstractAdoService;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

@Stateless
@LocalBean
public class CampaignFormMetaService extends AbstractAdoService<CampaignFormMeta> {

	public CampaignFormMetaService() {
		super(CampaignFormMeta.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<CampaignFormMeta, CampaignFormMeta> from) {
		return null;
	}
}
