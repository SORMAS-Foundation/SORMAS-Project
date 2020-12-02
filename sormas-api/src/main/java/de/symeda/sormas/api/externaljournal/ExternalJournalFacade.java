package de.symeda.sormas.api.externaljournal;

import javax.ejb.Remote;

import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryRegisterResult;
import de.symeda.sormas.api.person.PersonDto;

@Remote
public interface ExternalJournalFacade {

	String getSymptomJournalAuthToken();

	String getPatientDiaryAuthToken();

	PatientDiaryPersonDto getPatientDiaryPerson(String personUuid);

	PatientDiaryRegisterResult registerPatientDiaryPerson(PersonDto person);

	ExternalJournalValidation validatePatientDiaryPerson(PersonDto person);
}
