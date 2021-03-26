package de.symeda.sormas.api.campaign.statistics;

import java.io.Serializable;

public class CampaignStatisticsDataDto implements Serializable, Cloneable {

	private final String field;
	private final int value;

	public CampaignStatisticsDataDto(String field, int value) {
		this.field = field;
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public int getValue() {
		return value;
	}
}
