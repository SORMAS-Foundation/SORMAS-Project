package de.symeda.sormas.api.externaljournal;

import javax.ejb.Remote;

import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryResult;
import de.symeda.sormas.api.person.PersonDto;

@Remote
public interface ExternalJournalFacade {

	String getSymptomJournalAuthToken();

	String getPatientDiaryAuthToken(boolean frontendRequest);

	ExternalJournalValidation validateSymptomJournalPerson(PersonDto person);

	PatientDiaryPersonDto getPatientDiaryPerson(String personUuid);

	PatientDiaryResult registerPatientDiaryPerson(PersonDto person);

	ExternalJournalValidation validatePatientDiaryPerson(PersonDto person);

	PatientDiaryResult cancelPatientDiaryFollowUp(PersonDto personDto);

	ExternalJournalSyncResponseDto notifyExternalJournal(PersonDto personDto);
}
