package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class CampaignDiagramCriteria extends BaseCriteria implements Serializable {

	private CampaignReferenceDto campaign;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy;

	public CampaignDiagramCriteria(
		CampaignReferenceDto campaign,
		AreaReferenceDto area,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		this.campaign = campaign;
		this.area = area;
		this.region = region;
		this.district = district;
		this.campaignJurisdictionLevelGroupBy = campaignJurisdictionLevelGroupBy;
	}

	public CampaignReferenceDto getCampaign() {
		return campaign;
	}

	public void setCampaign(CampaignReferenceDto campaign) {
		this.campaign = campaign;
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

	public CampaignJurisdictionLevel getCampaignJurisdictionLevelGroupBy() {
		return campaignJurisdictionLevelGroupBy;
	}

	public void setCampaignJurisdictionLevelGroupBy(CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		this.campaignJurisdictionLevelGroupBy = campaignJurisdictionLevelGroupBy;
	}
}
