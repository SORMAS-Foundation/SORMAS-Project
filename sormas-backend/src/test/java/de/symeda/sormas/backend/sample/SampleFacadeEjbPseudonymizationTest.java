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
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
		SampleDto sample = createCaseSample(caze);

		assertNotPseudonymized(getSampleFacade().getSampleByUuid(sample.getUuid()));
	}

	@Test
	public void testGetSamplekWithCaseOutsideJurisdiction() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		SampleDto sample = createCaseSample(caze);

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
		SampleDto sample1 = createCaseSample(caze1);

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
		SampleDto sample2 = createCaseSample(caze2);
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
		SampleDto sample1 = createCaseSample(caze1);

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
		SampleDto sample2 = createCaseSample(caze2);
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

		SampleExportDto export2 = exportList.stream().filter(t -> t.getUuid().equals(sample2.getUuid())).findFirst().get();
		assertThat(export2.getAssociatedCase().getFirstName(), isEmptyString());
		assertThat(export2.getAssociatedCase().getLastName(), isEmptyString());

		// export contact sample not yet implemented
		Optional<SampleExportDto> export3 = exportList.stream().filter(t -> t.getUuid().equals(sample3.getUuid())).findFirst();
		assertThat(export3.isPresent(), is(false));

		Optional<SampleExportDto> export4 = exportList.stream().filter(t -> t.getUuid().equals(sample4.getUuid())).findFirst();
		assertThat(export4.isPresent(), is(false));
	}

	@Test
	public void testPseudonymizeGetAllAfter() {
		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		SampleDto sample1 = createCaseSample(caze1);

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
		SampleDto sample2 = createCaseSample(caze2);
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

	private SampleDto createCaseSample(CaseDataDto caze) {
		Facility lab = new Facility();
		getFacilityService().persist(lab);

		return creator.createSample(caze.toReference(), user1.toReference(), lab);
	}

	private SampleDto createContactSample(ContactDto contactDto) {
		Facility lab = new Facility();
		getFacilityService().persist(lab);

		return creator.createSample(contactDto.toReference(), new Date(), new Date(), user1.toReference(), SampleMaterial.BLOOD, lab);
	}

	private void assertNotPseudonymized(SampleDto sample) {
		assertThat(sample.getAssociatedCase().getFirstName(), is("John"));
		assertThat(sample.getAssociatedCase().getLastName(), is("Smith"));
	}

	private void assertPseudonymized(SampleDto sample) {
		assertThat(sample.getAssociatedCase().getFirstName(), isEmptyString());
		assertThat(sample.getAssociatedCase().getLastName(), isEmptyString());
	}

}
