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
		if (!(o instanceof CampaignStatisticsDto)) {
			return false;
		}
		CampaignStatisticsDto campaignStatisticsDto = (CampaignStatisticsDto) o;
		return this.campaign.equals(campaignStatisticsDto.getCampaign())
			&& this.form.equals(campaignStatisticsDto.getForm())
			&& this.area.equals(campaignStatisticsDto.getArea())
			&& this.region.equals(campaignStatisticsDto.getRegion())
			&& this.district.equals(campaignStatisticsDto.getDistrict())
			&& this.community.equals(campaignStatisticsDto.getCommunity());
	}
}
