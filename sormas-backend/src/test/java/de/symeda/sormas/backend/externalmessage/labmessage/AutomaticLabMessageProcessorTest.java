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

import static de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus.CANCELED;
import static de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus.DONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
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
	public void testProcessEmptyMessage() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = new ExternalMessageDto();

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);

		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
	}

	@Test
	public void testProcessWithNewData() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(null);

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);

		assertThat(result.getStatus(), is(DONE));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.PROCESSED));

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
		assertThat(samples.get(0).getSampleMaterial(), is(SampleMaterial.CRUST));

		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getAllBySample(samples.get(0).toReference());
		assertThat(pathogenTests, hasSize(1));
		TestReportDto testReport = sampleReport.getTestReports().get(0);
		assertThat(pathogenTests.get(0).getTestResult(), is(testReport.getTestResult()));
		assertThat(pathogenTests.get(0).getTestDateTime().getTime(), is(testReport.getTestDateTime().getTime()));
		assertThat(pathogenTests.get(0).getTestType(), is(PathogenTestType.PCR_RT_PCR));
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

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.PROCESSED));

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
		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));

		// set the threshold
		creator.updateDiseaseConfiguration(externalMessage.getDisease(), true, true, true, true, null, 10);
		getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

		// set the case report date back before the threshold
		caze.setReportDate(DateHelper.subtractDays(new Date(), 11));
		getCaseFacade().save(caze);

		result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.PROCESSED));
		List<CaseDataDto> cases = getCaseFacade().getAllAfter(new Date(0));
		assertThat(cases, hasSize(2));
		CaseDataDto newCase = cases.stream().filter(c -> !DataHelper.isSame(c, caze)).findFirst().get();
		assertThat(newCase.getPerson(), is(caze.getPerson()));

		// set the case report after the threshold
		caze.setReportDate(DateHelper.subtractDays(new Date(), 5));
		getCaseFacade().save(caze);

		result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		assertThat(persons.get(0).getUuid(), is(person.getUuid()));
		cases = getCaseFacade().getByPersonUuids(persons.stream().map(PersonDto::getUuid).collect(Collectors.toList()));
		assertThat(cases, hasSize(2));
		// the sample should be added on the new case
		List<SampleDto> samples = getSampleFacade().getByCaseUuids(Collections.singletonList(newCase.getUuid()));
		assertThat(samples, hasSize(2));
	}

	/**
	 * External message with sample date in the threshold period should generate a new sample to the existing case
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test
	public void testThresholdAgainstSampleDate() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(e -> {
			e.getSampleReports().get(0).setSampleDateTime(DateHelper.subtractDays(new Date(), 10));
		});

		PersonDto person =
			creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex(), p -> {
				p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
			});

		CaseDataDto caze = creator.createCase(reportingUser.toReference(), person.toReference(), rdcf, c -> {
			c.setDisease(externalMessage.getDisease());
			c.setReportDate(DateHelper.subtractDays(new Date(), 15));
		});
		creator.createSample(caze.toReference(), reportingUser.toReference(), rdcf.facility, s -> {
			s.setSampleDateTime(DateHelper.subtractDays(new Date(), 15));
		});

		// set the threshold
		creator.updateDiseaseConfiguration(externalMessage.getDisease(), true, true, true, true, null, 10);
		getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

		assertThat(getCaseFacade().count(new CaseCriteria().person(caze.getPerson())), is(1L));
		assertThat(getSampleFacade().count(new SampleCriteria().caze(caze.toReference())), is(2L));
	}

	@Test
	public void testProcessWithExistingPersonAndCaseWithBySampleDate() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(null);

		PersonDto person =
			creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex(), p -> {
				p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
			});

		CaseDataDto caze = creator.createCase(reportingUser.toReference(), person.toReference(), rdcf, c -> {
			c.setReportDate(DateHelper.subtractDays(new Date(), 1));
			c.setDisease(externalMessage.getDisease());
		});

		SampleDto sample = creator.createSample(caze.toReference(), reportingUser.toReference(), lab.toReference());

		// can't process if there is no automaticSampleAssignmentThreshold set for the disease
		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.UNPROCESSED));

		// set the threshold
		creator.updateDiseaseConfiguration(externalMessage.getDisease(), true, true, true, true, null, 10);
		getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

		// set the sample date time back before the threshold
		sample.setSampleDateTime(DateHelper.subtractDays(new Date(), 11));
		getSampleFacade().saveSample(sample);

		result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		List<CaseDataDto> cases = getCaseFacade().getByPersonUuids(persons.stream().map(PersonDto::getUuid).collect(Collectors.toList()));
		assertThat(cases, hasSize(2));
		CaseDataDto newCase = cases.stream().filter(c -> !DataHelper.isSame(c, caze)).findFirst().get();

		// set the sample date time after the threshold
		sample.setSampleDateTime(DateHelper.subtractDays(new Date(), 5));
		getSampleFacade().saveSample(sample);

		result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

		persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		assertThat(persons.get(0).getUuid(), is(person.getUuid()));
		cases = getCaseFacade().getByPersonUuids(persons.stream().map(PersonDto::getUuid).collect(Collectors.toList()));
		assertThat(cases, hasSize(2));
		List<SampleDto> samples = getSampleFacade().getByCaseUuids(Collections.singletonList(newCase.getUuid()));
		assertThat(samples, hasSize(2));
		SampleDto processedSample = samples.stream().filter(s -> !DataHelper.isSame(s, sample)).findFirst().get();
		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getAllBySample(processedSample.toReference());
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

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.UNPROCESSED));

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

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.UNPROCESSED));

		assertThat(getCaseFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getSampleFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getPathogenTestFacade().getAllActiveUuids(), hasSize(0));
	}

	@Test
	public void testProcessWithExistingPersonWithSameNationalHealthIdAndPersonDetailsNormalizedCheck()
		throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(m -> {
			m.setPersonFirstName("john vander");
			m.setPersonLastName("DOÉ");
			m.setPersonCity("	PERSON  	 city  \n");
			m.setPersonStreet(" person   STREET   12A");
		});

		creator.createPerson("John Van Der", "Doe", Sex.MALE, p -> {
			p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
			p.getAddress().setCity("person city");
			p.getAddress().setStreet("PERSON STREET, 12a");
		});

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.PROCESSED));
		assertThat(getPersonFacade().count(new PersonCriteria()), is(1L));
	}

	@Test
	public void testProcessWithExistingSimilarPerson() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(m -> m.setPersonNationalHealthId(null));

		PersonDto person =
			creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex());
		// link the person to a contact to be visible in the system
		creator.createContact(rdcf, reportingUser.toReference(), person.toReference());

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(CANCELED));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.UNPROCESSED));

		assertThat(getCaseFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getSampleFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getPathogenTestFacade().getAllActiveUuids(), hasSize(0));
	}

	@Test
	public void testProcessMessageWithNoNationalHealthId() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = createExternalMessage(m -> m.setPersonNationalHealthId(null));

		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
		assertThat(result.getStatus(), is(DONE));
		assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.PROCESSED));

		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		assertThat(persons, hasSize(1));
		List<CaseDataDto> cases = getCaseFacade().getByPersonUuids(persons.stream().map(PersonDto::getUuid).collect(Collectors.toList()));
		assertThat(cases, hasSize(1));
		List<SampleDto> samples = getSampleFacade().getByCaseUuids(cases.stream().map(CaseDataDto::getUuid).collect(Collectors.toList()));
		assertThat(samples, hasSize(1));
		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getAllBySample(samples.get(0).toReference());
		assertThat(pathogenTests, hasSize(1));
	}

	@Test
	public void testProcessPertussisMessageTestTypeCulture() throws ExecutionException, InterruptedException {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "lu");
		ExternalMessageDto cultureMessage = createExternalMessage((messageDto) -> {
			messageDto.setDisease(Disease.PERTUSSIS);
			messageDto.getSampleReports().get(0).getTestReports().get(0).setTestType(PathogenTestType.CULTURE);
			messageDto.getSampleReports().get(0).getTestReports().get(0).setTestResult(PathogenTestResultType.POSITIVE);
		});
		ProcessingResult<ExternalMessageProcessingResult> result = runFlow(cultureMessage);
		assertThat(result.getStatus(), is(DONE));
		assertThat(cultureMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		assertThat(getExternalMessageFacade().getByUuid(cultureMessage.getUuid()).getStatus(), is(ExternalMessageStatus.PROCESSED));
		CaseDataDto positiveCase = getCaseData();
		assertThat(positiveCase, is(notNullValue()));
		assertThat(positiveCase.getDisease(), is(cultureMessage.getDisease()));
		assertThat(positiveCase.getCaseClassification(), is(CaseClassification.CONFIRMED));
		assertThat(positiveCase.getInvestigationStatus(), is(InvestigationStatus.PENDING));
		assertThat(positiveCase.getOutcome(), is(CaseOutcome.NO_OUTCOME));
	}

	@Test
	public void testProcessPertussisTestTypePCR() throws ExecutionException, InterruptedException {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "lu");
		ExternalMessageDto pcrMessage = createExternalMessage((messageDto) -> {
			messageDto.setDisease(Disease.PERTUSSIS);
			messageDto.getSampleReports().get(0).getTestReports().get(0).setTestType(PathogenTestType.PCR_RT_PCR);
			messageDto.getSampleReports().get(0).getTestReports().get(0).setTestResult(PathogenTestResultType.POSITIVE);
		});
		runFlow(pcrMessage);
		assertThat(pcrMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		CaseDataDto pcrCase = getCaseData();
		assertThat(pcrCase, is(notNullValue()));
		assertThat(pcrCase.getDisease(), is(pcrMessage.getDisease()));
		assertThat(pcrCase.getCaseClassification(), is(CaseClassification.CONFIRMED));
		assertThat(pcrCase.getInvestigationStatus(), is(InvestigationStatus.PENDING));
		assertThat(pcrCase.getOutcome(), is(CaseOutcome.NO_OUTCOME));
	}

	@Test
	public void testProcessPertussisTestNegativeResult() throws ExecutionException, InterruptedException {
		ExternalMessageDto negativeMessage = createExternalMessage((messageDto) -> {
			messageDto.setDisease(Disease.PERTUSSIS);
			messageDto.getSampleReports().get(0).getTestReports().get(0).setTestType(PathogenTestType.CULTURE);
			messageDto.getSampleReports().get(0).getTestReports().get(0).setTestResult(PathogenTestResultType.NEGATIVE);
		});
		runFlow(negativeMessage);
		assertThat(negativeMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		CaseDataDto negativeCase = getCaseData();
		assertThat(negativeCase, is(notNullValue()));
		assertThat(negativeCase.getDisease(), is(negativeMessage.getDisease()));
		assertThat(negativeCase.getCaseClassification(), is(CaseClassification.NOT_CLASSIFIED));
		assertThat(negativeCase.getInvestigationStatus(), is(InvestigationStatus.PENDING));
		assertThat(negativeCase.getOutcome(), is(CaseOutcome.NO_OUTCOME));
	}

	@Test
	public void testProcessPertussisOtherTestType() throws ExecutionException, InterruptedException {
		ExternalMessageDto rapidTestMessage = createExternalMessage((messageDto) -> {
			messageDto.setDisease(Disease.PERTUSSIS);
			messageDto.getSampleReports().get(0).getTestReports().get(0).setTestType(PathogenTestType.RAPID_TEST);
			messageDto.getSampleReports().get(0).getTestReports().get(0).setTestResult(PathogenTestResultType.POSITIVE);
		});
		runFlow(rapidTestMessage);
		assertThat(rapidTestMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		CaseDataDto rapidTestcase = getCaseData();
		assertThat(rapidTestcase, is(notNullValue()));
		assertThat(rapidTestcase.getDisease(), is(rapidTestMessage.getDisease()));
		assertThat(rapidTestcase.getCaseClassification(), is(CaseClassification.NOT_CLASSIFIED));
		assertThat(rapidTestcase.getInvestigationStatus(), is(InvestigationStatus.PENDING));
		assertThat(rapidTestcase.getOutcome(), is(CaseOutcome.NO_OUTCOME));
	}

	private CaseDataDto getCaseData() {
		List<PersonDto> persons = getPersonFacade().getAllAfter(new Date(0));
		List<CaseDataDto> cases = getCaseFacade().getByPersonUuids(persons.stream().map(PersonDto::getUuid).collect(Collectors.toList()));
		return cases.get(0);
	}

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
