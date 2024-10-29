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

package de.symeda.sormas.backend.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class DeleteOldPathogenTestsAndSamplesTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private UserReferenceDto user;
	private CaseDataDto caze;

	final int negativeCovidTestMaxAge = 15;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF();
		user = creator.createUser(rdcf).toReference();
		caze = creator.createCase(user, creator.createPerson().toReference(), rdcf);

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.NEGAIVE_COVID_SAMPLES_MAX_AGE_DAYS, String.valueOf(negativeCovidTestMaxAge));
	}

	@Test
	public void testDeleteOldTestsOnly() {
		SampleDto sample = creator.createSample(caze.toReference(), user, rdcf.facility, s -> {
			s.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
		});
		PathogenTestDto oldTest = creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});
		PathogenTestDto newTest = creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.RAPID_TEST);
			s.setTestDateTime(new Date());
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});

		getSampleService().cleanupOldCovidSamples();

		List<PathogenTestDto> sampleTests = getPathogenTestFacade().getAllBySample(sample.toReference());
		assertThat(sampleTests.size(), is(1));
		assertThat(sampleTests.get(0).getUuid(), is(newTest.getUuid()));
		assertThat(getPathogenTestFacade().getByUuid(oldTest.getUuid()).isDeleted(), is(true));
	}

	@Test
	public void testDeleteOnlyNegativeTests() {
		SampleDto sample = creator.createSample(caze.toReference(), user, rdcf.facility, s -> {
			s.setPathogenTestResult(PathogenTestResultType.POSITIVE);
		});
		PathogenTestDto positiveTest = creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.POSITIVE);
		});
		creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});

		getSampleService().cleanupOldCovidSamples();

		List<PathogenTestDto> sampleTests = getPathogenTestFacade().getAllBySample(sample.toReference());
		assertThat(sampleTests.size(), is(1));
		assertThat(sampleTests.get(0).getUuid(), is(positiveTest.getUuid()));
	}

	@Test
	public void testDeleteSampleIfEmptied() {

		SampleDto sample = creator.createSample(caze.toReference(), user, rdcf.facility, s -> {
			s.setSampleDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
		});
		creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 5));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});
		creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});

		getSampleService().cleanupOldCovidSamples();

		List<PathogenTestDto> sampleTests = getPathogenTestFacade().getAllBySample(sample.toReference());
		assertThat(sampleTests.size(), is(0));
		assertThat(getSampleFacade().getSampleByUuid(sample.getUuid()).isDeleted(), is(true));
	}

	@Test
	public void testDeleteSampleWithOldAndDeletedTests() {

		SampleDto sample = creator.createSample(caze.toReference(), user, rdcf.facility, s -> {
			s.setSampleDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
		});
		creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 5));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});
		PathogenTestDto deletedTest = creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});
		getPathogenTestFacade().deletePathogenTest(deletedTest.getUuid(), new DeletionDetails(DeletionReason.DELETION_REQUEST, null));

		getSampleService().cleanupOldCovidSamples();

		List<PathogenTestDto> sampleTests = getPathogenTestFacade().getAllBySample(sample.toReference());
		assertThat(sampleTests.size(), is(0));
		assertThat(getSampleFacade().getSampleByUuid(sample.getUuid()).isDeleted(), is(true));
	}

	@Test
	public void testCovidTestsOnly() {
		SampleDto sample = creator.createSample(caze.toReference(), user, rdcf.facility, s -> {
			s.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
		});
		creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});
		PathogenTestDto otherDiseaseTest = creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.DENGUE);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});

		getSampleService().cleanupOldCovidSamples();

		List<PathogenTestDto> sampleTests = getPathogenTestFacade().getAllBySample(sample.toReference());
		assertThat(sampleTests.size(), is(1));
		assertThat(sampleTests.get(0).getUuid(), is(otherDiseaseTest.getUuid()));
	}

	@Test
	public void testDeletionReferenceDate() {

		SampleDto sample = creator.createSample(caze.toReference(), user, rdcf.facility, s -> {
			s.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
		});
		// old by creation date
		creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(null);
			s.setCreationDate(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});
		// old by result date
		creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setCreationDate(new Date());
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});

		// old by reporting date
		creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setCreationDate(new Date());
			s.setTestDateTime(null);
			s.setReportDate(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});

		getSampleService().cleanupOldCovidSamples();

		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getAllBySample(sample.toReference());
		assertThat(getSampleFacade().getSampleByUuid(sample.getUuid()).isDeleted(), is(true));
	}

	@Test
	public void testNotConfigured() {
		MockProducer.getProperties().remove(ConfigFacadeEjb.NEGAIVE_COVID_SAMPLES_MAX_AGE_DAYS);

		SampleDto sample = creator.createSample(caze.toReference(), user, rdcf.facility, s -> {
			s.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
		});
		creator.createPathogenTest(sample.toReference(), user, s -> {
			s.setLab(rdcf.facility);
			s.setTestedDisease(Disease.CORONAVIRUS);
			s.setTestType(PathogenTestType.PCR_RT_PCR);
			s.setTestDateTime(DateHelper.subtractDays(new Date(), negativeCovidTestMaxAge + 1));
			s.setTestResult(PathogenTestResultType.NEGATIVE);
		});

		getSampleService().cleanupOldCovidSamples();

		assertThat(getSampleFacade().getSampleByUuid(sample.getUuid()).isDeleted(), is(false));
		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getAllBySample(sample.toReference());
		assertThat(pathogenTests.size(), is(1));
	}
}
