package de.symeda.sormas.backend.deletionconfiguration;

import de.symeda.sormas.api.deletionconfiguration.DeletionReference;

public enum CoreEntityType {

	//ENTITY LIST
	CASE(DeletionReference.CREATION, null),
	CONTACT(DeletionReference.CREATION, null),
	EVENT(DeletionReference.CREATION, null),
	EVENT_PARTICIPANT(DeletionReference.CREATION, null),
	IMMUNIZATION(DeletionReference.CREATION, null),
	TRAVEL_ENTRY(DeletionReference.ORIGIN, null);

	private final DeletionReference deletionReference;
	private final Integer deletionPeriod;

	CoreEntityType(DeletionReference deletionReference, Integer deletionPeriod) {
		this.deletionReference = deletionReference;
		this.deletionPeriod = deletionPeriod;
	}

	public DeletionReference getDeletionReference() {
		return deletionReference;
	}

	public Integer getDeletionPeriod() {
		return deletionPeriod;
	}
}
