package de.symeda.sormas.api.externaljournal;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryResult;
import de.symeda.sormas.api.person.PersonDto;

import java.util.Date;

@Remote
public interface ExternalJournalFacade {

	String getSymptomJournalAuthToken();

	String getPatientDiaryAuthToken();

	ExternalJournalValidation validateSymptomJournalPerson(PersonDto person);

	PatientDiaryPersonDto getPatientDiaryPerson(String personUuid);

	PatientDiaryResult registerPatientDiaryPerson(PersonDto person);

	ExternalJournalValidation validatePatientDiaryPerson(PersonDto person);

	PatientDiaryResult cancelPatientDiaryFollowUp(PersonDto personDto);
}
