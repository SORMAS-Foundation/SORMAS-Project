package de.symeda.sormas.api.survey.external;

import de.symeda.sormas.api.survey.external.views.ExternalSurveyView;

/**
 * To avoid integrating a specific survey-tool integration within SORMAS, this contract was specified to stay tool-agnostic.
 */
public interface ExternalSurveyProviderFacade {

	ExternalSurveyView getExternalSurveyView(String externalSurveyId, String externalRespondentId);
}
