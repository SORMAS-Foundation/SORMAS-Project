package de.symeda.sormas.api.externalmessage.survey;

import java.util.Date;
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

	private String externalRespondentId;

	private Date responseReceivedDate;

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

	/**
	 * If true, for enumeration-like targetTypes the default value will be used.
	 * Mostly "OTHER".
	 */
	private boolean allowFallbackValues = true;

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

	public String getExternalRespondentId() {
		return externalRespondentId;
	}

	public ExternalMessageSurveyResponseRequest setExternalRespondentId(String externalRespondentId) {
		this.externalRespondentId = externalRespondentId;
		return this;
	}

	public Date getResponseReceivedDate() {
		return responseReceivedDate;
	}

	public ExternalMessageSurveyResponseRequest setResponseReceivedDate(Date responseReceivedDate) {
		this.responseReceivedDate = responseReceivedDate;
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

	public boolean isAllowFallbackValues() {
		return allowFallbackValues;
	}

	public ExternalMessageSurveyResponseRequest setAllowFallbackValues(boolean allowFallbackValues) {
		this.allowFallbackValues = allowFallbackValues;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ExternalMessageSurveyResponseRequest that = (ExternalMessageSurveyResponseRequest) o;
		return patchedInCaseOfFailures == that.patchedInCaseOfFailures
			&& allowFallbackValues == that.allowFallbackValues
			&& Objects.equals(token, that.token)
			&& Objects.equals(externalSurveyId, that.externalSurveyId)
			&& Objects.equals(externalRespondentId, that.externalRespondentId)
			&& Objects.equals(responseReceivedDate, that.responseReceivedDate)
			&& replacementStrategy == that.replacementStrategy
			&& emptyValueBehavior == that.emptyValueBehavior
			&& Objects.equals(patchDictionary, that.patchDictionary)
			&& Objects.equals(origin, that.origin)
			&& Objects.equals(inputLanguages, that.inputLanguages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			token,
			externalSurveyId,
			externalRespondentId,
			responseReceivedDate,
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
		return "ExternalMessageSurveyResponseRequest{" + "token='" + token + '\'' + ", externalSurveyId='" + externalSurveyId + '\''
			+ ", externalRespondentId='" + externalRespondentId + '\'' + ", responseReceivedDate=" + responseReceivedDate
			+ ", patchedInCaseOfFailures=" + patchedInCaseOfFailures + ", replacementStrategy=" + replacementStrategy + ", emptyValueBehavior="
			+ emptyValueBehavior + ", patchDictionary=" + patchDictionary + ", origin='" + origin + '\'' + ", inputLanguages=" + inputLanguages
			+ ", allowFallbackValues=" + allowFallbackValues + '}';
	}
}
