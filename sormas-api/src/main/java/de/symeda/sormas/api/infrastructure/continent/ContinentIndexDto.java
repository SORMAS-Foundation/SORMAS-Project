package de.symeda.sormas.api.infrastructure.continent;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ContinentIndexDto extends ContinentDto {

	public static final String DISPLAY_NAME = "displayName";

	private String displayName;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String buildCaption() {
		return getDefaultName();
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}
}
