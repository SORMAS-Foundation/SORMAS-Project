/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.externaljournal;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.After;
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
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class ExternalJournalServiceTest extends AbstractBeanTest {

	private static final int WIREMOCK_TESTING_PORT = 7777;
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(options().port(WIREMOCK_TESTING_PORT), false);

	public void init() {
		super.init();
		creator.createUser("", "", "", "Nat", "Usr", creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		when(MockProducer.getPrincipal().getName()).thenReturn("NatUsr");

		MockitoAnnotations.initMocks(this);

		String wireMockUrl = "http://localhost:" + WIREMOCK_TESTING_PORT;
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_AUTH_URL, wireMockUrl + "/auth");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_EMAIL, "test@patientdiary.de");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_PASSWORD, "testpass");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_PROBANDS_URL, wireMockUrl);
		stubFor(post(urlEqualTo("/auth")).willReturn(aResponse().withBody("{\"success\": true, \"token\": \"token\"}").withStatus(HttpStatus.SC_OK)));
		stubFor(
			get(urlPathEqualTo("/probands")).atPriority(2)
				.willReturn(
					aResponse().withBody("{ \"total\": 0, \"count\": 0, \"results\": [] }")
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
						.withStatus(HttpStatus.SC_OK)));

		// Pretend that the number +49 621 121 849-3 already is in use by another person
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

		try {
			stubFor(
				get(urlPathEqualTo("/probands")).withQueryParam("q", matching("\"Mobile phone\" = \"\\+49 621 121 849-3\".*"))
					.atPriority(1)
					.willReturn(
						aResponse().withBody(new ObjectMapper().writeValueAsString(queryResponse))
							.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
							.withStatus(HttpStatus.SC_OK)));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}

		// Pretend that the number taken@test.de already is in use by another person
		try {
			stubFor(
				get(urlPathEqualTo("/probands")).withQueryParam("q", matching("\"Email\" = \"taken@test.de\".*"))
					.atPriority(1)
					.willReturn(
						aResponse().withBody(new ObjectMapper().writeValueAsString(queryResponse))
							.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
							.withStatus(HttpStatus.SC_OK)));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}

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
	public void givenValidEmailWithPhoneOnlyAccepted() {

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		person.setEmailAddress("test@test.de");

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.PHONE_TAKEN.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenValidEmailWithPhoneOnlyNotAccepted() {

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT, Boolean.FALSE.toString());

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		person.setEmailAddress("test@test.de");

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenNoEmailWithPhoneOnlyAccepted() {

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.NO_PHONE_OR_EMAIL.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_PHONES_OR_EMAILS.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.PHONE_TAKEN.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenNoEmailWithPhoneOnlyNotAccepted() {

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT, Boolean.FALSE.toString());

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.NO_EMAIL.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.NO_EMAIL.getErrorLanguageKey()));

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.NO_EMAIL.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.NO_EMAIL.getErrorLanguageKey()));

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.NO_EMAIL.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.NO_EMAIL.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.NO_EMAIL.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenMultipleNonPrimaryEmailsWithPhoneOnlyAccepted() {

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		List<PersonContactDetailDto> contactDetails = new ArrayList<>();
		PersonContactDetailDto nonPrimaryMail1 =
			creator.createPersonContactDetail(person.toReference(), false, PersonContactDetailType.EMAIL, "test1@test.de");
		PersonContactDetailDto nonPrimaryMail2 =
			creator.createPersonContactDetail(person.toReference(), false, PersonContactDetailType.EMAIL, "test2@test.de");
		contactDetails.add(nonPrimaryMail1);
		contactDetails.add(nonPrimaryMail2);
		person.setPersonContactDetails(contactDetails);

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_PHONES_OR_EMAILS.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_PHONES_OR_EMAILS.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertTrue(result.isValid());
		assertTrue(result.getMessage().isEmpty());

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.PHONE_TAKEN.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenMultipleNonPrimaryEmailsWithPhoneOnlyNotAccepted() {

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT, Boolean.FALSE.toString());

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		List<PersonContactDetailDto> contactDetails = new ArrayList<>();
		PersonContactDetailDto nonPrimaryMail1 =
			creator.createPersonContactDetail(person.toReference(), false, PersonContactDetailType.EMAIL, "test1@test.de");
		PersonContactDetailDto nonPrimaryMail2 =
			creator.createPersonContactDetail(person.toReference(), false, PersonContactDetailType.EMAIL, "test2@test.de");
		contactDetails.add(nonPrimaryMail1);
		contactDetails.add(nonPrimaryMail2);
		person.setPersonContactDetails(contactDetails);

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_EMAILS.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_EMAILS.getErrorLanguageKey()));

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_EMAILS.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_EMAILS.getErrorLanguageKey()));

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_EMAILS.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_EMAILS.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.SEVERAL_EMAILS.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenInvalidPrimaryEmailWithPhoneOnlyAccepted() {

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		person.setEmailAddress("test@test");

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()) + "\n"
				+ I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()) + "\n"
				+ I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()) + "\n"
				+ I18nProperties.getValidationError(PatientDiaryValidationError.PHONE_TAKEN.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenInvalidPrimaryEmailWithPhoneOnlyNotAccepted() {

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT, Boolean.FALSE.toString());

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		person.setEmailAddress("test@test");

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenInvalidNonPrimaryEmailWithPhoneOnlyAccepted() {

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		PersonContactDetailDto contactDetail =
			creator.createPersonContactDetail(person.toReference(), false, PersonContactDetailType.EMAIL, "test@test");
		person.setPersonContactDetails(new ArrayList<>(Collections.singletonList(contactDetail)));

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()) + "\n"
				+ I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()) + "\n"
				+ I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()) + "\n"
				+ I18nProperties.getValidationError(PatientDiaryValidationError.PHONE_TAKEN.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenInvalidNonprimaryEmailWithPhoneOnlyNotAccepted() {

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT, Boolean.FALSE.toString());

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		PersonContactDetailDto contactDetail =
			creator.createPersonContactDetail(person.toReference(), false, PersonContactDetailType.EMAIL, "test@test");
		person.setPersonContactDetails(new ArrayList<>(Collections.singletonList(contactDetail)));

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_EMAIL.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenTakenEmailWithPhoneOnlyAccepted() {

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		// this address is pretended to be already in use (see @Before)
		person.setEmailAddress("taken@test.de");

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()) + "\n"
				+ I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.INVALID_PHONE.getErrorLanguageKey()) + "\n"
				+ I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(
			result.getMessage(),
			I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()) + "\n"
				+ I18nProperties.getValidationError(PatientDiaryValidationError.PHONE_TAKEN.getErrorLanguageKey()));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void givenTakenEmailWithPhoneOnlyNotAccepted() {

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT, Boolean.FALSE.toString());

		PatientDiaryQueryResponse response = new PatientDiaryQueryResponse();
		response.setCount(0);
		PersonDto person = PersonDto.build();
		person.setFirstName("James");
		// this address is pretended to be already in use (see @Before)
		person.setEmailAddress("taken@test.de");

		ExternalJournalValidation result;

		// phone == null
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == non primary
		person.setAdditionalPhone("+49 621 121 849-0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == multiple non primary
		person.setAdditionalPhone("+49 621 121 849-1");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == valid primary
		person.setPhone("+49 621 121 849-2");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == invalid non primary
		removePhoneContactDetails(person);

		person.setAdditionalPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone == invalid primary
		person.setPhone("0");
		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));

		// phone taken
		// this number is pretended to be already in use (see @Before)
		person.setPhone("+49 621 121 849-3");

		result = getExternalJournalService().validatePatientDiaryPerson(person);
		assertFalse(result.isValid());
		assertEquals(result.getMessage(), I18nProperties.getValidationError(PatientDiaryValidationError.EMAIL_TAKEN.getErrorLanguageKey()));
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

	private void removePhoneContactDetails(PersonDto person) {
		List<PersonContactDetailDto> contactDetails = person.getPersonContactDetails()
			.stream()
			.filter(d -> PersonContactDetailType.EMAIL.equals(d.getPersonContactDetailType()))
			.collect(Collectors.toList());
		person.setPersonContactDetails(contactDetails);
	}
}
