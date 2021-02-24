package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ExportEntityName {
    CAMPAIGN_DATA("entityCampaignData", "campaign data"),
    CASES("entityCases", "cases"),
    CONTACTS("entityContacts", "contacts"),
    EVENT_PARTICIPANTS("entityEventParticipants", "event participants");

    private final String languageKey;
    private final String defaultName;

    ExportEntityName(String languageKey, String defaultName) {
        this.languageKey = languageKey;
        this.defaultName = defaultName;
    }

    public String getLocalizedName() {
        return I18nProperties.getString(languageKey, defaultName);
    }
}
