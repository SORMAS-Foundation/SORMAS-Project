package de.symeda.sormas.api.externaljournal;

import javax.ejb.Remote;

@Remote
public interface ExternalJournalFacade {

	String getSymptomJournalAuthToken();

	String getPatientDiaryAuthToken();
}
