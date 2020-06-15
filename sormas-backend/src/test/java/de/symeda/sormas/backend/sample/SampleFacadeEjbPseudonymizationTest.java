/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleExportDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.facility.Facility;

@RunWith(MockitoJUnitRunner.class)
public class SampleFacadeEjbPseudonymizationTest extends AbstractBeanTest {

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
	public void testGetSampleWithCaseInJurisdiction() {
		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		SampleDto sample = createCaseSample(caze, user2);

		assertNotPseudonymized(getSampleFacade().getSampleByUuid(sample.getUuid()));
	}

	@Test
	public void testGetSampleWithCaseOutsideJurisdiction() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		SampleDto sample = createCaseSample(caze, user1);

		assertPseudonymized(getSampleFacade().getSampleByUuid(sample.getUuid()));
	}

	@Test
	public void testGetSampleWithContactInJurisdiction() {
		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("John", "Doe").toReference(), rdcf2);
		ContactDto contact = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("James", "Smith").toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		SampleDto sample = createContactSample(contact);

		SampleDto savedSample = getSampleFacade().getSampleByUuid(sample.getUuid());
		assertThat(savedSample.getAssociatedContact().getCaption(), is("James SMITH to case John Doe"));
	}

	@Test
	public void testGetSampleWithContactOutsideJurisdiction() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Doe").toReference(), rdcf1);
		ContactDto contact = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("James", "Smith").toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		SampleDto sample = createContactSample(contact);

		SampleDto savedSample = getSampleFacade().getSampleByUuid(sample.getUuid());
		assertThat(savedSample.getAssociatedContact().getCaption(), is(DataHelper.getShortUuid(savedSample.getAssociatedContact().getUuid())));
	}

	@Test
	public void testPseudonymizeIndexList() {
		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		SampleDto sample1 = createCaseSample(caze1, user2);

		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		ContactDto contact1 = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		SampleDto sample2 = createCaseSample(caze2, user1);
		SampleDto sample3 = createContactSample(contact1);

		ContactDto contact2 = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		SampleDto sample4 = createContactSample(contact2);

		List<SampleIndexDto> indexList = getSampleFacade().getIndexList(new SampleCriteria(), null, null, Collections.emptyList());
		SampleIndexDto index1 = indexList.stream().filter(t -> t.getUuid().equals(sample1.getUuid())).findFirst().get();
		assertThat(index1.getAssociatedCase().getFirstName(), is("John"));
		assertThat(index1.getAssociatedCase().getLastName(), is("Smith"));

		SampleIndexDto index2 = indexList.stream().filter(t -> t.getUuid().equals(sample2.getUuid())).findFirst().get();
		assertThat(index2.getAssociatedCase().getFirstName(), isEmptyString());
		assertThat(index2.getAssociatedCase().getLastName(), isEmptyString());

		SampleIndexDto index3 = indexList.stream().filter(t -> t.getUuid().equals(sample3.getUuid())).findFirst().get();
		assertThat(index3.getAssociatedContact().getCaption(), is("John SMITH"));

		SampleIndexDto index4 = indexList.stream().filter(t -> t.getUuid().equals(sample4.getUuid())).findFirst().get();
		assertThat(index4.getAssociatedContact().getCaption(), is(DataHelper.getShortUuid(sample4.getAssociatedContact().getUuid())));
	}

	@Test
	public void testPseudonymizeExportList() {
		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		SampleDto sample1 = createCaseSample(caze1, user2);
		createPathogenTest(sample1, user2);

		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		ContactDto contact1 = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		SampleDto sample2 = createCaseSample(caze2, user1);
		createPathogenTest(sample2, user1);
		createPathogenTest(sample2, user1);
		createPathogenTest(sample2, user1);
		createPathogenTest(sample2, user1);

		SampleDto sample3 = createContactSample(contact1);

		ContactDto contact2 = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		SampleDto sample4 = createContactSample(contact2);

		List<SampleExportDto> exportList = getSampleFacade().getExportList(new SampleCriteria(), 0, 100);
		SampleExportDto export1 = exportList.stream().filter(t -> t.getUuid().equals(sample1.getUuid())).findFirst().get();
		assertThat(export1.getAssociatedCase().getFirstName(), is("John"));
		assertThat(export1.getAssociatedCase().getLastName(), is("Smith"));
		assertThat(export1.getLab(), is("Lab - Test lab details"));
		assertThat(export1.getPathogenTestLab1(), is("Lab - Test lab details"));
		assertThat(export1.getPathogenTestType1(), is("Test type text"));


		SampleExportDto export2 = exportList.stream().filter(t -> t.getUuid().equals(sample2.getUuid())).findFirst().get();
		assertThat(export2.getAssociatedCase().getFirstName(), isEmptyString());
		assertThat(export2.getAssociatedCase().getLastName(), isEmptyString());
		assertThat(export2.getLab(), isEmptyString());
		assertThat(export2.getPathogenTestLab1(), isEmptyString());
		assertThat(export2.getPathogenTestType1(), isEmptyString());
		assertThat(export2.getPathogenTestLab2(), isEmptyString());
		assertThat(export2.getPathogenTestType2(), isEmptyString());
		assertThat(export2.getPathogenTestLab3(), isEmptyString());
		assertThat(export2.getPathogenTestType3(), isEmptyString());
		assertThat(export2.getOtherPathogenTestsDetails(), is("2020-06-10 (COVID-19, Pending)"));

		// export contact sample not yet implemented
		Optional<SampleExportDto> export3 = exportList.stream().filter(t -> t.getUuid().equals(sample3.getUuid())).findFirst();
		assertThat(export3.isPresent(), is(false));

		Optional<SampleExportDto> export4 = exportList.stream().filter(t -> t.getUuid().equals(sample4.getUuid())).findFirst();
		assertThat(export4.isPresent(), is(false));
	}

	private void createPathogenTest(SampleDto sample, UserDto user) {
		Date testDateTime = new Date(1591747200000L);//2020-06-10
		creator.createPathogenTest(sample.toReference(), PathogenTestType.ISOLATION, Disease.CORONAVIRUS, testDateTime, sample.getLab(), user.toReference(), PathogenTestResultType.PENDING, "", true, t -> {
			t.setLabDetails("Test lab details");
			t.setTestType(PathogenTestType.OTHER);
			t.setTestTypeText("Test type text");
		});
	}

	@Test
	public void testPseudonymizeGetAllAfter() {
		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		SampleDto sample1 = createCaseSample(caze1, user2);

		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		ContactDto contact1 = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		SampleDto sample2 = createCaseSample(caze2, user1);
		SampleDto sample3 = createContactSample(contact1);

		ContactDto contact2 = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		SampleDto sample4 = createContactSample(contact2);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2019);
		List<SampleDto> activeSamples = getSampleFacade().getAllActiveSamplesAfter(calendar.getTime());

		SampleDto active1 = activeSamples.stream().filter(t -> t.getUuid().equals(sample1.getUuid())).findFirst().get();
		assertNotPseudonymized(active1);

		SampleDto active2 = activeSamples.stream().filter(t -> t.getUuid().equals(sample2.getUuid())).findFirst().get();
		assertPseudonymized(active2);

		// case samples not yet implemented
		Optional<SampleDto> active3 = activeSamples.stream().filter(t -> t.getUuid().equals(sample3.getUuid())).findFirst();
		assertThat(active3.isPresent(), is(false));

		Optional<SampleDto> active4 = activeSamples.stream().filter(t -> t.getUuid().equals(sample4.getUuid())).findFirst();
		assertThat(active4.isPresent(), is(false));
	}

	@Test
	public void testUpdateSampleOutsideJurisdiction() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		SampleDto sample = createCaseSample(caze, user1);

		sample.setReportLat(null);
		sample.setReportLon(null);
		sample.setReportLatLonAccuracy(null);
		sample.setLab(null);

		getSampleFacade().saveSample(sample);

		Sample updatedSample = getSampleService().getByUuid(sample.getUuid());

		assertThat(updatedSample.getReportLat(), is(43.4321));
		assertThat(updatedSample.getReportLon(), is(23.4321));
		assertThat(updatedSample.getReportLatLonAccuracy(), is(10F));
		assertThat(updatedSample.getLab().getName(), is("Lab"));
	}

	@Test
	public void testUpdateSampleInJurisdictionWithPseudonymizedDto() {
		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		SampleDto sample = createCaseSample(caze, user2);

		sample.setPseudonymized(true);
		sample.setReportLat(null);
		sample.setReportLon(null);
		sample.setReportLatLonAccuracy(null);
		sample.setLab(null);

		getSampleFacade().saveSample(sample);

		Sample updatedSample = getSampleService().getByUuid(sample.getUuid());

		assertThat(updatedSample.getReportLat(), is(43.4321));
		assertThat(updatedSample.getReportLon(), is(23.4321));
		assertThat(updatedSample.getReportLatLonAccuracy(), is(10F));
		assertThat(updatedSample.getLab().getName(), is("Lab"));
	}

	private SampleDto createCaseSample(CaseDataDto caze, UserDto reportingUser) {
		Facility lab = new Facility();
		lab.setName("Lab");
		getFacilityService().persist(lab);

		return creator.createSample(caze.toReference(), reportingUser.toReference(), lab, s -> {
			s.setReportLat(43.4321);
			s.setReportLon(23.4321);
			s.setReportLatLonAccuracy(10f);
			s.setLabDetails("Test lab details");
			s.setShipmentDetails("Test shipment details");
			s.setComment("Test comment");
		});
	}

	private SampleDto createContactSample(ContactDto contactDto) {
		Facility lab = new Facility();
		getFacilityService().persist(lab);

		return creator.createSample(contactDto.toReference(), new Date(), new Date(), user1.toReference(), SampleMaterial.BLOOD, lab);
	}

	private void assertNotPseudonymized(SampleDto sample) {
		assertThat(sample.getAssociatedCase().getFirstName(), is("John"));
		assertThat(sample.getAssociatedCase().getLastName(), is("Smith"));

		//sensitive data
		assertThat(sample.getReportingUser().getUuid(), is(user2.getUuid()));
		assertThat(sample.getReportLat(), is(43.4321));
		assertThat(sample.getReportLon(), is(23.4321));
		assertThat(sample.getReportLatLonAccuracy(), is(10f));
		assertThat(sample.getLab(), is(notNullValue()));
		assertThat(sample.getLabDetails(), is("Test lab details"));
		assertThat(sample.getShipmentDetails(), is("Test shipment details"));
		assertThat(sample.getComment(), is("Test comment"));
	}

	private void assertPseudonymized(SampleDto sample) {
		assertThat(sample.getAssociatedCase().getFirstName(), isEmptyString());
		assertThat(sample.getAssociatedCase().getLastName(), isEmptyString());

		//sensitive data
		assertThat(sample.getReportingUser(), is(nullValue()));
		assertThat(sample.getReportLat(), is(nullValue()));
		assertThat(sample.getReportLon(), is(nullValue()));
		assertThat(sample.getReportLatLonAccuracy(), is(nullValue()));
		assertThat(sample.getLab(), is(nullValue()));
		assertThat(sample.getLabDetails(), isEmptyString());
		assertThat(sample.getShipmentDetails(), isEmptyString());
		assertThat(sample.getComment(), isEmptyString());
	}

}
