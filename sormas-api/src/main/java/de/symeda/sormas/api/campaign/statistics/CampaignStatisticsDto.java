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

	private final CampaignStatisticsGroupingDto campaignStatisticsGroupingDto;
	private long formCount;

	private List<CampaignFormDataEntry> statisticsData;

	public CampaignStatisticsDto(CampaignStatisticsGroupingDto campaignStatisticsGroupingDto, long formCount) {
		this.campaignStatisticsGroupingDto = campaignStatisticsGroupingDto;
		this.formCount = formCount;

		this.statisticsData = new ArrayList<>();
	}

	public String getCampaign() {
		return campaignStatisticsGroupingDto.getCampaign();
	}

	public String getForm() {
		return campaignStatisticsGroupingDto.getForm();
	}

	public String getArea() {
		return campaignStatisticsGroupingDto.getArea();
	}

	public String getRegion() {
		return campaignStatisticsGroupingDto.getRegion();
	}

	public String getDistrict() {
		return campaignStatisticsGroupingDto.getDistrict();
	}

	public String getCommunity() {
		return campaignStatisticsGroupingDto.getCommunity();
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

	public void addStatisticsData(CampaignFormDataEntry value) {
		this.statisticsData.add(value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(campaignStatisticsGroupingDto, formCount);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CampaignStatisticsDto)) {
			return false;
		}
		CampaignStatisticsDto campaignStatisticsDto = (CampaignStatisticsDto) o;
		return this.campaignStatisticsGroupingDto.equals(campaignStatisticsDto.campaignStatisticsGroupingDto)
			&& this.formCount == campaignStatisticsDto.formCount;
	}
}
