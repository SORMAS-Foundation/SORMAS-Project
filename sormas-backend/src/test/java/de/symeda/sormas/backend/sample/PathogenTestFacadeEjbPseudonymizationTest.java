/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.facility.Facility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PathogenTestFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator
			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Test
	public void testPathogenTestOnSampleInJurisdiction() {
		Facility lab = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf2.region, rdcf2.district, rdcf2.community).getUuid());
		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("First", "Last").toReference(), rdcf2);
		SampleDto sample = creator.createSample(caze.toReference(), user2.toReference(), lab);

		PathogenTestDto pathogenTest = createPathogenTest(lab, sample, user2);

		assertNotPseudonymized(getPathogenTestFacade().getByUuid(pathogenTest.getUuid()));
	}

	@Test
	public void testPathogenTestOnSampleOutsideJurisdiction() {
		Facility lab = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf1.region, rdcf1.district, rdcf1.community).getUuid());
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("First", "Last").toReference(), rdcf1);
		SampleDto sample = creator.createSample(caze.toReference(), user1.toReference(), lab);

		PathogenTestDto pathogenTest = createPathogenTest(lab, sample, user1);

		assertPseudonymized(getPathogenTestFacade().getByUuid(pathogenTest.getUuid()));
	}

	@Test
	public void testPseudonymizeGetAllActive() {
		Facility lab1 = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf2.region, rdcf2.district, rdcf2.community).getUuid());
		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("First", "Last").toReference(), rdcf2);
		SampleDto sample1 = creator.createSample(caze1.toReference(), user2.toReference(), lab1);

		PathogenTestDto pathogenTest1 = createPathogenTest(lab1, sample1, user2);

		Facility lab2 = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf1.region, rdcf1.district, rdcf1.community).getUuid());
		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("First", "Last").toReference(), rdcf1);
		creator.createContact(user2.toReference(), creator.createPerson("First", "Last").toReference(), caze2);
		SampleDto sample2 = creator.createSample(caze2.toReference(), user1.toReference(), lab2);

		PathogenTestDto pathogenTest2 = createPathogenTest(lab2, sample2, user1);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2019);
		List<PathogenTestDto> activeTests = getPathogenTestFacade().getAllActivePathogenTestsAfter(calendar.getTime());

		assertNotPseudonymized(activeTests.stream().filter(t -> t.getUuid().equals(pathogenTest1.getUuid())).findFirst().get());
		assertPseudonymized(activeTests.stream().filter(t -> t.getUuid().equals(pathogenTest2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeGetAllByUuds() {
		Facility lab1 = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf2.region, rdcf2.district, rdcf2.community).getUuid());
		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("First", "Last").toReference(), rdcf2);
		SampleDto sample1 = creator.createSample(caze1.toReference(), user2.toReference(), lab1);

		PathogenTestDto pathogenTest1 = createPathogenTest(lab1, sample1, user2);

		Facility lab2 = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf1.region, rdcf1.district, rdcf1.community).getUuid());
		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("First", "Last").toReference(), rdcf1);
		creator.createContact(user2.toReference(), creator.createPerson("First", "Last").toReference(), caze2);
		SampleDto sample2 = creator.createSample(caze2.toReference(), user1.toReference(), lab2);

		PathogenTestDto pathogenTest2 = createPathogenTest(lab2, sample2, user1);

		List<PathogenTestDto> activeTests = getPathogenTestFacade().getByUuids(Arrays.asList(pathogenTest1.getUuid(), pathogenTest2.getUuid()));

		assertNotPseudonymized(activeTests.stream().filter(t -> t.getUuid().equals(pathogenTest1.getUuid())).findFirst().get());
		assertPseudonymized(activeTests.stream().filter(t -> t.getUuid().equals(pathogenTest2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeGetAllBySample() {
		Facility lab1 = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf2.region, rdcf2.district, rdcf2.community).getUuid());
		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("First", "Last").toReference(), rdcf2);
		SampleDto sample1 = creator.createSample(caze1.toReference(), user2.toReference(), lab1);

		PathogenTestDto pathogenTest1 = createPathogenTest(lab1, sample1, user2);

		Facility lab2 = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf1.region, rdcf1.district, rdcf1.community).getUuid());
		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("First", "Last").toReference(), rdcf1);
		SampleDto sample2 = creator.createSample(caze2.toReference(), user1.toReference(), lab2);

		PathogenTestDto pathogenTest2 = createPathogenTest(lab2, sample2, user1);

		List<PathogenTestDto> sample1Tests = getPathogenTestFacade().getAllBySample(sample1.toReference());
		assertNotPseudonymized(sample1Tests.stream().filter(t -> t.getUuid().equals(pathogenTest1.getUuid())).findFirst().get());

		List<PathogenTestDto> sample2Tests = getPathogenTestFacade().getAllBySample(sample2.toReference());
		assertPseudonymized(sample2Tests.stream().filter(t -> t.getUuid().equals(pathogenTest2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeGetAllBySampleUuds() {
		Facility lab1 = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf2.region, rdcf2.district, rdcf2.community).getUuid());
		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("First", "Last").toReference(), rdcf2);
		SampleDto sample1 = creator.createSample(caze1.toReference(), user2.toReference(), lab1);

		PathogenTestDto pathogenTest1 = createPathogenTest(lab1, sample1, user2);

		Facility lab2 = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf1.region, rdcf1.district, rdcf1.community).getUuid());
		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("First", "Last").toReference(), rdcf1);
		SampleDto sample2 = creator.createSample(caze2.toReference(), user1.toReference(), lab2);

		PathogenTestDto pathogenTest2 = createPathogenTest(lab2, sample2, user1);

		List<PathogenTestDto> activeTests = getPathogenTestFacade().getBySampleUuids(Arrays.asList(sample1.getUuid(), sample2.getUuid()));

		assertNotPseudonymized(activeTests.stream().filter(t -> t.getUuid().equals(pathogenTest1.getUuid())).findFirst().get());
		assertPseudonymized(activeTests.stream().filter(t -> t.getUuid().equals(pathogenTest2.getUuid())).findFirst().get());
	}

	@Test
	public void updatePathogenTestOutsideJurisdiction() {
		Facility lab = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf1.region, rdcf1.district, rdcf1.community).getUuid());
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("First", "Last").toReference(), rdcf1);
		SampleDto sample = creator.createSample(caze.toReference(), user1.toReference(), lab);

		PathogenTestDto pathogenTest = createPathogenTest(lab, sample, user1);

		pathogenTest.setLab(null);
		pathogenTest.setLabUser(null);

		getPathogenTestFacade().savePathogenTest(pathogenTest);

		PathogenTest updatedTest = getPathogenTestService().getByUuid(pathogenTest.getUuid());

		assertThat(updatedTest.getLab().getName(), is("Lab"));
		assertThat(updatedTest.getLabUser().getUuid(), is(user1.getUuid()));
	}

	@Test
	public void updatePathogenTestInJurisdictionWithPseudonymizedDto() {
		Facility lab = getFacilityService().getByUuid(creator.createFacility("Lab", rdcf2.region, rdcf2.district, rdcf2.community).getUuid());
		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("First", "Last").toReference(), rdcf2);
		SampleDto sample = creator.createSample(caze.toReference(), user2.toReference(), lab);

		PathogenTestDto pathogenTest = createPathogenTest(lab, sample, user2);

		pathogenTest.setPseudonymized(true);
		pathogenTest.setLab(null);
		pathogenTest.setLabUser(null);

		getPathogenTestFacade().savePathogenTest(pathogenTest);

		PathogenTest updatedTest = getPathogenTestService().getByUuid(pathogenTest.getUuid());

		assertThat(updatedTest.getLab().getName(), is("Lab"));
		assertThat(updatedTest.getLabUser().getUuid(), is(user2.getUuid()));
	}

	private PathogenTestDto createPathogenTest(Facility lab, SampleDto sample, UserDto labUser) {
		return creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.ISOLATION,
			Disease.CORONAVIRUS,
			new Date(),
			lab,
			labUser.toReference(),
			PathogenTestResultType.PENDING,
			"",
			true,
			t -> {
				t.setLabDetails("Test lab details");
				t.setTestTypeText("Test type text");
			});
	}

	private void assertNotPseudonymized(PathogenTestDto pathogenTes) {
		assertThat(pathogenTes.getLab().getCaption(), is("Lab"));
		assertThat(pathogenTes.getLabDetails(), is("Test lab details"));
		assertThat(pathogenTes.getLabUser().getUuid(), is(user2.getUuid()));
		assertThat(pathogenTes.getTestTypeText(), is("Test type text"));
	}

	private void assertPseudonymized(PathogenTestDto pathogenTes) {
		assertThat(pathogenTes.getLab(), is(nullValue()));
		assertThat(pathogenTes.getLabDetails(), isEmptyString());
		assertThat(pathogenTes.getLabUser(), is(nullValue()));
		assertThat(pathogenTes.getTestTypeText(), isEmptyString());
	}
}
