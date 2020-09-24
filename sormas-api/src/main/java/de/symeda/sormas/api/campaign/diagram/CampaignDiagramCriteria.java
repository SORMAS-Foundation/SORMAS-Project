package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class CampaignDiagramCriteria extends BaseCriteria implements Serializable {

	private CampaignReferenceDto campaign;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;

	public CampaignDiagramCriteria(CampaignReferenceDto campaign, AreaReferenceDto area, RegionReferenceDto region, DistrictReferenceDto district) {
		this.campaign = campaign;
		this.area = area;
		this.region = region;
		this.district = district;
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
}
