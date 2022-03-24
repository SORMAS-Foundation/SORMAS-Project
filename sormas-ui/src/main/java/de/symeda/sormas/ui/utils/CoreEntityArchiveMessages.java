package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.Strings;

public enum CoreEntityArchiveMessages {

	CASE(Strings.entityCase,
		Strings.headingArchiveCase,
		Strings.confirmationArchiveCase,
		Strings.messageCaseArchived,
		Strings.headingDearchiveCase,
		Strings.confirmationDearchiveCase,
		Strings.messageCaseDearchived),

	CONTACT(Strings.entityContact,
		Strings.headingArchiveContact,
		Strings.confirmationArchiveContact,
		Strings.messageContactArchived,
		Strings.headingDearchiveContact,
		Strings.confirmationDearchiveContact,
		Strings.messageContactDearchived),

	EVENT(Strings.entityEvent,
		Strings.headingArchiveEvent,
		Strings.confirmationArchiveEvent,
		Strings.messageEventArchived,
		Strings.headingDearchiveEvent,
		Strings.confirmationDearchiveEvent,
		Strings.messageEventDearchived),

	EVENT_PARTICIPANT(Strings.entityEventParticipant,
		Strings.headingArchiveEventParticipant,
		Strings.confirmationArchiveEventParticipant,
		Strings.messageEventParticipantArchived,
		Strings.headingDearchiveEventParticipant,
		Strings.confirmationDearchiveEventParticipant,
		Strings.messageEventParticipantDearchived),

	IMMUNIZATION(Strings.entityImmunization,
		Strings.headingArchiveImmunization,
		Strings.confirmationArchiveImmunization,
		Strings.messageImmunizationArchived,
		Strings.headingDearchiveImmunization,
		Strings.confirmationDearchiveImmunization,
		Strings.messageImmunizationDearchived),

	TRAVEL_ENTRY(Strings.entityTravel,
		Strings.headingArchiveTravelEntry,
		Strings.confirmationArchiveTravelEntry,
		Strings.messageTravelEntryArchived,
		Strings.headingDearchiveTravelEntry,
		Strings.confirmationDearchiveTravelEntry,
		Strings.messageTravelEntryDearchived),

	CAMPAIGN(Strings.entityCampaign,
		Strings.headingArchiveCampaign,
		Strings.confirmationArchiveCampaign,
		Strings.messageCampaignArchived,
		Strings.headingDearchiveCampaign,
		Strings.confirmationDearchiveCampaign,
		Strings.messageCampaignDearchived);

	private final String entityName;
	private final String headingArchiveEntity;
	private final String confirmationArchiveEntity;
	private final String messageEntityArchived;
	private final String headingDearchiveEntity;
	private final String confirmationDearchiveEntity;
	private final String messageEntityDearchived;

	CoreEntityArchiveMessages(
		String entityName,
		String headingArchiveEntity,
		String confirmationArchiveEntity,
		String messageEntityArchived,
		String headingDearchiveEntity,
		String confirmationDearchiveEntity,
		String messageEntityDearchived) {
		this.entityName = entityName;
		this.headingArchiveEntity = headingArchiveEntity;
		this.confirmationArchiveEntity = confirmationArchiveEntity;
		this.messageEntityArchived = messageEntityArchived;
		this.headingDearchiveEntity = headingDearchiveEntity;
		this.confirmationDearchiveEntity = confirmationDearchiveEntity;
		this.messageEntityDearchived = messageEntityDearchived;
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

	public String getMessageEntityArchived() {
		return messageEntityArchived;
	}

	public String getHeadingDearchiveEntity() {
		return headingDearchiveEntity;
	}

	public String getConfirmationDearchiveEntity() {
		return confirmationDearchiveEntity;
	}

	public String getMessageEntityDearchived() {
		return messageEntityDearchived;
	}
}
