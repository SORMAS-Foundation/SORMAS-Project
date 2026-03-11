package de.symeda.sormas.backend.externalmessage;

import de.symeda.sormas.api.externalmessage.survey.ExternalMessageSurveyResponseWrapper;

/**
 * Allows to detach the actual type name from the DB.
 * Avoids to handle things like: "class-injection" or breaking behavior when changing className.
 */
public enum ExternalMessageAdditionalDataType {

	SURVEY_RESPONSE_WRAPPER(ExternalMessageSurveyResponseWrapper.class);

	private final Class<?> dataClass;

	ExternalMessageAdditionalDataType(Class<?> dataClass) {
		this.dataClass = dataClass;
	}

	public Class<?> getDataClass() {
		return dataClass;
	}
}
