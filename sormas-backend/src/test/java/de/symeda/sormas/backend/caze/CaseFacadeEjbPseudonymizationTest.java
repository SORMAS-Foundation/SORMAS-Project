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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.SampleMaterial;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.location.AreaType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

@RunWith(MockitoJUnitRunner.class)
public class CaseFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private CommunityDto rdcf2NewCommunity;
	private FacilityDto rdcf2NewFacility;
	private PointOfEntryDto rdcf2NewPointOfEntry;
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
		rdcf2NewCommunity = creator.createCommunity("New community", rdcf2.district);
		rdcf2NewFacility = creator.createFacility("New facility", rdcf2.region, rdcf2.district, rdcf2NewCommunity.toReference());
		rdcf2NewPointOfEntry = creator.createPointOfEntry("New point of entry", rdcf2.region, rdcf2.district);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Test
	public void testGetCaseInJurisdiction() {

		CaseDataDto caze = createCase(rdcf2, user2);
		assertNotPseudonymized(getCaseFacade().getCaseDataByUuid(caze.getUuid()));
	}

	@Test
	public void testGetCaseOutsideJurisdiction() {

		CaseDataDto caze = createCase(rdcf1, user1);
		assertPseudonymized(getCaseFacade().getCaseDataByUuid(caze.getUuid()));
	}

	@Test
	public void testPseudonymizeGetByUuids() {

		CaseDataDto caze1 = createCase(rdcf1, user1);
		CaseDataDto caze2 = createCase(rdcf2, user2);

		List<CaseDataDto> cases = getCaseFacade().getByUuids(Arrays.asList(caze1.getUuid(), caze2.getUuid()));

		assertPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get());
		assertNotPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeGetActiveCases() {

		CaseDataDto caze1 = createCase(rdcf1, user1);
		// create contact in current jurisdiction to have access on pseudonymized case
		creator.createContact(user2.toReference(), createPerson().toReference(), caze1);
		CaseDataDto caze2 = createCase(rdcf2, user2);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2019);
		List<CaseDataDto> cases = getCaseFacade().getAllActiveCasesAfter(calendar.getTime());

		assertPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get());
		assertNotPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeGetPersonCases() {

		PersonDto person = createPerson();

		CaseDataDto caze1 = createCase(rdcf1, person.toReference(), user1);
		// create contact in current jurisdiction to have access on pseudonymized case
		creator.createContact(user2.toReference(), person.toReference(), caze1);

		CaseDataDto caze2 = createCase(rdcf2, person.toReference(), user2);

		List<CaseDataDto> cases = getCaseFacade().getAllCasesOfPerson(person.getUuid());

		assertPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get());
		assertNotPseudonymized(cases.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeCasIndexData() {

		CaseDataDto caze1 = createCase(rdcf1, user1);
		// create contact in current jurisdiction to have access on pseudonymized case
		creator.createContact(user2.toReference(), createPerson().toReference(), caze1);
		CaseDataDto caze2 = createCase(rdcf2, user2);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), null, null, Collections.emptyList());

		CaseIndexDto caseIndex1 = indexList.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get();

		assertThat(caseIndex1.getPersonFirstName(), isEmptyString());
		assertThat(caseIndex1.getPersonLastName(), isEmptyString());
		assertThat(caseIndex1.getHealthFacilityName(), isEmptyString());
		assertThat(caseIndex1.getPointOfEntryName(), isEmptyString());

		CaseIndexDto caseIndex2 = indexList.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get();
		assertThat(caseIndex2.getPersonFirstName(), is("James"));
		assertThat(caseIndex2.getPersonLastName(), is("Smith"));
		assertThat(caseIndex2.getHealthFacilityName(), is("Facility 2 - Test Facility details"));
		assertThat(caseIndex2.getPointOfEntryName(), is("Point of entry 2 - Test point of entry details"));
	}

	@Test
	public void testPseudonymizeCasExportData() {

		CaseDataDto caze1 = createCase(rdcf1, user1);
		// create contact in current jurisdiction to have access on pseudonymized case
		creator.createContact(user2.toReference(), createPerson().toReference(), caze1);
		Date sampleDate = new Date(1591747200000L);//2020-06-10
		creator.createSample(caze1.toReference(), sampleDate, sampleDate, user1.toReference(), SampleMaterial.BLOOD, rdcf1.facility);

		CaseDataDto caze2 = createCase(rdcf2, user2);
		creator.createSample(caze2.toReference(), sampleDate, sampleDate, user2.toReference(), SampleMaterial.BLOOD, rdcf2.facility);

		List<CaseExportDto> exportList =
			getCaseFacade().getExportList(new CaseCriteria(), CaseExportType.CASE_SURVEILLANCE, 0, 100, null, Language.EN);

		CaseExportDto caseIndex1 = exportList.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get();

		assertThat(caseIndex1.getCommunity(), isEmptyString());
		assertThat(caseIndex1.getHealthFacility(), isEmptyString());
		assertThat(caseIndex1.getPointOfEntry(), isEmptyString());
		assertThat(caseIndex1.getFirstName(), isEmptyString());
		assertThat(caseIndex1.getLastName(), isEmptyString());
		assertThat(caseIndex1.getAddress(), isEmptyString());
		assertThat(caseIndex1.getPostalCode(), isEmptyString());
		assertThat(caseIndex1.getAddressGpsCoordinates(), isEmptyString());
		assertThat(caseIndex1.getBurialInfo().getBurialPlaceDescription(), is(isEmptyString()));
		assertThat(caseIndex1.getSample1().stringFormat(), is("2020-06-10 (Pending)"));

		CaseExportDto caseIndex2 = exportList.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get();
		assertThat(caseIndex2.getCommunity(), is(rdcf2.community.getCaption()));
		assertThat(caseIndex2.getHealthFacility(), is("Facility 2 - Test Facility details"));
		assertThat(caseIndex2.getPointOfEntry(), is("Point of entry 2 - Test point of entry details"));
		assertThat(caseIndex2.getFirstName(), is("James"));
		assertThat(caseIndex2.getLastName(), is("Smith"));
		assertThat(caseIndex2.getAddress(), is("Test address"));
		assertThat(caseIndex2.getPostalCode(), is("12345"));
		assertThat(caseIndex2.getAddressGpsCoordinates(), is("26.533, 46.233 +-10m"));
		assertThat(caseIndex2.getBurialInfo().getBurialPlaceDescription(), is("Burial place desc"));
		assertThat(caseIndex2.getSample1().stringFormat(), is("2020-06-10 (Facility 2, Pending)"));
	}

	@Test
	public void testUpdateCaseInJurisdiction() {

		CaseDataDto caze = createCase(rdcf2, user2);
		updateCase(caze, user1);
		assertPseudonymizedDataUpdated(caze);
	}

	@Test
	public void testUpdateCaseOutsideJurisdiction() {

		CaseDataDto caze = createCase(rdcf1, user1);
		updateCase(caze, user2);
		assertPseudonymizedDataNotUpdated(caze, rdcf1, user1);
	}

	@Test
	public void testUpdateWithPseudonymizedDto() {

		CaseDataDto caze = createCase(rdcf2, user2);
		caze.setPseudonymized(true);
		caze.setCommunity(null);
		caze.setHealthFacility(null);
		caze.setHealthFacilityDetails("");
		caze.setPointOfEntry(null);
		caze.setPointOfEntryDetails("");

		//sensitive data
		caze.setReportingUser(null);
		caze.setSurveillanceOfficer(null);
		caze.setClassificationUser(null);

		caze.setReportLat(null);
		caze.setReportLon(null);
		caze.setReportLatLonAccuracy(null);

		getCaseFacade().saveCase(caze);

		assertPseudonymizedDataNotUpdated(caze, rdcf2, user2);
	}

	private CaseDataDto createCase(TestDataCreator.RDCF rdcf, UserDto reportingUser) {
		return createCase(rdcf, createPerson().toReference(), reportingUser);
	}

	private CaseDataDto createCase(TestDataCreator.RDCF rdcf, PersonReferenceDto person, UserDto reportingUser) {

		return creator.createCase(
			user1.toReference(),
			person,
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			(c) -> {
				c.setReportingUser(reportingUser.toReference());
					c.setClassificationUser(reportingUser.toReference());
					c.setSurveillanceOfficer(reportingUser.toReference());

					c.setHealthFacilityDetails("Test Facility details");
					c.setPointOfEntryDetails("Test point of entry details");

					c.setReportLat(43.2344);
					c.setReportLon(26.6422);
					c.setReportLatLonAccuracy(10F);
				});
	}

	private PersonDto createPerson() {

		LocationDto address = new LocationDto();
		address.setRegion(rdcf1.region);
		address.setDistrict(rdcf1.district);
		address.setCommunity(rdcf1.community);
		address.setCity("Test City");
		address.setAddress("Test address");
		address.setPostalCode("12345");
		address.setAreaType(AreaType.URBAN);
		address.setDetails("Test address details");
		address.setLongitude(46.233);
		address.setLatitude(26.533);
		address.setLatLonAccuracy(10F);

		return creator.createPerson("James", "Smith", Sex.MALE, 1980, 1, 1, p -> {
			p.setAddress(address);
			p.setPresentCondition(PresentCondition.BURIED);
			p.setBurialPlaceDescription("Burial place desc");
		});
	}

	private void assertNotPseudonymized(CaseDataDto caze) {
		assertThat(caze.isPseudonymized(), is(false));
		assertThat(caze.getRegion(), is(rdcf2.region));
		assertThat(caze.getDistrict(), is(rdcf2.district));
		assertThat(caze.getCommunity(), is(rdcf2.community));
		assertThat(caze.getHealthFacility(), is(rdcf2.facility));
		assertThat(caze.getHealthFacilityDetails(), is("Test Facility details"));
		assertThat(caze.getPointOfEntry(), is(rdcf2.pointOfEntry));
		assertThat(caze.getPointOfEntryDetails(), is("Test point of entry details"));
		assertThat(caze.getPerson().getFirstName(), is("James"));
		assertThat(caze.getPerson().getLastName(), is("Smith"));

		//sensitive data
		assertThat(caze.getReportingUser().getUuid(), is(user2.getUuid()));
		assertThat(caze.getSurveillanceOfficer().getUuid(), is(user2.getUuid()));
		assertThat(caze.getClassificationUser().getUuid(), is(user2.getUuid()));

		assertThat(caze.getReportLat(), is(43.2344));
		assertThat(caze.getReportLon(), is(26.6422));
		assertThat(caze.getReportLatLonAccuracy(), is(10F));
	}

	private void assertPseudonymized(CaseDataDto caze) {
		assertThat(caze.isPseudonymized(), is(true));
		assertThat(caze.getRegion(), is(rdcf1.region));
		assertThat(caze.getDistrict(), is(rdcf1.district));
		assertThat(caze.getCommunity(), is(nullValue()));
		assertThat(caze.getHealthFacility(), is(nullValue()));
		assertThat(caze.getHealthFacilityDetails(), is(isEmptyString()));
		assertThat(caze.getPointOfEntry(), is(nullValue()));
		assertThat(caze.getPointOfEntryDetails(), is(isEmptyString()));
		assertThat(caze.getPerson().getFirstName(), is(isEmptyString()));
		assertThat(caze.getPerson().getLastName(), is(isEmptyString()));

		//sensitive data
		assertThat(caze.getReportingUser(), is(nullValue()));
		assertThat(caze.getSurveillanceOfficer(), is(nullValue()));
		assertThat(caze.getClassificationUser(), is(nullValue()));

		assertThat(caze.getReportLat(), is(nullValue()));
		assertThat(caze.getReportLon(), is(nullValue()));
		assertThat(caze.getReportLatLonAccuracy(), is(nullValue()));
	}

	private void updateCase(CaseDataDto caze, UserDto user) {
		caze.setCommunity(rdcf2NewCommunity.toReference());
		caze.setHealthFacility(rdcf2NewFacility.toReference());
		caze.setHealthFacilityDetails("New HF details");
		caze.setPointOfEntry(new PointOfEntryReferenceDto(rdcf2NewPointOfEntry.getUuid()));
		caze.setPointOfEntryDetails("New PoE detail");

		//sensitive data
		caze.setReportingUser(user.toReference());
		caze.setSurveillanceOfficer(user.toReference());
		caze.setClassificationUser(user.toReference());

		caze.setReportLat(23.234);
		caze.setReportLon(43.432);
		caze.setReportLatLonAccuracy(20F);

		getCaseFacade().saveCase(caze);
	}

	private void assertPseudonymizedDataUpdated(CaseDataDto caze) {
		Case savedCase = getCaseService().getByUuid(caze.getUuid());
		assertThat(savedCase.getCommunity().getUuid(), is(rdcf2NewCommunity.getUuid()));
		assertThat(savedCase.getHealthFacility().getUuid(), is(rdcf2NewFacility.getUuid()));
		assertThat(savedCase.getHealthFacilityDetails(), is("New HF details"));
		assertThat(savedCase.getPointOfEntry().getUuid(), is(rdcf2NewPointOfEntry.getUuid()));
		assertThat(savedCase.getPointOfEntryDetails(), is("New PoE detail"));

		//sensitive data
		assertThat(caze.getReportingUser(), is(user1));
		assertThat(caze.getSurveillanceOfficer(), is(user1));
		assertThat(caze.getClassificationUser(), is(user1));

		assertThat(caze.getReportLat(), is(23.234));
		assertThat(caze.getReportLon(), is(43.432));
		assertThat(caze.getReportLatLonAccuracy(), is(20F));
	}

	private void assertPseudonymizedDataNotUpdated(CaseDataDto caze, TestDataCreator.RDCF rdfc, UserDto user) {
		Case savedCase = getCaseService().getByUuid(caze.getUuid());
		assertThat(savedCase.getCommunity().getUuid(), is(rdfc.community.getUuid()));
		assertThat(savedCase.getHealthFacility().getUuid(), is(rdfc.facility.getUuid()));
		assertThat(savedCase.getHealthFacilityDetails(), is("Test Facility details"));
		assertThat(savedCase.getPointOfEntry().getUuid(), is(rdfc.pointOfEntry.getUuid()));
		assertThat(savedCase.getPointOfEntryDetails(), is("Test point of entry details"));

		//sensitive data
		assertThat(caze.getReportingUser(), is(user));
		assertThat(caze.getSurveillanceOfficer(), is(user));
		assertThat(caze.getClassificationUser(), is(user));

		assertThat(caze.getReportLat(), is(43.2344));
		assertThat(caze.getReportLon(), is(26.6422));
		assertThat(caze.getReportLatLonAccuracy(), is(10F));
	}
}
