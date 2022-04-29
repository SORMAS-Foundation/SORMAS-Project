package de.symeda.sormas.api.campaign.statistics;

import java.io.Serializable;

import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class CampaignStatisticsCriteria extends BaseCriteria implements Serializable {

	public static final String CAMPAIGN = "campaign";
	public static final String CAMPAIGN_FORM_META = "campaignFormMeta";
	public static final String AREA = "area";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";

	private CampaignReferenceDto campaign;
	private CampaignFormMetaReferenceDto campaignFormMeta;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community; 

	private CampaignJurisdictionLevel groupingLevel;

	public CampaignReferenceDto getCampaign() {
		return campaign;
	}

	public void setCampaign(CampaignReferenceDto campaign) {
		this.campaign = campaign;
	}

	public CampaignFormMetaReferenceDto getCampaignFormMeta() {
		return campaignFormMeta;
	}

	public void setCampaignFormMeta(CampaignFormMetaReferenceDto campaignFormMeta) {
		this.campaignFormMeta = campaignFormMeta;
	}

	public AreaReferenceDto getArea() {
		return area;
	}

	public void setArea(AreaReferenceDto area) {
		this.area = area;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public CampaignJurisdictionLevel getGroupingLevel() {
		return groupingLevel;
	}

	public void setGroupingLevel(CampaignJurisdictionLevel groupingLevel) {
		this.groupingLevel = groupingLevel;
	}
}
