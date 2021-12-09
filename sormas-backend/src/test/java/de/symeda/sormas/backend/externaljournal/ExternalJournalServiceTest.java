package de.symeda.sormas.backend.externaljournal;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import de.symeda.sormas.api.externaljournal.ExternalJournalValidation;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryIdatId;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonData;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryQueryResponse;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.JournalPersonDto;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;

public class ExternalJournalServiceTest extends AbstractBeanTest {

	private static final int WIREMOCK_TESTING_PORT = 7777;
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(options().port(WIREMOCK_TESTING_PORT), false);

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

		String wireMockUrl = "http://localhost:" + WIREMOCK_TESTING_PORT;
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_AUTH_URL, wireMockUrl + "/auth");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_EMAIL, "test@patientdiary.de");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_PASSWORD, "testpass");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_PROBANDS_URL, wireMockUrl);
		stubFor(post(urlEqualTo("/auth")).willReturn(aResponse().withBody("{\"success\": true, \"token\": \"token\"}").withStatus(HttpStatus.SC_OK)));
		stubFor(
			get(urlPathEqualTo("/probands")).willReturn(
				aResponse().withBody("{ \"total\": 0, \"count\": 0, \"results\": [] }")
					.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.withStatus(HttpStatus.SC_OK)));
	}

	@After
	public void teardown() {
		MockProducer.getProperties().remove(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_AUTH_URL);
		MockProducer.getProperties().remove(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_EMAIL);
		MockProducer.getProperties().remove(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_PASSWORD);
		MockProducer.getProperties().remove(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_PROBANDS_URL);
		MockProducer.getProperties().remove(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT);
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
		assertTrue(getExternalJournalService().validatePatientDiaryPerson(person).isValid());
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
		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));
		person.setPhone("+496211218490");
		assertFalse(getExternalJournalService().validatePatientDiaryPerson(person).isValid());
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
		assertTrue(getExternalJournalService().validatePatientDiaryPerson(person).isValid());
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
		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		person.setPhone("+9940311849");
		validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		person.setEmailAddress("test@test.de");
		assertFalse(getExternalJournalService().validatePatientDiaryPerson(person).isValid());
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
		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.NO_PHONE_OR_EMAIL.getErrorLanguageKey()));
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
		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_BIRTHDATE.getErrorLanguageKey()));

		person.setBirthdateMM(6);
		validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_BIRTHDATE.getErrorLanguageKey()));

		person.setBirthdateYYYY(null);
		person.setBirthdateDD(1);
		validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_BIRTHDATE.getErrorLanguageKey()));

		person.setBirthdateYYYY(2000);
		validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(validationResult.isValid());
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
		PersonFacadeEjb.PersonFacadeEjbLocal personFacade = (PersonFacadeEjb.PersonFacadeEjbLocal) getPersonFacade();
		personFacade.setExternalJournalService(getExternalJournalService());
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

		assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

		// Define relevant changes
		HashMap<String, Object> relevantChanges = new HashMap<String, Object>() {

			{
				put(Person.FIRST_NAME, "Heinz");
				put(Person.LAST_NAME, "Müller");
				put(Person.SEX, Sex.FEMALE);
				put(Person.BIRTHDATE_YYYY, 2001);
				put(Person.BIRTHDATE_MM, 7);
				put(Person.BIRTHDATE_DD, 2);
			}
		};

		person.setPhone("+496211218491");
		person.setEmailAddress("heinz@test.de");

		// Apply each change and make sure it makes notification considered necessary
		for (String propertyName : relevantChanges.keySet()) {
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			setPersonProperty(person, propertyName, relevantChanges.get(propertyName));
			person = entityManager.merge(person);
			assertTrue(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			// Modify the SymptomJournalStatus of the original person
			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.DELETED);
			person = entityManager.merge(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.REJECTED);
			person = entityManager.merge(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.UNREGISTERED);
			person = entityManager.merge(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			journalPerson = personFacade.getPersonForJournal(person.getUuid());
			person.setSymptomJournalStatus(SymptomJournalStatus.ACCEPTED);
			person = entityManager.merge(person);
			assertFalse(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());

			// Apply any other relevant change and make sure notification is still considered necessary
			for (String secondPropertyName : relevantChanges.keySet()) {
				if (!secondPropertyName.equals(propertyName)) {
					journalPerson = personFacade.getPersonForJournal(person.getUuid());
					setPersonProperty(person, secondPropertyName, relevantChanges.get(secondPropertyName));
					person = entityManager.merge(person);
					assertTrue(getExternalJournalService().notifyExternalJournalPersonUpdate(journalPerson).getElement0());
				}
			}

			setPersonRelevantFields(person);
			person = entityManager.merge(person);
		}
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenSeveralNonPrimaryEmailsIsNotExportable() {
		PersonDto person = new PersonDto();
		person.getPersonContactDetails()
			.add(
				PersonContactDetailDto
					.build(person.toReference(), false, PersonContactDetailType.EMAIL, null, null, "test1@test.de", null, false, null, null));
		person.getPersonContactDetails()
			.add(
				PersonContactDetailDto
					.build(person.toReference(), false, PersonContactDetailType.EMAIL, null, null, "test2@test.de", null, false, null, null));

		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_PHONES_OR_EMAILS.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenSeveralNonPrimaryPhoneIsNotExportable() {
		PersonDto person = new PersonDto();
		person.getPersonContactDetails()
			.add(
				PersonContactDetailDto.build(
					person.toReference(),
					false,
					PersonContactDetailType.PHONE,
					PhoneNumberType.MOBILE,
					null,
					"+496211218490",
					null,
					false,
					null,
					null));
		person.getPersonContactDetails()
			.add(
				PersonContactDetailDto.build(
					person.toReference(),
					false,
					PersonContactDetailType.PHONE,
					PhoneNumberType.LANDLINE,
					null,
					"+496211218491",
					null,
					false,
					null,
					null));

		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_PHONES_OR_EMAILS.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenEmailTakenIsNotExportable() throws JsonProcessingException {
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		person.setEmailAddress("test@test.de");

		PatientDiaryQueryResponse queryResponse = new PatientDiaryQueryResponse();
		queryResponse.setCount(1);
		PatientDiaryPersonData diaryPersonData = new PatientDiaryPersonData();
		PatientDiaryIdatId idatId = new PatientDiaryIdatId();
		PatientDiaryPersonDto diaryPersonDto = new PatientDiaryPersonDto();
		diaryPersonDto.setPersonUUID(DataHelper.createUuid());
		diaryPersonDto.setFirstName("James");
		idatId.setIdat(diaryPersonDto);
		diaryPersonData.setIdatId(idatId);
		queryResponse.setResults(Collections.singletonList(diaryPersonData));

		stubFor(
			get(urlPathEqualTo("/probands")).willReturn(
				aResponse().withBody(new ObjectMapper().writeValueAsString(queryResponse))
					.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.withStatus(HttpStatus.SC_OK)));

		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(validationResult.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenPhoneTakenIsNotExportable() throws JsonProcessingException {
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		person.setPhone("+496211218490");

		PatientDiaryQueryResponse queryResponse = new PatientDiaryQueryResponse();
		queryResponse.setCount(1);
		PatientDiaryPersonData diaryPersonData = new PatientDiaryPersonData();
		PatientDiaryIdatId idatId = new PatientDiaryIdatId();
		PatientDiaryPersonDto diaryPersonDto = new PatientDiaryPersonDto();
		diaryPersonDto.setPersonUUID(DataHelper.createUuid());
		diaryPersonDto.setFirstName("James");
		idatId.setIdat(diaryPersonDto);
		diaryPersonData.setIdatId(idatId);
		queryResponse.setResults(Collections.singletonList(diaryPersonData));

		stubFor(
			get(urlPathEqualTo("/probands")).willReturn(
				aResponse().withBody(new ObjectMapper().writeValueAsString(queryResponse))
					.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.withStatus(HttpStatus.SC_OK)));

		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(validationResult.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.PHONE_TAKEN.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenNoEmailAndNoPhoneContactAcceptedIsNotExportable() throws JsonProcessingException {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT, Boolean.FALSE.toString());

		PersonDto person = PersonDto.build();
		person.setPhone("+496211218490");

		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(validationResult.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.NO_EMAIL.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenInvalidEmailAndNoPhoneContactAcceptedIsNotExportable() throws JsonProcessingException {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT, Boolean.FALSE.toString());

		PersonDto person = PersonDto.build();
		person.setPhone("0");
		person.setEmailAddress("test@email");

		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenSeveralNonPrimaryEmailsAndNoPhoneContactAcceptedIsNotExportable() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT, Boolean.FALSE.toString());

		PersonDto person = new PersonDto();
		person.setPhone("+496211218490");
		person.getPersonContactDetails()
			.add(
				PersonContactDetailDto
					.build(person.toReference(), false, PersonContactDetailType.EMAIL, null, null, "test1@test.de", null, false, null, null));
		person.getPersonContactDetails()
			.add(
				PersonContactDetailDto
					.build(person.toReference(), false, PersonContactDetailType.EMAIL, null, null, "test2@test.de", null, false, null, null));

		ExternalJournalValidation validationResult = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(validationResult.isValid());
		assertEquals(
			validationResult.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_EMAILS.getErrorLanguageKey()));
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
			BeanUtils.setProperty(person, propertyName, propertyValue);
		} catch (IllegalAccessException | InvocationTargetException e) {
			fail();
			e.printStackTrace();
		}
	}
}
