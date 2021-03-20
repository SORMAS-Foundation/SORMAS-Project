package de.symeda.sormas.api.campaign.statistics;

import java.io.Serializable;

public class CampaignStatisticsIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "CampaignStatistics";

	public static final String UUID = "uuid";
	public static final String CAMPAIGN = "campaign";
	public static final String FORM = "form";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String FORM_COUNT = "formCount";

	private String uuid;
	private String campaign;
	private String form;
	private String region;
	private String district;
	private String community;
	private int formCount;

	public CampaignStatisticsIndexDto(String uuid, String campaign, String form, String region, String district, String community, int formCount) {
		this.uuid = uuid;
		this.campaign = campaign;
		this.form = form;
		this.region = region;
		this.district = district;
		this.community = community;
		this.formCount = formCount;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public int getFormCount() {
		return formCount;
	}

	public void setFormCount(int formCount) {
		this.formCount = formCount;
	}
}
