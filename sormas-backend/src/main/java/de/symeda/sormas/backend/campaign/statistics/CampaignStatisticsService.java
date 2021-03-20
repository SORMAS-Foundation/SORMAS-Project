package de.symeda.sormas.backend.campaign.statistics;

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
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class CampaignStatisticsService extends AdoServiceWithUserFilter<CampaignStatistics> {

	public CampaignStatisticsService() {
		super(CampaignStatistics.class);
	}

	public Predicate createCriteriaFilter(CampaignFormDataCriteria criteria, CriteriaBuilder cb, Root<CampaignStatistics> root) {
		Join<CampaignFormData, Campaign> campaignJoin = root.join(CampaignFormData.CAMPAIGN, JoinType.LEFT);
		Join<CampaignFormData, CampaignFormMeta> campaignFormJoin = root.join(CampaignFormData.CAMPAIGN_FORM_META, JoinType.LEFT);
		Join<CampaignFormData, Region> regionJoin = root.join(CampaignFormData.REGION, JoinType.LEFT);
		Join<CampaignFormData, District> districtJoin = root.join(CampaignFormData.DISTRICT, JoinType.LEFT);
		Join<CampaignFormData, Community> communityJoin = root.join(CampaignFormData.COMMUNITY, JoinType.LEFT);
		Predicate filter = null;

		if (criteria.getCampaign() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(campaignJoin.get(Campaign.UUID), criteria.getCampaign().getUuid()));
		} else {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.or(cb.equal(campaignJoin.get(Campaign.ARCHIVED), false), cb.isNull(campaignJoin.get(Campaign.ARCHIVED))));
		}
		if (criteria.getCampaignFormMeta() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(campaignFormJoin.get(CampaignFormMeta.UUID), criteria.getCampaignFormMeta().getUuid()));
		}
		if (criteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(regionJoin.get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(districtJoin.get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(communityJoin.get(Community.UUID), criteria.getCommunity().getUuid()));
		}
		if (criteria.getFormDate() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.greaterThanOrEqualTo(root.get(CampaignFormData.FORM_DATE), DateHelper.getStartOfDay(criteria.getFormDate())),
				cb.lessThanOrEqualTo(root.get(CampaignFormData.FORM_DATE), DateHelper.getEndOfDay(criteria.getFormDate())));
		}

		return filter;
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CampaignStatistics> campaignPath) {
		final User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		Predicate filter = null;

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel != JurisdictionLevel.NATION) {
			switch (jurisdictionLevel) {
			case REGION:
				final Region region = currentUser.getRegion();
				if (region != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(campaignPath.get(CampaignFormData.REGION).get(Region.ID), region.getId()));
				}
				break;
			case DISTRICT:
				final District district = currentUser.getDistrict();
				if (district != null) {
					filter = CriteriaBuilderHelper
						.or(cb, filter, cb.equal(campaignPath.get(CampaignFormData.DISTRICT).get(District.ID), district.getId()));
				}
				break;
			case COMMUNITY:
				final Community community = currentUser.getCommunity();
				if (community != null) {
					filter = CriteriaBuilderHelper
						.or(cb, filter, cb.equal(campaignPath.get(CampaignFormData.COMMUNITY).get(Community.ID), community.getId()));
				}
				break;
			default:
				return null;
			}
		}

		return filter;
	}
}
