package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.Strings;

public enum ArchiveMessages {

	CASE(Strings.headingArchiveCase,
		Strings.confirmationArchiveCase,
		Strings.confirmationArchiveCases,
		Strings.messageCaseArchived,
		Strings.headingDearchiveCase,
		Strings.confirmationDearchiveCase,
		Strings.confirmationDearchiveCases,
		Strings.messageCaseDearchived,
		Strings.headingNoCasesSelected,
		Strings.messageNoCasesSelected,
		Strings.messageCountCasesNotArchivedExternalReason),

	CONTACT(Strings.headingArchiveContact,
		Strings.confirmationArchiveContact,
		Strings.confirmationArchiveContacts,
		Strings.messageContactArchived,
		Strings.headingDearchiveContact,
		Strings.confirmationDearchiveContact,
		Strings.confirmationDearchiveContacts,
		Strings.messageContactDearchived,
		Strings.headingNoContactsSelected,
		Strings.messageNoContactsSelected,
		null),

	ENVIRONMENT(Strings.headingArchiveEnvironment,
		Strings.confirmationArchiveEnvironment,
		Strings.confirmationArchiveEnvironments,
		Strings.messageEnvironmentArchived,
		Strings.headingDearchiveEnvironment,
		Strings.confirmationDearchiveEnvironment,
		Strings.confirmationDearchiveEnvironments,
		Strings.messageEnvironmentDearchived,
		Strings.headingNoEnvironmentSelected,
		Strings.messageNoEnvironmentsSelected,
		null),

	EVENT(Strings.headingArchiveEvent,
		Strings.confirmationArchiveEvent,
		Strings.confirmationArchiveEvents,
		Strings.messageEventArchived,
		Strings.headingDearchiveEvent,
		Strings.confirmationDearchiveEvent,
		Strings.confirmationDearchiveEvents,
		Strings.messageEventDearchived,
		Strings.headingNoEventsSelected,
		Strings.messageNoEventsSelected,
		Strings.messageCountEventsNotArchivedExternalReason),

	EVENT_PARTICIPANT(Strings.headingArchiveEventParticipant,
		Strings.confirmationArchiveEventParticipant,
		null,
		Strings.messageEventParticipantArchived,
		Strings.headingDearchiveEventParticipant,
		Strings.confirmationDearchiveEventParticipant,
		null,
		Strings.messageEventParticipantDearchived,
		Strings.headingNoEventParticipantsSelected,
		Strings.messageNoEventParticipantsSelected,
		null),

	IMMUNIZATION(Strings.headingArchiveImmunization,
		Strings.confirmationArchiveImmunization,
		null,
		Strings.messageImmunizationArchived,
		Strings.headingDearchiveImmunization,
		Strings.confirmationDearchiveImmunization,
		null,
		Strings.messageImmunizationDearchived,
		null,
		null,
		null),

	TRAVEL_ENTRY(Strings.headingArchiveTravelEntry,
		Strings.confirmationArchiveTravelEntry,
		null,
		Strings.messageTravelEntryArchived,
		Strings.headingDearchiveTravelEntry,
		Strings.confirmationDearchiveTravelEntry,
		null,
		Strings.messageTravelEntryDearchived,
		Strings.headingNoTravelEntriesSelected,
		Strings.messageNoTravelEntriesSelected,
		null),

	CAMPAIGN(Strings.headingArchiveCampaign,
		Strings.confirmationArchiveCampaign,
		null,
		Strings.messageCampaignArchived,
		Strings.headingDearchiveCampaign,
		Strings.confirmationDearchiveCampaign,
		null,
		Strings.messageCampaignDearchived,
		null,
		null,
		null),

	TASK(Strings.headingConfirmArchiving,
		Strings.confirmationArchiveTask,
		Strings.confirmationArchiveTasks,
		Strings.messageTaskArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveTask,
		Strings.confirmationDearchiveTasks,
		Strings.messageTaskDearchived,
		Strings.headingNoTasksSelected,
		Strings.messageNoTasksSelected,
		null),

	CONTINENT(Strings.headingConfirmArchiving,
		Strings.confirmationArchiveContinent,
		Strings.confirmationArchiveContinents,
		Strings.messageContinentArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveContinent,
		Strings.confirmationDearchiveContinents,
		Strings.messageContinentDearchived,
		Strings.headingNoRowsSelected,
		Strings.messageNoRowsSelected,
		null),

	SUBCONTINENT(Strings.headingConfirmArchiving,
		Strings.confirmationArchiveSubcontinent,
		Strings.confirmationArchiveSubcontinents,
		Strings.messageSubcontinentArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveSubcontinent,
		Strings.confirmationDearchiveSubcontinents,
		Strings.messageSubcontinentDearchived,
		Strings.headingNoRowsSelected,
		Strings.messageNoRowsSelected,
		null),

	AREA(Strings.headingConfirmArchiving,
		Strings.confirmationArchiveArea,
		Strings.confirmationArchiveAreas,
		Strings.messageAreaArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveArea,
		Strings.confirmationDearchiveAreas,
		Strings.messageAreaDearchived,
		Strings.headingNoRowsSelected,
		Strings.messageNoRowsSelected,
		null),

	COUNTRY(Strings.headingConfirmArchiving,
		Strings.confirmationArchiveCountry,
		Strings.confirmationArchiveCountries,
		Strings.messageCountryArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveCountry,
		Strings.confirmationDearchiveCountries,
		Strings.messageCountryDearchived,
		Strings.headingNoRowsSelected,
		Strings.messageNoRowsSelected,
		null),

	REGION(Strings.headingConfirmArchiving,
		Strings.confirmationArchiveRegion,
		Strings.confirmationArchiveRegions,
		Strings.messageRegionArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveRegion,
		Strings.confirmationDearchiveRegions,
		Strings.messageRegionDearchived,
		Strings.headingNoRowsSelected,
		Strings.messageNoRowsSelected,
		null),

	DISTRICT(Strings.headingConfirmArchiving,
		Strings.confirmationArchiveDistrict,
		Strings.confirmationArchiveDistricts,
		Strings.messageDistrictArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveDistrict,
		Strings.confirmationDearchiveDistricts,
		Strings.messageDistrictDearchived,
		Strings.headingNoRowsSelected,
		Strings.messageNoRowsSelected,
		null),

	COMMUNITY(Strings.headingConfirmArchiving,
		Strings.confirmationArchiveCommunity,
		Strings.confirmationArchiveCommunities,
		Strings.messageCommunityArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveCommunity,
		Strings.confirmationDearchiveCommunities,
		Strings.messageCommunityDearchived,
		Strings.headingNoRowsSelected,
		Strings.messageNoRowsSelected,
		null),

	FACILITY(Strings.headingConfirmArchiving,
		Strings.confirmationArchiveFacility,
		Strings.confirmationArchiveFacilities,
		Strings.messageFacilityArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveFacility,
		Strings.confirmationDearchiveFacilities,
		Strings.messageFacilityDearchived,
		Strings.headingNoRowsSelected,
		Strings.messageNoRowsSelected,
		null),

	POINT_OF_ENTRY(Strings.headingConfirmArchiving,
		Strings.confirmationArchivePointOfEntry,
		Strings.confirmationArchivePointsOfEntry,
		Strings.messagePointOfEntryArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchivePointOfEntry,
		Strings.confirmationDearchivePointsOfEntry,
		Strings.messagePointOfEntryDearchived,
		Strings.headingNoRowsSelected,
		Strings.messageNoRowsSelected,
		null);

	private final String headingArchiveEntity;
	private final String confirmationArchiveEntity;
	private final String confirmationArchiveEntities;
	private final String messageEntityArchived;
	private final String headingDearchiveEntity;
	private final String confirmationDearchiveEntity;
	private final String confirmDearchiveEntities;
	private final String messageEntityDearchived;

	private final String headingNoEntitySelected;
	private final String messageNoEntitySelected;
	private final String messageCountEntitiesNotArchivedExternalReason;

	private final String headingConfirmationArchiving = Strings.headingConfirmArchiving;
	private final String headingConfirmationDearchiving = Strings.headingConfirmDearchiving;
	private final String messageAllEntitiesArchived = Strings.messageAllEntitiesArchived;
	private final String messageSomeEntitiesArchived = Strings.messageSomeEntitiesArchived;
	private final String messageAllEntitiesDearchived = Strings.messageAllEntitiesDearchived;
	private final String messageSomeEntitiesDearchived = Strings.messageSomeEntitiesDearchived;

	//TODO: check if is necessary to be added for each entity separately
	private final String headingSomeEntitiesNotArchived = Strings.headingSomeEntitiesNotArchived;
	private final String headingEntitiesNotArchived = Strings.headingEntitiesNotArchived;

	//private final String messageCountEntitiesNotArchived;

	/*
	 * private final String headingSomeEntitiesDearchived = Strings.headingSomeEntitiesDearchived;
	 * private final String headingEntitiesNotDearchived = Strings.headingEntitiesNotDearchived;
	 */

	ArchiveMessages(
		String headingArchiveEntity,
		String confirmationArchiveEntity,
		String confirmationArchiveEntities,
		String messageEntityArchived,
		String headingDearchiveEntity,
		String confirmationDearchiveEntity,
		String confirmDearchiveEntities,
		String messageEntityDearchived,
		String headingNoEntitySelected,
		String messageNoEntitySelected,
		String messageCountEntitiesNotArchivedExternalReason) {
		this.headingArchiveEntity = headingArchiveEntity;
		this.confirmationArchiveEntity = confirmationArchiveEntity;
		this.confirmationArchiveEntities = confirmationArchiveEntities;
		this.messageEntityArchived = messageEntityArchived;
		this.headingDearchiveEntity = headingDearchiveEntity;
		this.confirmationDearchiveEntity = confirmationDearchiveEntity;
		this.confirmDearchiveEntities = confirmDearchiveEntities;
		this.messageEntityDearchived = messageEntityDearchived;
		this.headingNoEntitySelected = headingNoEntitySelected;
		this.messageNoEntitySelected = messageNoEntitySelected;
		this.messageCountEntitiesNotArchivedExternalReason = messageCountEntitiesNotArchivedExternalReason;
	}

	public String getHeadingArchiveEntity() {
		return headingArchiveEntity;
	}

	public String getConfirmationArchiveEntity() {
		return confirmationArchiveEntity;
	}

	public String getConfirmationArchiveEntities() {
		return confirmationArchiveEntities;
	}

	public String getMessageEntityArchived() {
		return messageEntityArchived;
	}

	public String getHeadingDearchiveEntity() {
		return headingDearchiveEntity;
	}

	public String getConfirmationDearchiveEntity() {
		return confirmationDearchiveEntity;
	}

	public String getConfirmDearchiveEntities() {
		return confirmDearchiveEntities;
	}

	public String getMessageEntityDearchived() {
		return messageEntityDearchived;
	}

	public String getHeadingNoEntitySelected() {
		return headingNoEntitySelected;
	}

	public String getMessageNoEntitySelected() {
		return messageNoEntitySelected;
	}

	public String getMessageCountEntitiesNotArchivedExternalReason() {
		return messageCountEntitiesNotArchivedExternalReason;
	}

	public String getHeadingConfirmationArchiving() {
		return headingConfirmationArchiving;
	}

	public String getHeadingConfirmationDearchiving() {
		return headingConfirmationDearchiving;
	}

	public String getMessageAllEntitiesArchived() {
		return messageAllEntitiesArchived;
	}

	public String getMessageSomeEntitiesArchived() {
		return messageSomeEntitiesArchived;
	}

	public String getMessageAllEntitiesDearchived() {
		return messageAllEntitiesDearchived;
	}

	public String getMessageSomeEntitiesDearchived() {
		return messageSomeEntitiesDearchived;
	}

	public String getHeadingSomeEntitiesNotArchived() {
		return headingSomeEntitiesNotArchived;
	}

	public String getHeadingEntitiesNotArchived() {
		return headingEntitiesNotArchived;
	}
}
