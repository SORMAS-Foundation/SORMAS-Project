package de.symeda.sormas.api.externalmessage.survey;

import javax.ejb.Remote;

import de.symeda.sormas.api.externalmessage.ExternalMessageAdapterFacade;

/**
 * A remote interface can only be implemented once, otherwise the build breaks.
 * This second interface keeps the same contract, but allows to create a separate entry points for external messages.
 * It allows to keep them both independent: separate fetching frequency | robustness: one can fail but not the other.
 */
@Remote
public interface SurveyAsExternalMessageAdapterFacade extends ExternalMessageAdapterFacade {

}
