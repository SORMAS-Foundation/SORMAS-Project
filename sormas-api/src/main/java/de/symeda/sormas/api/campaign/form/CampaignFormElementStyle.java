package de.symeda.sormas.api.campaign.form;

public enum CampaignFormElementStyle {

	INLINE,
	ROW,
	FIRST,
	COL_1,
	COL_2,
	COL_3,
	COL_4,
	COL_5,
	COL_6,
	COL_7,
	COL_8,
	COL_9,
	COL_10,
	COL_11,
	COL_12;

	public String toString() {
		return name().toLowerCase().replaceAll("_", "-");
	}

}
