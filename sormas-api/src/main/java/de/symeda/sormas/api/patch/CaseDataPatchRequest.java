package de.symeda.sormas.api.patch;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.info.InfoFacade;

public class CaseDataPatchRequest {

	@NotNull
	private String caseUuid;

	@NotNull
	private DataReplacementStrategy replacementStrategy = DataReplacementStrategy.IF_NOT_ALREADY_PRESENT;

	private EmptyValueBehavior emptyValueBehavior = EmptyValueBehavior.IGNORE;

	/**
	 * Key are those from with root being the {@link de.symeda.sormas.api.caze.CaseDataDto}.
	 * The accepted fields are those from {@link InfoFacade#generateDataDictionary()}.
	 */
	@NotNull
	private Map<String, Object> patchDictionary;

	/**
	 * Can be used to add some custom descriptions in some fields.
	 */
	@Nullable
	private String origin;

	/**
	 * To be able to support I18n inputs the input languages can be passed, system locale by default.
	 */
	@Nullable
	private List<Language> inputLanguages;

	private boolean allowDefaultValues = true;

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

	public Map<String, Object> getPatchDictionary() {
		return patchDictionary;
	}

	public CaseDataPatchRequest setPatchDictionary(Map<String, Object> patchDictionary) {
		this.patchDictionary = patchDictionary;
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

	public boolean isAllowDefaultValues() {
		return allowDefaultValues;
	}

	public CaseDataPatchRequest setAllowDefaultValues(boolean allowDefaultValues) {
		this.allowDefaultValues = allowDefaultValues;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CaseDataPatchRequest that = (CaseDataPatchRequest) o;
		return allowDefaultValues == that.allowDefaultValues
			&& Objects.equals(caseUuid, that.caseUuid)
			&& replacementStrategy == that.replacementStrategy
			&& emptyValueBehavior == that.emptyValueBehavior
			&& Objects.equals(patchDictionary, that.patchDictionary)
			&& Objects.equals(origin, that.origin)
			&& Objects.equals(inputLanguages, that.inputLanguages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseUuid, replacementStrategy, emptyValueBehavior, patchDictionary, origin, inputLanguages, allowDefaultValues);
	}

	@Override
	public String toString() {
		return "CaseDataPatchRequest{" + "caseUuid='" + caseUuid + '\'' + ", replacementStrategy=" + replacementStrategy + ", emptyValueBehavior="
			+ emptyValueBehavior + ", patchDictionary=" + patchDictionary + ", origin='" + origin + '\'' + ", inputLanguages=" + inputLanguages
			+ ", allowDefaultValues=" + allowDefaultValues + '}';
	}
}
