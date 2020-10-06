package de.symeda.sormas.backend.externaljournal;

import de.symeda.sormas.api.externaljournal.ExternalJournalFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "ExternalJournalFacade")
public class ExternalJournalFacadeEjb implements ExternalJournalFacade {

    @EJB
    private ExternalJournalService externalJournalService;

    @Override
    public String getSymptomJournalAuthToken() {
        return externalJournalService.getSymptomJournalAuthToken();
    }

    @Override
    public String getPatientDiaryAuthToken() {
        return externalJournalService.getPatientDiaryAuthToken();
    }
}
