package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.Strings;

public enum DeleteRestoreMessages {

	CASE(Strings.entityCases,
		Strings.headingNoCasesSelected,
		Strings.messageNoCasesSelected,
		null,
		null,
		Strings.headingCasesDeleted,
		Strings.messageCasesDeleted,
		Strings.messageCasesNotDeleted,
		Strings.messageCasesNotDeletedExternalReason,
		null,
		Strings.headingSomeCasesNotDeleted,
		Strings.messageCountCasesNotDeleted,
		Strings.headingCasesRestored,
		Strings.messageCasesRestored,
		Strings.messageCasesNotRestored,
		Strings.headingSomeCasesNotRestored,
		Strings.messageCountCasesNotRestored),

	EVENT(Strings.entityEvents,
		Strings.headingNoEventsSelected,
		Strings.messageNoEventsSelected,
		Strings.messageNoEligibleEventForDeletionSelected,
		null,
		Strings.headingEventsDeleted,
		Strings.messageEventsDeleted,
		Strings.messageEventsNotDeleted,
		Strings.messageEventsNotDeletedExternalReason,
		Strings.messageEventsNotDeletedLinkedEntitiesReason,
		Strings.headingSomeEventsNotDeleted,
		Strings.messageCountEventsNotDeleted,
		Strings.headingEventsRestored,
		Strings.messageEventsRestored,
		Strings.messageEventsNotRestored,
		Strings.headingSomeEventsNotRestored,
		Strings.messageCountEventsNotRestored),

	EVENT_PARTICIPANT(Strings.entityEventParticipants,
		Strings.headingNoEventParticipantsSelected,
		Strings.messageNoEventParticipantsSelected,
		null,
		null,
		Strings.headingEventParticipantsDeleted,
		Strings.messageEventParticipantsDeleted,
		Strings.messageEventParticipantsNotDeleted,
		null,
		null,
		Strings.headingSomeEventParticipantsNotDeleted,
		Strings.messageCountEventParticipantsNotDeleted,
		Strings.headingEventParticipantsRestored,
		Strings.messageEventParticipantsRestored,
		Strings.messageEventParticipantsNotRestored,
		Strings.headingSomeEventParticipantsNotRestored,
		Strings.messageCountEventParticipantsNotRestored),

	CONTACT(Strings.entityContacts,
		Strings.headingNoContactsSelected,
		Strings.messageNoContactsSelected,
		null,
		null,
		Strings.headingContactsDeleted,
		Strings.messageContactsDeleted,
		Strings.messageContactsNotDeleted,
		null,
		null,
		Strings.headingSomeContactsNotDeleted,
		Strings.messageCountContactsNotDeleted,
		Strings.headingContactsRestored,
		Strings.messageContactsRestored,
		Strings.messageContactsNotRestored,
		Strings.headingSomeContactsNotRestored,
		Strings.messageCountContactsNotRestored),

	TRAVEL_ENTRY(Strings.entityTravelEntries,
		Strings.headingNoTravelEntriesSelected,
		Strings.messageNoTravelEntriesSelected,
		null,
		null,
		Strings.headingTravelEntriesDeleted,
		Strings.messageTravelEntriesDeleted,
		Strings.messageTravelEntriesNotDeleted,
		null,
		null,
		Strings.headingSomeTravelEntriesNotDeleted,
		Strings.messageCountTravelEntriesNotDeleted,
		Strings.headingTravelEntriesRestored,
		Strings.messageTravelEntriesRestored,
		Strings.messageTravelEntriesNotRestored,
		Strings.headingSomeTravelEntriesNotRestored,
		Strings.messageCountTravelEntriesNotRestored),

	VISIT(Strings.entityVisits,
		Strings.headingNoVisitsSelected,
		Strings.messageNoVisitsSelected,
		null,
		null,
		Strings.headingVisitsDeleted,
		Strings.messageVisitsDeleted,
		Strings.messageVisitsNotDeleted,
		null,
		null,
		Strings.headingSomeVisitsNotDeleted,
		Strings.messageCountVisitsNotDeleted,
		null,
		null,
		null,
		null,
		null),

	TASK(Strings.entityTasks,
		Strings.headingNoTasksSelected,
		Strings.messageNoTasksSelected,
		null,
		null,
		Strings.headingTasksDeleted,
		Strings.messageTasksDeleted,
		Strings.messageTasksNotDeleted,
		null,
		null,
		Strings.headingSomeTasksNotDeleted,
		Strings.messageCountTasksNotDeleted,
		null,
		null,
		null,
		null,
		null),

	EXTERNAL_MESSAGE(Strings.entityExternalMessages,
		Strings.headingNoExternalMessagesSelected,
		Strings.messageNoExternalMessagesSelected,
		null,
		Strings.messageExternalMessagesEligibleForDeletion,
		Strings.headingExternalMessagesDeleted,
		Strings.messageExternalMessagesDeleted,
		Strings.messageExternalMessagesNotDeleted,
		null,
		null,
		Strings.headingSomeExternalMessagesNotDeleted,
		Strings.messageCountExternalMessagesNotDeleted,
		null,
		null,
		null,
		null,
		null),

	SAMPLE(Strings.entitySamples,
		Strings.headingNoSamplesSelected,
		Strings.messageNoSamplesSelected,
		null,
		null,
		Strings.headingSamplesDeleted,
		Strings.messageSamplesDeleted,
		Strings.messageSamplesNotDeleted,
		null,
		null,
		Strings.headingSomeSamplesNotDeleted,
		Strings.messageCountSamplesNotDeleted,
		Strings.headingSamplesRestored,
		Strings.messageSamplesRestored,
		Strings.messageSamplesNotRestored,
		Strings.headingSomeSamplesNotRestored,
		Strings.messageCountSamplesNotRestored),

	IMMUNIZATION(Strings.entityImmunizations,
		Strings.headingNoImmunizationsSelected,
		Strings.messageNoImmunizationsSelected,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		Strings.headingImmunizationsRestored,
		Strings.messageImmunizationsRestored,
		Strings.messageImmunizationsNotRestored,
		Strings.headingSomeImmunizationsNotRestored,
		Strings.messageCountImmunizationsNotRestored),
	ENVIRONMENT_SAMPLE(Strings.entityEnvironmentSamples,
		Strings.headingNoEnvironmentSamplesSelected,
		Strings.messageNoEnvironmentSamplesSelected,
		null,
		null,
		Strings.headingEnvironmentSamplesDeleted,
		Strings.messageEnvironmentSamplesDeleted,
		Strings.messageEnvironmentSamplesNotDeleted,
		null,
		null,
		Strings.headingSomeEnvironmentSamplesNotDeleted,
		Strings.messageCountEnvironmentSamplesNotDeleted,
		Strings.headingEnvironmentSamplesRestored,
		Strings.messageEnvironmentSamplesRestored,
		Strings.messageEnvironmentSamplesNotRestored,
		Strings.headingEnvironmentSomeSamplesNotRestored,
		Strings.messageEnvironmentCountSamplesNotRestored);

	private final String entities;
	private final String headingNoSelection;
	private final String messageNoSelection;
	private final String messageNoEligibleEntitySelected;
	private final String messageEntitiesEligibleForDeletion;
	private final String headingEntitiesDeleted;
	private final String messageEntitiesDeleted;
	private final String messageEntitiesNotDeleted;
	private final String messageEntitiesNotDeletedExternalReason;
	private final String messageEntitiesNotDeletedLinkedEntitiesReason;
	private final String headingSomeEntitiesNotDeleted;
	private final String messageCountEntitiesNotDeleted;

	private final String headingEntitiesRestored;
	private final String messageEntitiesRestored;
	private final String messageEntitiesNotRestored;
	private final String headingSomeEntitiesNotRestored;
	private final String messageCountEntitiesNotRestored;

	DeleteRestoreMessages(
		String entities,
		String headingNoSelection,
		String messageNoSelection,
		String messageNoEligibleEntitySelected,
		String messageEntitiesEligibleForDeletion,
		String headingEntitiesDeleted,
		String messageEntitiesDeleted,
		String messageEntitiesNotDeleted,
		String messageEntitiesNotDeletedExternalReason,
		String messageEntitiesNotDeletedLinkedEntitiesReason,
		String headingSomeEntitiesNotDeleted,
		String messageCountEntitiesNotDeleted,
		String headingEntitiesRestored,
		String messageEntitiesRestored,
		String messageEntitiesNotRestored,
		String headingSomeEntitiesNotRestored,
		String messageCountEntitiesNotRestored) {

		this.entities = entities;
		this.headingNoSelection = headingNoSelection;
		this.messageNoSelection = messageNoSelection;
		this.messageNoEligibleEntitySelected = messageNoEligibleEntitySelected;
		this.messageEntitiesEligibleForDeletion = messageEntitiesEligibleForDeletion;
		this.headingEntitiesDeleted = headingEntitiesDeleted;
		this.messageEntitiesDeleted = messageEntitiesDeleted;
		this.messageEntitiesNotDeleted = messageEntitiesNotDeleted;
		this.messageEntitiesNotDeletedExternalReason = messageEntitiesNotDeletedExternalReason;
		this.messageEntitiesNotDeletedLinkedEntitiesReason = messageEntitiesNotDeletedLinkedEntitiesReason;

		this.headingSomeEntitiesNotDeleted = headingSomeEntitiesNotDeleted;
		this.messageCountEntitiesNotDeleted = messageCountEntitiesNotDeleted;

		this.headingEntitiesRestored = headingEntitiesRestored;
		this.messageEntitiesRestored = messageEntitiesRestored;
		this.messageEntitiesNotRestored = messageEntitiesNotRestored;
		this.headingSomeEntitiesNotRestored = headingSomeEntitiesNotRestored;
		this.messageCountEntitiesNotRestored = messageCountEntitiesNotRestored;
	}

	public String getEntities() {
		return entities;
	}

	public String getHeadingNoSelection() {
		return headingNoSelection;
	}

	public String getMessageNoSelection() {
		return messageNoSelection;
	}

	public String getMessageNoEligibleEntitySelected() {
		return messageNoEligibleEntitySelected;
	}

	public String getMessageEntitiesEligibleForDeletion() {
		return messageEntitiesEligibleForDeletion;
	}

	public String getHeadingEntitiesDeleted() {
		return headingEntitiesDeleted;
	}

	public String getMessageEntitiesDeleted() {
		return messageEntitiesDeleted;
	}

	public String getMessageEntitiesNotDeleted() {
		return messageEntitiesNotDeleted;
	}

	public String getMessageEntitiesNotDeletedExternalReason() {
		return messageEntitiesNotDeletedExternalReason;
	}

	public String getMessageEntitiesNotDeletedLinkedEntitiesReason() {
		return messageEntitiesNotDeletedLinkedEntitiesReason;
	}

	public String getHeadingSomeEntitiesNotDeleted() {
		return headingSomeEntitiesNotDeleted;
	}

	public String getMessageCountEntitiesNotDeleted() {
		return messageCountEntitiesNotDeleted;
	}

	public String getHeadingEntitiesRestored() {
		return headingEntitiesRestored;
	}

	public String getMessageEntitiesRestored() {
		return messageEntitiesRestored;
	}

	public String getMessageEntitiesNotRestored() {
		return messageEntitiesNotRestored;
	}

	public String getHeadingSomeEntitiesNotRestored() {
		return headingSomeEntitiesNotRestored;
	}

	public String getMessageCountEntitiesNotRestored() {
		return messageCountEntitiesNotRestored;
	}
}
