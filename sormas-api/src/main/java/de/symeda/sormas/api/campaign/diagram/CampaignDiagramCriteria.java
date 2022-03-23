package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;

import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class CampaignDiagramCriteria extends BaseCriteria implements Serializable {

	private CampaignReferenceDto campaign;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy;
	private String formType;

	public CampaignDiagramCriteria(
		CampaignReferenceDto campaign,
		AreaReferenceDto area,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy,
		String formType) {
		this.campaign = campaign;
		this.area = area;
		this.region = region;
		this.district = district;
		this.campaignJurisdictionLevelGroupBy = campaignJurisdictionLevelGroupBy;
		this.formType = formType;
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

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}
	
	
}
