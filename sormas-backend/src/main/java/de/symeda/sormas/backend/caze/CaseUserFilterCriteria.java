package de.symeda.sormas.backend.caze;

public class CaseUserFilterCriteria {

	/**
	 * Exclude cases that are only visible to the user because they have access to at least one of their contacts.
	 */
	private boolean excludeCasesFromContacts;
	private Boolean includeCasesFromOtherJurisdictions = Boolean.FALSE;
	private boolean excludeLimitedSyncRestrictions;

	public boolean isExcludeCasesFromContacts() {
		return excludeCasesFromContacts;
	}

	public CaseUserFilterCriteria excludeCasesFromContacts(boolean excludeCasesFromContacts) {
		this.excludeCasesFromContacts = excludeCasesFromContacts;
		return this;
	}

	public Boolean getIncludeCasesFromOtherJurisdictions() {
		return includeCasesFromOtherJurisdictions;
	}

	public void setIncludeCasesFromOtherJurisdictions(Boolean includeCasesFromOtherJurisdictions) {
		this.includeCasesFromOtherJurisdictions = includeCasesFromOtherJurisdictions;
	}

	public boolean isExcludeLimitedSyncRestrictions() {
		return excludeLimitedSyncRestrictions;
	}

	public CaseUserFilterCriteria excludeLimitedSyncRestrictions(boolean excludeLimitedSyncRestrictions) {
		this.excludeLimitedSyncRestrictions = excludeLimitedSyncRestrictions;
		return this;
	}
}
