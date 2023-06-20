package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.Strings;

public enum CoreEntityDeleteMessages {

	CASE(Strings.entityCases,
		Strings.headingNoCasesSelected,
		Strings.messageNoCasesSelected,
		Strings.headingCasesDeleted,
		Strings.messageCasesDeleted,
		Strings.messageCasesNotDeleted,
		Strings.headingSomeCasesNotDeleted,
		Strings.messageCountCasesNotDeleted),

	CONTACT(Strings.entityContacts,
		Strings.headingNoContactsSelected,
		Strings.messageNoContactsSelected,
		Strings.headingContactsDeleted,
		Strings.messageContactsDeleted,
		Strings.messageContactsNotDeleted,
		Strings.headingSomeContactsNotDeleted,
		Strings.messageCountContactsNotDeleted),

	TRAVEL_ENTRY(Strings.entityTravelEntries,
		Strings.headingNoTravelEntriesSelected,
		Strings.messageNoTravelEntriesSelected,
		Strings.headingTravelEntriesDeleted,
		Strings.messageTravelEntriesDeleted,
		Strings.messageTravelEntriesNotDeleted,
		Strings.headingSomeTravelEntriesNotDeleted,
		Strings.messageCountTravelEntriesNotDeleted),

	VISIT(Strings.entityVisits,
		Strings.headingNoVisitsSelected,
		Strings.messageNoVisitsSelected,
		Strings.headingVisitsDeleted,
		Strings.messageVisitsDeleted,
		Strings.messageVisitsNotDeleted,
		Strings.headingSomeVisitsNotDeleted,
		Strings.messageCountVisitsNotDeleted),

	EVENT_PARTICIPANT(Strings.entityEventParticipants,
		Strings.headingNoEventParticipantsSelected,
		Strings.messageNoEventParticipantsSelected,
		Strings.headingEventParticipantsDeleted,
		Strings.messageEventParticipantsDeleted,
		Strings.messageEventParticipantsNotDeleted,
		Strings.headingSomeEventParticipantsNotDeleted,
		Strings.messageCountEventParticipantsNotDeleted),

	TASK(Strings.entityTasks,
		Strings.headingNoTasksSelected,
		Strings.messageNoTasksSelected,
		Strings.headingTasksDeleted,
		Strings.messageTasksDeleted,
		Strings.messageTasksNotDeleted,
		Strings.headingSomeTasksNotDeleted,
		Strings.messageCountTasksNotDeleted);

	/*
	 * EVENT(Strings.entityEvents,
	 * Strings.headingNoEventsSelected,
	 * Strings.messageNoEventsSelected,
	 * Strings.headingEventsDeleted,
	 * Strings.messageEventsDeleted,
	 * Strings.headingSomeEventsNotRestored,
	 * Strings.messageCountEventsNotDeleted),
	 * IMMUNIZATION(Strings.entityImmunizations,
	 * Strings.headingNoImmunizationsSelected,
	 * Strings.messageNoImmunizationsSelected,
	 * Strings.headingImmunizationsRestored,
	 * Strings.messageImmunizationsRestored,
	 * Strings.headingSomeImmunizationsNotRestored,
	 * Strings.messageCountImmunizationsNotRestored),
	 * SAMPLE(Strings.entitySamples,
	 * Strings.headingNoSamplesSelected,
	 * Strings.messageNoSamplesSelected,
	 * Strings.headingSamplesRestored,
	 * Strings.messageSamplesRestored,
	 * Strings.headingSomeSamplesNotRestored,
	 * Strings.messageCountSamplesNotRestored),
	 */

	private final String entities;
	private final String headingNoSelection;
	private final String messageNoSelection;
	private final String headingEntitiesDeleted;
	private final String messageEntitiesDeleted;
	private final String messageEntitiesNotDeleted;
	private final String headingSomeEntitiesNotDeleted;
	private final String messageCountEntitiesNotDeleted;

	CoreEntityDeleteMessages(
		String entities,
		String headingNoSelection,
		String messageNoSelection,
		String headingEntitiesDeleted,
		String messageEntitiesDeleted,
		String messageEntitiesNotDeleted,
		String headingSomeEntitiesNotDeleted,
		String messageCountEntitiesNotDeleted) {
		this.entities = entities;
		this.headingNoSelection = headingNoSelection;
		this.messageNoSelection = messageNoSelection;
		this.headingEntitiesDeleted = headingEntitiesDeleted;
		this.messageEntitiesDeleted = messageEntitiesDeleted;
		this.messageEntitiesNotDeleted = messageEntitiesNotDeleted;
		this.headingSomeEntitiesNotDeleted = headingSomeEntitiesNotDeleted;
		this.messageCountEntitiesNotDeleted = messageCountEntitiesNotDeleted;
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

	public String getHeadingEntitiesDeleted() {
		return headingEntitiesDeleted;
	}

	public String getMessageEntitiesDeleted() {
		return messageEntitiesDeleted;
	}

	public String getMessageEntitiesNotDeleted() {
		return messageEntitiesNotDeleted;
	}

	public String getHeadingSomeEntitiesNotDeleted() {
		return headingSomeEntitiesNotDeleted;
	}

	public String getMessageCountEntitiesNotDeleted() {
		return messageCountEntitiesNotDeleted;
	}
}
