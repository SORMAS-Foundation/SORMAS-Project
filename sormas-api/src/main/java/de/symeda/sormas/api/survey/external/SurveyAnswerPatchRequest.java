package de.symeda.sormas.api.survey.external;

import java.time.OffsetDateTime;
import java.util.Map;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.patch.DataReplacementStrategy;
import de.symeda.sormas.api.patch.EmptyValueBehavior;

/**
 * Request sent to SORMAS to update the information.
 */
public class SurveyAnswerPatchRequest {

	@NotNull
	private String surveyId;

	@NotNull
	private String token;

	@NotNull
	private String respondentId;

	@NotNull
	private OffsetDateTime answerDate;

	@NotNull
	private DataReplacementStrategy dataReplacementStrategy = DataReplacementStrategy.IF_NOT_ALREADY_PRESENT;

	@NotNull
	private EmptyValueBehavior emptyValueBehavior = EmptyValueBehavior.IGNORE;

	/**
	 * Key is a field to replace and value is the actual value that will be replaced.
	 * <p>
	 * Values will be applied according to the dataReplacementType.
	 */
	@NotNull
	private Map<String, String> patchDictionary;

	/**
	 * Could imagine that the cron in addition to the webhook could process elements twice.
	 * Set to false to force a refresh: data etc.
	 */
	private boolean skipIfAlreadyProcessed = true;

	/**
	 * In case 1 or more fields cannot be mapped, should the entire patch fail (nothing applied).
	 * True: means will not be applied in case of error, false: patching will still be applied.
	 */
	private boolean failOnError = false;

	public String getSurveyId() {
		return surveyId;
	}

	public SurveyAnswerPatchRequest setSurveyId(String surveyId) {
		this.surveyId = surveyId;
		return this;
	}

	public String getToken() {
		return token;
	}

	public SurveyAnswerPatchRequest setToken(String token) {
		this.token = token;
		return this;
	}

	public String getRespondentId() {
		return respondentId;
	}

	public SurveyAnswerPatchRequest setRespondentId(String respondentId) {
		this.respondentId = respondentId;
		return this;
	}

	public OffsetDateTime getAnswerDate() {
		return answerDate;
	}

	public SurveyAnswerPatchRequest setAnswerDate(OffsetDateTime answerDate) {
		this.answerDate = answerDate;
		return this;
	}

	public DataReplacementStrategy getDataReplacementType() {
		return dataReplacementStrategy;
	}

	public SurveyAnswerPatchRequest setDataReplacementType(DataReplacementStrategy dataReplacementStrategy) {
		this.dataReplacementStrategy = dataReplacementStrategy;
		return this;
	}

	public EmptyValueBehavior getEmptyValueBehavior() {
		return emptyValueBehavior;
	}

	public SurveyAnswerPatchRequest setEmptyValueBehavior(EmptyValueBehavior emptyValueBehavior) {
		this.emptyValueBehavior = emptyValueBehavior;
		return this;
	}

	public Map<String, String> getPatchDictionary() {
		return patchDictionary;
	}

	public SurveyAnswerPatchRequest setPatchDictionary(Map<String, String> patchDictionary) {
		this.patchDictionary = patchDictionary;
		return this;
	}

	public boolean isSkipIfAlreadyProcessed() {
		return skipIfAlreadyProcessed;
	}

	public SurveyAnswerPatchRequest setSkipIfAlreadyProcessed(boolean skipIfAlreadyProcessed) {
		this.skipIfAlreadyProcessed = skipIfAlreadyProcessed;
		return this;
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public SurveyAnswerPatchRequest setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
		return this;
	}

	@Override
	public String toString() {
		return "SurveyAnswerPatchRequest{" + "surveyId='" + surveyId + '\'' + ", token='" + token + '\'' + ", respondentId='" + respondentId + '\''
			+ ", answerDate=" + answerDate + ", dataReplacementType=" + dataReplacementStrategy + ", emptyValueBehavior=" + emptyValueBehavior
			+ ", patchDictionary=" + patchDictionary + ", skipIfAlreadyProcessed=" + skipIfAlreadyProcessed + ", failOnError=" + failOnError + '}';
	}
}
