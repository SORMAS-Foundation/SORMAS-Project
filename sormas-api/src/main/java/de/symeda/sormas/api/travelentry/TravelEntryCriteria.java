package de.symeda.sormas.api.travelentry;

import java.io.Serializable;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class TravelEntryCriteria extends BaseCriteria implements Serializable, Cloneable {

	public static final String NAME_UUID_EXTERNAL_ID_LIKE = "nameUuidExternalIDLike";
	public static final String ONLY_RECOVERED_ENTRIES = "onlyRecoveredEntries";
	public static final String ONLY_VACCINATED_ENTRIES = "onlyVaccinatedEntries";
	public static final String ONLY_ENTRIES_TESTED_NEGATIVE = "onlyEntriesTestedNegative";
	public static final String ONLY_ENTRIES_CONVERTED_TO_CASE = "onlyEntriesConvertedToCase";

	private String nameUuidExternalIDLike;
	private Boolean onlyRecoveredEntries = Boolean.FALSE;
	private Boolean onlyVaccinatedEntries = Boolean.FALSE;
	private Boolean onlyEntriesTestedNegative = Boolean.FALSE;
	private Boolean onlyEntriesConvertedToCase = Boolean.FALSE;

	public String getNameUuidExternalIDLike() {
		return nameUuidExternalIDLike;
	}

	public void setNameUuidExternalIDLike(String nameUuidExternalIDLike) {
		this.nameUuidExternalIDLike = nameUuidExternalIDLike;
	}

	public Boolean getOnlyRecoveredEntries() {
		return onlyRecoveredEntries;
	}

	public void setOnlyRecoveredEntries(Boolean onlyRecoveredEntries) {
		this.onlyRecoveredEntries = onlyRecoveredEntries;
	}

	public Boolean getOnlyVaccinatedEntries() {
		return onlyVaccinatedEntries;
	}

	public void setOnlyVaccinatedEntries(Boolean onlyVaccinatedEntries) {
		this.onlyVaccinatedEntries = onlyVaccinatedEntries;
	}

	public Boolean getOnlyEntriesTestedNegative() {
		return onlyEntriesTestedNegative;
	}

	public void setOnlyEntriesTestedNegative(Boolean onlyEntriesTestedNegative) {
		this.onlyEntriesTestedNegative = onlyEntriesTestedNegative;
	}

	public Boolean getOnlyEntriesConvertedToCase() {
		return onlyEntriesConvertedToCase;
	}

	public void setOnlyEntriesConvertedToCase(Boolean onlyEntriesConvertedToCase) {
		this.onlyEntriesConvertedToCase = onlyEntriesConvertedToCase;
	}
}
