package de.symeda.sormas.api.externaljournal;

import javax.ejb.Remote;

import de.symeda.sormas.api.person.PersonDto;

@Remote
public interface ExternalJournalFacade {

	String getSymptomJournalAuthToken();

	String getPatientDiaryAuthToken();

	ExternalPatientDto getPatientDiaryPerson(String personUuid);

	RegisterResult registerPatientDiaryPerson(PersonDto person);

	ExternalPersonValidation validatePatientDiaryPerson(PersonDto person);
}
