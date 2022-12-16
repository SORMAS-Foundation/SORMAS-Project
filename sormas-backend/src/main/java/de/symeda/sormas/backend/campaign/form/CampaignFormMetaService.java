package de.symeda.sormas.backend.campaign.form;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;

@Stateless
@LocalBean
public class CampaignFormMetaService extends AdoServiceWithUserFilterAndJurisdiction<CampaignFormMeta> {

	public CampaignFormMetaService() {
		super(CampaignFormMeta.class);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CampaignFormMeta> from) {
		return null;
	}

	@Override
	public Predicate createUserFilterForObsoleteSync(CriteriaBuilder cb, CriteriaQuery cq, From<?, CampaignFormMeta> from) {
		return null;
	}

	@Override
	protected Predicate createLimitedChangeDateFilter(CriteriaBuilder cb, From<?, CampaignFormMeta> from) {
		return null;
	}

	@Override
	protected Predicate createLimitedChangeDateFilterForObsoleteEntities(CriteriaBuilder cb, From<?, CampaignFormMeta> from) {
		return null;
	}

	public List<CampaignFormMetaReferenceDto> getCampaignFormMetasAsReferencesByCampaign(String uuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormMetaReferenceDto> cq = cb.createQuery(CampaignFormMetaReferenceDto.class);
		Root<Campaign> campaignRoot = cq.from(Campaign.class);
		Join<Campaign, CampaignFormMeta> campaignFormMetaJoin = campaignRoot.join(Campaign.CAMPAIGN_FORM_METAS);
		Predicate filter = cb.equal(campaignRoot.get(Campaign.UUID), uuid);
		cq = cq.where(filter);
		cq.multiselect(campaignFormMetaJoin.get(CampaignFormMeta.UUID), campaignFormMetaJoin.get(CampaignFormMeta.FORM_NAME));
		return em.createQuery(cq).getResultList();
	}
}
