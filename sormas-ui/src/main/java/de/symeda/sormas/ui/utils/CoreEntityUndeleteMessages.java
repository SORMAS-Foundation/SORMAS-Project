package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.Strings;

public enum CoreEntityUndeleteMessages {

	CASE(Strings.entityCases,
		Strings.headingNoCasesSelected,
		Strings.messageNoCasesSelected,
		Strings.headingCasesRestored,
		Strings.messageCasesRestored,
		Strings.headingSomeCasesNotRestored,
		Strings.messageCountCasesNotRestored),

	CONTACT(Strings.entityContacts,
		Strings.headingNoContactsSelected,
		Strings.messageNoContactsSelected,
		Strings.headingContactsRestored,
		Strings.messageContactsRestored,
		Strings.headingSomeContactsNotRestored,
		Strings.messageCountContactsNotRestored),

	EVENT(Strings.entityEvents,
		Strings.headingNoEventsSelected,
		Strings.messageNoEventsSelected,
		Strings.headingEventsRestored,
		Strings.messageEventsRestored,
		Strings.headingSomeEventsNotRestored,
		Strings.messageCountEventsNotRestored),

	EVENT_PARTICIPANT(Strings.entityEventParticipants,
		Strings.headingNoEventParticipantsSelected,
		Strings.messageNoEventParticipantsSelected,
		Strings.headingEventParticipantsRestored,
		Strings.messageEventParticipantsRestored,
		Strings.headingSomeEventParticipantsNotRestored,
		Strings.messageCountEventParticipantsNotRestored),

	IMMUNIZATION(Strings.entityImmunizations,
		Strings.headingNoImmunizationsSelected,
		Strings.messageNoImmunizationsSelected,
		Strings.headingImmunizationsRestored,
		Strings.messageImmunizationsRestored,
		Strings.headingSomeImmunizationsNotRestored,
		Strings.messageCountImmunizationsNotRestored),

	SAMPLE(Strings.entitySamples,
		Strings.headingNoSamplesSelected,
		Strings.messageNoSamplesSelected,
		Strings.headingSamplesRestored,
		Strings.messageSamplesRestored,
		Strings.headingSomeSamplesNotRestored,
		Strings.messageCountSamplesNotRestored),

	TRAVEL_ENTRY(Strings.entityTravelEntries,
		Strings.headingNoTravelEntriesSelected,
		Strings.messageNoTravelEntriesSelected,
		Strings.headingTravelEntriesRestored,
		Strings.messageTravelEntriesRestored,
		Strings.headingSomeTravelEntriesNotRestored,
		Strings.messageCountTravelEntriesNotRestored);

	private final String entities;
	private final String headingNoSelection;
	private final String messageNoSelection;
	private final String headingEntitiesRestored;
	private final String messageEntitiesRestored;
	private final String headingSomeEntitiesNotRestored;
	private final String messageCountEntitiesNotRestored;

	CoreEntityUndeleteMessages(
		String entities,
		String headingNoSelection,
		String messageNoSelection,
		String headingEntitiesRestored,
		String messageEntitiesRestored,
		String headingSomeEntitiesNotRestored,
		String messageCountEntitiesNotRestored) {
		this.entities = entities;
		this.headingNoSelection = headingNoSelection;
		this.messageNoSelection = messageNoSelection;
		this.headingEntitiesRestored = headingEntitiesRestored;
		this.messageEntitiesRestored = messageEntitiesRestored;
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

	public String getHeadingEntitiesRestored() {
		return headingEntitiesRestored;
	}

	public String getMessageEntitiesRestored() {
		return messageEntitiesRestored;
	}

	public String getHeadingSomeEntitiesNotRestored() {
		return headingSomeEntitiesNotRestored;
	}

	public String getMessageCountEntitiesNotRestored() {
		return messageCountEntitiesNotRestored;
	}
}
