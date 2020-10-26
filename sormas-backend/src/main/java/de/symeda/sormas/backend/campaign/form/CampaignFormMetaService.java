package de.symeda.sormas.backend.campaign.form;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class CampaignFormMetaService extends AbstractAdoService<CampaignFormMeta> {

	public CampaignFormMetaService() {
		super(CampaignFormMeta.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CampaignFormMeta> from) {
		return null;
	}

	public List<CampaignFormMetaReferenceDto> getCampaignFormMetasAsReferencesByCampaign(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormMetaReferenceDto> cq = cb.createQuery(CampaignFormMetaReferenceDto.class);
		Root<CampaignFormData> campaignFormMetaRoot = cq.from(CampaignFormData.class);
		Join<CampaignFormMeta, CampaignFormData> formJoin = campaignFormMetaRoot.join(CampaignFormData.CAMPAIGN_FORM_META);
		Join<CampaignFormData, Campaign> campaignJoin = campaignFormMetaRoot.join(CampaignFormData.CAMPAIGN, JoinType.LEFT);
		Predicate filter = cb.equal(campaignJoin.get(Campaign.UUID), uuid);
		cq = cq.where(filter);
		cq.multiselect(campaignJoin.get(CampaignFormMeta.UUID), formJoin.get(CampaignFormMeta.FORM_NAME));
		TypedQuery query = em.createQuery(cq);
		List<CampaignFormMetaReferenceDto> result = query.getResultList();
		return result;
	}
}
