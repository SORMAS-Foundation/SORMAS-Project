package de.symeda.sormas.backend.externaljournal;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.externaljournal.ExternalJournalFacade;
import de.symeda.sormas.api.externaljournal.ExternalJournalSyncResponseDto;
import de.symeda.sormas.api.externaljournal.ExternalJournalValidation;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryResult;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ExternalJournalFacade")
@RightsAllowed(UserRight._MANAGE_EXTERNAL_SYMPTOM_JOURNAL)
public class ExternalJournalFacadeEjb implements ExternalJournalFacade {

	@EJB
	private ExternalJournalService externalJournalService;
	@EJB
	private PatientDiaryClient patientDiaryClient;
	@EJB
	private PersonFacadeEjbLocal personFacade;

	@Override
	public String getSymptomJournalAuthToken() {
		return externalJournalService.getSymptomJournalAuthToken();
	}

	@Override
	public String getPatientDiaryAuthToken(boolean frontendRequest) {
		return patientDiaryClient.getPatientDiaryAuthToken(frontendRequest);
	}

	@Override
	public PatientDiaryPersonDto getPatientDiaryPerson(String personUuid) {
		return patientDiaryClient.getPatientDiaryPerson(personUuid).orElse(null);
	}

	@Override
	public ExternalJournalValidation validateSymptomJournalPerson(PersonDto person) {
		return externalJournalService.validateSymptomJournalPerson(person);
	}

	@Override
	public PatientDiaryResult registerPatientDiaryPerson(PersonDto person) {
		return patientDiaryClient.registerPatientDiaryPerson(person.getUuid(), () -> {
			person.setSymptomJournalStatus(SymptomJournalStatus.REGISTERED);
			personFacade.save(person);
		});
	}

	@Override
	public ExternalJournalValidation validatePatientDiaryPerson(PersonDto person) {
		return externalJournalService.validatePatientDiaryPerson(person);
	}

	@Override
	public PatientDiaryResult cancelPatientDiaryFollowUp(PersonDto person) {
		return patientDiaryClient.deletePatientDiaryPerson(person.getUuid(), () -> {
			person.setSymptomJournalStatus(SymptomJournalStatus.DELETED);
			personFacade.save(person);
		});
	}

	@Override
	@RightsAllowed({
		UserRight._MANAGE_EXTERNAL_SYMPTOM_JOURNAL,
		UserRight._PERSON_EDIT })
	public ExternalJournalSyncResponseDto notifyExternalJournal(PersonDto personDto) {
		return externalJournalService.handleExternalJournalPersonUpdateSync(personDto);
	}

}
