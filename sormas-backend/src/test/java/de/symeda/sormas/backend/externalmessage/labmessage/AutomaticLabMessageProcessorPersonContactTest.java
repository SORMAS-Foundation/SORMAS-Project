/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.externalmessage.labmessage;

import static de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus.DONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;

/**
 * Integration tests for person contact detail and address handling through the full
 * {@link AutomaticLabMessageProcessor} processing flow.
 *
 * <p>
 * These tests complement the unit-level {@code ExternalMessageMapperPersonTest} by exercising
 * the end-to-end path: from an {@link ExternalMessageDto} through the processor flow down to the
 * persisted {@link PersonDto} retrieved from the database.
 */
class AutomaticLabMessageProcessorPersonContactTest extends AbstractBeanTest {

	private AutomaticLabMessageProcessor flow;

	private TestDataCreator.RDCF rdcf;
	private FacilityDto lab;

	@Override
	public void init() {
		super.init();
		flow = getAutomaticLabMessageProcessingFlow();
		rdcf = creator.createRDCF();
		creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_OFFICER);
		lab = creator.createFacility("Lab", rdcf.region, rdcf.district, f -> {
			f.setType(FacilityType.LABORATORY);
			f.setExternalID("test-facility-ext-id-1");
		});
	}

	// ----------------------------------------------------------------
	// New-person path: contact details applied during person creation
	// ---------------------------------------------------------------------------

	/**
	 * When the lab message contains a {@code personPhone}, a new person created by the flow
	 * should have that number as their primary phone contact detail.
	 */
	@Test
	void testNewPersonContactDetailsPhoneFromMessage() throws ExecutionException, InterruptedException {

		ExternalMessageDto message = createExternalMessage(m -> m.setPersonPhone("+49123456789"));

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(message);

		assertThat(result.getStatus(), is(DONE));
		assertThat(message.getStatus(), is(ExternalMessageStatus.PROCESSED));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		assertThat(persons.get(0).getPhone(), is("+49123456789"));

		String primaryPhoneInfo = persons.get(0)
			.getPersonContactDetails()
			.stream()
			.filter(pcd -> PersonContactDetailType.PHONE == pcd.getPersonContactDetailType() && pcd.isPrimaryContact())
			.findFirst()
			.map(PersonContactDetailDto::getContactInformation)
			.orElse(null);
		assertThat(primaryPhoneInfo, is("+49123456789"));
	}

	/**
	 * When the lab message contains a {@code personEmail}, a new person created by the flow
	 * should have that address as their primary e-mail contact detail.
	 */
	@Test
	void testNewPersonContactDetailsEmailFromMessage() throws ExecutionException, InterruptedException {

		ExternalMessageDto message = createExternalMessage(m -> m.setPersonEmail("john.doe@example.com"));

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(message);

		assertThat(result.getStatus(), is(DONE));
		assertThat(message.getStatus(), is(ExternalMessageStatus.PROCESSED));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		assertThat(persons.get(0).getEmailAddress(), is("john.doe@example.com"));

		String primaryEmailInfo = persons.get(0)
			.getPersonContactDetails()
			.stream()
			.filter(pcd -> PersonContactDetailType.EMAIL == pcd.getPersonContactDetailType() && pcd.isPrimaryContact())
			.findFirst()
			.map(PersonContactDetailDto::getContactInformation)
			.orElse(null);
		assertThat(primaryEmailInfo, is("john.doe@example.com"));
	}

	/**
	 * When the lab message carries a non-empty {@code additionalPersonContactDetails} JSON payload,
	 * the entries are applied to a newly created person and persisted.
	 */
	@Test
	void testNewPersonAdditionalContactDetailFromJson() throws Exception {

		PersonContactDetailDto extraContact = new PersonContactDetailDto();
		extraContact.setPersonContactDetailType(PersonContactDetailType.PHONE);
		extraContact.setContactInformation("+49-extra-mobile");
		extraContact.setPrimaryContact(false);

		String additionalContactsJson = new ObjectMapper().writeValueAsString(Collections.singletonList(extraContact));

		ExternalMessageDto message = createExternalMessage(m -> m.setAdditionalPersonContactDetails(additionalContactsJson));

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(message);

		assertThat(result.getStatus(), is(DONE));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		boolean hasExtraContact =
			persons.get(0).getPersonContactDetails().stream().anyMatch(pcd -> "+49-extra-mobile".equals(pcd.getContactInformation()));
		assertThat(hasExtraContact, is(true));
	}

	// ---------------------------------------------------------------------------
	// Existing-person path: contact details merged when person is found by NHI
	// ---------------------------------------------------------------------------

	/**
	 * When a message carries a phone number the existing person does not yet have, the processor
	 * should add it as the new primary phone and demote the current primary to non-primary.
	 */
	@Test
	void testExistingPersonNewPhoneAddedAsPrimary() throws ExecutionException, InterruptedException {

		PersonDto existingPerson = creator.createPerson("John", "Doe", Sex.MALE, p -> {
			p.setPhone("+4910000001");
			p.setNationalHealthId("1234567890");
		});

		ExternalMessageDto message = createExternalMessage(m -> m.setPersonPhone("+4910000002"));

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(message);

		assertThat(result.getStatus(), is(DONE));

		PersonDto updatedPerson = getPersonFacade().getByUuid(existingPerson.getUuid());
		assertThat(updatedPerson.getPhone(), is("+4910000002"));
		assertThat(updatedPerson.getPersonContactDetails(), hasSize(2));

		boolean oldPhoneIsDemoted = updatedPerson.getPersonContactDetails()
			.stream()
			.filter(pcd -> "+4910000001".equals(pcd.getContactInformation()))
			.findFirst()
			.map(pcd -> !pcd.isPrimaryContact())
			.orElse(false);
		assertThat(oldPhoneIsDemoted, is(true));
	}

	/**
	 * When a message carries a phone number that the existing person already has as a
	 * non-primary entry, the processor should promote that entry to primary and demote the
	 * current primary – without creating a duplicate contact.
	 */
	@Test
	void testExistingPersonKnownPhonePromotedToPrimary() throws ExecutionException, InterruptedException {

		PersonDto existingPerson = creator.createPerson("John", "Doe", Sex.MALE, p -> {
			p.setPhone("+4910000003");
			p.setAdditionalPhone("+4910000004");
			p.setNationalHealthId("1234567890");
		});

		ExternalMessageDto message = createExternalMessage(m -> m.setPersonPhone("+4910000004"));

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(message);

		assertThat(result.getStatus(), is(DONE));

		PersonDto updatedPerson = getPersonFacade().getByUuid(existingPerson.getUuid());

		// no new contact should have been added
		assertThat(updatedPerson.getPersonContactDetails(), hasSize(2));
		assertThat(updatedPerson.getPhone(), is("+4910000004"));

		boolean oldPrimaryIsDemoted = updatedPerson.getPersonContactDetails()
			.stream()
			.filter(pcd -> "+4910000003".equals(pcd.getContactInformation()))
			.findFirst()
			.map(pcd -> !pcd.isPrimaryContact())
			.orElse(false);
		assertThat(oldPrimaryIsDemoted, is(true));
	}

	/**
	 * When a message carries an e-mail address the existing person does not yet have, the
	 * processor should add it as the new primary e-mail and demote the current primary.
	 */
	@Test
	void testExistingPersonNewEmailAddedAsPrimary() throws ExecutionException, InterruptedException {

		PersonDto existingPerson = creator.createPerson("John", "Doe", Sex.MALE, p -> {
			p.setEmailAddress("old@example.com");
			p.setNationalHealthId("1234567890");
		});

		ExternalMessageDto message = createExternalMessage(m -> m.setPersonEmail("new@example.com"));

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(message);

		assertThat(result.getStatus(), is(DONE));

		PersonDto updatedPerson = getPersonFacade().getByUuid(existingPerson.getUuid());
		assertThat(updatedPerson.getEmailAddress(), is("new@example.com"));
		assertThat(updatedPerson.getPersonContactDetails(), hasSize(2));

		boolean oldEmailIsDemoted = updatedPerson.getPersonContactDetails()
			.stream()
			.filter(pcd -> "old@example.com".equals(pcd.getContactInformation()))
			.findFirst()
			.map(pcd -> !pcd.isPrimaryContact())
			.orElse(false);
		assertThat(oldEmailIsDemoted, is(true));
	}

	/**
	 * When a message contains address fields and the existing person's address fields are null,
	 * the processor should write those values into the person's primary address.
	 */
	@Test
	void testExistingPersonNullAddressFieldsFilledFromMessage() throws ExecutionException, InterruptedException {

		PersonDto existingPerson = creator.createPerson("John", "Doe", Sex.MALE, p -> {
			// Leave address fields null so that personDetailsMatch succeeds regardless
			// of the address values carried by the test message
			p.setNationalHealthId("1234567890");
		});

		ExternalMessageDto message = createExternalMessage(m -> {
			m.setPersonCity("Hamburg");
			m.setPersonStreet("Main St 1");
			m.setPersonPostalCode("20095");
		});

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(message);

		assertThat(result.getStatus(), is(DONE));

		PersonDto updatedPerson = getPersonFacade().getByUuid(existingPerson.getUuid());
		assertThat(updatedPerson.getAddress().getCity(), is("Hamburg"));
		assertThat(updatedPerson.getAddress().getStreet(), is("Main St 1"));
		assertThat(updatedPerson.getAddress().getPostalCode(), is("20095"));
	}

	/**
	 * When the existing person already has a non-null address field and the message carries a
	 * different value for that field, {@code mergePersonAddress} should overwrite it.
	 *
	 * <p>
	 * Note: {@code street}, {@code city} and {@code postalCode} are used by
	 * {@code personDetailsMatch} to identify the existing person, so they cannot differ between
	 * person and message without breaking the match. {@code houseNumber} is not part of that
	 * check and is therefore used here to verify the overwrite behaviour.
	 */
	@Test
	void testExistingPersonAddressFieldOverwrittenByMessage() throws ExecutionException, InterruptedException {

		PersonDto existingPerson = creator.createPerson("John", "Doe", Sex.MALE, p -> {
			p.getAddress().setStreet("Main St");
			p.getAddress().setHouseNumber("1");
			p.setNationalHealthId("1234567890");
		});

		// street matches so personDetailsMatch succeeds; houseNumber is different → should be overwritten
		ExternalMessageDto message = createExternalMessage(m -> {
			m.setPersonStreet("Main St");
			m.setPersonHouseNumber("99");
		});

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(message);

		assertThat(result.getStatus(), is(DONE));

		PersonDto updatedPerson = getPersonFacade().getByUuid(existingPerson.getUuid());
		assertThat(updatedPerson.getAddress().getStreet(), is("Main St"));
		assertThat(updatedPerson.getAddress().getHouseNumber(), is("99"));
	}

	/**
	 * When the same {@code additionalPersonContactDetails} JSON is present in two successive
	 * messages for the same person, the contact entry should appear exactly once in the
	 * persisted person data (deduplication by type + contactInformation).
	 */
	@Test
	void testExistingPersonAdditionalContactDetailsNotDuplicated() throws Exception {

		PersonContactDetailDto extraContact = new PersonContactDetailDto();
		extraContact.setPersonContactDetailType(PersonContactDetailType.PHONE);
		extraContact.setContactInformation("+49-shared-mobile");
		extraContact.setPrimaryContact(false);

		String additionalContactsJson = new ObjectMapper().writeValueAsString(Collections.singletonList(extraContact));

		// First message creates the person and applies the additional contact
		ExternalMessageDto firstMessage = createExternalMessage(m -> m.setAdditionalPersonContactDetails(additionalContactsJson));
		ProcessingResult<ExternalMessageProcessingResult> firstResult = runFlow(firstMessage);
		assertThat(firstResult.getStatus(), is(DONE));

		List<PersonDto> personsAfterFirst = getPersonFacade().getAllAfter(new Date(0));
		assertThat(personsAfterFirst, hasSize(1));
		PersonDto personAfterFirst = personsAfterFirst.get(0);

		long contactCountAfterFirst =
			personAfterFirst.getPersonContactDetails().stream().filter(pcd -> "+49-shared-mobile".equals(pcd.getContactInformation())).count();
		assertThat(contactCountAfterFirst, is(1L));

		// Configure threshold so the second message can be assigned to the existing case (not cancelled)
		creator.updateDiseaseConfiguration(Disease.CORONAVIRUS, true, true, true, true, null, 10, false, 0, 0);
		getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

		// Second message carries the same JSON for the same person (found by NHI)
		ExternalMessageDto secondMessage = createExternalMessage(m -> m.setAdditionalPersonContactDetails(additionalContactsJson));
		ProcessingResult<ExternalMessageProcessingResult> secondResult = runFlow(secondMessage);
		assertThat(secondResult.getStatus(), is(DONE));

		PersonDto personAfterSecond = getPersonFacade().getByUuid(personAfterFirst.getUuid());
		long contactCountAfterSecond =
			personAfterSecond.getPersonContactDetails().stream().filter(pcd -> "+49-shared-mobile".equals(pcd.getContactInformation())).count();
		assertThat(contactCountAfterSecond, is(1L));
	}

	// --------
	// Helpers
	// --------

	private ProcessingResult<ExternalMessageProcessingResult> runFlow(ExternalMessageDto labMessage) throws ExecutionException, InterruptedException {
		return flow.processLabMessage(labMessage);
	}

	private ExternalMessageDto createExternalMessage(Consumer<ExternalMessageDto> extraConfig) {
		return creator.createExternalMessage(externalMessage -> {
			externalMessage.setType(ExternalMessageType.LAB_MESSAGE);
			externalMessage.setMessageDateTime(new Date());
			externalMessage.setDisease(Disease.CORONAVIRUS);
			externalMessage.setPersonFirstName("John");
			externalMessage.setPersonLastName("Doe");
			externalMessage.setPersonSex(Sex.MALE);
			externalMessage.setPersonNationalHealthId("1234567890");
			externalMessage.setPersonFacility(rdcf.facility);
			externalMessage.setReporterExternalIds(Collections.singletonList(lab.getExternalID()));

			SampleReportDto sampleReport = new SampleReportDto();
			sampleReport.setSampleDateTime(new Date());
			sampleReport.setSpecimenCondition(SpecimenCondition.ADEQUATE);
			sampleReport.setSampleMaterial(SampleMaterial.CRUST);

			TestReportDto testReport = new TestReportDto();
			testReport.setTestResult(PathogenTestResultType.PENDING);
			testReport.setTestDateTime(new Date());
			testReport.setTestType(PathogenTestType.PCR_RT_PCR);

			sampleReport.setTestReports(Collections.singletonList(testReport));
			externalMessage.setSampleReports(Collections.singletonList(sampleReport));

			if (extraConfig != null) {
				extraConfig.accept(externalMessage);
			}
		});
	}
}
