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
		Strings.headingCasesNotDeleted,
		Strings.messageCountCasesNotDeletedExternalReason,
		Strings.messageCountCasesNotDeletedSormasToSormasReason,
		Strings.messageCountCasesNotDeletedAccessDeniedReason,
		null,
		Strings.messageCountCasesNotDeleted,
		Strings.headingCasesRestored,
		Strings.messageCasesRestored,
		Strings.headingCasesNotRestored,
		Strings.headingSomeCasesNotRestored,
		Strings.messageCountCasesNotRestored),

	EVENT(Strings.entityEvents,
		Strings.headingNoEventsSelected,
		Strings.messageNoEventsSelected,
		Strings.messageNoEligibleEventForDeletionSelected,
		null,
		Strings.headingEventsDeleted,
		Strings.messageEventsDeleted,
		Strings.headingEventsNotDeleted,
		Strings.messageCountEventsNotDeletedExternalReason,
		Strings.messageCountContactsNotDeletedSormasToSormasReason,
		Strings.messageCountEventsNotDeletedAccessDeniedReason,
		Strings.messageEventsNotDeletedLinkedEntitiesReason,
		Strings.messageCountEventsNotDeleted,
		Strings.headingEventsRestored,
		Strings.messageEventsRestored,
		Strings.headingEventsNotRestored,
		Strings.headingSomeEventsNotRestored,
		Strings.messageCountEventsNotRestored),

	EVENT_PARTICIPANT(Strings.entityEventParticipants,
		Strings.headingNoEventParticipantsSelected,
		Strings.messageNoEventParticipantsSelected,
		null,
		null,
		Strings.headingEventParticipantsDeleted,
		Strings.messageEventParticipantsDeleted,
		Strings.headingEventParticipantsNotDeleted,
		null,
		null,
		Strings.messageCountEventParticipantsNotDeletedAccessDeniedReason,
		null,
		Strings.messageCountEventParticipantsNotDeleted,
		Strings.headingEventParticipantsRestored,
		Strings.messageEventParticipantsRestored,
		Strings.headingEventParticipantsNotRestored,
		Strings.headingSomeEventParticipantsNotRestored,
		Strings.messageCountEventParticipantsNotRestored),

	CONTACT(Strings.entityContacts,
		Strings.headingNoContactsSelected,
		Strings.messageNoContactsSelected,
		null,
		null,
		Strings.headingContactsDeleted,
		Strings.messageContactsDeleted,
		Strings.headingContactsNotDeleted,
		null,
		Strings.messageCountContactsNotDeletedSormasToSormasReason,
		Strings.messageCountContactsNotDeletedAccessDeniedReason,
		null,
		Strings.messageCountContactsNotDeleted,
		Strings.headingContactsRestored,
		Strings.messageContactsRestored,
		Strings.headingContactsNotRestored,
		Strings.headingSomeContactsNotRestored,
		Strings.messageCountContactsNotRestored),

	TRAVEL_ENTRY(Strings.entityTravelEntries,
		Strings.headingNoTravelEntriesSelected,
		Strings.messageNoTravelEntriesSelected,
		null,
		null,
		Strings.headingTravelEntriesDeleted,
		Strings.messageTravelEntriesDeleted,
		Strings.headingTravelEntriesNotDeleted,
		null,
		null,
		Strings.messageCountTravelEntriesNotDeletedAccessDeniedReason,
		null,
		Strings.messageCountTravelEntriesNotDeleted,
		Strings.headingTravelEntriesRestored,
		Strings.messageTravelEntriesRestored,
		Strings.headingTravelEntriesNotRestored,
		Strings.headingSomeTravelEntriesNotRestored,
		Strings.messageCountTravelEntriesNotRestored),

	VISIT(Strings.entityVisits,
		Strings.headingNoVisitsSelected,
		Strings.messageNoVisitsSelected,
		null,
		null,
		Strings.headingVisitsDeleted,
		Strings.messageVisitsDeleted,
		Strings.headingVisitsNotDeleted,
		null,
		null,
		Strings.messageCountVisitsNotDeletedAccessDeniedReason,
		null,
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
		Strings.headingTasksNotDeleted,
		null,
		null,
		Strings.messageCountTasksNotDeletedAccessDeniedReason,
		null,
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
		Strings.headingExternalMessagesNotDeleted,
		null,
		null,
		null,
		null,
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
		Strings.headingSamplesNotDeleted,
		null,
		null,
		null,
		null,
		Strings.messageCountSamplesNotDeleted,
		Strings.headingSamplesRestored,
		Strings.messageSamplesRestored,
		Strings.headingSamplesNotRestored,
		Strings.headingSomeSamplesNotRestored,
		Strings.messageCountSamplesNotRestored),

	IMMUNIZATION(Strings.entityImmunizations,
		Strings.headingNoImmunizationsSelected,
		Strings.messageNoImmunizationsSelected,
		null,
		null,
		Strings.headingImmunizationsDeleted,
		Strings.messageImmunizationsDeleted,
		Strings.headingImmunizationsNotDeleted,
		null,
		null,
		Strings.messageCountImmunizationsNotDeletedAccessDeniedReason,
		null,
		Strings.messageCountImmunizationsNotDeleted,
		Strings.headingImmunizationsRestored,
		Strings.messageImmunizationsRestored,
		Strings.headingImmunizationsNotRestored,
		Strings.headingSomeImmunizationsNotRestored,
		Strings.messageCountImmunizationsNotRestored),

	ENVIRONMENT_SAMPLE(Strings.entityEnvironmentSamples,
		Strings.headingNoEnvironmentSamplesSelected,
		Strings.messageNoEnvironmentSamplesSelected,
		null,
		null,
		Strings.headingEnvironmentSamplesDeleted,
		Strings.messageEnvironmentSamplesDeleted,
		Strings.headingEnvironmentSamplesNotDeleted,
		null,
		null,
		Strings.messageCountEnvironmentSamplesNotDeletedAccessDeniedReason,
		null,
		Strings.messageCountEnvironmentSamplesNotDeleted,
		Strings.headingEnvironmentSamplesRestored,
		Strings.messageEnvironmentSamplesRestored,
		Strings.headingEnvironmentSamplesNotRestored,
		Strings.headingSomeEnvironmentSamplesNotRestored,
		Strings.messageCountEnvironmentSamplesNotRestored);

	private final String entities;
	private final String headingNoSelection;
	private final String messageNoSelection;
	private final String messageNoEligibleEntitySelected;
	private final String messageEntitiesEligibleForDeletion;
	private final String headingEntitiesDeleted;
	private final String messageEntitiesDeleted;
	private final String headingEntitiesNotDeleted;
	private final String messageCountEntitiesNotDeletedExternalReason;
	private final String messageCountEntitiesNotDeletedSormasToSormasReason;
	private final String messageCountEntitiesNotDeletedAccessDeniedReason;
	private final String messageEntitiesNotDeletedLinkedEntitiesReason;
	private final String messageCountEntitiesNotDeleted;
	private final String headingEntitiesRestored;
	private final String messageEntitiesRestored;
	private final String headingEntitiesNotRestored;
	private final String headingSomeEntitiesNotRestored;
	private final String messageCountEntitiesNotRestored;

	private final String headingSomeEntitiesNotDeleted = Strings.headingSomeEntitiesNotDeleted;

	DeleteRestoreMessages(
		String entities,
		String headingNoSelection,
		String messageNoSelection,
		String messageNoEligibleEntitySelected,
		String messageEntitiesEligibleForDeletion,
		String headingEntitiesDeleted,
		String messageEntitiesDeleted,
		String headingEntitiesNotDeleted,
		String messageCountEntitiesNotDeletedExternalReason,
		String messageCountEntitiesNotDeletedSormasToSormasReason,
		String messageCountEntitiesNotDeletedAccessDeniedReason,
		String messageEntitiesNotDeletedLinkedEntitiesReason,
		String messageCountEntitiesNotDeleted,
		String headingEntitiesRestored,
		String messageEntitiesRestored,
		String headingEntitiesNotRestored,
		String headingSomeEntitiesNotRestored,
		String messageCountEntitiesNotRestored) {

		this.entities = entities;
		this.headingNoSelection = headingNoSelection;
		this.messageNoSelection = messageNoSelection;
		this.messageNoEligibleEntitySelected = messageNoEligibleEntitySelected;
		this.messageEntitiesEligibleForDeletion = messageEntitiesEligibleForDeletion;
		this.headingEntitiesDeleted = headingEntitiesDeleted;
		this.messageEntitiesDeleted = messageEntitiesDeleted;
		this.headingEntitiesNotDeleted = headingEntitiesNotDeleted;
		this.messageCountEntitiesNotDeletedExternalReason = messageCountEntitiesNotDeletedExternalReason;
		this.messageCountEntitiesNotDeletedSormasToSormasReason = messageCountEntitiesNotDeletedSormasToSormasReason;
		this.messageCountEntitiesNotDeletedAccessDeniedReason = messageCountEntitiesNotDeletedAccessDeniedReason;
		this.messageEntitiesNotDeletedLinkedEntitiesReason = messageEntitiesNotDeletedLinkedEntitiesReason;
		this.messageCountEntitiesNotDeleted = messageCountEntitiesNotDeleted;
		this.headingEntitiesRestored = headingEntitiesRestored;
		this.messageEntitiesRestored = messageEntitiesRestored;
		this.headingEntitiesNotRestored = headingEntitiesNotRestored;
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

	public String getHeadingEntitiesNotDeleted() {
		return headingEntitiesNotDeleted;
	}

	public String getMessageCountEntitiesNotDeletedExternalReason() {
		return messageCountEntitiesNotDeletedExternalReason;
	}

	public String getMessageCountEntitiesNotDeletedSormasToSormasReason() {
		return messageCountEntitiesNotDeletedSormasToSormasReason;
	}

	public String getMessageCountEntitiesNotDeletedAccessDeniedReason() {
		return messageCountEntitiesNotDeletedAccessDeniedReason;
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

	public String getHeadingEntitiesNotRestored() {
		return headingEntitiesNotRestored;
	}

	public String getHeadingSomeEntitiesNotRestored() {
		return headingSomeEntitiesNotRestored;
	}

	public String getMessageCountEntitiesNotRestored() {
		return messageCountEntitiesNotRestored;
	}
}
