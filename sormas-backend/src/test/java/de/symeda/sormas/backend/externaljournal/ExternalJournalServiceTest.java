package de.symeda.sormas.backend.externaljournal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryQueryResponse;
import de.symeda.sormas.api.person.JournalPersonDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import static org.junit.Assert.fail;

public class ExternalJournalServiceTest extends AbstractBeanTest {

	@Spy
	private ExternalJournalService externalJournalService;

	private UserDto natUser;
	private TestDataCreator.RDCF rdcf;

	@Before
	public void init() {
		super.init();
		natUser = creator.createUser("", "", "", "Nat", "Usr", UserRole.NATIONAL_USER);
		rdcf = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1");
		when(MockProducer.getPrincipal().getName()).thenReturn("NatUsr");

		MockitoAnnotations.initMocks(this);
		PatientDiaryQueryResponse queryResponse = new PatientDiaryQueryResponse();
		queryResponse.setCount(0);
		queryResponse.setResults(Collections.emptyList());
		doReturn(Optional.of(queryResponse)).when(externalJournalService).queryPatientDiary(Mockito.any(String.class), Mockito.any(String.class));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenValidEmailIsExportable() {
		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
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
		EntityManager entityManager = getEntityManager();
		PersonFacadeEjbLocal personFacade = (PersonFacadeEjbLocal) getPersonFacade();
		personFacade.setExternalJournalService(externalJournalService);
		PersonService personService = getPersonService();

		Person person = personService.createPerson();
		setPersonRelevantFields(person);

		// cannot use PersonFacade save since it also calls the method being tested
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(person);
		entityManager.flush();
		transaction.commit();

		// need to create a case with the person to avoid pseudonymization related errors
		creator.createCase(natUser.toReference(), new PersonReferenceDto(person.getUuid()), rdcf);
		JournalPersonDto journalPerson = personFacade.getPersonForJournal(person.getUuid());

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
			person = entityManager.merge(person);
			assertTrue(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));

			// Modify the SymptomJournalStatus of the original person
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.DELETED);
			person = entityManager.merge(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));

			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.REJECTED);
			person = entityManager.merge(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));

			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.UNREGISTERED);
			person = entityManager.merge(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));

			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.ACCEPTED);
			person = entityManager.merge(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));

			// Apply any other relevant change and make sure notification is still considered necessary
			for (String secondPropertyName : relevantChanges.keySet()) {
				if (!secondPropertyName.equals(propertyName)) {
					journalPerson = personFacade.getPersonForJournal(person.getUuid());
					setPersonProperty(person, secondPropertyName, relevantChanges.get(secondPropertyName));
					person = entityManager.merge(person);
					assertTrue(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson));
				}
			}

			setPersonRelevantFields(person);
			person = entityManager.merge(person);
		}
	}

	protected void setPersonRelevantFields(Person person) {
		person.setFirstName("Klaus");
		person.setLastName("Draufle");
		person.setSex(Sex.MALE);
		person.setEmailAddress("test@test.de");
		person.setPhone("+496211218490");
		person.setBirthdateYYYY(2000);
		person.setBirthdateMM(6);
		person.setBirthdateDD(1);
		person.setSymptomJournalStatus(SymptomJournalStatus.REGISTERED);
	}

	/*
	 * If you need to change this method to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	private void setPersonProperty(Person person, String propertyName, Object propertyValue) {
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
