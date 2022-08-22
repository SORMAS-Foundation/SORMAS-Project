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

import java.util.Collections;
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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataReferenceDto;
import de.symeda.sormas.api.campaign.data.MapCampaignDataDto;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.utils.CaseJoins;

@Stateless
@LocalBean
public class CampaignFormDataService extends AdoServiceWithUserFilter<CampaignFormData> {

	public CampaignFormDataService() {
		super(CampaignFormData.class);
	}

	public Predicate createCriteriaFilter(CampaignFormDataCriteria criteria, CriteriaBuilder cb,
			Root<CampaignFormData> root) {
		Join<CampaignFormData, Campaign> campaignJoin = root.join(CampaignFormData.CAMPAIGN, JoinType.LEFT);
		Join<CampaignFormData, CampaignFormMeta> campaignFormJoin = root.join(CampaignFormData.CAMPAIGN_FORM_META,
				JoinType.LEFT);
		Join<CampaignFormData, Area> areaJoin = root.join(CampaignFormData.AREA, JoinType.LEFT);
		Join<CampaignFormData, Region> regionJoin = root.join(CampaignFormData.REGION, JoinType.LEFT);
		Join<CampaignFormData, District> districtJoin = root.join(CampaignFormData.DISTRICT, JoinType.LEFT);
		Join<CampaignFormData, Community> communityJoin = root.join(CampaignFormData.COMMUNITY, JoinType.LEFT);
		Predicate filter = null;

		if (criteria.getCampaign() != null && criteria.getFormType() == null) {
			filter = CriteriaBuilderHelper.and(cb, filter,
					cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));
		} else if (criteria.getCampaign() != null && criteria.getFormType() != null
				&& !"ALL PHASES".equals(criteria.getFormType())) {
			filter = CriteriaBuilderHelper.and(cb, filter,
					cb.and(cb.equal(campaignFormJoin.get(CampaignFormMeta.FORM_TYPE),
							criteria.getFormType().toLowerCase())),
					cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()),
					cb.isFalse(campaignJoin.get(Campaign.ARCHIVED)));
		} else if (criteria.getCampaign() == null && criteria.getFormType() != null
				&& !"ALL PHASES".equals(criteria.getFormType())) {
			filter = CriteriaBuilderHelper.and(cb, filter,
					cb.and(cb.equal(campaignFormJoin.get(CampaignFormMeta.FORM_TYPE),
							criteria.getFormType().toLowerCase()), cb.isFalse(campaignJoin.get(Campaign.ARCHIVED)),
							cb.isFalse(campaignJoin.get(Campaign.DELETED))));
		} else {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(campaignJoin.get(Campaign.ARCHIVED), false),
					cb.isNull(campaignJoin.get(Campaign.ARCHIVED))));

		}

		if (criteria.getCampaignFormMeta() != null) {
			// System.out.println("=======%%%%%%%%%%%%%%%=======
			// "+criteria.getCampaignFormMeta().getUuid());
			filter = CriteriaBuilderHelper.and(cb, filter,
					cb.equal(campaignFormJoin.get(CampaignFormMeta.UUID), criteria.getCampaignFormMeta().getUuid()));
		}
		if (criteria.getArea() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter,
					cb.equal(areaJoin.get(Area.UUID), criteria.getArea().getUuid()));
		}
		if (criteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter,
					cb.equal(regionJoin.get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter,
					cb.equal(districtJoin.get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter,
					cb.equal(communityJoin.get(Community.UUID), criteria.getCommunity().getUuid()));
		}
		if (criteria.getFormDate() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter,
					cb.greaterThanOrEqualTo(root.get(CampaignFormData.FORM_DATE),
							DateHelper.getStartOfDay(criteria.getFormDate())),
					cb.lessThanOrEqualTo(root.get(CampaignFormData.FORM_DATE),
							DateHelper.getEndOfDay(criteria.getFormDate())));
		}

		// System.out.println(filter);

		return filter;
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CampaignFormData> campaignPath) {
		final User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		Predicate filter = null;

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel != JurisdictionLevel.NATION) {
			switch (jurisdictionLevel) {
			case AREA:
				final Area area = currentUser.getArea();
				if (area != null) {
					filter = CriteriaBuilderHelper.or(cb, filter,
							cb.equal(campaignPath.get(CampaignFormData.AREA).get(Area.ID), area.getId()));
				}
				break;
			case REGION:
				final Region region = currentUser.getRegion();
				if (region != null) {
					filter = CriteriaBuilderHelper.or(cb, filter,
							cb.equal(campaignPath.get(CampaignFormData.REGION).get(Region.ID), region.getId()));
				}
				break;
			case DISTRICT:
				final District district = currentUser.getDistrict();
				if (district != null) {
					filter = CriteriaBuilderHelper.or(cb, filter,
							cb.equal(campaignPath.get(CampaignFormData.DISTRICT).get(District.ID), district.getId()));
				}
				break;
			case COMMUNITY:
				final Community community = currentUser.getCommunity();
				if (community != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, cb
							.equal(campaignPath.get(CampaignFormData.COMMUNITY).get(Community.ID), community.getId()));
				}
				break;
			default:
				return null;
			}
		}

		return filter;
	}

	public List<String> getAllActiveUuids() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CampaignFormData> from = cq.from(getElementClass());

		Predicate filter = cb.and();

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, cb.isFalse(from.get(CampaignFormData.ARCHIVED)), userFilter);
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
			filter = CriteriaBuilderHelper.and(cb, cb.isFalse(from.get(CampaignFormData.ARCHIVED)), userFilter);
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

	public List<CampaignFormData> getAllActive() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormData> cq = cb.createQuery(CampaignFormData.class);
		Root<CampaignFormData> from = cq.from(getElementClass());

		Predicate filter = cb.and();

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, cb.isFalse(from.get(CampaignFormData.ARCHIVED)), userFilter);
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(AbstractDomainObject.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public List<CampaignFormData> getByCampaignFormMeta_id(Long meta_id) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CampaignFormData> cq = cb.createQuery(CampaignFormData.class);
		Root<CampaignFormData> from = cq.from(getElementClass());

		Predicate filter = cb.and();

		// if (getCurrentUser() != null) {
		Predicate userFilter = createUserFilter(cb, cq, from);
		filter = CriteriaBuilderHelper.and(cb, cb.isFalse(from.get(CampaignFormData.ARCHIVED)), userFilter);
		// }

		Predicate predicateForMetaId = cb.equal(from.get(CampaignFormMeta.ID), meta_id);

		Predicate finalPredicate = cb.and(filter, predicateForMetaId);

		cq.where(finalPredicate);
		cq.orderBy(cb.desc(from.get(AbstractDomainObject.CHANGE_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public List<MapCampaignDataDto> getCampaignFormDataForMap() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapCampaignDataDto> cq = cb.createQuery(MapCampaignDataDto.class);
		Root<CampaignFormData> caze = cq.from(getElementClass());

		List<MapCampaignDataDto> result;

		cq.multiselect(caze.get(CampaignFormData.UUID), caze.get(CampaignFormData.LAT), caze.get(CampaignFormData.LON));

		result = em.createQuery(cq).getResultList();

		return result;
	}

}
