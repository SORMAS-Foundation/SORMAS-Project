package de.symeda.sormas.api.patch;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.externalmessage.survey.PatchDictionary;
import de.symeda.sormas.api.info.InfoFacade;

/**
 * Specifies how the patch operation must be performed.
 */
public class CaseDataPatchRequest {

	@NotNull
	private String caseUuid;

	private boolean patchedInCaseOfFailures = false;

	@NotNull
	private DataReplacementStrategy replacementStrategy = DataReplacementStrategy.IF_NOT_ALREADY_PRESENT;

	@NotNull
	private EmptyValueBehavior emptyValueBehavior = EmptyValueBehavior.IGNORE;

	/**
	 * Key are those from with root being the {@link de.symeda.sormas.api.caze.CaseDataDto}.
	 * The accepted fields are those from {@link InfoFacade#generateDataDictionary()}.
	 */
	@NotNull
	private PatchDictionary patchDictionary;

	/**
	 * Origin that wants the patch operation.
	 * Can be used within {@link de.symeda.sormas.api.patch.mapping.FieldCustomMapper}.
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

	public String getCaseUuid() {
		return caseUuid;
	}

	public CaseDataPatchRequest setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
		return this;
	}

	public DataReplacementStrategy getReplacementStrategy() {
		return replacementStrategy;
	}

	public CaseDataPatchRequest setReplacementStrategy(DataReplacementStrategy replacementStrategy) {
		this.replacementStrategy = replacementStrategy;
		return this;
	}

	public PatchDictionary getPatchDictionary() {
		return patchDictionary;
	}

	public CaseDataPatchRequest setPatchDictionary(PatchDictionary patchDictionary) {
		this.patchDictionary = patchDictionary;
		return this;
	}

	public CaseDataPatchRequest setPatchDictionary(Map<String, Object> patchDictionary) {
		PatchDictionary patchDictionaryWrapper = new PatchDictionary();
		patchDictionary.forEach(patchDictionaryWrapper::put);
		this.patchDictionary = patchDictionaryWrapper;
		return this;
	}

	public EmptyValueBehavior getEmptyValueBehavior() {
		return emptyValueBehavior;
	}

	public CaseDataPatchRequest setEmptyValueBehavior(EmptyValueBehavior emptyValueBehavior) {
		this.emptyValueBehavior = emptyValueBehavior;
		return this;
	}

	@Nullable
	public String getOrigin() {
		return origin;
	}

	public CaseDataPatchRequest setOrigin(@Nullable String origin) {
		this.origin = origin;
		return this;
	}

	public List<Language> getInputLanguages() {
		return inputLanguages;
	}

	public CaseDataPatchRequest setInputLanguages(List<Language> inputLanguages) {
		this.inputLanguages = inputLanguages;
		return this;
	}

	public boolean isAllowFallbackValues() {
		return allowFallbackValues;
	}

	public CaseDataPatchRequest setAllowFallbackValues(boolean allowFallbackValues) {
		this.allowFallbackValues = allowFallbackValues;
		return this;
	}

	public boolean isPatchedInCaseOfFailures() {
		return patchedInCaseOfFailures;
	}

	public CaseDataPatchRequest setPatchedInCaseOfFailures(boolean patchedInCaseOfFailures) {
		this.patchedInCaseOfFailures = patchedInCaseOfFailures;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CaseDataPatchRequest that = (CaseDataPatchRequest) o;
		return patchedInCaseOfFailures == that.patchedInCaseOfFailures
			&& allowFallbackValues == that.allowFallbackValues
			&& Objects.equals(caseUuid, that.caseUuid)
			&& replacementStrategy == that.replacementStrategy
			&& emptyValueBehavior == that.emptyValueBehavior
			&& Objects.equals(patchDictionary, that.patchDictionary)
			&& Objects.equals(origin, that.origin)
			&& Objects.equals(inputLanguages, that.inputLanguages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			caseUuid,
			patchedInCaseOfFailures,
			replacementStrategy,
			emptyValueBehavior,
			patchDictionary,
			origin,
			inputLanguages,
			allowFallbackValues);
	}

	@Override
	public String toString() {
		return "CaseDataPatchRequest{" + "caseUuid='" + caseUuid + '\'' + ", patchedInCaseOfFailures=" + patchedInCaseOfFailures
			+ ", replacementStrategy=" + replacementStrategy + ", emptyValueBehavior=" + emptyValueBehavior + ", patchDictionary=" + patchDictionary
			+ ", origin='" + origin + '\'' + ", inputLanguages=" + inputLanguages + ", allowFallbackValues=" + allowFallbackValues + '}';
	}
}
