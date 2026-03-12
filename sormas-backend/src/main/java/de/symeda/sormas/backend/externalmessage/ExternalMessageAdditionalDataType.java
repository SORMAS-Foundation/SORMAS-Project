package de.symeda.sormas.backend.externalmessage;

import de.symeda.sormas.api.externalmessage.survey.ExternalSurveyResponseData;

/**
 * Allows to detach the actual type name from the DB.
 * Avoids to handle things like: "class-injection" or breaking behavior when changing className.
 * <p>
 * For versioning purposes a new entry can be added like: CURRENT_MEMBER_NAME_V2.
 */
public enum ExternalMessageAdditionalDataType {

	SURVEY_RESPONSE_WRAPPER(ExternalSurveyResponseData.class);

	private final Class<?> dataClass;

	ExternalMessageAdditionalDataType(Class<?> dataClass) {
		this.dataClass = dataClass;
	}

	public Class<?> getDataClass() {
		return dataClass;
	}
}
