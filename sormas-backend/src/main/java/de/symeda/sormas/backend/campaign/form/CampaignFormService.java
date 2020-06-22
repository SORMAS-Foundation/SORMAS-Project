package de.symeda.sormas.backend.campaign.form;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class CampaignFormService extends AbstractAdoService<CampaignForm> {

	public CampaignFormService() {
		super(CampaignForm.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<CampaignForm, CampaignForm> from) {
		return null;
	}
}
