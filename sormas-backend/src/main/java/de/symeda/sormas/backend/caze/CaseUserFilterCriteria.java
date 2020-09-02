package de.symeda.sormas.backend.caze;

public class CaseUserFilterCriteria {

	/**
	 * Exclude cases that are only visible to the user because they have been shared to the whole country.
	 */
	private boolean excludeSharedCases;

	/**
	 * Exclude cases that are only visible to the user because they have access to at least one of their contacts.
	 */
	private boolean excludeCasesFromContacts;

	public boolean isExcludeSharedCases() {
		return excludeSharedCases;
	}

	public CaseUserFilterCriteria excludeSharedCases(boolean excludeSharedCases) {
		this.excludeSharedCases = excludeSharedCases;
		return this;
	}

	public boolean isExcludeCasesFromContacts() {
		return excludeCasesFromContacts;
	}

	public CaseUserFilterCriteria excludeCasesFromContacts(boolean excludeCasesFromContacts) {
		this.excludeCasesFromContacts = excludeCasesFromContacts;
		return this;
	}
}
