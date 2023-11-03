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

package de.symeda.sormas.ui.externalmessage.labmessage;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractRelatedLabMessageHandler;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractRelatedLabMessageHandler.HandlerResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractRelatedLabMessageHandler.HandlerResultStatus;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractRelatedLabMessageHandler.RelatedEntities;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractRelatedLabMessageHandler.RelatedLabMessageHandlerChain;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
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
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.ui.AbstractUiBeanTest;

public class RelatedLabMessageHandlerTest extends AbstractUiBeanTest {

	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private UserReferenceDto userRef;
	private PersonDto person;
	private FacilityDto lab;

	private final String reportId = "test-report-id";
	private final String labSampleId = "test-lab-si";

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF();
		lab = FacilityDto.build();
		lab.setName("Lab");
		lab.setType(FacilityType.LABORATORY);
		lab.setExternalID("facility-ext-ID");
		lab.setRegion(rdcf.region);
		lab.setDistrict(rdcf.district);
		getFacilityFacade().save(lab);

		user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		userRef = user.toReference();
		person = creator.createPerson("James", "Smith", Sex.MALE);
	}

	@Test
	public void test_getRelatedEntities_returnNullIfNoReportIdOrLabSampleId() {
		ExternalMessageDto labMessage = ExternalMessageDto.build();

		labMessage.setReportId(null);
		labMessage.addSampleReport(SampleReportDto.build());
		labMessage.getSampleReports().get(0).setLabSampleId(null);

		RelatedLabMessageTestHandler handler = getHandler(labMessage);

		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessage);
		assertThat(relatedEntities, is(nullValue()));

		labMessage.setReportId("repId");
		labMessage.getSampleReports().get(0).setLabSampleId(null);

		relatedEntities = handler.getRelatedEntities(labMessage);
		assertThat(relatedEntities, is(nullValue()));

		labMessage.setReportId(null);
		labMessage.getSampleReports().get(0).setLabSampleId("labSampleId");

		relatedEntities = handler.getRelatedEntities(labMessage);
		assertThat(relatedEntities, is(nullValue()));
	}

	@Test
	public void test_getRelatedEntities_relatedSample() {

		SampleDto sample = createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);

		RelatedEntities relatedEntities = getHandler(labMessageToProcess).getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getSample(), equalTo(sample));
	}

	@Test
	public void test_getRelatedEntities_returnNullIfMultipleRelatedSamplesFound() {

		SampleDto sample = createProcessedLabMessage();

		SampleDto sample2 = creator.createSample(sample.getAssociatedCase(), userRef, lab.toReference(), (s) -> {
			s.setLabSampleID(labSampleId);
		});

		//create a processed lab message with same report id
		creator.createExternalMessage((lm) -> {
			lm.setReportId(reportId);
			lm.addSampleReport(buildSampleReport(sample2));
			lm.getSampleReports().get(0).setLabSampleId(labSampleId);
			lm.setStatus(ExternalMessageStatus.PROCESSED);
		});

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.getSampleReportsNullSafe().get(0).setLabSampleId(labSampleId);

		RelatedEntities relatedEntities = getHandler(labMessageToProcess).getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities, is(nullValue()));
	}

	@Test
	public void test_getRelatedEntities_relatedCasePerson() {
		createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);

		RelatedEntities relatedEntities = getHandler(labMessageToProcess).getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getPerson(), equalTo(person));
	}

	@Test
	public void test_getRelatedEntities_relatedContactPerson() {

		ContactReferenceDto contactRef = creator.createContact(userRef, person.toReference(), Disease.CORONAVIRUS, rdcf).toReference();

		SampleDto sample = creator.createSample(contactRef, userRef, lab.toReference(), (s) -> {
			s.setLabSampleID(labSampleId);
		});

		//create a processed lab message with same report id
		creator.createExternalMessage((lm) -> {
			lm.setReportId(reportId);
			lm.addSampleReport(buildSampleReport(sample));
			lm.getSampleReports().get(0).setLabSampleId(labSampleId);
			lm.setStatus(ExternalMessageStatus.PROCESSED);
		});

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);

		RelatedEntities relatedEntities = getHandler(labMessageToProcess).getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getPerson(), equalTo(person));
	}

	@Test
	public void test_getRelatedEntities_relatedPathogenTest() {

		SampleDto sample = createProcessedLabMessage();

		final String sampleExternalId = "test-sample-ext-id";
		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, t -> {
			t.setExternalId(sampleExternalId);
		});

		//create a processed lab message with same report id
		creator.createExternalMessage((lm) -> {
			lm.addSampleReport(buildSampleReport(sample));
			lm.setReportId(reportId);
			lm.getSampleReports().get(0).setLabSampleId(labSampleId);
			lm.setStatus(ExternalMessageStatus.PROCESSED);
		});

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);

		TestReportDto testReport = TestReportDto.build();
		testReport.setExternalId(sampleExternalId);
		SampleReportDto sampleReport = SampleReportDto.build();
		sampleReport.addTestReport(testReport);
		labMessageToProcess.addSampleReport(sampleReport);
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);

		RelatedEntities relatedEntities = getHandler(labMessageToProcess).getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getPathogenTests(), hasSize(1));
		assertThat(relatedEntities.getPathogenTests().get(0), equalTo(pathogenTest));
	}

	@Test
	public void test_getRelatedEntities_pathogenTestMismatch() {

		SampleDto sample = createProcessedLabMessage();
		creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, (t) -> {
			t.setExternalId("external");
		});
		creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, (t) -> {
			t.setExternalId("external");
		});

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setExternalId("external");

		labMessageToProcess.getSampleReports().get(0).setTestReports(Collections.singletonList(testReport1));

		RelatedEntities relatedEntities = getHandler(labMessageToProcess).getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getUnmatchedTestReports(), hasSize(1));
		assertThat(relatedEntities.getUnmatchedTestReports().get(0).getUuid(), equalTo(testReport1.getUuid()));
		assertThat(relatedEntities.isPathogenTestMisMatch(), is(true));
	}

	@Test
	public void test_getRelatedEntities_unmatchedTestReports() {

		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, (t) -> {
			t.setExternalId(null);
		});

		creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, (t) -> {
			t.setExternalId("external");
		});

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setExternalId("external2");

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setExternalId(null);

		labMessageToProcess.getSampleReports().get(0).setTestReports(Arrays.asList(testReport1, testReport2));

		RelatedEntities relatedEntities = getHandler(labMessageToProcess).getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getUnmatchedTestReports(), hasSize(2));
		assertThat(relatedEntities.getUnmatchedTestReports().get(0).getExternalId(), equalTo("external2"));
		assertThat(relatedEntities.getUnmatchedTestReports().get(1).getUuid(), equalTo(testReport2.getUuid()));
	}

	@Test
	public void test_handle_noRelatedEntities() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId + "1");
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId + "1");

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);
		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.NOT_HANDLED));
		assertThat(result.getSample(), is(nullValue()));
		Mockito.verify(handler.personChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_notConfirmCorrectionHandling() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(handler.correctionFlowConfirmation).get();

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));
		assertThat(result.getSample(), is(sample));

		Mockito.verify(handler.personChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_notConfirmShortcut() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(handler.correctionFlowConfirmation).get();
		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(handler.shortcutConfirmation).apply(Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.CONTINUE));
		assertThat(result.getSample(), is(sample));

		Mockito.verify(handler.personChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_handlePersonChanges() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName("NewLastName");
		labMessageToProcess.setPersonSex(person.getSex());

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

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
		}).when(handler.personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));
		assertThat(result.getSample(), is(sample));
		Mockito.verify(handler.personChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_handleSampleChanges() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(SampleMaterial.BLOOD);
		labMessageToProcess.setReporterExternalIds(Collections.singletonList(sample.getLab().getExternalId()));
		labMessageToProcess.getSampleReports().get(0).setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.getSampleReports().get(0).setSpecimenCondition(sample.getSpecimenCondition());

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

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
		}).when(handler.sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_handlePathogenTestChanges() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, p -> {
			p.setExternalId("test-external-id");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setReporterExternalIds(Collections.singletonList(sample.getLab().getExternalId()));
		labMessageToProcess.getSampleReports().get(0).setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.getSampleReports().get(0).setSpecimenCondition(sample.getSpecimenCondition());

		TestReportDto testReport = TestReportDto.build();
		testReport.setExternalId("test-external-id");
		testReport.setTestResult(pathogenTest.getTestResult());
		testReport.setTestResultVerified(pathogenTest.getTestResultVerified());
		testReport.setTestDateTime(pathogenTest.getTestDateTime());
		testReport.setTestType(PathogenTestType.RAPID_TEST);
		labMessageToProcess.getSampleReports().get(0).setTestReports(Collections.singletonList(testReport));

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

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
		}).when(handler.pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));

		Mockito.verify(handler.personChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

	}

	@Test
	public void test_handle_handleMultiplePathogenTests() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest1 = creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, p -> {
			p.setExternalId("test-external-id-1");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		PathogenTestDto pathogenTest2 = creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, p -> {
			p.setExternalId("test-external-id-2");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setReporterExternalIds(Collections.singletonList(sample.getLab().getExternalId()));
		labMessageToProcess.getSampleReports().get(0).setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.getSampleReports().get(0).setSpecimenCondition(sample.getSpecimenCondition());

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

		labMessageToProcess.getSampleReports().get(0).setTestReports(Arrays.asList(testReport1, testReport2));

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

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
		}).when(handler.pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));
		Mockito.verify(handler.personChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(2))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_createPathogenTest() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest1 = creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, p -> {
			p.setExternalId("test-external-id-1");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName() + " Changed");
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setReporterExternalIds(Collections.singletonList(sample.getLab().getExternalId()));
		labMessageToProcess.getSampleReports().get(0).setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.getSampleReports().get(0).setSpecimenCondition(sample.getSpecimenCondition());

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

		labMessageToProcess.getSampleReports().get(0).setTestReports(Arrays.asList(testReport1, testReport2));

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> {
			TestReportDto testReport = invocation.getArgument(1);

			assertThat(testReport.getUuid(), is(testReport2.getUuid()));

			((RelatedLabMessageHandlerChain) invocation.getArgument(3)).next(true);

			return null;
		}).when(handler.createPathogenTestHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));
		Mockito.verify(handler.personChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.createPathogenTestHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_confirmCorrectionFlowCalledOnce() throws ExecutionException, InterruptedException {
		SampleDto sample = createProcessedLabMessage();
		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, p -> {
			p.setExternalId("test-external-id");
			p.setTestResult(sample.getPathogenTestResult());
			p.setTestType(PathogenTestType.CULTURE);
			p.setTestResultVerified(true);
		});

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName(person.getLastName() + " Changed");
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(SampleMaterial.BLOOD);
		labMessageToProcess.setReporterExternalIds(Collections.singletonList(sample.getLab().getExternalId()));
		labMessageToProcess.getSampleReports().get(0).setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.getSampleReports().get(0).setSpecimenCondition(sample.getSpecimenCondition());

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setExternalId("test-external-id");
		testReport1.setTestResult(pathogenTest.getTestResult());
		testReport1.setTestResultVerified(pathogenTest.getTestResultVerified());
		testReport1.setTestDateTime(pathogenTest.getTestDateTime());
		testReport1.setTestType(PathogenTestType.RAPID_TEST);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setExternalId("test-external-id-2");

		labMessageToProcess.getSampleReports().get(0).setTestReports(Arrays.asList(testReport1, testReport2));

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(handler.correctionFlowConfirmation).get();

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));

		Mockito.verify(handler.correctionFlowConfirmation, Mockito.times(1)).get();

		Mockito.verify(handler.personChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_confirmShortcut() throws ExecutionException, InterruptedException {
		SampleDto sample = createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName() + "Changed");
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setReporterExternalIds(Collections.singletonList(sample.getLab().getExternalId()));
		labMessageToProcess.getSampleReports().get(0).setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.getSampleReports().get(0).setSpecimenCondition(sample.getSpecimenCondition());

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false)).when(handler.correctionFlowConfirmation).get();
		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(true)).when(handler.shortcutConfirmation).apply(Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));

		Mockito.verify(handler.correctionFlowConfirmation, Mockito.times(1)).get();
		Mockito.verify(handler.shortcutConfirmation, Mockito.times(1)).apply(Mockito.any());

		Mockito.verify(handler.personChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.shortcutConfirmation, Mockito.times(1)).apply(Mockito.any());
		Mockito.verify(handler.shortcutHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_correctionAdShortcutCalled() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName() + " Changed");
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setReporterExternalIds(Collections.singletonList(sample.getLab().getExternalId()));
		labMessageToProcess.getSampleReports().get(0).setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.getSampleReports().get(0).setSpecimenCondition(sample.getSpecimenCondition());

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));
		Mockito.verify(handler.personChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.shortcutHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_unconfirmCorrectionAndShortcut() throws ExecutionException, InterruptedException {
		SampleDto sample = createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName(person.getLastName());
		labMessageToProcess.setPersonSex(person.getSex());
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(sample.getSampleMaterial());
		labMessageToProcess.setReporterExternalIds(Collections.singletonList(sample.getLab().getExternalId()));
		labMessageToProcess.getSampleReports().get(0).setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.getSampleReports().get(0).setSpecimenCondition(sample.getSpecimenCondition());

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));

		Mockito.verify(handler.correctionFlowConfirmation, Mockito.times(0)).get();
		Mockito.verify(handler.shortcutConfirmation, Mockito.times(1)).apply(Mockito.any());

		Mockito.verify(handler.personChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_cancelOnPerson() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).cancel();
			return null;
		}).when(handler.personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.CANCELED));
		Mockito.verify(handler.personChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handle_cancelOnSample() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(SampleMaterial.RECTAL_SWAB);

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).cancel();
			return null;
		}).when(handler.sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.CANCELED_WITH_UPDATES));
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

	}

	@Test
	public void test_handle_cancelOnSampleWithNoUpdates() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");
		labMessageToProcess.getSampleReports().get(0).setSampleOverallTestResult(PathogenTestResultType.POSITIVE);

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(false);
			return null;
		}).when(handler.personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandlerChain) invocation.getArgument(4)).cancel();
			return null;
		}).when(handler.sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.CANCELED));
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

	}

	private static class TestException extends RuntimeException {

	}

	@Test()
	public void test_handle_exceptionOnPerson() {

		createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> {
			throw new TestException();
		}).when(handler.personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = null;
		try {
			result = handler.handle(labMessageToProcess).toCompletableFuture().get();
		} catch (Exception e) {
			assertThat(e.getCause(), instanceOf(TestException.class));
		}

		assertThat(result, is(nullValue()));
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test()
	public void test_handle_exceptionInTheMiddleOfTheChain() {

		createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");
		labMessageToProcess.getSampleReports().get(0).setSampleMaterial(SampleMaterial.RECTAL_SWAB);

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> {
			throw new TestException();
		}).when(handler.sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		HandlerResult result = null;
		try {
			result = handler.handle(labMessageToProcess).toCompletableFuture().get();
		} catch (Exception e) {
			assertThat(e.getCause(), instanceOf(TestException.class));
		}

		assertThat(result, is(nullValue()));
		Mockito.verify(handler.personChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test()
	public void test_handle_doneAfterCorrection() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName("Updated");

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		Mockito.doAnswer(invocation -> CompletableFuture.completedFuture(false))
			.when(handler.continueProcessingConfirmation)
			.apply(Mockito.any(), Mockito.any());

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));
		Mockito.verify(handler.shortcutHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test()
	public void test_handle_newTestReportOnly() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();
		creator.createPathogenTest(sample.toReference(), userRef, rdcf.facility, t -> {
			t.setExternalId("external");
		});
		ExternalMessageDto labMessageToProcess = ExternalMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.addSampleReport(SampleReportDto.build());
		labMessageToProcess.getSampleReports().get(0).setLabSampleId(labSampleId);

		TestReportDto testReport = TestReportDto.build();
		testReport.setExternalId("external 2");
		labMessageToProcess.getSampleReports().get(0).setTestReports(Collections.singletonList(testReport));

		RelatedLabMessageTestHandler handler = getHandler(labMessageToProcess);

		HandlerResult result = handler.handle(labMessageToProcess).toCompletableFuture().get();

		assertThat(result.getStatus(), is(HandlerResultStatus.HANDLED));
		Mockito.verify(handler.personChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.sampleChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.createPathogenTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(handler.shortcutHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any());
	}

	private SampleDto createProcessedLabMessage() {
		CaseReferenceDto cazeRef = creator
			.createCase(userRef, person.toReference(), Disease.CORONAVIRUS, CaseClassification.SUSPECT, InvestigationStatus.PENDING, new Date(), rdcf)
			.toReference();

		SampleDto sample = creator.createSample(cazeRef, userRef, lab.toReference(), (s) -> {
			s.setLabSampleID(labSampleId);
			s.setSampleMaterial(SampleMaterial.CRUST);
			s.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		});

		//create a processed lab message with same report id
		creator.createExternalMessage((lm) -> {
			lm.setReportId(reportId);
			lm.addSampleReport(buildSampleReport(sample));
			lm.getSampleReports().get(0).setLabSampleId(labSampleId);
			lm.setStatus(ExternalMessageStatus.PROCESSED);
		});
		return sample;
	}

	private SampleReportDto buildSampleReport(SampleDto sample) {
		var sampleReport = SampleReportDto.build();
		sampleReport.setSample(sample.toReference());
		return sampleReport;
	}

	private RelatedLabMessageTestHandler getHandler(ExternalMessageDto labMessage) {
		return new RelatedLabMessageTestHandler(labMessage, getExternalMessageProcessingFacade(), user);
	}

	public interface CreatePathogenTestHandler {

		void handle(ExternalMessageDto labMessage, TestReportDto testReportDto, SampleDto sample, RelatedLabMessageHandlerChain chain);
	}

	public interface ShortcutHandler {

		void handle(ExternalMessageDto labMessage, SampleDto sample, RelatedLabMessageHandlerChain chain);
	}

	/**
	 * Needed, because cdi-test InvocationTargetManager.onMockCreated doesn't allow multiple mocks for the same (generic) class
	 */
	private interface CorrectedPersonHandler extends AbstractRelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> {
	}
	private interface CorrectedSampleHandler extends AbstractRelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> {
	}
	private interface CorrectedPathogenTestHandler extends AbstractRelatedLabMessageHandler.CorrectedEntityHandler<PathogenTestDto> {
	}

	/**
	 * Using an anonymous class for this lead to the error below.
	 * This seems to be a bug in weld, but I couldn't find any details on it.
	 * We will have to migrate to Jakarta before we can use more recent version of weld.
	 *
	 * org.jboss.classfilewriter.InvalidBytecodeException: Cannot load variable at 1. Local Variables: Local Variables: [StackEntry
	 * [descriptor=Lorg/mockito/invocation/InvocationOnMock;, type=OBJECT]]
	 * at org.jboss.classfilewriter.code.CodeAttribute.aload(CodeAttribute.java:196)
	 * at org.jboss.weld.util.bytecode.BytecodeUtils.addLoadInstruction(BytecodeUtils.java:72)
	 * at org.jboss.weld.bean.proxy.InterceptedSubclassFactory.invokeMethodHandler(InterceptedSubclassFactory.java:488)
	 */
	private static class RelatedLabMessageTestHandler extends AbstractRelatedLabMessageHandler {

		public CorrectedPersonHandler personChangesHandler;
		public CorrectedSampleHandler sampleChangesHandler;
		public CorrectedPathogenTestHandler pathogenTestChangesHandler;
		public CreatePathogenTestHandler createPathogenTestHandler;
		public Supplier<CompletionStage<Boolean>> correctionFlowConfirmation;
		public Function<Boolean, CompletionStage<Boolean>> shortcutConfirmation;
		public BiFunction<ExternalMessageDto, SampleReferenceDto, CompletionStage<Boolean>> continueProcessingConfirmation;
		public ShortcutHandler shortcutHandler;

		public RelatedLabMessageTestHandler(ExternalMessageDto labMessage, ExternalMessageProcessingFacade processingFacade, UserDto user) {
			super(user, processingFacade, new ExternalMessageMapper(labMessage, processingFacade));

			personChangesHandler = Mockito.mock(CorrectedPersonHandler.class);
			Mockito.doAnswer(invocation -> {
				((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);
				return null;
			}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
			sampleChangesHandler = Mockito.mock(CorrectedSampleHandler.class);
			Mockito.doAnswer(invocation -> {
				((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);
				return null;
			}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
			pathogenTestChangesHandler = Mockito.mock(CorrectedPathogenTestHandler.class);
			Mockito.doAnswer(invocation -> {
				((RelatedLabMessageHandlerChain) invocation.getArgument(4)).next(true);
				return null;
			}).when(pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
			createPathogenTestHandler = Mockito.mock(CreatePathogenTestHandler.class);
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
		}

		@Override
		protected CompletionStage<Boolean> confirmShortcut(boolean hasRelatedLabMessages) {
			return shortcutConfirmation.apply(hasRelatedLabMessages);
		}

		@Override
		protected CompletionStage<Boolean> confirmContinueProcessing(ExternalMessageDto labMessage, SampleReferenceDto sample) {
			return continueProcessingConfirmation.apply(labMessage, sample);
		}

		@Override
		protected void handleShortcut(ExternalMessageDto labMessage, SampleDto sample, RelatedLabMessageHandlerChain chain) {
			shortcutHandler.handle(labMessage, sample, chain);
		}

		@Override
		protected CompletionStage<Boolean> confirmCorrectionFlow() {
			return correctionFlowConfirmation.get();
		}

		@Override
		protected void handlePersonCorrection(
			ExternalMessageDto labMessage,
			PersonDto person,
			PersonDto updatedPerson,
			List<String[]> changedFields,
			RelatedLabMessageHandlerChain chain) {
			personChangesHandler.handle(labMessage, person, updatedPerson, changedFields, chain);
		}

		@Override
		protected void handleSampleCorrection(
			ExternalMessageDto labMessage,
			SampleDto sample,
			SampleDto updatedSample,
			List<String[]> changedFields,
			RelatedLabMessageHandlerChain chain) {
			sampleChangesHandler.handle(labMessage, sample, updatedSample, changedFields, chain);
		}

		@Override
		protected void handlePathogenTestCorrection(
			ExternalMessageDto labMessage,
			PathogenTestDto pathogenTest,
			PathogenTestDto updatedPathogenTest,
			List<String[]> changedFields,
			RelatedLabMessageHandlerChain chain) {
			pathogenTestChangesHandler.handle(labMessage, pathogenTest, updatedPathogenTest, changedFields, chain);
		}

		@Override
		protected void handlePathogenTestCreation(
			ExternalMessageDto labMessage,
			TestReportDto testReport,
			SampleDto sample,
			RelatedLabMessageHandlerChain chain) {
			createPathogenTestHandler.handle(labMessage, testReport, sample, chain);
		}
	}
}
