package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.Strings;

public enum ArchiveMessages {

	CASE(Strings.entityCase,
		Strings.headingArchiveCase,
		Strings.confirmationArchiveCase,
		Strings.confirmationArchiveCases,
		Strings.messageCaseArchived,
		Strings.headingDearchiveCase,
		Strings.confirmationDearchiveCase,
		Strings.confirmationDearchiveCases,
		Strings.messageCaseDearchived,
		Strings.headingNoCasesSelected,
		Strings.messageNoCasesSelected),

	CONTACT(Strings.entityContact,
		Strings.headingArchiveContact,
		Strings.confirmationArchiveContact,
		Strings.confirmationArchiveContacts,
		Strings.messageContactArchived,
		Strings.headingDearchiveContact,
		Strings.confirmationDearchiveContact,
		Strings.confirmationDearchiveContacts,
		Strings.messageContactDearchived,
		Strings.headingNoContactsSelected,
		Strings.messageNoContactsSelected),

	EVENT(Strings.entityEvent,
		Strings.headingArchiveEvent,
		Strings.confirmationArchiveEvent,
		Strings.confirmationArchiveEvents,
		Strings.messageEventArchived,
		Strings.headingDearchiveEvent,
		Strings.confirmationDearchiveEvent,
		Strings.confirmationDearchiveEvents,
		Strings.messageEventDearchived,
		Strings.headingNoEventsSelected,
		Strings.messageNoEventsSelected),

	EVENT_PARTICIPANT(Strings.entityEventParticipant,
		Strings.headingArchiveEventParticipant,
		Strings.confirmationArchiveEventParticipant,
		null,
		Strings.messageEventParticipantArchived,
		Strings.headingDearchiveEventParticipant,
		Strings.confirmationDearchiveEventParticipant,
		null,
		Strings.messageEventParticipantDearchived,
		Strings.headingNoEventParticipantsSelected,
		Strings.messageNoEventParticipantsSelected),

	IMMUNIZATION(Strings.entityImmunization,
		Strings.headingArchiveImmunization,
		Strings.confirmationArchiveImmunization,
		null,
		Strings.messageImmunizationArchived,
		Strings.headingDearchiveImmunization,
		Strings.confirmationDearchiveImmunization,
		null,
		Strings.messageImmunizationDearchived,
		null,
		null),

	TRAVEL_ENTRY(Strings.entityTravelEntry,
		Strings.headingArchiveTravelEntry,
		Strings.confirmationArchiveTravelEntry,
		null,
		Strings.messageTravelEntryArchived,
		Strings.headingDearchiveTravelEntry,
		Strings.confirmationDearchiveTravelEntry,
		null,
		Strings.messageTravelEntryDearchived,
		Strings.headingNoTravelEntriesSelected,
		Strings.messageNoTravelEntriesSelected),

	CAMPAIGN(Strings.entityCampaign,
		Strings.headingArchiveCampaign,
		Strings.confirmationArchiveCampaign,
		null,
		Strings.messageCampaignArchived,
		Strings.headingDearchiveCampaign,
		Strings.confirmationDearchiveCampaign,
		null,
		Strings.messageCampaignDearchived,
		null,
		null),
	TASK(Strings.entityTask,
		Strings.headingConfirmArchiving,
		Strings.confirmationArchiveTask,
		Strings.confirmationArchiveTasks,
		Strings.messageTaskArchived,
		Strings.headingConfirmDearchiving,
		Strings.confirmationDearchiveTask,
		Strings.confirmationDearchiveTasks,
		Strings.messageTaskDearchived,
		Strings.headingNoTasksSelected,
		Strings.messageNoTasksSelected);

	private final String entityName;
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

	private final String headingConfirmationDearchiving = Strings.headingConfirmDearchiving;

	ArchiveMessages(
		String entityName,
		String headingArchiveEntity,
		String confirmationArchiveEntity,
		String confirmationArchiveEntities,
		String messageEntityArchived,
		String headingDearchiveEntity,
		String confirmationDearchiveEntity,
		String confirmDearchiveEntities,
		String messageEntityDearchived,
		String headingNoEntitySelected,
		String messageNoEntitySelected) {
		this.entityName = entityName;
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
	}

	public String getEntityName() {
		return entityName;
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

	public String getHeadingConfirmationDearchiving() {
		return headingConfirmationDearchiving;
	}
}
