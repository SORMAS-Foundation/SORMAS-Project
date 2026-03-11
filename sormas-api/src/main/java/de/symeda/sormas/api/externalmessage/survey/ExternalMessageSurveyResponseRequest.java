package de.symeda.sormas.api.externalmessage.survey;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.info.InfoFacade;
import de.symeda.sormas.api.patch.DataReplacementStrategy;
import de.symeda.sormas.api.patch.EmptyValueBehavior;

/**
 * Mandatory fields that require
 * Will be present for {@link ExternalMessageType#SURVEY_RESPONSE}.
 */
public class ExternalMessageSurveyResponseRequest {

	private String token;
	private String externalSurveyId;

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
	private Map<String, Object> patchDictionary;

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

	public String getToken() {
		return token;
	}

	public ExternalMessageSurveyResponseRequest setToken(String token) {
		this.token = token;
		return this;
	}

	public String getExternalSurveyId() {
		return externalSurveyId;
	}

	public ExternalMessageSurveyResponseRequest setExternalSurveyId(String externalSurveyId) {
		this.externalSurveyId = externalSurveyId;
		return this;
	}

	public boolean isPatchedInCaseOfFailures() {
		return patchedInCaseOfFailures;
	}

	public ExternalMessageSurveyResponseRequest setPatchedInCaseOfFailures(boolean patchedInCaseOfFailures) {
		this.patchedInCaseOfFailures = patchedInCaseOfFailures;
		return this;
	}

	public DataReplacementStrategy getReplacementStrategy() {
		return replacementStrategy;
	}

	public ExternalMessageSurveyResponseRequest setReplacementStrategy(DataReplacementStrategy replacementStrategy) {
		this.replacementStrategy = replacementStrategy;
		return this;
	}

	public EmptyValueBehavior getEmptyValueBehavior() {
		return emptyValueBehavior;
	}

	public ExternalMessageSurveyResponseRequest setEmptyValueBehavior(EmptyValueBehavior emptyValueBehavior) {
		this.emptyValueBehavior = emptyValueBehavior;
		return this;
	}

	public Map<String, Object> getPatchDictionary() {
		return patchDictionary;
	}

	public ExternalMessageSurveyResponseRequest setPatchDictionary(Map<String, Object> patchDictionary) {
		this.patchDictionary = patchDictionary;
		return this;
	}

	@Nullable
	public String getOrigin() {
		return origin;
	}

	public ExternalMessageSurveyResponseRequest setOrigin(@Nullable String origin) {
		this.origin = origin;
		return this;
	}

	@Nullable
	public List<Language> getInputLanguages() {
		return inputLanguages;
	}

	public ExternalMessageSurveyResponseRequest setInputLanguages(@Nullable List<Language> inputLanguages) {
		this.inputLanguages = inputLanguages;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ExternalMessageSurveyResponseRequest that = (ExternalMessageSurveyResponseRequest) o;
		return patchedInCaseOfFailures == that.patchedInCaseOfFailures
			&& Objects.equals(token, that.token)
			&& Objects.equals(externalSurveyId, that.externalSurveyId)
			&& replacementStrategy == that.replacementStrategy
			&& emptyValueBehavior == that.emptyValueBehavior
			&& Objects.equals(patchDictionary, that.patchDictionary)
			&& Objects.equals(origin, that.origin)
			&& Objects.equals(inputLanguages, that.inputLanguages);
	}

	@Override
	public int hashCode() {
		return Objects
			.hash(token, externalSurveyId, patchedInCaseOfFailures, replacementStrategy, emptyValueBehavior, patchDictionary, origin, inputLanguages);
	}
}
