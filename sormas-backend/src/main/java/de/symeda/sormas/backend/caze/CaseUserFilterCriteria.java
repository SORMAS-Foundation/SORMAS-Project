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

	/**
	 * Ignore user filter restrictions that would otherwise be applied by the limited synchronization feature.
	 * Necessary e.g. when retrieving UUIDs of cases that are supposed to be removed from the
	 * mobile app, because otherwise the user filter would exclude those cases.
	 */
	public boolean isExcludeLimitedSyncRestrictions() {
		return excludeLimitedSyncRestrictions;
	}

	public CaseUserFilterCriteria excludeLimitedSyncRestrictions(boolean excludeLimitedSyncRestrictions) {
		this.excludeLimitedSyncRestrictions = excludeLimitedSyncRestrictions;
		return this;
	}
}
