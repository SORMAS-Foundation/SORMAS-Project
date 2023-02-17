package de.symeda.sormas.api.campaign;

import java.io.Serializable;
import java.util.Date;

public class CampaignIndexDto implements Serializable {

	private static final long serialVersionUID = 2448753530580084851L; //.save

	public static final String I18N_PREFIX = "Campaign";

	public static final String UUID = "uuid";
	public static final String ROUND = "round";
	public static final String CAMPAIGN_YEAR = "campaignYear";
	public static final String NAME = "name";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String CAMPAIGN_STATUS = "campaignStatus";

	private String uuid;
	private String name;
	private String round;
	private String campaignYear;
	private Date startDate;
	private Date endDate;
	private String campaignStatus;

	public CampaignIndexDto(String uuid, String name, boolean campaignStatus, String cluster,  String campaignYear, Date startDate, Date endDate) {
		this.uuid = uuid;
		this.name = name;
		//this.round = round;
		this.campaignYear = campaignYear;
		this.startDate = startDate;
		this.endDate = endDate;
		this.campaignStatus = campaignStatus == true ? "Open" : "Closed" ;
	}

	public String getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(String campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public String getCampaignYear() {
		return campaignYear;
	}

	public void setCampaignYear(String campaignYear) {
		this.campaignYear = campaignYear;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
