package de.symeda.sormas.api.systemconfiguration;

import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;

public interface ExternalClientConfigurationFacade {

	SormasToSormasConfig getS2SConfig();

	SymptomJournalConfig getSymptomJournalConfig();

	PatientDiaryConfig getPatientDiaryConfig();
}
