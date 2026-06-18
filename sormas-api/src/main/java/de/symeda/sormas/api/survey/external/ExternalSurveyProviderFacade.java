package de.symeda.sormas.api.survey.external;

import javax.ejb.Remote;

import de.symeda.sormas.api.survey.external.views.ExternalSurveyView;

/**
 * To avoid integrating a specific survey-tool integration within SORMAS, this contract was specified to stay tool-agnostic.
 */
@Remote
public interface ExternalSurveyProviderFacade {

	/**
	 * Must return a view to display Survey-results from an external tool.
	 * 
	 * @param externalSurveyId
	 *            identifier in the external tool.
	 * @param externalRespondentId
	 *            response identifier in the external tool.
	 * @return View that can be displayed within SORMAS.
	 */
	ExternalSurveyView getExternalSurveyView(String externalSurveyId, String externalRespondentId);
}
