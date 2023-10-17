/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus.CANCELED;
import static de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus.DONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.RelatedSamplesReportsAndPathogenTests;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;

public class AutomaticLabMessageProcessorTest extends AbstractBeanTest {

	private AutomaticLabMessageProcessor flow;

	private TestDataCreator.RDCF rdcf;
	private UserDto reportingUser;
	private FacilityDto lab;

	@Override
	public void init() {
		super.init();

		flow = getAutomaticLabMessageProcessingFlow();

		rdcf = creator.createRDCF();
		reportingUser = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_OFFICER);
		lab = creator.createFacility("Lab", rdcf.region, rdcf.district, f -> {
			f.setType(FacilityType.LABORATORY);
			f.setExternalID("test-facility-ext-id-1");
		});

	}

	@Test
	public void testProcessMessage() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = new ExternalMessageDto();

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);

		assertThat(result.getStatus(), is(CANCELED));
	}

	@Test
	public void testProcessWithNewData() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(null);

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);

		assertThat(result.getStatus(), is(DONE));
//		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		assertThat(persons.get(0).getFirstName(), is(externalMessage.getPersonFirstName()));
		assertThat(persons.get(0).getLastName(), is(externalMessage.getPersonLastName()));
		assertThat(persons.get(0).getSex(), is(externalMessage.getPersonSex()));

		List<CaseDataDto> cases = getCaseFacade().getByPersonUuids(persons.stream().map(PersonDto::getUuid).collect(Collectors.toList()));
		assertThat(cases, hasSize(1));
		assertThat(cases.get(0).getDisease(), is(externalMessage.getDisease()));
		assertThat(cases.get(0).getResponsibleRegion(), is(rdcf.region));
		assertThat(cases.get(0).getResponsibleDistrict(), is(rdcf.district));
		assertThat(cases.get(0).getHealthFacility(), is(rdcf.facility));

		List<SampleDto> samples = getSampleFacade().getByCaseUuids(cases.stream().map(CaseDataDto::getUuid).collect(Collectors.toList()));
		assertThat(samples, hasSize(1));
		SampleReportDto sampleReport = externalMessage.getSampleReports().get(0);
		assertThat(samples.get(0).getSpecimenCondition(), is(sampleReport.getSpecimenCondition()));
		assertThat(samples.get(0).getSamplePurpose(), is(SamplePurpose.EXTERNAL));
		assertThat(samples.get(0).getSampleDateTime().getTime(), is(sampleReport.getSampleDateTime().getTime()));
		assertThat(samples.get(0).getLab(), is(lab.toReference()));
		assertThat(samples.get(0).getSampleMaterial(), is(SampleMaterial.OTHER));
		assertThat(samples.get(0).getSampleMaterialText(), is("Automatically processed"));

		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getAllBySample(samples.get(0).toReference());
		assertThat(pathogenTests, hasSize(1));
		TestReportDto testReport = sampleReport.getTestReports().get(0);
		assertThat(pathogenTests.get(0).getTestResult(), is(testReport.getTestResult()));
		assertThat(pathogenTests.get(0).getTestDateTime().getTime(), is(testReport.getTestDateTime().getTime()));
		assertThat(pathogenTests.get(0).getTestType(), is(PathogenTestType.OTHER));
		assertThat(pathogenTests.get(0).getTestTypeText(), is("Automatically processed"));
	}

	@Test
	public void testProcessWithExistingPersonNoCase() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(null);

		PersonDto existingPerson =
			creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex(), p -> {
				p.setBirthdateDD(12);
				p.setBirthdateMM(12);
				p.setBirthdateYYYY(1952);
				p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
			});

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
//		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		assertThat(persons.get(0).getUuid(), is(existingPerson.getUuid()));
		List<CaseDataDto> cases = getCaseFacade().getByPersonUuids(persons.stream().map(PersonDto::getUuid).collect(Collectors.toList()));
		assertThat(cases, hasSize(1));
		List<SampleDto> samples = getSampleFacade().getByCaseUuids(cases.stream().map(CaseDataDto::getUuid).collect(Collectors.toList()));
		assertThat(samples, hasSize(1));
		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getAllBySample(samples.get(0).toReference());
		assertThat(pathogenTests, hasSize(1));
	}

	@Test
	public void testProcessWithExistingPersonAndCase() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(null);

		PersonDto person =
			creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex(), p -> {
				p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
			});

		CaseDataDto caze = creator.createCase(reportingUser.toReference(), person.toReference(), rdcf, c -> {
			c.setDisease(externalMessage.getDisease());
		});

		// can't process if there is no automaticSampleAssignmentThreshold set for the disease
		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));

		creator.updateDiseaseConfiguration(externalMessage.getDisease(), true, true, true, true, null, 10);
		getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

		result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
//		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		assertThat(persons.get(0).getUuid(), is(person.getUuid()));
		List<CaseDataDto> cases = getCaseFacade().getByPersonUuids(persons.stream().map(PersonDto::getUuid).collect(Collectors.toList()));
		assertThat(cases, hasSize(1));
		assertThat(cases.get(0).getUuid(), is(caze.getUuid()));
		List<SampleDto> samples = getSampleFacade().getByCaseUuids(cases.stream().map(CaseDataDto::getUuid).collect(Collectors.toList()));
		assertThat(samples, hasSize(1));
		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getAllBySample(samples.get(0).toReference());
		assertThat(pathogenTests, hasSize(1));
	}

	@Test
	public void testProcessWithMultiplePersonsWithSameNationalHealthId() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(null);

		creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex(), p -> {
			p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
		});
		creator.createPerson("James", "Smith", Sex.MALE, p -> {
			p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
		});

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));

		assertThat(getCaseFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getSampleFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getPathogenTestFacade().getAllActiveUuids(), hasSize(0));
	}

	@Test
	public void testProcessWithExistingPersonWithSameNationalHealthIdButDifferentDetails() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(null);

		creator.createPerson("James", "Smith", Sex.MALE, p -> {
			p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
		});

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));

		assertThat(getCaseFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getSampleFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getPathogenTestFacade().getAllActiveUuids(), hasSize(0));
	}

	@Test
	public void testProcessWithExistingSimilarPerson() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(m -> m.setPersonNationalHealthId(null));

		PersonDto person =
			creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex());
		// link the person to a contact to be visible in the system
		creator.createContact(rdcf, reportingUser.toReference(), person.toReference());

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));

		assertThat(getCaseFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getSampleFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getPathogenTestFacade().getAllActiveUuids(), hasSize(0));
	}

	@Test
	public void testProcessMessageWithNoNationalHealthId() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(m -> m.setPersonNationalHealthId(null));

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
//		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		List<CaseDataDto> cases = getCaseFacade().getByPersonUuids(persons.stream().map(PersonDto::getUuid).collect(Collectors.toList()));
		assertThat(cases, hasSize(1));
		List<SampleDto> samples = getSampleFacade().getByCaseUuids(cases.stream().map(CaseDataDto::getUuid).collect(Collectors.toList()));
		assertThat(samples, hasSize(1));
		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getAllBySample(samples.get(0).toReference());
		assertThat(pathogenTests, hasSize(1));
	}

	private ProcessingResult<RelatedSamplesReportsAndPathogenTests> runFlow(ExternalMessageDto labMessage)
		throws ExecutionException, InterruptedException {

		return flow.processLabMessage(labMessage).toCompletableFuture().get();
	}

	private ExternalMessageDto createExternalMessage(Consumer<ExternalMessageDto> extraConfig) {
		ExternalMessageDto externalMessage = new ExternalMessageDto();
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

		TestReportDto testReport = new TestReportDto();
		testReport.setTestResult(PathogenTestResultType.PENDING);
		testReport.setTestDateTime(new Date());

		sampleReport.setTestReports(Collections.singletonList(testReport));
		externalMessage.setSampleReports(Collections.singletonList(sampleReport));

		if (extraConfig != null) {
			extraConfig.accept(externalMessage);
		}

		return externalMessage;
	}
}
