package de.symeda.sormas.api.travelentry;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
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
	private PersonReferenceDto person;
	private CaseReferenceDto caze;
	private Boolean deleted = Boolean.FALSE;
	private EntityRelevanceStatus relevanceStatus;
	private Date reportDateFrom;
	private Date reportDateTo;
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;

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

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public TravelEntryCriteria person(PersonReferenceDto person) {
		this.person = person;
		return this;
	}

	public CaseReferenceDto getCase() {
		return caze;
	}

	public void setCase(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public TravelEntryCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public void relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
	}

	public Date getReportDateFrom() {
		return reportDateFrom;
	}

	public void setReportDateFrom(Date reportDateFrom) {
		this.reportDateFrom = reportDateFrom;
	}

	public Date getReportDateTo() {
		return reportDateTo;
	}

	public void setReportDateTo(Date reportDateTo) {
		this.reportDateTo = reportDateTo;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public void setDateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
	}

	public TravelEntryCriteria reportDateBetween(
			Date reportDateFrom,
			Date reportDateTo,
			DateFilterOption dateFilterOption) {
		this.reportDateFrom = reportDateFrom;
		this.reportDateTo = reportDateTo;
		this.dateFilterOption = dateFilterOption;
		return this;
	}
}
