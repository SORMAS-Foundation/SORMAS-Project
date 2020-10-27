/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.campaign.data;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Stateless
@LocalBean
public class CampaignFormDataService extends AbstractAdoService<CampaignFormData> {

	public CampaignFormDataService() {
		super(CampaignFormData.class);
	}

	public Predicate createCriteriaFilter(CampaignFormDataCriteria criteria, CriteriaBuilder cb, Root<CampaignFormData> root) {
		Join<CampaignFormData, Campaign> campaignJoin = root.join(CampaignFormData.CAMPAIGN, JoinType.LEFT);
		Join<CampaignFormData, CampaignFormMeta> campaignFormJoin = root.join(CampaignFormData.CAMPAIGN_FORM_META, JoinType.LEFT);
		Join<CampaignFormData, Region> regionJoin = root.join(CampaignFormData.REGION, JoinType.LEFT);
		Join<CampaignFormData, District> districtJoin = root.join(CampaignFormData.DISTRICT, JoinType.LEFT);
		Join<CampaignFormData, Community> communityJoin = root.join(CampaignFormData.COMMUNITY, JoinType.LEFT);
		Predicate filter = null;

		if (criteria.getCampaign() != null) {
			filter = and(cb, filter, cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));
		}
		if (criteria.getCampaignFormMeta() != null) {
			filter = and(cb, filter, cb.equal(campaignFormJoin.get(CampaignFormMeta.UUID), criteria.getCampaignFormMeta().getUuid()));
		}
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(regionJoin.get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(districtJoin.get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getCommunity() != null) {
			filter = and(cb, filter, cb.equal(communityJoin.get(Community.UUID), criteria.getCommunity().getUuid()));
		}

		return filter;
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CampaignFormData> from) {
		return null;
	}

	public List<String> getAllActiveUuids() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CampaignFormData> from = cq.from(getElementClass());

		Predicate filter = cb.and();

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, cb.isFalse(from.get(CampaignFormData.ARCHIVED)), userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Campaign.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<CampaignFormData> getAllActiveAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormData> cq = cb.createQuery(CampaignFormData.class);
		Root<CampaignFormData> from = cq.from(getElementClass());

		Predicate filter = cb.and();

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, cb.isFalse(from.get(CampaignFormData.ARCHIVED)), userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date));
			if (dateFilter != null) {
				filter = cb.and(filter, dateFilter);
			}
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(AbstractDomainObject.CHANGE_DATE)));

		return em.createQuery(cq).getResultList();
	}
}
