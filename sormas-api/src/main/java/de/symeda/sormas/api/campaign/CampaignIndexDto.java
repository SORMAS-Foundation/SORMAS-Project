package de.symeda.sormas.api.campaign;

import java.io.Serializable;
import java.util.Date;

public class CampaignIndexDto implements Serializable {

	private static final long serialVersionUID = 2448753530580084851L;

	public static final String I18N_PREFIX = "Campaign";

	public static final String UUID = "uuid";
	public static final String NAME = "name";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";

	private String uuid;
	private String name;
	private Date startDate;
	private Date endDate;

	public CampaignIndexDto(String uuid, String name, Date startDate, Date endDate) {
		this.uuid = uuid;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
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
