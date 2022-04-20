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

package de.symeda.sormas.ui.labmessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.labmessage.RelatedLabMessageHandler.CorrectedEntityHandler;
import de.symeda.sormas.ui.labmessage.RelatedLabMessageHandler.CratePathogenTestHandler;
import de.symeda.sormas.ui.labmessage.RelatedLabMessageHandler.HandlerResult;
import de.symeda.sormas.ui.labmessage.RelatedLabMessageHandler.RelatedEntities;
import de.symeda.sormas.ui.labmessage.RelatedLabMessageHandler.RelatedLabMessageHandlerChain;
import de.symeda.sormas.ui.labmessage.RelatedLabMessageHandler.ShortcutHandler;

public class RelatedLabMessageHandlerTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private UserReferenceDto userRef;
	private PersonDto person;
	private FacilityDto lab;

	private final String reportId = "test-report-id";
	private final String labSampleId = "test-lab-si";

	private CorrectedEntityHandler<PersonDto> personChangesHandler;
	private CorrectedEntityHandler<SampleDto> sampleChangesHandler;
	private CorrectedEntityHandler<PathogenTestDto> pathogenTestChangesHandler;
	private CratePathogenTestHandler createPathogenTestHandler;
	private Supplier<CompletionStage<Boolean>> correctionFlowConfirmation;
	private Function<Boolean, CompletionStage<Boolean>> shortcutConfirmation;
	private BiFunction<LabMessageDto, SampleReferenceDto, CompletionStage<Boolean>> continueProcessingConfirmation;
	private ShortcutHandler shortcutHandler;

	private RelatedLabMessageHandler handler;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF();
		lab = FacilityDto.build();
		lab.setName("Lab");
		lab.setType(FacilityType.LABORATORY);
		lab.setExternalID("facility-ext-ID");
		lab.setRegion(rdcf.region.toReference());
		lab.setDistrict(rdcf.district.toReference());
		getFacilityFacade().save(lab);

		userRef = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.NATIONAL_USER)).toReference();
		person = creator.createPerson("James", "Smith", Sex.MALE);

		personChangesHandler = Mockito.mock(CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		sampleChangesHandler = Mockito.mock(CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		pathogenTestChangesHandler = Mockito.mock(CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);
			return null;
		}).when(pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		createPathogenTestHandler = Mockito.mock(CratePathogenTestHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(3)).next(true);
			return null;
		}).when(createPathogenTestHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		correctionFlowConfirmation = Mockito.mock(Supplier.class);
		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(true)).when(correctionFlowConfirmation).get();

		shortcutConfirmation = Mockito.mock(Function.class);
		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(true)).when(shortcutConfirmation).apply(Mockito.any());

		continueProcessingConfirmation = Mockito.mock(BiFunction.class);
		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(true))
			.when(continueProcessingConfirmation)
			.apply(Mockito.any(), Mockito.any());

		shortcutHandler = Mockito.mock(ShortcutHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(2)).next(true);
			return null;
		}).when(shortcutHandler).handle(Mockito.any(), Mockito.any(), Mockito.any());

		handler = new RelatedLabMessageHandler(
			correctionFlowConfirmation,
			shortcutConfirmation,
			personChangesHandler,
			sampleChangesHandler,
			pathogenTestChangesHandler,
			createPathogenTestHandler,
			continueProcessingConfirmation,
			shortcutHandler);
	}

	@Test
	public void test_getRelatedEntities_returnNullIfNoReportIdOrLabSampleId() {
		LabMessageDto labMessage = LabMessageDto.build();

		labMessage.setReportId(null);
		labMessage.setLabSampleId(null);

		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessage);
		assertThat(relatedEntities, is(nullValue()));

		labMessage.setReportId("repId");
		labMessage.setLabSampleId(null);

		relatedEntities = handler.getRelatedEntities(labMessage);
		assertThat(relatedEntities, is(nullValue()));

		labMessage.setReportId(null);
		labMessage.setLabSampleId("labSampleId");

		relatedEntities = handler.getRelatedEntities(labMessage);
		assertThat(relatedEntities, is(nullValue()));
	}

	@Test
	public void test_getRelatedEntities_relatedSample() {

		SampleDto sample = createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getSample(), equalTo(sample));
	}

	@Test
	public void test_getRelatedEntities_returnNullIfMultipleRelatedSamplesFound() {

		SampleDto sample = createProcessedLabMessage();

		SampleDto sample2 = creator.createSample(sample.getAssociatedCase(), userRef, lab, (s) -> {
			s.setLabSampleID(labSampleId);
		});

		//create a processed lab message with same report id
		creator.createLabMessage((lm) -> {
			lm.setReportId(reportId);
			lm.setLabSampleId(labSampleId);
			lm.setStatus(LabMessageStatus.PROCESSED);
			lm.setSample(sample2.toReference());
		});

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities, is(nullValue()));
	}

	@Test
	public void test_getRelatedEntities_relatedCasePerson() {
		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getPerson(), equalTo(person));
	}

	@Test
	public void test_getRelatedEntities_relatedContactPerson() {

		ContactReferenceDto contactRef = creator.createContact(userRef, person.toReference(), Disease.CORONAVIRUS, rdcf).toReference();

		SampleDto sample = creator.createSample(contactRef, userRef, lab.toReference(), (s) -> {
			s.setLabSampleID(labSampleId);
		});

		//create a processed lab message with same report id
		creator.createLabMessage((lm) -> {
			lm.setReportId(reportId);
			lm.setLabSampleId(labSampleId);
			lm.setStatus(LabMessageStatus.PROCESSED);
			lm.setSample(sample.toReference());
		});

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getPerson(), equalTo(person));
	}

	@Test
	public void test_getRelatedEntities_relatedPathogenTest() {

		SampleDto sample = createProcessedLabMessage();

		final String sampleExternalId = "test-sample-ext-id";
		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), userRef, t -> {
			t.setExternalId(sampleExternalId);
		});

		//create a processed lab message with same report id
		creator.createLabMessage((lm) -> {
			lm.setReportId(reportId);
			lm.setLabSampleId(labSampleId);
			lm.setStatus(LabMessageStatus.PROCESSED);
			lm.setSample(sample.toReference());
		});

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		TestReportDto testReport = TestReportDto.build();
		testReport.setExternalId(sampleExternalId);
		labMessageToProcess.setTestReports(Collections.singletonList(testReport));

		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getPathogenTests(), hasSize(1));
		assertThat(relatedEntities.getPathogenTests().get(0), equalTo(pathogenTest));
	}

	@Test
	public void test_getRelatedEntities_pathogenTestMismatch() {

		SampleDto sample = createProcessedLabMessage();
		creator.createPathogenTest(sample.toReference(), userRef, (t) -> {
			t.setExternalId("external");
		});
		creator.createPathogenTest(sample.toReference(), userRef, (t) -> {
			t.setExternalId("external");
		});

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setExternalId("external");

		labMessageToProcess.setTestReports(Collections.singletonList(testReport1));

		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getUnmatchedTestReports(), hasSize(1));
		assertThat(relatedEntities.getUnmatchedTestReports().get(0).getUuid(), equalTo(testReport1.getUuid()));
		assertThat(relatedEntities.isPathogenTestMisMatch(), is(true));
	}

	@Test
	public void test_getRelatedEntities_unmatchedTestReports() {

		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), userRef, (t) -> {
			t.setExternalId(null);
		});

		creator.createPathogenTest(sample.toReference(), userRef, (t) -> {
			t.setExternalId("external");
		});

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setExternalId("external2");

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setExternalId(null);

		labMessageToProcess.setTestReports(Arrays.asList(testReport1, testReport2));

		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getUnmatchedTestReports(), hasSize(2));
		assertThat(relatedEntities.getUnmatchedTestReports().get(0).getExternalId(), equalTo("external2"));
		assertThat(relatedEntities.getUnmatchedTestReports().get(1).getUuid(), equalTo(testReport2.getUuid()));
	}

	@Test
	public void test_handle_noRelatedEntities() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId + "1");
		labMessageToProcess.setLabSampleId(labSampleId + "1");

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.NOT_HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_notConfirmCorrectionHandling() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(correctionFlowConfirmation).get();

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));

		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_notConfirmShortcut() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(correctionFlowConfirmation).get();
		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(shortcutConfirmation).apply(Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.CONTINUE));

		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_handlePersonChanges() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName("NewLastName");
		labMessageToProcess.setPersonSex(person.getSex());

		Mockito.doAnswer(invocation -> {
			PersonDto originalPersonData = invocation.getArgument(1);
			assertThat(originalPersonData.getFirstName(), is(person.getFirstName()));
			assertThat(originalPersonData.getLastName(), is(person.getLastName()));

			PersonDto updatedPersonData = invocation.getArgument(2);
			assertThat(updatedPersonData.getFirstName(), is(person.getFirstName()));
			assertThat(updatedPersonData.getLastName(), is("NewLastName"));

			assertThat(originalPersonData.getUuid(), is(updatedPersonData.getUuid()));

			List<String[]> changedFields = invocation.getArgument(3);
			assertThat(changedFields, hasSize(1));
			assertThat(changedFields.get(0), arrayContaining(Person.LAST_NAME));

			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_handleSampleChanges() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setSampleMaterial(SampleMaterial.BLOOD);
		labMessageToProcess.setLabExternalId(sample.getLab().getExternalId());
		labMessageToProcess.setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.setSpecimenCondition(sample.getSpecimenCondition());

		Mockito.doAnswer(invocation -> {
			SampleDto originalSample = invocation.getArgument(1);
			assertThat(originalSample.getSampleMaterial(), is(sample.getSampleMaterial()));

			SampleDto updatedSample = invocation.getArgument(2);
			assertThat(updatedSample.getSampleMaterial(), is(SampleMaterial.BLOOD));
			assertThat(updatedSample.getSampleMaterial(), is(not(sample.getSampleMaterial())));

			assertThat(originalSample.getUuid(), is(updatedSample.getUuid()));

			List<String[]> changedFields = invocation.getArgument(3);
			assertThat(changedFields, hasSize(1));
			assertThat(changedFields.get(0), arrayContaining(SampleDto.SAMPLE_MATERIAL));

			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);

			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));
		Mockito.verify(sampleChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_handlePathogenTestChanges() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), userRef, p -> {
			p.setExternalId("test-external-id");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setLabExternalId(sample.getLab().getExternalId());
		labMessageToProcess.setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.setSpecimenCondition(sample.getSpecimenCondition());

		TestReportDto testReport = TestReportDto.build();
		testReport.setExternalId("test-external-id");
		testReport.setTestResult(pathogenTest.getTestResult());
		testReport.setTestResultVerified(pathogenTest.getTestResultVerified());
		testReport.setTestDateTime(pathogenTest.getTestDateTime());
		testReport.setTestType(PathogenTestType.RAPID_TEST);
		labMessageToProcess.setTestReports(Collections.singletonList(testReport));

		Mockito.doAnswer(invocation -> {
			PathogenTestDto originalTest = invocation.getArgument(1);
			assertThat(originalTest.getExternalId(), is("test-external-id"));
			assertThat(originalTest.getTestType(), is(pathogenTest.getTestType()));

			PathogenTestDto updatedTest = invocation.getArgument(2);
			assertThat(updatedTest.getTestType(), is(testReport.getTestType()));

			assertThat(originalTest.getUuid(), is(updatedTest.getUuid()));

			List<String[]> changedFields = invocation.getArgument(3);
			assertThat(changedFields, hasSize(1));
			assertThat(changedFields.get(0), arrayContaining(PathogenTestDto.TEST_TYPE));

			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);

			return null;
		}).when(pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));

		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

	}

	@Test
	public void test_handle_handleMultiplePathogenTests() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest1 = creator.createPathogenTest(sample.toReference(), userRef, p -> {
			p.setExternalId("test-external-id-1");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		PathogenTestDto pathogenTest2 = creator.createPathogenTest(sample.toReference(), userRef, p -> {
			p.setExternalId("test-external-id-2");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setLabExternalId(sample.getLab().getExternalId());
		labMessageToProcess.setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.setSpecimenCondition(sample.getSpecimenCondition());

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setExternalId("test-external-id-1");
		testReport1.setTestResult(pathogenTest1.getTestResult());
		testReport1.setTestResultVerified(pathogenTest1.getTestResultVerified());
		testReport1.setTestDateTime(pathogenTest1.getTestDateTime());
		testReport1.setTestType(PathogenTestType.RAPID_TEST);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setExternalId("test-external-id-2");
		testReport2.setTestResult(PathogenTestResultType.NEGATIVE);
		testReport2.setTestResultVerified(pathogenTest2.getTestResultVerified());
		testReport2.setTestDateTime(pathogenTest2.getTestDateTime());
		testReport2.setTestType(pathogenTest2.getTestType());

		labMessageToProcess.setTestReports(Arrays.asList(testReport1, testReport2));

		Mockito.doAnswer(invocation -> {
			PathogenTestDto originalTest = invocation.getArgument(1);
			PathogenTestDto updatedTest = invocation.getArgument(2);
			List<String[]> changedFields = invocation.getArgument(3);

			assertThat(originalTest.getUuid(), is(updatedTest.getUuid()));
			if (originalTest.getUuid().equals(pathogenTest1.getUuid())) {
				assertThat(originalTest.getExternalId(), is("test-external-id-1"));
				assertThat(originalTest.getTestType(), is(pathogenTest1.getTestType()));

				assertThat(updatedTest.getTestType(), is(testReport1.getTestType()));

				assertThat(changedFields, hasSize(1));
				assertThat(changedFields.get(0), arrayContaining(PathogenTestDto.TEST_TYPE));
			} else if (originalTest.getUuid().equals(pathogenTest2.getUuid())) {
				assertThat(originalTest.getExternalId(), is("test-external-id-2"));
				assertThat(originalTest.getTestType(), is(pathogenTest2.getTestType()));

				assertThat(updatedTest.getTestType(), is(testReport2.getTestType()));
				assertThat(updatedTest.getTestResult(), is(testReport2.getTestResult()));

				assertThat(changedFields, hasSize(1));
				assertThat(changedFields.get(0), arrayContaining(PathogenTestDto.TEST_RESULT));
			}

			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);

			return null;
		}).when(pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(2))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_createPathogenTest() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest1 = creator.createPathogenTest(sample.toReference(), userRef, p -> {
			p.setExternalId("test-external-id-1");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName() + " Changed");
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setLabExternalId(sample.getLab().getExternalId());
		labMessageToProcess.setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.setSpecimenCondition(sample.getSpecimenCondition());

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setExternalId("test-external-id-1");
		testReport1.setTestResult(pathogenTest1.getTestResult());
		testReport1.setTestResultVerified(pathogenTest1.getTestResultVerified());
		testReport1.setTestDateTime(pathogenTest1.getTestDateTime());
		testReport1.setTestType(pathogenTest1.getTestType());

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setExternalId("test-external-id-2");
		testReport2.setTestResult(PathogenTestResultType.NEGATIVE);
		testReport2.setTestResultVerified(true);
		Date newTestDateTime = new Date();
		testReport2.setTestDateTime(newTestDateTime);
		testReport2.setTestType(PathogenTestType.RAPID_TEST);

		labMessageToProcess.setTestReports(Arrays.asList(testReport1, testReport2));

		Mockito.doAnswer(invocation -> {
			TestReportDto testReport = invocation.getArgument(1);

			assertThat(testReport.getUuid(), is(testReport2.getUuid()));

			((RelatedLabMessageHandlerChain) invocation.getArgument(3)).next(true);

			return null;
		}).when(createPathogenTestHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_confirmCorrectionFlowCalledOnce() throws ExecutionException, InterruptedException {
		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), userRef, p -> {
			p.setExternalId("test-external-id");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName(person.getLastName() + " Changed");
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.setSampleMaterial(SampleMaterial.BLOOD);
		labMessageToProcess.setLabExternalId(sample.getLab().getExternalId());
		labMessageToProcess.setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.setSpecimenCondition(sample.getSpecimenCondition());

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setExternalId("test-external-id");
		testReport1.setTestResult(pathogenTest.getTestResult());
		testReport1.setTestResultVerified(pathogenTest.getTestResultVerified());
		testReport1.setTestDateTime(pathogenTest.getTestDateTime());
		testReport1.setTestType(PathogenTestType.RAPID_TEST);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setExternalId("test-external-id-2");

		labMessageToProcess.setTestReports(Arrays.asList(testReport1, testReport2));

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(correctionFlowConfirmation).get();

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));

		Mockito.verify(correctionFlowConfirmation, Mockito.times(1)).get();

		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_confirmShortcut() throws ExecutionException, InterruptedException {
		SampleDto sample = createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName() + "Changed");
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setLabExternalId(sample.getLab().getExternalId());
		labMessageToProcess.setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.setSpecimenCondition(sample.getSpecimenCondition());

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(correctionFlowConfirmation).get();
		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(true)).when(shortcutConfirmation).apply(Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));

		Mockito.verify(correctionFlowConfirmation, Mockito.times(1)).get();
		Mockito.verify(shortcutConfirmation, Mockito.times(1)).apply(Mockito.any());

		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(shortcutConfirmation, Mockito.times(1)).apply(Mockito.any());
		Mockito.verify(shortcutHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_correctionAdShortcutCalled() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName() + " Changed");
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setLabExternalId(sample.getLab().getExternalId());
		labMessageToProcess.setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.setSpecimenCondition(sample.getSpecimenCondition());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(shortcutHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_unconfirmCorrectionAndShortcut() throws ExecutionException, InterruptedException {
		SampleDto sample = createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setLabExternalId(sample.getLab().getExternalId());
		labMessageToProcess.setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.setSpecimenCondition(sample.getSpecimenCondition());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));

		Mockito.verify(correctionFlowConfirmation, Mockito.times(0)).get();
		Mockito.verify(shortcutConfirmation, Mockito.times(1)).apply(Mockito.any());

		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_cancelOnPerson() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");

		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).cancel();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.CANCELED));
		Mockito.verify(personChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_cancelOnSample() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");
		labMessageToProcess.setSampleMaterial(SampleMaterial.RECTAL_SWAB);

		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).cancel();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.CANCELED_WITH_UPDATES));
		Mockito.verify(sampleChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

	}

	@Test
	public void test_handle_cancelOnSampleWithNoUpdates() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");
		labMessageToProcess.setSampleOverallTestResult(PathogenTestResultType.POSITIVE);

		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(false);
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).cancel();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.CANCELED));
		Mockito.verify(sampleChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

	}

	private static class TestException extends RuntimeException {

	}

	@Test()
	public void test_handle_exceptionOnPerson() {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");

		Mockito.doAnswer(invocation -> {
			throw new TestException();
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = null;
		try {
			result = handler.handle(labMessageToProcess).toCompletableFuture().get();
		} catch (Exception e) {
			assertThat(e.getCause(), instanceOf(TestException.class));
		}

		assertThat(result, is(nullValue()));
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test()
	public void test_handle_exceptionInTheMiddleOfTheChain() {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");
		labMessageToProcess.setSampleMaterial(SampleMaterial.RECTAL_SWAB);

		Mockito.doAnswer(invocation -> {
			throw new TestException();
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = null;
		try {
			result = handler.handle(labMessageToProcess).toCompletableFuture().get();
		} catch (Exception e) {
			assertThat(e.getCause(), instanceOf(TestException.class));
		}

		assertThat(result, is(nullValue()));
		Mockito.verify(personChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test()
	public void test_handle_doneAfterCorrection() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false))
			.when(continueProcessingConfirmation)
			.apply(Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));
		Mockito.verify(shortcutHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test()
	public void test_handle_newTestReportOnly() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();
		creator.createPathogenTest(sample.toReference(), userRef, t -> {
			t.setExternalId("external");
		});
		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		TestReportDto testReport = TestReportDto.build();
		testReport.setExternalId("external 2");
		labMessageToProcess.setTestReports(Collections.singletonList(testReport));

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result, is(HandlerResult.HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(shortcutHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any());
	}

	private SampleDto createProcessedLabMessage() {
		CaseReferenceDto cazeRef = creator
			.createCase(userRef, person.toReference(), Disease.CORONAVIRUS, CaseClassification.SUSPECT, InvestigationStatus.PENDING, new Date(), rdcf)
			.toReference();

		SampleDto sample = creator.createSample(cazeRef, userRef, lab, (s) -> {
			s.setLabSampleID(labSampleId);
			s.setSampleMaterial(SampleMaterial.CRUST);
			s.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		});

		//create a processed lab message with same report id
		creator.createLabMessage((lm) -> {
			lm.setReportId(reportId);
			lm.setLabSampleId(labSampleId);
			lm.setStatus(LabMessageStatus.PROCESSED);
			lm.setSample(sample.toReference());
		});
		return sample;
	}
}
