package de.symeda.sormas.api.campaign.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;

public class CampaignStatisticsDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "CampaignStatistics";

	public static final String CAMPAIGN = "campaign";
	public static final String FORM = "form";
	public static final String AREA = "area";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String FORM_COUNT = "formCount";

	private String campaign;
	private String form;
	private String area;
	private String region;
	private String district;
	private String community;
	private long formCount;

	private List<CampaignFormDataEntry> statisticsData;

	public CampaignStatisticsDto(String campaign, String form, String area, String region, String district, String community, long formCount) {
		this.campaign = campaign;
		this.form = form;
		this.area = area;
		this.region = region;
		this.district = district;
		this.community = community;
		this.formCount = formCount;

		this.statisticsData = new ArrayList<>();
	}

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public long getFormCount() {
		return formCount;
	}

	public void setFormCount(long formCount) {
		this.formCount = formCount;
	}

	public List<CampaignFormDataEntry> getStatisticsData() {
		return statisticsData;
	}

	public void setStatisticsData(List<CampaignFormDataEntry> statisticsData) {
		this.statisticsData = statisticsData;
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
