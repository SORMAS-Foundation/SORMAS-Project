package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum ExportEntityName {

	AGGREGATE_REPORTS("entityAggregateReports", "aggregate reports"),
	AREAS("entityAreas", "areas"),
	STATISTICS("entityStatistics", "statistics"),
	BAG_CASES("entityBagCases", "bag cases"),
	BAG_CONTACTS("entityBagContacts", "bag contacts"),
	CAMPAIGN_DATA("entityCampaignData", "campaign data"),
	CAMPAIGN_STATISTICS("entityCampaignStatistics", "campaign statistics"),
	CASES("entityCases", "cases"),
	CASE_MANAGEMENT("entityCaseManagement", "case management"),
	CASE_VISITS("entityCaseVisits", "case visits"),
	CLINICAL_VISITS("entityClinicalVisits", "clinical assessments"),
	COMMUNITIES("entityCommunities", "communities"),
	CONTACTS("entityContacts", "contacts"),
	CONTACT_FOLLOW_UPS("entityContactFollowUps", "contact follow ups"),
	CONTACT_VISITS("entityContactVisits", "contact visits"),
	COUNTRIES("entityCountries", "countries"),
	CONTINENTS("entityContinents", "continents"),
	SUBCONTINENTS("entitySubcontinents", "subcontinents"),
	DISTRICTS("entityDistricts", "districts"),
	EVENTS("entityEvents", "events"),
	EVENT_PARTICIPANTS("entityEventParticipants", "event participants"),
	EVENT_ACTIONS("entityEventActions", "events actions"),
	FACILITIES("entityFacilities", "facilities"),
	PRESCRIPTIONS("entityPrescriptions", "prescriptions"),
	POINTS_OF_ENTRY("entityPointsOfEntry", "points of entry"),
	POPULATION_DATA("entityPopulationData", "population data"),
	REGIONS("entityRegions", "regions"),
	SAMPLES("entitySamples", "samples"),
	TASKS("entityTasks", "tasks"),
	USER_ROLES("entityUserRoles", "user roles"),
	DOCUMENTS("entityDocuments", "documents"),
	DATA_DICTIONARY("entityDataDictionary", "data dictionary"),
	DATA_PROTECTION_DICTIONARY("entityDataProtectionDictionary", "data protection dictionary"),
	PERSONS("entityPersons", "persons"),
	ENVIRONMENTS("entityEnvironments", "environments"),
	ENVIRONMeNT_SAMPLES("entityEnvironmentSamples", "environment samples"),;

	private final String languageKey;
	private final String defaultName;

	ExportEntityName(String languageKey, String defaultName) {
		this.languageKey = languageKey;
		this.defaultName = defaultName;
	}

	public String getLocalizedName() {
		return I18nProperties.getString(languageKey, defaultName);
	}

	public String getLocalizedNameInSystemLanguage() {
		String systemLocale = FacadeProvider.getConfigFacade().getCountryLocale();
		Language language = Language.fromLocaleString(systemLocale);
		String entityName = I18nProperties.getString(language, languageKey);
		return entityName == null ? defaultName : entityName;
	}
}
