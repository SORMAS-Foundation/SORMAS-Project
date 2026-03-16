package de.symeda.sormas.api.externalmessage.survey;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Some survey mapping might be valid and processed on first try, but for others another attempt might be required.
 */
public class ExternalSurveyResponseData {

	/**
	 * Represents the original mapping contract (request) and response for the survey mapping.
	 */
	@NotNull
	private ExternalMessageSurveyResponseWrapper original;

	/**
	 * May be present in case the original had failures and a new request-response was specified by some user.
	 */
	@Nullable
	private ExternalMessageSurveyResponseWrapper updated;

	public ExternalMessageSurveyResponseWrapper getOriginal() {
		return original;
	}

	public ExternalSurveyResponseData setOriginal(ExternalMessageSurveyResponseWrapper original) {
		this.original = original;
		return this;
	}

	@Nullable
	public ExternalMessageSurveyResponseWrapper getUpdated() {
		return updated;
	}

	public ExternalSurveyResponseData setUpdated(@Nullable ExternalMessageSurveyResponseWrapper updated) {
		this.updated = updated;
		return this;
	}

	@NotNull
	@JsonIgnore
	public ExternalMessageSurveyResponseWrapper getLatest() {
		return Optional.ofNullable(updated).orElse(original);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ExternalSurveyResponseData that = (ExternalSurveyResponseData) o;
		return Objects.equals(original, that.original) && Objects.equals(updated, that.updated);
	}

	@Override
	public int hashCode() {
		return Objects.hash(original, updated);
	}

	@Override
	public String toString() {
		return "ExternalSurveyResponseData{" + "original=" + original + ", updated=" + updated + '}';
	}
}
