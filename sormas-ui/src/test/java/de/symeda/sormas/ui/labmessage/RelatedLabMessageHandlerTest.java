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

import static de.symeda.sormas.ui.labmessage.LabMessageMapper.forLabMessage;
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
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.labmessage.RelatedLabMessageHandler.CorrectionResult;
import de.symeda.sormas.ui.labmessage.RelatedLabMessageHandler.RelatedEntities;

public class RelatedLabMessageHandlerTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
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
		lab.setRegion(rdcf.region.toReference());
		lab.setDistrict(rdcf.district.toReference());
		getFacilityFacade().save(lab);

		userRef = creator.createUser(rdcf, UserRole.NATIONAL_USER).toReference();
		person = creator.createPerson("James", "Smith", Sex.MALE);
	}

	@Test
	public void test_returnNullIfNoReportIdOrLabSampleId() {
		LabMessageDto labMessage = LabMessageDto.build();

		labMessage.setReportId(null);
		labMessage.setLabSampleId(null);

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(null, null, null, null, null);

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
	public void test_relatedSample() {

		SampleDto sample = createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(null, null, null, null, null);
		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getSample(), equalTo(sample));
	}

	@Test
	public void test_returnNullIfMultipleRelatedSamplesFound() {

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

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(null, null, null, null, null);
		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities, is(nullValue()));
	}

	@Test
	public void test_relatedCasePerson() {
		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(null, null, null, null, null);
		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getPerson(), equalTo(person));
	}

	@Test
	public void test_relatedContactPerson() {

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

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(null, null, null, null, null);
		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getPerson(), equalTo(person));
	}

	@Test
	public void test_relatedPathogenTest() {

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

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(null, null, null, null, null);
		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getPathogenTests(), hasSize(1));
		assertThat(relatedEntities.getPathogenTests().get(0), equalTo(pathogenTest));
	}

	@Test
	public void test_unmatchedTestReports() {

		SampleDto sample = createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		TestReportDto testReport = TestReportDto.build();
		testReport.setExternalId("external2");

		labMessageToProcess.setTestReports(Collections.singletonList(testReport));

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(null, null, null, null, null);
		RelatedEntities relatedEntities = handler.getRelatedEntities(labMessageToProcess);
		assertThat(relatedEntities.getUnmatchedTestReports(), hasSize(1));
		assertThat(relatedEntities.getUnmatchedTestReports().get(0).getExternalId(), equalTo("external2"));
	}

	@Test
	public void test_noRelatedEntities() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId + "1");
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		RelatedLabMessageHandler.CorrectedEntityHandler<PathogenTestDto> pathogenTestChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		RelatedLabMessageHandler.CratePathogenTestHandler createPathogenTestTestHandler =
			Mockito.mock(RelatedLabMessageHandler.CratePathogenTestHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(3)).next();
			return null;
		}).when(createPathogenTestTestHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(
			() -> CompletableFuture.completedFuture(true),
			personChangesHandler,
			sampleChangesHandler,
			pathogenTestChangesHandler,
			null);
		CorrectionResult result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();

		assertThat(result, is(CorrectionResult.NOT_HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_notConfirmHandling() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		RelatedLabMessageHandler.CorrectedEntityHandler<PathogenTestDto> pathogenTestChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		RelatedLabMessageHandler.CratePathogenTestHandler createPathogenTestTestHandler =
			Mockito.mock(RelatedLabMessageHandler.CratePathogenTestHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(3)).next();
			return null;
		}).when(createPathogenTestTestHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(
			() -> CompletableFuture.completedFuture(false),
			personChangesHandler,
			sampleChangesHandler,
			pathogenTestChangesHandler,
			createPathogenTestTestHandler);
		CorrectionResult result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();

		assertThat(result, is(CorrectionResult.NOT_HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(createPathogenTestTestHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handlePersonChanges() throws ExecutionException, InterruptedException {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setPersonFirstName(person.getFirstName());
		labMessageToProcess.setPersonLastName("NewLastName");
		labMessageToProcess.setPersonSex(person.getSex());

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
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

			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler =
			new RelatedLabMessageHandler(() -> CompletableFuture.completedFuture(true), personChangesHandler, sampleChangesHandler, null, null);
		CorrectionResult result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();

		assertThat(result, is(CorrectionResult.HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handleSampleChanges() throws ExecutionException, InterruptedException {

		SampleDto sample = createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);
		labMessageToProcess.setSampleMaterial(SampleMaterial.BLOOD);
		labMessageToProcess.setLabExternalId(sample.getLab().getExternalId());
		labMessageToProcess.setSampleDateTime(sample.getSampleDateTime());
		labMessageToProcess.setSpecimenCondition(sample.getSpecimenCondition());

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
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

			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();

			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler =
			new RelatedLabMessageHandler(() -> CompletableFuture.completedFuture(true), personChangesHandler, sampleChangesHandler, null, null);
		CorrectionResult result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();

		assertThat(result, is(CorrectionResult.HANDLED));
		Mockito.verify(sampleChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_handlePathogenTestChanges() throws ExecutionException, InterruptedException {

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

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<PathogenTestDto> pathogenTestChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
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

			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();

			return null;
		}).when(pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(
			() -> CompletableFuture.completedFuture(true),
			personChangesHandler,
			sampleChangesHandler,
			pathogenTestChangesHandler,
			null);
		CorrectionResult result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();

		assertThat(result, is(CorrectionResult.HANDLED));

		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(1))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

	}

	@Test
	public void test_handleMultiplePathogenTests() throws ExecutionException, InterruptedException {

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

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<PathogenTestDto> pathogenTestChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
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

			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();

			return null;
		}).when(pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(
			() -> CompletableFuture.completedFuture(true),
			personChangesHandler,
			sampleChangesHandler,
			pathogenTestChangesHandler,
			null);
		CorrectionResult result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();

		assertThat(result, is(CorrectionResult.HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(2))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_createPathogenTest() throws ExecutionException, InterruptedException {

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
		testReport1.setTestType(pathogenTest1.getTestType());

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setExternalId("test-external-id-2");
		testReport2.setTestResult(PathogenTestResultType.NEGATIVE);
		testReport2.setTestResultVerified(true);
		Date newTestDateTime = new Date();
		testReport2.setTestDateTime(newTestDateTime);
		testReport2.setTestType(PathogenTestType.RAPID_TEST);

		labMessageToProcess.setTestReports(Arrays.asList(testReport1, testReport2));

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<PathogenTestDto> pathogenTestChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(pathogenTestChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CratePathogenTestHandler pathogenTestCreateHandler =
			Mockito.mock(RelatedLabMessageHandler.CratePathogenTestHandler.class);
		Mockito.doAnswer(invocation -> {
			TestReportDto testReport = invocation.getArgument(1);

			assertThat(testReport.getUuid(), is(testReport2.getUuid()));

			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(3)).next();

			return null;
		}).when(pathogenTestCreateHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(
			() -> CompletableFuture.completedFuture(true),
			personChangesHandler,
			sampleChangesHandler,
			pathogenTestChangesHandler,
			pathogenTestCreateHandler);
		CorrectionResult result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();

		assertThat(result, is(CorrectionResult.HANDLED));
		Mockito.verify(personChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestCreateHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_cancelOnPersonLevel() {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).cancel();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler =
			new RelatedLabMessageHandler(() -> CompletableFuture.completedFuture(true), personChangesHandler, sampleChangesHandler, null, null);
		CorrectionResult result = null;
		try {
			result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();
		} catch (Exception e) {
			assertThat(e.getCause(), instanceOf(CancellationException.class));
		}

		assertThat(result, is(nullValue()));
		Mockito.verify(personChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void test_cancelOnSample() {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).cancel();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<PathogenTestDto> pathogenTestChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> null)
			.when(pathogenTestChangesHandler)
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler = new RelatedLabMessageHandler(
			() -> CompletableFuture.completedFuture(true),
			personChangesHandler,
			sampleChangesHandler,
			pathogenTestChangesHandler,
			null);
		CorrectionResult result = null;
		try {
			result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();
		} catch (Exception e) {
			assertThat(e.getCause(), instanceOf(CancellationException.class));
		}

		assertThat(result, is(nullValue()));
		Mockito.verify(sampleChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(pathogenTestChangesHandler, Mockito.times(0))
			.handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

	}

	private static class TestException extends RuntimeException {

	}

	@Test()
	public void test_exceptionOnPerson() {

		createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			throw new TestException();
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler =
			new RelatedLabMessageHandler(() -> CompletableFuture.completedFuture(true), personChangesHandler, sampleChangesHandler, null, null);
		CorrectionResult result = null;
		try {
			result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();
		} catch (Exception e) {
			assertThat(e, instanceOf(TestException.class));
		}

		assertThat(result, is(nullValue()));
		Mockito.verify(sampleChangesHandler, Mockito.times(0)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test()
	public void test_exceptionInTheMiddleOfTheChain() {

		SampleDto sample = createProcessedLabMessage();

		LabMessageDto labMessageToProcess = LabMessageDto.build();
		labMessageToProcess.setReportId(reportId);
		labMessageToProcess.setLabSampleId(labSampleId);

		RelatedLabMessageHandler.CorrectedEntityHandler<PersonDto> personChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			((RelatedLabMessageHandler.CorrectionHandlerChain) invocation.getArgument(4)).next();
			return null;
		}).when(personChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler.CorrectedEntityHandler<SampleDto> sampleChangesHandler =
			Mockito.mock(RelatedLabMessageHandler.CorrectedEntityHandler.class);
		Mockito.doAnswer(invocation -> {
			throw new TestException();
		}).when(sampleChangesHandler).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		RelatedLabMessageHandler handler =
			new RelatedLabMessageHandler(() -> CompletableFuture.completedFuture(true), personChangesHandler, sampleChangesHandler, null, null);
		CorrectionResult result = null;
		try {
			result = handler.handle(labMessageToProcess, forLabMessage(labMessageToProcess)).toCompletableFuture().get();
		} catch (Exception e) {
			assertThat(e.getCause(), instanceOf(TestException.class));
		}

		assertThat(result, is(nullValue()));
		Mockito.verify(personChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(sampleChangesHandler, Mockito.times(1)).handle(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
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
