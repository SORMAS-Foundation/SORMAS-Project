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
		Strings.messageCountCasesNotArchivedExternalReason,
		Strings.messageCountCasesNotDearchivedExternalReason,
		null,
		null,
		null,
		null),

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
		null,
		null,
		null,
		null,
		null,
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
		null,
		null,
		null,
		null,
		null,
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
		Strings.messageCountEventsNotArchivedExternalReason,
		Strings.messageCountEventsNotDearchivedExternalReason,
		null,
		null,
		null,
		null),

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
		null,
		null,
		null,
		null,
		null,
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
		null,
		null,
		null,
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
		null,
		null,
		null,
		null,
		null,
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
		null,
		null,
		null,
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
		null,
		null,
		null,
		null,
		null,
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
		null,
		null,
		Strings.messageCountEntitiesNotArchivedAccessDeniedReason,
		Strings.messageCountEntitiesNotDearchivedAccessDeniedReason,
		Strings.messageContinentArchivingNotPossible,
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
		null,
		null,
		Strings.messageCountEntitiesNotArchivedAccessDeniedReason,
		Strings.messageCountEntitiesNotDearchivedAccessDeniedReason,
		Strings.messageSubcontinentArchivingNotPossible,
		Strings.messageSubcontinentDearchivingNotPossible),

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
		null,
		null,
		Strings.messageCountEntitiesNotArchivedAccessDeniedReason,
		Strings.messageCountEntitiesNotDearchivedAccessDeniedReason,
		Strings.messageAreaArchivingNotPossible,
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
		null,
		null,
		Strings.messageCountEntitiesNotArchivedAccessDeniedReason,
		Strings.messageCountEntitiesNotDearchivedAccessDeniedReason,
		null,
		Strings.messageCountryDearchivingNotPossible),

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
		null,
		null,
		Strings.messageCountEntitiesNotArchivedAccessDeniedReason,
		Strings.messageCountEntitiesNotDearchivedAccessDeniedReason,
		Strings.messageRegionArchivingNotPossible,
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
		null,
		null,
		Strings.messageCountEntitiesNotArchivedAccessDeniedReason,
		Strings.messageCountEntitiesNotDearchivedAccessDeniedReason,
		Strings.messageDistrictArchivingNotPossible,
		Strings.messageDistrictDearchivingNotPossible),

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
		null,
		null,
		Strings.messageCountEntitiesNotArchivedAccessDeniedReason,
		Strings.messageCountEntitiesNotDearchivedAccessDeniedReason,
		Strings.messageCommunityArchivingNotPossible,
		Strings.messageCommunityDearchivingNotPossible),

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
		null,
		null,
		Strings.messageCountEntitiesNotArchivedAccessDeniedReason,
		Strings.messageCountEntitiesNotDearchivedAccessDeniedReason,
		null,
		Strings.messageFacilityDearchivingNotPossible),

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
		null,
		null,
		Strings.messageCountEntitiesNotArchivedAccessDeniedReason,
		Strings.messageCountEntitiesNotDearchivedAccessDeniedReason,
		null,
		Strings.messagePointOfEntryDearchivingNotPossible);

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
	private final String messageCountEntitiesNotDearchivedExternalReason;
	private final String messageCountEntitiesNotArchivedAccessDeniedReason;
	private final String messageCountEntitiesNotDearchivedAccessDeniedReason;
	private final String messageEntityArchivingNotPossible;
	private final String messageEntityDearchivingNotPossible;

	private final String headingConfirmationArchiving = Strings.headingConfirmArchiving;
	private final String headingConfirmationDearchiving = Strings.headingConfirmDearchiving;
	private final String messageAllEntitiesArchived = Strings.messageAllEntitiesArchived;
	private final String messageAllEntitiesDearchived = Strings.messageAllEntitiesDearchived;
	private final String messageCountEntitiesNotArchived = Strings.messageCountEntitiesNotArchived;
	private final String messageCountEntitiesNotDearchived = Strings.messageCountEntitiesNotDearchived;
	private final String headingSomeEntitiesNotArchived = Strings.headingSomeEntitiesNotArchived;
	private final String headingEntitiesNotArchived = Strings.headingEntitiesNotArchived;
	private final String headingSomeEntitiesNotDearchived = Strings.headingSomeEntitiesNotDearchived;
	private final String headingEntitiesNotDearchived = Strings.headingEntitiesNotDearchived;

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
		String messageCountEntitiesNotArchivedExternalReason,
		String messageCountEntitiesNotDearchivedExternalReason,
		String messageCountEntitiesNotArchivedAccessDeniedReason,
		String messageCountEntitiesNotDearchivedAccessDeniedReason,
		String messageEntityArchivingNotPossible,
		String messageEntityDearchivingNotPossible) {
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
		this.messageCountEntitiesNotDearchivedExternalReason = messageCountEntitiesNotDearchivedExternalReason;
		this.messageCountEntitiesNotArchivedAccessDeniedReason = messageCountEntitiesNotArchivedAccessDeniedReason;
		this.messageCountEntitiesNotDearchivedAccessDeniedReason = messageCountEntitiesNotDearchivedAccessDeniedReason;
		this.messageEntityArchivingNotPossible = messageEntityArchivingNotPossible;
		this.messageEntityDearchivingNotPossible = messageEntityDearchivingNotPossible;
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

	public String getMessageCountEntitiesNotDearchivedExternalReason() {
		return messageCountEntitiesNotDearchivedExternalReason;
	}

	public String getMessageCountEntitiesNotArchivedAccessDeniedReason() {
		return messageCountEntitiesNotArchivedAccessDeniedReason;
	}

	public String getMessageCountEntitiesNotDearchivedAccessDeniedReason() {
		return messageCountEntitiesNotDearchivedAccessDeniedReason;
	}

	public String getMessageEntityArchivingNotPossible() {
		return messageEntityArchivingNotPossible;
	}

	public String getMessageEntityDearchivingNotPossible() {
		return messageEntityDearchivingNotPossible;
	}

	public String getMessageCountEntitiesNotArchived() {
		return messageCountEntitiesNotArchived;
	}

	public String getMessageCountEntitiesNotDearchived() {
		return messageCountEntitiesNotDearchived;
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

	public String getMessageAllEntitiesDearchived() {
		return messageAllEntitiesDearchived;
	}

	public String getHeadingSomeEntitiesNotArchived() {
		return headingSomeEntitiesNotArchived;
	}

	public String getHeadingEntitiesNotArchived() {
		return headingEntitiesNotArchived;
	}

	public String getHeadingSomeEntitiesNotDearchived() {
		return headingSomeEntitiesNotDearchived;
	}

	public String getHeadingEntitiesNotDearchived() {
		return headingEntitiesNotDearchived;
	}

}
