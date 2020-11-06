package de.symeda.sormas.backend.externaljournal;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.externaljournal.ExternalJournalFacade;
import de.symeda.sormas.api.externaljournal.ExternalPatientDto;
import de.symeda.sormas.api.externaljournal.ExternalPersonValidation;
import de.symeda.sormas.api.externaljournal.RegisterResult;
import de.symeda.sormas.api.person.PersonDto;

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

	@Override
	public ExternalPatientDto getPatientDiaryPerson(String personUuid) {
		return externalJournalService.getPatientDiaryPerson(personUuid).orElse(null);
	}

	@Override
	public RegisterResult registerPatientDiaryPerson(PersonDto person) {
		return externalJournalService.registerPatientDiaryPerson(person);
	}

	@Override
	public ExternalPersonValidation validatePatientDiaryPerson(PersonDto person) {
		return externalJournalService.validatePatientDiaryPerson(person);
	}


}
