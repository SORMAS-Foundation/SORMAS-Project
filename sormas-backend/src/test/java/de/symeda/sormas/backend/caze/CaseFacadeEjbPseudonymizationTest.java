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
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.CaseFollowUpDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class CaseFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private CommunityDto rdcf2NewCommunity;
	private FacilityDto rdcf2NewFacility;
	private PointOfEntryDto rdcf2NewPointOfEntry;
	private UserDto user1;
	private UserDto user2;
	private UserDto nationalClinician;

	@Override
	public void init() {

		super.init();

		UserRoleReferenceDto newUserRole = creator.createUserRole(
			"NoEventNoCaseView",
			JurisdictionLevel.DISTRICT,
			UserRight.CASE_CLINICIAN_VIEW,
			UserRight.CASE_VIEW,
			UserRight.PERSON_VIEW);

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER),
			newUserRole);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER),
			newUserRole);
		rdcf2NewCommunity = creator.createCommunity("New community", rdcf2.district);
		rdcf2NewFacility = creator.createFacility("New facility", rdcf2.region, rdcf2.district, rdcf2NewCommunity.toReference());
		rdcf2NewPointOfEntry = creator.createPointOfEntry("New point of entry", rdcf2.region, rdcf2.district);

		nationalClinician =
			creator.createUser(null, null, null, null, "National", "Clinician", creator.getUserRoleReference(DefaultUserRole.NATIONAL_CLINICIAN));

		loginWith(user2);
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
		List<CaseDataDto> cases = getCaseFacade().getAllAfter(calendar.getTime());

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
	public void testPseudonymizeCaseIndexData() {

		CaseDataDto caze1 = createCase(rdcf1, user1);
		// create contact in current jurisdiction to have access on pseudonymized case
		creator.createContact(user2.toReference(), createPerson().toReference(), caze1);
		CaseDataDto caze2 = createCase(rdcf2, user2);

		CaseCriteria criteria = new CaseCriteria();
		criteria.setIncludeCasesFromOtherJurisdictions(true);
		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(criteria, null, null, Collections.emptyList());

		CaseIndexDto caseIndex1 = indexList.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get();

		assertThat(caseIndex1.getPersonFirstName(), is("Confidential"));
		assertThat(caseIndex1.getPersonLastName(), is("Confidential"));
		assertThat(caseIndex1.getHealthFacilityName(), is("Confidential"));
		assertThat(caseIndex1.getPointOfEntryName(), is("Confidential"));

		CaseIndexDto caseIndex2 = indexList.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get();
		assertThat(caseIndex2.getPersonFirstName(), is("James"));
		assertThat(caseIndex2.getPersonLastName(), is("Smith"));
		assertThat(caseIndex2.getHealthFacilityName(), is("Facility 2 - Test Facility details"));
		assertThat(caseIndex2.getPointOfEntryName(), is("Point of entry 2 - Test point of entry details"));
	}

	@Test
	public void testPseudonymizeCaseExportData() {

		CaseDataDto caze1 = createCase(rdcf1, user1);
		// create contact in current jurisdiction to have access on pseudonymized case
		creator.createContact(user2.toReference(), createPerson().toReference(), caze1);
		Date sampleDate = new Date(1591747200000L);//2020-06-10
		creator.createSample(caze1.toReference(), sampleDate, sampleDate, user1.toReference(), SampleMaterial.BLOOD, rdcf1.facility);

		CaseDataDto caze2 = createCase(rdcf2, user2);
		creator.createSample(caze2.toReference(), sampleDate, sampleDate, user2.toReference(), SampleMaterial.BLOOD, rdcf2.facility);

		List<CaseExportDto> exportList =
			getCaseFacade().getExportList(new CaseCriteria(), Collections.emptySet(), CaseExportType.CASE_SURVEILLANCE, 0, 100, null, Language.EN);

		CaseExportDto caseIndex1 = exportList.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get();

		assertThat(caseIndex1.getCommunity(), is("Confidential"));
		assertThat(caseIndex1.getHealthFacility(), is("Confidential"));
		assertThat(caseIndex1.getHealthFacilityDetails(), is("Confidential"));
		assertThat(caseIndex1.getPointOfEntry(), is("Confidential"));
		assertThat(caseIndex1.getPointOfEntryDetails(), is("Confidential"));
		assertThat(caseIndex1.getFirstName(), is("Confidential"));
		assertThat(caseIndex1.getLastName(), is("Confidential"));
		assertThat(caseIndex1.getStreet(), is("Confidential"));
		assertThat(caseIndex1.getHouseNumber(), is("Confidential"));
		assertThat(caseIndex1.getAdditionalInformation(), is("Confidential"));
		assertThat(caseIndex1.getPostalCode(), is("123"));
		assertThat(caseIndex1.getAddressGpsCoordinates(), is("Confidential"));
		assertThat(caseIndex1.getBurialInfo().getBurialPlaceDescription(), is("Confidential"));
		assertThat(caseIndex1.getSample1().formatString(), is("2020-06-10 (Confidential, Pending)"));

		CaseExportDto caseIndex2 = exportList.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get();
		assertThat(caseIndex2.getCommunity(), is(rdcf2.community.getCaption()));
		assertThat(caseIndex2.getHealthFacility(), is("Facility 2"));
		assertThat(caseIndex2.getHealthFacilityDetails(), is("Test Facility details"));
		assertThat(caseIndex2.getPointOfEntry(), is("Point of entry 2"));
		assertThat(caseIndex2.getPointOfEntryDetails(), is("Test point of entry details"));
		assertThat(caseIndex2.getFirstName(), is("James"));
		assertThat(caseIndex2.getLastName(), is("Smith"));
		assertThat(caseIndex2.getStreet(), is("Test street"));
		assertThat(caseIndex2.getHouseNumber(), is("Test number"));
		assertThat(caseIndex2.getAdditionalInformation(), is("Test information"));
		assertThat(caseIndex2.getPostalCode(), is("12345"));
		assertThat(caseIndex2.getAddressGpsCoordinates(), is("26.533, 46.233 +-10m"));
		assertThat(caseIndex2.getBurialInfo().getBurialPlaceDescription(), is("Burial place desc"));
		assertThat(caseIndex2.getSample1().formatString(), is("2020-06-10 (Facility 2, Pending)"));
	}

	@Test
	public void testUpdateCaseInJurisdiction() {

		CaseDataDto caze = createCase(rdcf2, user2);
		updateCase(caze, user1);
		assertPseudonymizedDataUpdated(caze);
	}

	@Test
	public void testUpdatePseudonymizedCase() {

		loginWith(user1);
		CaseDataDto caze = createCase(rdcf1, user1);
		loginWith(nationalClinician);
		updateCase(caze, nationalClinician);
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

		caze.setFollowUpComment(null);

		getCaseFacade().save(caze);

		assertPseudonymizedDataNotUpdated(caze, rdcf2, user2);
	}

	@Test
	public void testPseudonymizeGetFollowupList() {
		CaseDataDto caze1 = createCase(rdcf1, user1);

		ContactDto contact1 = createContact(user2, caze1, rdcf1);
		ContactDto contact2 = createContact(user2, caze1, rdcf1);
		assertThat(contact1.getUuid(), not(is(contact2.getUuid())));
		// case in other jurisdiction, but visible since linked via contacts reported by user
		// --> should be pseudonymized

		CaseDataDto caze2 = createCase(rdcf2, user2);

		CaseDataDto caze3 = createCase(rdcf2, user1);

		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.setIncludeCasesFromOtherJurisdictions(true);

		List<CaseFollowUpDto> caseFollowUpList = getCaseFacade().getCaseFollowUpList(caseCriteria, new Date(), 10, 0, 100, Collections.emptyList());
		assertThat(caseFollowUpList.size(), is(3));

		CaseFollowUpDto followup1 = caseFollowUpList.stream().filter(c -> c.getUuid().equals(caze1.getUuid())).findFirst().get();
		assertThat(followup1.getFirstName(), is("Confidential"));
		assertThat(followup1.getLastName(), is("Confidential"));

		CaseFollowUpDto followup2 = caseFollowUpList.stream().filter(c -> c.getUuid().equals(caze2.getUuid())).findFirst().get();
		assertThat(followup2.getFirstName(), is("James"));
		assertThat(followup2.getLastName(), is("Smith"));

		CaseFollowUpDto followup3 = caseFollowUpList.stream().filter(c -> c.getUuid().equals(caze3.getUuid())).findFirst().get();
		assertThat(followup3.getFirstName(), is("James"));
		assertThat(followup3.getLastName(), is("Smith"));
	}

	@Test
	public void testPseudonymizeGpsCoordinates() {
		CaseDataDto caze = creator.createCase(
			user1.toReference(),
			createPerson().toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.NOT_CLASSIFIED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf1,
			(c) -> {
				c.setReportingUser(user1.toReference());

				c.setReportLat(46.432);
				c.setReportLon(23.234);
				c.setReportLatLonAccuracy(10F);
			});

		CaseDataDto pseudonymizedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(pseudonymizedCase.getReportLat(), is(not(caze.getReportLat())));
		assertThat(pseudonymizedCase.getReportLat().toString(), startsWith("46."));
		assertThat(pseudonymizedCase.getReportLon(), is(not(caze.getReportLon())));
		assertThat(pseudonymizedCase.getReportLon().toString(), startsWith("23."));

		assertThat(pseudonymizedCase.getReportLatLonAccuracy(), is(caze.getReportLatLonAccuracy()));
	}

	@Test
	public void testUpdateGpsCoordinatesWithPseudonymizedData() {
		CaseDataDto caze = creator.createCase(
			user2.toReference(),
			createPerson().toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.NOT_CLASSIFIED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf2,
			(c) -> {
				c.setReportingUser(user2.toReference());

				c.setReportLat(46.432);
				c.setReportLon(23.234);
				c.setReportLatLonAccuracy(10F);
			});

		caze.setPseudonymized(true);
		caze.setReportLat(44.432);
		caze.setReportLon(22.234);

		CaseDataDto savedCase = getCaseFacade().save(caze);

		/**
		 * Expected to save the updated data because, it is a really rare edge case that is not handled at the moment.
		 * Probably won't be a need to handle it.
		 * 
		 * @see de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer#isValuePseudonymized(Double)
		 *      and
		 * @see de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer#isValuePseudonymized(Double)
		 */
		assertThat(savedCase.getReportLat(), is(44.432));
		assertThat(savedCase.getReportLon(), is(22.234));
	}

	@Test
	public void testSpecialCaseAccessOutsideJurisdiction() {

		CaseDataDto caze = createCase(rdcf1, user1);
		creator.createSpecialCaseAccess(caze.toReference(), user1.toReference(), user2.toReference(), DateHelper.addDays(new Date(), 1));

		assertNotPseudonymized(getCaseFacade().getCaseDataByUuid(caze.getUuid()), rdcf1, user1);
		assertNotPseudonymized(getCaseFacade().getByUuid(caze.getUuid()), rdcf1, user1);
		assertNotPseudonymized(getCaseFacade().getByUuids(Collections.singletonList(caze.getUuid())).get(0), rdcf1, user1);
		assertNotPseudonymized(getCaseFacade().getAllAfter(new Date(0)).get(0), rdcf1, user1);
		assertThat(getCaseFacade().getIndexList(new CaseCriteria(), null, null, null).get(0).isPseudonymized(), is(false));
		assertThat(getCaseFacade().getExportList(new CaseCriteria(), null, null, 0, Integer.MAX_VALUE, null, Language.EN).get(0).getHealthFacilityDetails(), is("Test Facility details"));
	}

	private CaseDataDto createCase(TestDataCreator.RDCF rdcf, UserDto reportingUser) {
		return createCase(rdcf, createPerson().toReference(), reportingUser);
	}

	private CaseDataDto createCase(TestDataCreator.RDCF rdcf, PersonReferenceDto person, UserDto reportingUser) {

		return creator.createCase(
			user1.toReference(),
			person,
			Disease.CORONAVIRUS,
			CaseClassification.NOT_CLASSIFIED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			(c) -> {
				c.setRegion(rdcf.region);
				c.setDistrict(rdcf.district);
				c.setCommunity(rdcf.community);
				c.setReportingUser(reportingUser.toReference());
				c.setClassificationUser(reportingUser.toReference());
				c.setSurveillanceOfficer(reportingUser.toReference());

				c.setHealthFacilityDetails("Test Facility details");
				c.setPointOfEntryDetails("Test point of entry details");

				c.setFollowUpComment("Test comment");
			});
	}

	private ContactDto createContact(UserDto reportingUser, CaseDataDto caze, TestDataCreator.RDCF rdcf) {
		return creator.createContact(
			reportingUser.toReference(),
			reportingUser.toReference(),
			createPerson().toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf,
			c -> {
				c.setResultingCaseUser(reportingUser.toReference());

//				c.setReportLat(46.432);
//				c.setReportLon(23.234);
//				c.setReportLatLonAccuracy(10F);
				c.setFollowUpComment("Test comment");
			});
	}

	private PersonDto createPerson() {
		LocationDto address = LocationDto.build();
		address.setRegion(rdcf1.region);
		address.setDistrict(rdcf1.district);
		address.setCommunity(rdcf1.community);
		address.setCity("Test City");
		address.setStreet("Test street");
		address.setHouseNumber("Test number");
		address.setAdditionalInformation("Test information");
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
		assertNotPseudonymized(caze, rdcf2, user2);
	}

	private void assertNotPseudonymized(CaseDataDto caze, TestDataCreator.RDCF rdfc, UserDto user) {
		assertThat(caze.isPseudonymized(), is(false));
		assertThat(caze.getResponsibleRegion(), is(rdfc.region));
		assertThat(caze.getResponsibleDistrict(), is(rdfc.district));
		assertThat(caze.getResponsibleCommunity(), is(rdfc.community));
		assertThat(caze.getRegion(), is(rdfc.region));
		assertThat(caze.getDistrict(), is(rdfc.district));
		assertThat(caze.getCommunity(), is(rdfc.community));
		assertThat(caze.getHealthFacility(), is(rdfc.facility));
		assertThat(caze.getHealthFacilityDetails(), is("Test Facility details"));
		assertThat(caze.getPointOfEntry(), is(rdfc.pointOfEntry));
		assertThat(caze.getPointOfEntryDetails(), is("Test point of entry details"));
		assertThat(caze.getPerson().getFirstName(), is("James"));
		assertThat(caze.getPerson().getLastName(), is("Smith"));

		//sensitive data
		assertThat(caze.getReportingUser().getUuid(), is(user.getUuid()));
		assertThat(caze.getSurveillanceOfficer().getUuid(), is(user.getUuid()));
		assertThat(caze.getClassificationUser().getUuid(), is(user.getUuid()));

		assertThat(caze.getFollowUpComment(), is("Test comment"));
//		assertThat(caze.getReportLat(), is(46.432));
//		assertThat(caze.getReportLon(), is(23.234));
//		assertThat(caze.getReportLatLonAccuracy(), is(10F));
	}

	private void assertPseudonymized(CaseDataDto caze) {
		assertThat(caze.isPseudonymized(), is(true));
		assertThat(caze.getResponsibleRegion(), is(rdcf1.region));
		assertThat(caze.getResponsibleDistrict(), is(rdcf1.district));
		assertThat(caze.getResponsibleCommunity(), is(nullValue()));
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

		assertThat(caze.getFollowUpComment(), is(emptyString()));
	}

	private void updateCase(CaseDataDto caze, UserDto user) {
		caze.setCommunity(rdcf2NewCommunity.toReference());
		caze.setHealthFacility(rdcf2NewFacility.toReference());
		caze.setHealthFacilityDetails("New HF details");
		caze.setPointOfEntry(new PointOfEntryReferenceDto(rdcf2NewPointOfEntry.getUuid(), null, null, null));
		caze.setPointOfEntryDetails("New PoE detail");

		//sensitive data
		caze.setReportingUser(user.toReference());
		caze.setSurveillanceOfficer(user.toReference());
		caze.setClassificationUser(user.toReference());

		caze.setFollowUpComment("Updated Comment");

		caze.setReportLat(46.432);
		caze.setReportLon(23.234);
		caze.setReportLatLonAccuracy(20F);

		getCaseFacade().save(caze);
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

		caze.setFollowUpComment("Updated Comment");

		assertThat(caze.getReportLat(), is(46.432));
		assertThat(caze.getReportLon(), is(23.234));
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

		assertThat(caze.getFollowUpComment(), is("Test comment"));
//		assertThat(caze.getReportLat(), is(46.432));
//		assertThat(caze.getReportLon(), is(23.234));
//		assertThat(caze.getReportLatLonAccuracy(), is(20F));
	}
}
