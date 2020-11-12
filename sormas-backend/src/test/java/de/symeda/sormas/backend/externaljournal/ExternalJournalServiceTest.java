package de.symeda.sormas.backend.externaljournal;

import de.symeda.sormas.api.externaljournal.PatientDiaryPersonQueryResponse;
import de.symeda.sormas.api.person.JournalPersonDto;
import de.symeda.sormas.api.person.PersonDto;

import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

public class ExternalJournalServiceTest extends AbstractBeanTest {

	@Spy
	private ExternalJournalService externalJournalService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		PatientDiaryPersonQueryResponse patientDiaryPersonQueryResponse = new PatientDiaryPersonQueryResponse();
		patientDiaryPersonQueryResponse.setCount(0);
		doReturn(Optional.of(patientDiaryPersonQueryResponse)).when(externalJournalService).queryPatientDiary("Email", "test@test.de");
		doReturn(Optional.of(patientDiaryPersonQueryResponse)).when(externalJournalService).queryPatientDiary("Email", "test@test");
		doReturn(Optional.of(patientDiaryPersonQueryResponse)).when(externalJournalService).queryPatientDiary("Email", "heinz@test.de");
		doReturn(Optional.of(patientDiaryPersonQueryResponse)).when(externalJournalService).queryPatientDiary("Mobile phone", "+49 621 1218490");
		doReturn(Optional.of(patientDiaryPersonQueryResponse)).when(externalJournalService).queryPatientDiary("Mobile phone", "+49 621 1218491");
		doReturn(Optional.of(patientDiaryPersonQueryResponse)).when(externalJournalService).queryPatientDiary("Mobile phone", "0");;
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenValidEmailIsExportable() {
		PatientDiaryPersonQueryResponse response = new PatientDiaryPersonQueryResponse();
		response.setCount(0);
		PersonDto person = new PersonDto();
		person.setEmailAddress("test@test.de");
		assertTrue(externalJournalService.validatePatientDiaryPerson(person).isValid());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenInvalidEmailIsNotExportable() {
		PersonDto person = new PersonDto();
		person.setEmailAddress("test@test");
		assertFalse(externalJournalService.validatePatientDiaryPerson(person).isValid());
		person.setPhone("+496211218490");
		assertFalse(externalJournalService.validatePatientDiaryPerson(person).isValid());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenValidPhoneIsExportable() {

		PersonDto person = new PersonDto();
		person.setPhone("+496211218490");
		assertTrue(externalJournalService.validatePatientDiaryPerson(person).isValid());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenInvalidPhoneIsNotExportable() {

		PersonDto person = new PersonDto();
		person.setPhone("0");
		assertFalse(getExternalJournalService().validatePatientDiaryPerson(person).isValid());
		person.setEmailAddress("test@test.de");
		assertFalse(externalJournalService.validatePatientDiaryPerson(person).isValid());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenNeitherEmailNorPhoneIsNotExportable() {

		PersonDto person = new PersonDto();
		person.setBirthdateYYYY(2000);
		person.setBirthdateMM(6);
		person.setBirthdateDD(1);
		assertFalse(externalJournalService.validatePatientDiaryPerson(person).isValid());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenIncompleteBirthdateIsNotExportable() {
		PersonDto person = new PersonDto();
		person.setEmailAddress("test@test.de");
		person.setPhone("+496211218490");
		person.setBirthdateYYYY(2000);
		assertFalse(externalJournalService.validatePatientDiaryPerson(person).isValid());
		person.setBirthdateMM(6);
		assertFalse(externalJournalService.validatePatientDiaryPerson(person).isValid());
		person.setBirthdateYYYY(null);
		person.setBirthdateDD(1);
		assertFalse(externalJournalService.validatePatientDiaryPerson(person).isValid());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenRelevantChangeShouldNotify() {
		PersonFacade personFacade = getPersonFacade();

		// Define relevant properties
		HashMap<String, Object> relevantProperties = new HashMap<String, Object>() {

			{
				put("FirstName", "Klaus");
				put("LastName", "Draufle");
				put("Sex", Sex.MALE);
				put("EmailAddress", "test@test.de");
				put("Phone", "+496211218490");
				put("BirthdateYYYY", 2000);
				put("BirthdateMM", 6);
				put("BirthdateDD", 1);
				put("SymptomJournalStatus", SymptomJournalStatus.REGISTERED);
			}
		};

		// Create two person with those properties
		PersonDto person = new PersonDto();
		person.setUuid(DataHelper.createUuid());
		personFacade.savePerson(person);
		JournalPersonDto journalPerson = personFacade.getPersonForJournal(person.getUuid());
		for (Map.Entry<String, Object> property : relevantProperties.entrySet()) {
			setPersonProperty(person, property.getKey(), property.getValue());
		}
		personFacade.savePerson(person);
		assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));

		// Define relevant changes
		HashMap<String, Object> relevantChanges = new HashMap<String, Object>() {
			{
				put("FirstName", "Heinz");
				put("LastName", "Müller");
				put("Sex", Sex.FEMALE);
				put("EmailAddress", "heinz@test.de");
				put("Phone", "+496211218491");
				put("BirthdateYYYY", 2001);
				put("BirthdateMM", 7);
				put("BirthdateDD", 2);
			}
		};
		// Apply each change and make sure it makes notification considered necessary
		for (String propertyName : relevantChanges.keySet()) {
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			setPersonProperty(person, propertyName, relevantChanges.get(propertyName));
			personFacade.savePerson(person);
			assertTrue(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));

			// Modify the SymptomJournalStatus of the original person
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.DELETED);
			personFacade.savePerson(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.REJECTED);
			personFacade.savePerson(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.UNREGISTERED);
			personFacade.savePerson(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.ACCEPTED);
			personFacade.savePerson(person);
			assertTrue(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));
			person.setSymptomJournalStatus(SymptomJournalStatus.REGISTERED);
			personFacade.savePerson(person);

			// Apply any other relevant change and make sure notification is still considered necessary
			for (String secondPropertyName : relevantChanges.keySet()) {
				journalPerson = personFacade.getPersonForJournal(person.getUuid());
				setPersonProperty(person, secondPropertyName, relevantChanges.get(secondPropertyName));
				personFacade.savePerson(person);
				assertTrue(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));
			}
		}
	}

	/*
	 * If you need to change this method to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	private static void setPersonProperty(PersonDto person, String propertyName, Object propertyValue) {
		try {
			Method method = person.getClass().getMethod("set" + propertyName, propertyValue.getClass());
			method.invoke(person, propertyValue);

		} catch (NoSuchMethodException e) {
			// This probably means that the set method is gone, which may impose changes to the External Journal Interface
			fail();
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
