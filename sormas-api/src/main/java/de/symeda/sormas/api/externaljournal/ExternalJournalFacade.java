package de.symeda.sormas.api.externaljournal;

import javax.ejb.Remote;

import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryRegisterResult;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonDto;

@Remote
public interface ExternalJournalFacade {

	String getSymptomJournalAuthToken();

	String getPatientDiaryAuthToken();

	PatientDiaryPersonDto getPatientDiaryPerson(String personUuid);

	PatientDiaryRegisterResult registerPatientDiaryPerson(de.symeda.sormas.api.person.PersonDto person);

	ExternalJournalValidation validatePatientDiaryPerson(de.symeda.sormas.api.person.PersonDto person);
}
