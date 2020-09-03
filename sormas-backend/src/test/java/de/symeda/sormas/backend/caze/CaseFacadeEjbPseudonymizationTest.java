/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.caze;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.backend.AbstractBeanTest;

@RunWith(MockitoJUnitRunner.class)
public class CaseFacadeEjbPseudonymizationTest extends AbstractBeanTest {

//	private TestDataCreator.RDCF rdcf1;
//	private TestDataCreator.RDCF rdcf2;
//	private CommunityDto rdcf2NewCommunity;
//	private FacilityDto rdcf2NewFacility;
//	private PointOfEntryDto rdcf2NewPointOfEntry;
//	private UserDto user1;
//	private UserDto user2;
//
//	@Override
//	public void init() {
//
//		super.init();
//
//		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
//		user1 = creator
//			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);
//
//		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
//		user2 = creator
//			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);
//		rdcf2NewCommunity = creator.createCommunity("New community", rdcf2.district);
//		rdcf2NewFacility = creator.createFacility("New facility", rdcf2.region, rdcf2.district, rdcf2NewCommunity.toReference());
//		rdcf2NewPointOfEntry = creator.createPointOfEntry("New point of entry", rdcf2.region, rdcf2.district);
//
//		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
//	}
//
	@Test
	public void testGetCaseInJurisdiction() {
//
//		CaseDataDto caze = createCase(rdcf2);
//		assertPersonalDataNotPseudonymized(getCaseFacade().getCaseDataByUuid(caze.getUuid()));
	}
//
//	@Test
//	public void testGetCaseOutsideJurisdiction() {
//
//		CaseDataDto caze = createCase(rdcf1);
//		assertPersonalDataPseudonymized(getCaseFacade().getCaseDataByUuid(caze.getUuid()));
//	}
//
//	@Test
//	public void testPseudonymizeGetByUuids() {
//
//		CaseDataDto caze1 = createCase(rdcf1);
//		CaseDataDto caze2 = createCase(rdcf2);
//
//		List<CaseDataDto> cases = getCaseFacade().getByUuids(Arrays.asList(caze1.getUuid(), caze2.getUuid()));
//
//		assertPersonalDataPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get());
//		assertPersonalDataNotPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get());
//	}
//
//	@Test
//	public void testPseudonymizeGetActiveCases() {
//
//		CaseDataDto caze1 = createCase(rdcf1);
//		// create contact in current jurisdiction to have access on pseudonymized case
//		creator.createContact(user2.toReference(), createPerson().toReference(), caze1);
//		CaseDataDto caze2 = createCase(rdcf2);
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.YEAR, 2019);
//		List<CaseDataDto> cases = getCaseFacade().getAllActiveCasesAfter(calendar.getTime());
//
//		assertPersonalDataPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get());
//		assertPersonalDataNotPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get());
//	}
//
//	@Test
//	public void testPseudonymizeGetPersonCases() {
//
//		PersonDto person = createPerson();
//
//		CaseDataDto caze1 = createCase(rdcf1, person.toReference());
//		// create contact in current jurisdiction to have access on pseudonymized case
//		creator.createContact(user2.toReference(), person.toReference(), caze1);
//
//		CaseDataDto caze2 = createCase(rdcf2, person.toReference());
//
//		List<CaseDataDto> cases = getCaseFacade().getAllCasesOfPerson(person.getUuid());
//
//		assertPersonalDataPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get());
//		assertPersonalDataNotPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get());
//	}
//
//	@Test
//	public void testPseudonymizeCasIndexData() {
//
//		CaseDataDto caze1 = createCase(rdcf1);
//		// create contact in current jurisdiction to have access on pseudonymized case
//		creator.createContact(user2.toReference(), createPerson().toReference(), caze1);
//		CaseDataDto caze2 = createCase(rdcf2);
//
//		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), null, null, Collections.emptyList());
//
//		CaseIndexDto caseIndex1 = indexList.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get();
//
//		assertThat(caseIndex1.getPersonFirstName(), isEmptyString());
//		assertThat(caseIndex1.getPersonLastName(), isEmptyString());
//		assertThat(caseIndex1.getHealthFacilityName(), isEmptyString());
//		assertThat(caseIndex1.getPointOfEntryName(), isEmptyString());
//
//		CaseIndexDto caseIndex2 = indexList.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get();
//		assertThat(caseIndex2.getPersonFirstName(), is("James"));
//		assertThat(caseIndex2.getPersonLastName(), is("Smith"));
//		assertThat(caseIndex2.getHealthFacilityName(), is("Facility 2 - Test Facility details"));
//		assertThat(caseIndex2.getPointOfEntryName(), is("Point of entry 2 - Test point of entry details"));
//	}
//
//	@Test
//	public void testPseudonymizeCasExportData() {
//
//		CaseDataDto caze1 = createCase(rdcf1);
//		// create contact in current jurisdiction to have access on pseudonymized case
//		creator.createContact(user2.toReference(), createPerson().toReference(), caze1);
//		CaseDataDto caze2 = createCase(rdcf2);
//
//		List<CaseExportDto> exportList =
//			getCaseFacade().getExportList(new CaseCriteria(), CaseExportType.CASE_SURVEILLANCE, 0, 100, null, Language.EN);
//
//		CaseExportDto caseIndex1 = exportList.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get();
//
//		assertThat(caseIndex1.getCommunity(), isEmptyString());
//		assertThat(caseIndex1.getHealthFacility(), isEmptyString());
//		assertThat(caseIndex1.getPointOfEntry(), isEmptyString());
//		assertThat(caseIndex1.getFirstName(), isEmptyString());
//		assertThat(caseIndex1.getLastName(), isEmptyString());
//		assertThat(caseIndex1.getAddress(), isEmptyString());
//		assertThat(caseIndex1.getPostalCode(), isEmptyString());
//		assertThat(caseIndex1.getAddressGpsCoordinates(), isEmptyString());
//
//		CaseExportDto caseIndex2 = exportList.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get();
//		assertThat(caseIndex2.getCommunity(), is(rdcf2.community.getCaption()));
//		assertThat(caseIndex2.getHealthFacility(), is("Facility 2 - Test Facility details"));
//		assertThat(caseIndex2.getPointOfEntry(), is("Point of entry 2 - Test point of entry details"));
//		assertThat(caseIndex2.getFirstName(), is("James"));
//		assertThat(caseIndex2.getLastName(), is("Smith"));
//		assertThat(caseIndex2.getAddress(), is("Test address"));
//		assertThat(caseIndex2.getPostalCode(), is("12345"));
//		assertThat(caseIndex2.getAddressGpsCoordinates(), is("26.533, 46.233 +-10m"));
//	}
//
//	@Test
//	public void testUpdateCaseInJurisdiction() {
//
//		CaseDataDto caze = createCase(rdcf2);
//		updateCase(caze);
//		assertPersonalDataUpdated(caze);
//	}
//
//	@Test
//	public void testUpdateCaseOutsideJurisdiction() {
//
//		CaseDataDto caze = createCase(rdcf1);
//		updateCase(caze);
//		assertPersonalDataNotUpdated(caze, rdcf1);
//	}
//
//	@Test
//	public void testUpdateWithPseudonymizedDto() {
//
//		CaseDataDto caze = createCase(rdcf2);
//		caze.setPseudonymized(true);
//		caze.setCommunity(null);
//		caze.setHealthFacility(null);
//		caze.setHealthFacilityDetails("");
//		caze.setPointOfEntry(null);
//		caze.setPointOfEntryDetails("");
//		getCaseFacade().saveCase(caze);
//
//		assertPersonalDataNotUpdated(caze, rdcf2);
//	}
//
//	private CaseDataDto createCase(TestDataCreator.RDCF rdcf) {
//		return createCase(rdcf, createPerson().toReference());
//	}
//
//	private CaseDataDto createCase(TestDataCreator.RDCF rdcf, PersonReferenceDto person) {
//
//		return creator.createCase(
//			user1.toReference(),
//			person,
//			Disease.CORONAVIRUS,
//			CaseClassification.PROBABLE,
//			InvestigationStatus.PENDING,
//			new Date(),
//			rdcf,
//			(c) -> {
//				c.setHealthFacilityDetails("Test Facility details");
//				c.setPointOfEntryDetails("Test point of entry details");
//			});
//	}
//
//	private PersonDto createPerson() {
//
//		LocationDto address = new LocationDto();
//		address.setRegion(rdcf1.region);
//		address.setDistrict(rdcf1.district);
//		address.setCommunity(rdcf1.community);
//		address.setCity("Test City");
//		address.setAddress("Test address");
//		address.setPostalCode("12345");
//		address.setAreaType(AreaType.URBAN);
//		address.setDetails("Test address details");
//		address.setLongitude(46.233);
//		address.setLatitude(26.533);
//		address.setLatLonAccuracy(10F);
//
//		return creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1, address);
//	}
//
//	private void assertPersonalDataNotPseudonymized(CaseDataDto caze) {
//
//		assertThat(caze.isPseudonymized(), is(false));
//		assertThat(caze.getRegion(), is(rdcf2.region));
//		assertThat(caze.getDistrict(), is(rdcf2.district));
//		assertThat(caze.getCommunity(), is(rdcf2.community));
//		assertThat(caze.getHealthFacility(), is(rdcf2.facility));
//		assertThat(caze.getHealthFacilityDetails(), is("Test Facility details"));
//		assertThat(caze.getPointOfEntry(), is(rdcf2.pointOfEntry));
//		assertThat(caze.getPointOfEntryDetails(), is("Test point of entry details"));
//		assertThat(caze.getPerson().getFirstName(), is("James"));
//		assertThat(caze.getPerson().getLastName(), is("Smith"));
//	}
//
//	private void assertPersonalDataPseudonymized(CaseDataDto caze) {
//
//		assertThat(caze.isPseudonymized(), is(true));
//		assertThat(caze.getRegion(), is(rdcf1.region));
//		assertThat(caze.getDistrict(), is(rdcf1.district));
//		assertThat(caze.getCommunity(), is(nullValue()));
//		assertThat(caze.getHealthFacility(), is(nullValue()));
//		assertThat(caze.getHealthFacilityDetails(), is(isEmptyString()));
//		assertThat(caze.getPointOfEntry(), is(nullValue()));
//		assertThat(caze.getPointOfEntryDetails(), is(isEmptyString()));
//		assertThat(caze.getPerson().getFirstName(), is(isEmptyString()));
//		assertThat(caze.getPerson().getLastName(), is(isEmptyString()));
//	}
//
//	private void updateCase(CaseDataDto caze) {
//
//		caze.setCommunity(rdcf2NewCommunity.toReference());
//		caze.setHealthFacility(rdcf2NewFacility.toReference());
//		caze.setHealthFacilityDetails("New HF details");
//		caze.setPointOfEntry(new PointOfEntryReferenceDto(rdcf2NewPointOfEntry.getUuid()));
//		caze.setPointOfEntryDetails("New PoE detail");
//
//		getCaseFacade().saveCase(caze);
//	}
//
//	private void assertPersonalDataUpdated(CaseDataDto caze) {
//
//		Case savedCase = getCaseService().getByUuid(caze.getUuid());
//		assertThat(savedCase.getCommunity().getUuid(), is(rdcf2NewCommunity.getUuid()));
//		assertThat(savedCase.getHealthFacility().getUuid(), is(rdcf2NewFacility.getUuid()));
//		assertThat(savedCase.getHealthFacilityDetails(), is("New HF details"));
//		assertThat(savedCase.getPointOfEntry().getUuid(), is(rdcf2NewPointOfEntry.getUuid()));
//		assertThat(savedCase.getPointOfEntryDetails(), is("New PoE detail"));
//	}
//
//	private void assertPersonalDataNotUpdated(CaseDataDto caze, TestDataCreator.RDCF rdfc) {
//
//		Case savedCase = getCaseService().getByUuid(caze.getUuid());
//		assertThat(savedCase.getCommunity().getUuid(), is(rdfc.community.getUuid()));
//		assertThat(savedCase.getHealthFacility().getUuid(), is(rdfc.facility.getUuid()));
//		assertThat(savedCase.getHealthFacilityDetails(), is("Test Facility details"));
//		assertThat(savedCase.getPointOfEntry().getUuid(), is(rdfc.pointOfEntry.getUuid()));
//		assertThat(savedCase.getPointOfEntryDetails(), is("Test point of entry details"));
//	}
}
