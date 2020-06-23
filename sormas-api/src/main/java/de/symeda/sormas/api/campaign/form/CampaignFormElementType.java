package de.symeda.sormas.api.campaign.form;

public enum CampaignFormElementType {

	LABEL,
	SECTION,
	INTEGER,
	STRING,
	YES_NO("YES", "NO");

	private final String[] allowedValues;

	CampaignFormElementType(String... allowedValues) {
		this.allowedValues = allowedValues;
	}

	public String[] getAllowedValues() {
		return allowedValues;
	}

	public String toString() {
		return name().toLowerCase().replaceAll("_", "-");
	}

}
