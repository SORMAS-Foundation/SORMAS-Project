package de.symeda.sormas.ui.campaign.components.importancefilterswitcher;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum CampaignFormElementImportance {

	ALL,
	IMPORTANT;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
