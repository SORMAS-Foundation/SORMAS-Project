package de.symeda.sormas.api.patch.mapping;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.patch.DataReplacementStrategy;
import de.symeda.sormas.api.patch.EmptyValueBehavior;
import de.symeda.sormas.api.person.PersonReferenceDto;

/**
 * Request object to patch more complex objects together will all fields belonging to that object.
 */
public class GroupedFieldsRequest {

	@NotNull
	private Disease disease;

	@NotNull
	private CaseReferenceDto caseData;

	@NotNull
	private Supplier<PersonReferenceDto> person;

	private boolean patchedInCaseOfFailures = false;

	@NotNull
	private DataReplacementStrategy replacementStrategy = DataReplacementStrategy.IF_NOT_ALREADY_PRESENT;

	@NotNull
	private EmptyValueBehavior emptyValueBehavior = EmptyValueBehavior.IGNORE;

	/**
	 * Only contains the key and values adapted for the current {@link GroupedFieldsMapper}.
	 */
	@NotNull
	private Map<String, Object> partialPatchDictionary;

	/**
	 * Origin that wants the patch operation.
	 */
	@Nullable
	private String origin;

	/**
	 * To be able to support I18n inputs the input languages can be passed, system locale by default.
	 */
	@Nullable
	private List<Language> inputLanguages;

	/**
	 * If true, for enumeration-like targetTypes the default value will be used.
	 * Mostly "OTHER".
	 */
	private boolean allowFallbackValues = true;

	public boolean isPatchedInCaseOfFailures() {
		return patchedInCaseOfFailures;
	}

	public GroupedFieldsRequest setPatchedInCaseOfFailures(boolean patchedInCaseOfFailures) {
		this.patchedInCaseOfFailures = patchedInCaseOfFailures;
		return this;
	}

	public DataReplacementStrategy getReplacementStrategy() {
		return replacementStrategy;
	}

	public GroupedFieldsRequest setReplacementStrategy(DataReplacementStrategy replacementStrategy) {
		this.replacementStrategy = replacementStrategy;
		return this;
	}

	public EmptyValueBehavior getEmptyValueBehavior() {
		return emptyValueBehavior;
	}

	public GroupedFieldsRequest setEmptyValueBehavior(EmptyValueBehavior emptyValueBehavior) {
		this.emptyValueBehavior = emptyValueBehavior;
		return this;
	}

	public Map<String, Object> getPartialPatchDictionary() {
		return partialPatchDictionary;
	}

	public GroupedFieldsRequest setPartialPatchDictionary(Map<String, Object> partialPatchDictionary) {
		this.partialPatchDictionary = partialPatchDictionary;
		return this;
	}

	@Nullable
	public String getOrigin() {
		return origin;
	}

	public GroupedFieldsRequest setOrigin(@Nullable String origin) {
		this.origin = origin;
		return this;
	}

	@Nullable
	public List<Language> getInputLanguages() {
		return inputLanguages;
	}

	public GroupedFieldsRequest setInputLanguages(@Nullable List<Language> inputLanguages) {
		this.inputLanguages = inputLanguages;
		return this;
	}

	public boolean isAllowFallbackValues() {
		return allowFallbackValues;
	}

	public GroupedFieldsRequest setAllowFallbackValues(boolean allowFallbackValues) {
		this.allowFallbackValues = allowFallbackValues;
		return this;
	}

	public CaseReferenceDto getCaseData() {
		return caseData;
	}

	public GroupedFieldsRequest setCaseData(CaseReferenceDto caseData) {
		this.caseData = caseData;
		return this;
	}

	public Supplier<PersonReferenceDto> getPerson() {
		return person;
	}

	public GroupedFieldsRequest setPerson(Supplier<PersonReferenceDto> person) {
		this.person = person;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public GroupedFieldsRequest setDisease(Disease disease) {
		this.disease = disease;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		GroupedFieldsRequest that = (GroupedFieldsRequest) o;
		return patchedInCaseOfFailures == that.patchedInCaseOfFailures
			&& allowFallbackValues == that.allowFallbackValues
			&& disease == that.disease
			&& Objects.equals(caseData, that.caseData)
			&& Objects.equals(person, that.person)
			&& replacementStrategy == that.replacementStrategy
			&& emptyValueBehavior == that.emptyValueBehavior
			&& Objects.equals(partialPatchDictionary, that.partialPatchDictionary)
			&& Objects.equals(origin, that.origin)
			&& Objects.equals(inputLanguages, that.inputLanguages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			disease,
			caseData,
			person,
			patchedInCaseOfFailures,
			replacementStrategy,
			emptyValueBehavior,
			partialPatchDictionary,
			origin,
			inputLanguages,
			allowFallbackValues);
	}

	@Override
	public String toString() {
		return "GroupedFieldsRequest{" + "disease=" + disease + ", caseData=" + caseData + ", person=" + person + ", patchedInCaseOfFailures="
			+ patchedInCaseOfFailures + ", replacementStrategy=" + replacementStrategy + ", emptyValueBehavior=" + emptyValueBehavior
			+ ", partialPatchDictionary=" + partialPatchDictionary + ", origin='" + origin + '\'' + ", inputLanguages=" + inputLanguages
			+ ", allowFallbackValues=" + allowFallbackValues + '}';
	}
}
