package de.symeda.sormas.api.campaign.statistics;

import java.io.Serializable;
import java.util.Objects;

public class CampaignStatisticsGroupingDto implements Serializable, Cloneable {

	private String campaign;
	private String form;
	private String area;
	private String region;
	private String district;
	private String community;

	public CampaignStatisticsGroupingDto(String campaign, String form, String area, String region, String district, String community) {
		this.campaign = campaign;
		this.form = form;
		this.area = area;
		this.region = region;
		this.district = district;
		this.community = community;
	}

	public String getCampaign() {
		return campaign;
	}

	public String getForm() {
		return form;
	}

	public String getArea() {
		return area;
	}

	public String getRegion() {
		return region;
	}

	public String getDistrict() {
		return district;
	}

	public String getCommunity() {
		return community;
	}

	@Override
	public int hashCode() {
		return Objects.hash(campaign, form, area, region, district, community);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CampaignStatisticsGroupingDto)) {
			return false;
		}
		CampaignStatisticsGroupingDto campaignStatisticsGroupingDto = (CampaignStatisticsGroupingDto) o;
		return this.campaign.equals(campaignStatisticsGroupingDto.getCampaign())
			&& this.form.equals(campaignStatisticsGroupingDto.getForm())
			&& this.area.equals(campaignStatisticsGroupingDto.getArea())
			&& this.region.equals(campaignStatisticsGroupingDto.getRegion())
			&& this.district.equals(campaignStatisticsGroupingDto.getDistrict())
			&& this.community.equals(campaignStatisticsGroupingDto.getCommunity());
	}
}
