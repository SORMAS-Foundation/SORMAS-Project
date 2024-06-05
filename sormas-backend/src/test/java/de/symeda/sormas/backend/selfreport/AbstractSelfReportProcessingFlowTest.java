/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.selfreport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.hasSize;
import static wiremock.org.hamcrest.Matchers.is;
import static wiremock.org.hamcrest.Matchers.not;
import static wiremock.org.hamcrest.Matchers.nullValue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportProcessingStatus;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.api.selfreport.processing.AbstractSelfReportProcessingFlow;
import de.symeda.sormas.api.selfreport.processing.AbstractSelfReportProcessingFlow.EntityAndOptions;
import de.symeda.sormas.api.selfreport.processing.SelfReportProcessingFacade;
import de.symeda.sormas.api.selfreport.processing.SelfReportProcessingResult;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

public class AbstractSelfReportProcessingFlowTest extends AbstractBeanTest {

	@Mock
	private PickOrCreatePersonHandler handlePickOrCreatePerson;
	@Mock
	private PickOrCreateCaseHandler handlePickOrCreateCase;
	@Mock
	private PickOrCreateContactHandler handlePickOrCreateContact;
	@Mock
	private CreateCaseHandler handleCreateCase;
	@Mock
	private CreateContactHandler handleCreateContact;
	@Mock
	private ConfirmContinueWithoutProcessingReferencedCaseReportHandler confirmContinueWithoutProcessingReferencedCaseReport;

	private TestDataCreator.RDCF rdcf;
	private UserDto survOff;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF(true);
		getFacilityService().createConstantFacilities();
		survOff = creator.createUser(rdcf, "Ltest", DefaultUserRole.SURVEILLANCE_OFFICER);

		doAnswer(answerPickOrCreatePerson(null)).when(handlePickOrCreatePerson).handle(any(), any());

		PickOrCreateEntryResult pickOrCreateCaseResult = new PickOrCreateEntryResult();
		pickOrCreateCaseResult.setNewCase(true);
		doAnswer(answerPickOrCreateCase(pickOrCreateCaseResult)).when(handlePickOrCreateCase).handle(any(), any());
		doAnswer(answerCreateCase()).when(handleCreateCase).handle(any(), any());

		PickOrCreateEntryResult pickOrCreateContactResult = new PickOrCreateEntryResult();
		pickOrCreateContactResult.setNewContact(true);
		doAnswer(answerPickOrCreateCase(pickOrCreateContactResult)).when(handlePickOrCreateContact).handle(any(), any());
		doAnswer(answerCreateContact()).when(handleCreateContact).handle(any(), any());

		doAnswer((i) -> CompletableFuture.completedFuture(true)).when(confirmContinueWithoutProcessingReferencedCaseReport).handle();
	}

	@Test
	public void testCaseProcessing() throws ExecutionException, InterruptedException {
		SelfReportDto selfReport = createSelfReport(SelfReportType.CASE);

		ProcessingResult<SelfReportProcessingResult> result = runCaseFlow(selfReport);

		assertProcessed(result, selfReport);
	}

	@Test
	public void testCreatePerson() throws ExecutionException, InterruptedException {
		SelfReportDto selfReport = createSelfReport(SelfReportType.CASE);

		ArgumentCaptor<PersonDto> personCaptor = ArgumentCaptor.forClass(PersonDto.class);
		doAnswer(invocation -> {
			PersonDto mappedPerson = invocation.getArgument(0);
			HandlerCallback<EntitySelection<PersonDto>> callback = invocation.getArgument(1);

			assertThat(mappedPerson.getFirstName(), is(selfReport.getFirstName()));
			assertThat(mappedPerson.getLastName(), is(selfReport.getLastName()));
			assertThat(mappedPerson.getSex(), is(selfReport.getSex()));
			assertThat(mappedPerson.getBirthdateDD(), is(selfReport.getBirthdateDD()));
			assertThat(mappedPerson.getBirthdateMM(), is(selfReport.getBirthdateMM()));
			assertThat(mappedPerson.getBirthdateYYYY(), is(selfReport.getBirthdateYYYY()));
			assertThat(mappedPerson.getNationalHealthId(), is(selfReport.getNationalHealthId()));
			assertThat(mappedPerson.getEmailAddress(), is(selfReport.getEmail()));
			assertThat(mappedPerson.getPhone(), is(selfReport.getPhoneNumber()));
			assertThat(mappedPerson.getAddress().getUuid(), is(not(selfReport.getAddress().getUuid())));
			assertThat(mappedPerson.getAddress().getCity(), is(selfReport.getAddress().getCity()));
			assertThat(mappedPerson.getAddress().getStreet(), is(selfReport.getAddress().getStreet()));
			assertThat(mappedPerson.getAddress().getHouseNumber(), is(selfReport.getAddress().getHouseNumber()));
			assertThat(mappedPerson.getAddress().getPostalCode(), is(selfReport.getAddress().getPostalCode()));

			callback.done(new EntitySelection<>(mappedPerson, true));

			return null;
		}).when(handlePickOrCreatePerson).handle(personCaptor.capture(), any());

		ProcessingResult<SelfReportProcessingResult> result = runCaseFlow(selfReport);

		assertProcessed(result, selfReport);

		assertThat(result.getData().getPerson().isNew(), is(true));
		assertThat(personCaptor.getValue().getUuid(), is(result.getData().getPerson().getEntity().getUuid()));
	}

	@Test
	public void testPickPerson() throws ExecutionException, InterruptedException {
		SelfReportDto selfReport = createSelfReport(SelfReportType.CASE);

		ArgumentCaptor<PersonDto> personCaptor = ArgumentCaptor.forClass(PersonDto.class);
		PersonDto existingPerson = PersonDto.build();
		doAnswer(answerPickOrCreatePerson(existingPerson)).when(handlePickOrCreatePerson).handle(personCaptor.capture(), any());

		ProcessingResult<SelfReportProcessingResult> result = runCaseFlow(selfReport);

		assertProcessed(result, selfReport);

		assertThat(result.getData().getPerson().isNew(), is(false));
		assertThat(result.getData().getPerson().getEntity(), is(existingPerson));
	}

	@Test
	public void testCreateCase() throws ExecutionException, InterruptedException {
		// enable hide jusrisdiction fields feature
		FeatureConfigurationIndexDto indexFeatureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(indexFeatureConfiguration, FeatureType.HIDE_JURISDICTION_FIELDS);

		SelfReportDto selfReport = createSelfReport(SelfReportType.CASE);
		doAnswer(invocation -> {
			PersonDto person = invocation.getArgument(0);
			getPersonFacade().save(person);

			HandlerCallback<EntitySelection<PersonDto>> callback = invocation.getArgument(1);
			callback.done(new EntitySelection<>(person, true));

			return null;
		}).when(handlePickOrCreatePerson).handle(any(), any());
		ArgumentCaptor<CaseDataDto> caseCaptor = ArgumentCaptor.forClass(CaseDataDto.class);
		doAnswer(invocation -> {
			CaseDataDto caze = invocation.getArgument(0);

			assertThat(caze.getCaseReferenceNumber(), is(selfReport.getCaseReference()));
			assertThat(caze.getDiseaseDetails(), is(selfReport.getDiseaseDetails()));
			assertThat(caze.getDiseaseVariant(), is(selfReport.getDiseaseVariant()));
			assertThat(caze.getDiseaseVariantDetails(), is(selfReport.getDiseaseVariantDetails()));
			assertThat(caze.getQuarantineFrom(), is(selfReport.getIsolationDate()));
			assertThat(caze.getSymptoms().getOnsetDate(), is(selfReport.getDateOfSymptoms()));

			getCaseFacade().save(caze);

			HandlerCallback<EntityAndOptions<CaseDataDto>> callback = invocation.getArgument(1);
			callback.done(new EntityAndOptions<>(caze, true));
			return null;
		}).when(handleCreateCase).handle(caseCaptor.capture(), any());

		ProcessingResult<SelfReportProcessingResult> result = runCaseFlow(selfReport);
		assertProcessed(result, selfReport);
		assertThat(result.getData().getCaze().isNew(), is(true));
		assertThat(result.getData().isOpenEntityOnDone(), is(true));
		assertThat(caseCaptor.getValue().getUuid(), is(result.getData().getCaze().getEntity().getUuid()));

		SelfReport savedSelfReport = getSelfReportService().getByReferenceDto(selfReport.toReference());
		assertThat(savedSelfReport.getResultingCase().getUuid(), is(caseCaptor.getValue().getUuid()));
	}

	@Test
	public void testCreateCaseAndLinkContacts() throws ExecutionException, InterruptedException {
		// enable hide jusrisdiction fields feature
		FeatureConfigurationIndexDto indexFeatureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(indexFeatureConfiguration, FeatureType.HIDE_JURISDICTION_FIELDS);

		SelfReportDto selfReport = createSelfReport(SelfReportType.CASE);

		ContactDto contactToLink =
			creator.createContact(survOff.toReference(), creator.createPerson().toReference(), Disease.CORONAVIRUS, contact -> {
				contact.setCaseReferenceNumber(selfReport.getCaseReference());
			});

		ContactDto contactLinkedToOtherCase =
			creator.createContact(survOff.toReference(), creator.createPerson().toReference(), Disease.CORONAVIRUS, contact -> {
				contact.setCaseReferenceNumber(selfReport.getCaseReference());
				contact.setCaze(creator.createCase(survOff.toReference(), creator.createPerson().toReference(), rdcf).toReference());
			});

		ContactDto contactWithDifferentCaseRef =
			creator.createContact(survOff.toReference(), creator.createPerson().toReference(), Disease.CORONAVIRUS, contact -> {
				contact.setCaseReferenceNumber("otherCaseRef");
			});

		doAnswer(invocation -> {
			PersonDto person = invocation.getArgument(0);
			getPersonFacade().save(person);

			HandlerCallback<EntitySelection<PersonDto>> callback = invocation.getArgument(1);
			callback.done(new EntitySelection<>(person, true));

			return null;
		}).when(handlePickOrCreatePerson).handle(any(), any());
		ArgumentCaptor<CaseDataDto> caseCaptor = ArgumentCaptor.forClass(CaseDataDto.class);
		doAnswer(invocation -> {
			CaseDataDto caze = invocation.getArgument(0);
			getCaseFacade().save(caze);

			HandlerCallback<EntityAndOptions<CaseDataDto>> callback = invocation.getArgument(1);
			callback.done(new EntityAndOptions<>(caze, false));
			return null;
		}).when(handleCreateCase).handle(caseCaptor.capture(), any());

		ProcessingResult<SelfReportProcessingResult> result = runCaseFlow(selfReport);
		assertProcessed(result, selfReport);

		SelfReport savedSelfReport = getSelfReportService().getByReferenceDto(selfReport.toReference());
		getCaseFacade().getByUuid(savedSelfReport.getResultingCase().getUuid());

		List<ContactIndexDto> caseContacts =
			getContactFacade().getIndexList(new ContactCriteria().caze(savedSelfReport.getResultingCase().toReference()), null, null, null);
		assertThat(caseContacts, hasSize(1));
		assertThat(caseContacts.get(0).getUuid(), is(contactToLink.getUuid()));
	}

	@Test
	public void testPickCase() throws ExecutionException, InterruptedException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();

		SelfReportDto selfReport = createSelfReport(SelfReportType.CASE);
		PersonDto person = creator.createPerson(selfReport.getFirstName(), selfReport.getLastName(), selfReport.getSex(), p -> {
			p.setNationalHealthId(selfReport.getNationalHealthId());
			p.setBirthdateDD(selfReport.getBirthdateDD());
			p.setBirthdateMM(selfReport.getBirthdateMM());
			p.setBirthdateYYYY(selfReport.getBirthdateYYYY());
		});

		CaseDataDto caze = creator.createCase(
			survOff.toReference(),
			person.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).handle(any(), any());
		doAnswer(invocation -> {
			List<CaseSelectionDto> similarCases = invocation.getArgument(0);
			HandlerCallback<PickOrCreateEntryResult> callback = invocation.getArgument(1);

			PickOrCreateEntryResult result = new PickOrCreateEntryResult();
			result.setCaze(similarCases.get(0));

			callback.done(result);
			return null;
		}).when(handlePickOrCreateCase).handle(any(), any());

		ProcessingResult<SelfReportProcessingResult> result = runCaseFlow(selfReport);
		assertProcessed(result, selfReport);
		assertThat(result.getData().getCaze().isNew(), is(false));
		assertThat(result.getData().getCaze().getEntity().getUuid(), is(caze.getUuid()));

		SelfReport savedSelfReport = getSelfReportService().getByReferenceDto(selfReport.toReference());
		assertThat(savedSelfReport.getResultingCase().getUuid(), is(caze.toReference().getUuid()));
	}

	@Test
	public void testContactProcessing() throws ExecutionException, InterruptedException {
		SelfReportDto selfReport = createSelfReport(SelfReportType.CONTACT);

		ProcessingResult<SelfReportProcessingResult> result = runContactFlow(selfReport);

		assertProcessed(result, selfReport);
	}

	@Test
	public void testCreateContact() throws ExecutionException, InterruptedException {
		// enable hide jusrisdiction fields feature
		FeatureConfigurationIndexDto indexFeatureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(indexFeatureConfiguration, FeatureType.HIDE_JURISDICTION_FIELDS);

		SelfReportDto selfReport = createSelfReport(SelfReportType.CONTACT);
		doAnswer(invocation -> {
			PersonDto person = invocation.getArgument(0);
			getPersonFacade().save(person);

			HandlerCallback<EntitySelection<PersonDto>> callback = invocation.getArgument(1);
			callback.done(new EntitySelection<>(person, true));

			return null;
		}).when(handlePickOrCreatePerson).handle(any(), any());
		ArgumentCaptor<ContactDto> contactCaptor = ArgumentCaptor.forClass(ContactDto.class);
		doAnswer(invocation -> {
			ContactDto contact = invocation.getArgument(0);

			assertThat(contact.getCaseReferenceNumber(), is(selfReport.getCaseReference()));
			assertThat(contact.getDisease(), is(selfReport.getDisease()));
			assertThat(contact.getDiseaseDetails(), is(selfReport.getDiseaseDetails()));
			assertThat(contact.getDiseaseVariant(), is(selfReport.getDiseaseVariant()));
			assertThat(contact.getLastContactDate(), is(selfReport.getContactDate()));
			assertThat(contact.getQuarantineFrom(), is(selfReport.getIsolationDate()));

			getContactFacade().save(contact);

			HandlerCallback<EntityAndOptions<ContactDto>> callback = invocation.getArgument(1);
			callback.done(new EntityAndOptions<>(contact, true));
			return null;
		}).when(handleCreateContact).handle(contactCaptor.capture(), any());

		ProcessingResult<SelfReportProcessingResult> result = runContactFlow(selfReport);
		assertProcessed(result, selfReport);
		assertThat(result.getData().getContact().isNew(), is(true));
		assertThat(contactCaptor.getValue().getUuid(), is(result.getData().getContact().getEntity().getUuid()));
		assertThat(result.getData().isOpenEntityOnDone(), is(true));

		SelfReport savedSelfReport = getSelfReportService().getByReferenceDto(selfReport.toReference());
		assertThat(savedSelfReport.getResultingContact().getUuid(), is(contactCaptor.getValue().getUuid()));
	}

	@Test
	public void testPickContact() throws ExecutionException, InterruptedException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();

		SelfReportDto selfReport = createSelfReport(SelfReportType.CONTACT);
		PersonDto person = creator.createPerson(selfReport.getFirstName(), selfReport.getLastName(), selfReport.getSex(), p -> {
			p.setNationalHealthId(selfReport.getNationalHealthId());
			p.setBirthdateDD(selfReport.getBirthdateDD());
			p.setBirthdateMM(selfReport.getBirthdateMM());
			p.setBirthdateYYYY(selfReport.getBirthdateYYYY());
		});

		ContactDto contact = creator.createContact(survOff.toReference(), person.toReference(), Disease.CORONAVIRUS, rdcf);

		doAnswer(answerPickOrCreatePerson(person)).when(handlePickOrCreatePerson).handle(any(), any());
		doAnswer(invocation -> {
			List<SimilarContactDto> similarContacts = invocation.getArgument(0);
			HandlerCallback<PickOrCreateEntryResult> callback = invocation.getArgument(1);

			PickOrCreateEntryResult result = new PickOrCreateEntryResult();
			result.setContact(similarContacts.get(0));

			callback.done(result);
			return null;
		}).when(handlePickOrCreateContact).handle(any(), any());

		ProcessingResult<SelfReportProcessingResult> result = runContactFlow(selfReport);
		assertProcessed(result, selfReport);
		assertThat(result.getData().getContact().isNew(), is(false));
		assertThat(result.getData().getContact().getEntity().getUuid(), is(contact.getUuid()));

		SelfReport savedSelfReport = getSelfReportService().getByReferenceDto(selfReport.toReference());
		assertThat(savedSelfReport.getResultingContact().getUuid(), is(contact.toReference().getUuid()));
	}

	@Test
	public void testCancelContactProcessing() throws ExecutionException, InterruptedException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();

		// create both case and contact reports with hte same case reference number
		SelfReportDto caseReport = createSelfReport(SelfReportType.CASE);
		SelfReportDto contactReport = createSelfReport(SelfReportType.CONTACT);

		doAnswer((i) -> CompletableFuture.completedFuture(false)).when(confirmContinueWithoutProcessingReferencedCaseReport).handle();
		ProcessingResult<SelfReportProcessingResult> result = runContactFlow(contactReport);

		assertThat(result.getStatus(), is(ProcessingResultStatus.CANCELED));

		SelfReport savedSelfReport = getSelfReportService().getByReferenceDto(contactReport.toReference());
		assertThat(savedSelfReport.getProcessingStatus(), is(SelfReportProcessingStatus.UNPROCESSED));
		assertThat(savedSelfReport.getResultingContact(), is(nullValue()));
	}

	@Test
	public void testContactProcessingContinueWithSameReferenceCaseReport() throws ExecutionException, InterruptedException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();

		// create both case and contact reports with hte same case reference number
		SelfReportDto caseReport = createSelfReport(SelfReportType.CASE);
		SelfReportDto contactReport = createSelfReport(SelfReportType.CONTACT);

		doAnswer((i) -> CompletableFuture.completedFuture(true)).when(confirmContinueWithoutProcessingReferencedCaseReport).handle();
		ProcessingResult<SelfReportProcessingResult> result = runContactFlow(contactReport);

		assertProcessed(result, contactReport);
	}

	@Test
	public void testContactProcessingLinkToExistingCase() throws ExecutionException, InterruptedException {
		// enable hide jusrisdiction fields feature
		FeatureConfigurationIndexDto indexFeatureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(indexFeatureConfiguration, FeatureType.HIDE_JURISDICTION_FIELDS);

		TestDataCreator.RDCF rdcf = creator.createRDCF();

		SelfReportDto contactReport = createSelfReport(SelfReportType.CONTACT);
		CaseDataDto cazeWithRefNumber = creator.createCase(survOff.toReference(), creator.createPerson().toReference(), rdcf, c -> {
			c.setCaseReferenceNumber(contactReport.getCaseReference());
		});

		doAnswer(invocation -> {
			PersonDto person = invocation.getArgument(0);
			getPersonFacade().save(person);

			HandlerCallback<EntitySelection<PersonDto>> callback = invocation.getArgument(1);
			callback.done(new EntitySelection<>(person, true));

			return null;
		}).when(handlePickOrCreatePerson).handle(any(), any());

		doAnswer(invocation -> {
			ContactDto contact = invocation.getArgument(0);
			getContactFacade().save(contact);

			HandlerCallback<EntityAndOptions<ContactDto>> callback = invocation.getArgument(1);
			callback.done(new EntityAndOptions<>(contact, false));
			return null;
		}).when(handleCreateContact).handle(any(), any());
		ProcessingResult<SelfReportProcessingResult> result = runContactFlow(contactReport);

		assertProcessed(result, contactReport);

		SelfReport savedSelfReport = getSelfReportService().getByReferenceDto(contactReport.toReference());

		List<ContactIndexDto> caseContacts =
			getContactFacade().getIndexList(new ContactCriteria().caze(cazeWithRefNumber.toReference()), null, null, null);
		assertThat(caseContacts, hasSize(1));
		assertThat(caseContacts.get(0).getUuid(), is(savedSelfReport.getResultingContact().getUuid()));
	}

	private void assertProcessed(ProcessingResult<SelfReportProcessingResult> result, SelfReportDto selfReport) {
		assertThat(result.getStatus(), is(ProcessingResultStatus.DONE));
		assertThat(getSelfReportFacade().getByUuid(selfReport.getUuid()).getProcessingStatus(), is(SelfReportProcessingStatus.PROCESSED));
	}

	private ProcessingResult<SelfReportProcessingResult> runCaseFlow(SelfReportDto selfReport) throws ExecutionException, InterruptedException {
		return createFlow().runCaseFlow(selfReport).toCompletableFuture().get();
	}

	private ProcessingResult<SelfReportProcessingResult> runContactFlow(SelfReportDto selfReport) throws ExecutionException, InterruptedException {
		return createFlow().runContactFlow(selfReport).toCompletableFuture().get();
	}

	private AbstractSelfReportProcessingFlow createFlow() {
		return new AbstractSelfReportProcessingFlow(
			new SelfReportProcessingFacade(
				getFeatureConfigurationFacade(),
				getCaseFacade(),
				getContactFacade(),
				getRegionFacade(),
				getDistrictFacade(),
				getCommunityFacade(),
				getFacilityFacade(),
				getSelfReportFacade()) {

				@Override
				public boolean hasAllUserRights(UserRight... userRights) {
					return true;
				}
			},
			survOff) {

			@Override
			protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {
				handlePickOrCreatePerson.handle(person, callback);
			}

			@Override
			protected void handlePickOrCreateCase(
				List<CaseSelectionDto> similarCases,
				SelfReportDto selfReport,
				HandlerCallback<PickOrCreateEntryResult> callback) {
				handlePickOrCreateCase.handle(similarCases, callback);
			}

			@Override
			protected void handleCreateCase(
				CaseDataDto caze,
				PersonDto person,
				boolean isNewPerson,
				SelfReportDto selfReport,
				HandlerCallback<EntityAndOptions<CaseDataDto>> callback) {
				handleCreateCase.handle(caze, callback);
			}

			@Override
			protected void handlePickOrCreateContact(
				List<SimilarContactDto> similarContacts,
				SelfReportDto selfReport,
				HandlerCallback<PickOrCreateEntryResult> callback) {
				handlePickOrCreateContact.handle(similarContacts, callback);
			}

			@Override
			protected void handleCreateContact(
				ContactDto contact,
				PersonDto person,
				boolean isNewPerson,
				SelfReportDto selfReport,
				HandlerCallback<EntityAndOptions<ContactDto>> callback) {
				handleCreateContact.handle(contact, callback);
			}

			@Override
			protected CompletionStage<Boolean> confirmLinkContactsToCase() {
				return CompletableFuture.completedFuture(true);
			}

			@Override
			protected CompletionStage<Boolean> confirmContinueWithoutProcessingReferencedCaseReport() {
				return confirmContinueWithoutProcessingReferencedCaseReport.handle();
			}

			@Override
			protected CompletionStage<Boolean> confirmLinkContactToCaseByReferenceNumber() {
				return CompletableFuture.completedFuture(true);
			}
		};
	}

	private SelfReportDto createSelfReport(SelfReportType type) {
		SelfReportDto selfReport = SelfReportDto.build(SelfReportType.CASE);
		selfReport.setType(type);
		selfReport.setReportDate(new Date());
		selfReport.setDisease(Disease.CORONAVIRUS);
		selfReport.setDiseaseDetails("Details");
		selfReport.setCaseReference("1234567");

		DiseaseVariant diseaseVariant = creator.createDiseaseVariant("BF.1.2", Disease.CORONAVIRUS);
		Mockito
			.when(MockProducer.getCustomizableEnumFacadeForConverter().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, diseaseVariant.getValue()))
			.thenReturn(diseaseVariant);
		selfReport.setDiseaseVariant(diseaseVariant);
		selfReport.setDiseaseVariantDetails("Variant Details");

		selfReport.setFirstName("John");
		selfReport.setLastName("Doe");
		selfReport.setSex(Sex.MALE);
		selfReport.setBirthdateDD(1);
		selfReport.setBirthdateMM(2);
		selfReport.setBirthdateYYYY(1985);
		selfReport.setNationalHealthId("12345678");
		selfReport.setEmail("test@email.com");
		selfReport.setPhoneNumber("1234567890");

		LocationDto address = LocationDto.build();
		address.setCity("City");
		address.setStreet("Street");
		address.setHouseNumber("1");
		address.setPostalCode("12345");
		selfReport.setAddress(address);

		selfReport.setIsolationDate(new Date());
		selfReport.setDateOfSymptoms(new Date());

		selfReport.setWorkplace("Workplace");
		selfReport.setDateWorkplace(new Date());
		selfReport.setDateOfTest(new Date());

		return getSelfReportFacade().save(selfReport);
	}

	private Answer<Object> answerPickOrCreatePerson(PersonDto existingPerson) {
		return invocation -> {
			HandlerCallback<EntitySelection<PersonDto>> callback = invocation.getArgument(invocation.getArguments().length - 1);
			callback
				.done(existingPerson != null ? new EntitySelection<>(existingPerson, false) : new EntitySelection<>(invocation.getArgument(0), true));

			return null;
		};
	}

	private Answer<?> answerPickOrCreateCase(PickOrCreateEntryResult pickOrCreateEntryResult) {
		return invocation -> {
			HandlerCallback<PickOrCreateEntryResult> callback = invocation.getArgument(invocation.getArguments().length - 1);
			callback.done(pickOrCreateEntryResult);
			return null;
		};
	}

	private static Answer<?> answerCreateCase() {
		return invocation -> {
			HandlerCallback<EntityAndOptions<CaseDataDto>> callback = invocation.getArgument(invocation.getArguments().length - 1);
			callback.done(new EntityAndOptions<>(invocation.getArgument(0, CaseDataDto.class), false));
			return null;
		};
	}

	private static Answer<?> answerCreateContact() {
		return invocation -> {
			HandlerCallback<EntityAndOptions<ContactDto>> callback = invocation.getArgument(invocation.getArguments().length - 1);
			callback.done(new EntityAndOptions<>(invocation.getArgument(0, ContactDto.class), false));
			return null;
		};
	}

	private interface PickOrCreatePersonHandler {

		void handle(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback);
	}

	private interface PickOrCreateCaseHandler {

		void handle(List<CaseSelectionDto> similarCases, HandlerCallback<PickOrCreateEntryResult> callback);
	}
	private interface PickOrCreateContactHandler {

		void handle(List<SimilarContactDto> similarContacts, HandlerCallback<PickOrCreateEntryResult> callback);
	}

	private interface CreateCaseHandler {

		void handle(CaseDataDto caze, HandlerCallback<EntityAndOptions<CaseDataDto>> callback);
	}
	private interface CreateContactHandler {

		void handle(ContactDto caze, HandlerCallback<EntityAndOptions<ContactDto>> callback);
	}

	private interface ConfirmContinueWithoutProcessingReferencedCaseReportHandler {

		CompletionStage<Boolean> handle();
	}
}
