package de.symeda.sormas.api.systemconfiguration;

import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;

public interface ExternalClientConfigurationFacade {

	boolean isS2SConfigured();

	SormasToSormasConfig getS2SConfig();

	@Deprecated
	boolean isExternalJournalActive();

	@Deprecated
	SymptomJournalConfig getSymptomJournalConfig();

	@Deprecated
	PatientDiaryConfig getPatientDiaryConfig();
}
