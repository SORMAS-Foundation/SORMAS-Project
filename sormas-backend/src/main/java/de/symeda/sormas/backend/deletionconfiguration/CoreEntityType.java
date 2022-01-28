package de.symeda.sormas.backend.deletionconfiguration;

import de.symeda.sormas.api.deletionconfiguration.DeletionReference;

public enum CoreEntityType {

	//ENTITY LIST
	CASE(DeletionReference.CREATION, 3650),
	CONTACT(DeletionReference.CREATION, 3650),
	EVENT(DeletionReference.CREATION, 3650),
	EVENT_PARTICIPANT(DeletionReference.CREATION, 3650),
	IMMUNIZATION(DeletionReference.CREATION, 3650),
	TRAVEL_ENTRY(DeletionReference.ORIGIN, 14);

	private final DeletionReference deletionReference;
	private final int deletionPeriod;

	CoreEntityType(DeletionReference deletionReference, int deletionPeriod) {
		this.deletionReference = deletionReference;
		this.deletionPeriod = deletionPeriod;
	}

	public DeletionReference getDeletionReference() {
		return deletionReference;
	}

	public int getDeletionPeriod() {
		return deletionPeriod;
	}
}
