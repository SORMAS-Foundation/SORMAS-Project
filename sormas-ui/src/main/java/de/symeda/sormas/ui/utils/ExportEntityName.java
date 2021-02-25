package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ExportEntityName {
    AGGREGATE_REPORTS("entityAggregateReports", "aggregate reports"),
    AREAS("entityAreas", "areas"),
    STATISTICS("entityStatistics", "statistics"),
    BAG_CASES("entityBagCases", "bag cases"),
    BAG_CONTACTS("entityBagContacts", "bag contacts"),
    CAMPAIGN_DATA("entityCampaignData", "campaign data"),
    CASES("entityCases", "cases"),
    CASE_MANAGEMENT("entityCaseManagement", "case management"),
    CASE_VISITS("entityCaseVisits", "case visits"),
    CLINICAL_VISITS("entityClinicalVisits", "clinical assessments"),
    COMMUNITIES("entityCommunities", "communities"),
    CONTACTS("entityContacts", "contacts"),
    CONTACT_FOLLOW_UPS("entityContactFollowUps", "contact follow ups"),
    CONTACT_VISITS("entityContactVisits", "contact visits"),
    COUNTRIES("entityCountries", "countries"),
    DISTRICTS("entityDistricts", "districts"),
    EVENTS("entityEvents", "events"),
    EVENT_PARTICIPANTS("entityEventParticipants", "event participants"),
    EVENT_ACTIONS("entityEventActions", "events actions"),
    FACILITIES("entityFacilities", "facilities"),
    PRESCRIPTIONS("entityPrescriptions", "prescriptions"),
    POINTS_OF_ENTRY("entityPointsOfEntry", "points of entry"),
    REGIONS("entityRegions", "regions"),
    SAMPLES("entitySamples", "samples"),
    TASKS("entityTasks", "tasks");

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
