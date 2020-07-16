package de.symeda.sormas.api.campaign.form;

public enum CampaignFormElementType {

	LABEL,
	SECTION,
	NUMBER,
	TEXT,
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

	public static CampaignFormElementType fromString(String stringValue) {
		return valueOf(stringValue.toUpperCase().replaceAll("-", "_"));
	}

}
