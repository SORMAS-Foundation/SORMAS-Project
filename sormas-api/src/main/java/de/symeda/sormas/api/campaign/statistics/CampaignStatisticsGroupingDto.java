package de.symeda.sormas.api.campaign.statistics;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;
import java.util.Objects;

@AuditedClass
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

	@AuditInclude
	public String getCampaign() {
		return campaign;
	}

	@AuditInclude
	public String getForm() {
		return form;
	}

	@AuditInclude
	public String getArea() {
		return area;
	}

	@AuditInclude
	public String getRegion() {
		return region;
	}

	@AuditInclude
	public String getDistrict() {
		return district;
	}

	@AuditInclude
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
