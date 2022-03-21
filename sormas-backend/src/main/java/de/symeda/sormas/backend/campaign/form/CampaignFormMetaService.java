package de.symeda.sormas.backend.campaign.form;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.vladmihalcea.hibernate.type.util.SQLExtractor;

import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class CampaignFormMetaService extends AdoServiceWithUserFilter<CampaignFormMeta> {

	public CampaignFormMetaService() {
		super(CampaignFormMeta.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CampaignFormMeta> from) {
		return null;
	}

	public List<CampaignFormMeta> getAllAfter(Date since, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormMeta> cq = cb.createQuery(getElementClass());
		Root<CampaignFormMeta> root = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, root);
		if (since != null) {
			Predicate dateFilter = createChangeDateFilter(cb, root, since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(root.get(AbstractDomainObject.CHANGE_DATE)));

		List<CampaignFormMeta> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<CampaignFormMetaReferenceDto> getCampaignFormMetasAsReferencesByCampaign(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormMetaReferenceDto> cq = cb.createQuery(CampaignFormMetaReferenceDto.class);
		Root<Campaign> campaignRoot = cq.from(Campaign.class);
		Join<Campaign, CampaignFormMeta> campaignFormMetaJoin = campaignRoot.join(Campaign.CAMPAIGN_FORM_METAS);
		Predicate filter = cb.equal(campaignRoot.get(Campaign.UUID), uuid);
		//TODO: post campaign implementations
		cq = cq.where(filter);
		cq.multiselect(campaignFormMetaJoin.get(CampaignFormMeta.UUID), campaignFormMetaJoin.get(CampaignFormMeta.FORM_NAME));
		System.out.println("SSSSSSSSSSSSSSSSSQLSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"+SQLExtractor.from(em.createQuery(cq)));
		return em.createQuery(cq).getResultList();
	}
	
	public List<CampaignFormMetaReferenceDto> getCampaignFormMetasAsReferencesByCampaignandRound(String round, String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormMetaReferenceDto> cq = cb.createQuery(CampaignFormMetaReferenceDto.class);
		Root<Campaign> campaignRoot = cq.from(Campaign.class);
		Join<Campaign, CampaignFormMeta> campaignFormMetaJoin = campaignRoot.join(Campaign.CAMPAIGN_FORM_METAS);
		Predicate filterc = cb.equal(campaignRoot.get(Campaign.UUID), uuid);
		Predicate filterx = cb.equal(campaignFormMetaJoin.get(CampaignFormMeta.FORM_TYPE), round);
		
		Predicate filter = cb.and(filterc, filterx);
		//TODO: post campaign implementations
		cq = cq.where(filter);
		cq.multiselect(campaignFormMetaJoin.get(CampaignFormMeta.UUID), campaignFormMetaJoin.get(CampaignFormMeta.FORM_NAME));
		System.out.println("SSSSSSSSS44SSSSSSSSSS"+SQLExtractor.from(em.createQuery(cq)));
		return em.createQuery(cq).getResultList();
	}
}
