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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleExportDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.feature.FeatureConfiguration;
import de.symeda.sormas.backend.infrastructure.facility.Facility;

public class SampleFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;
	private UserDto labUser;
	private UserDto nationalClinician;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
		labUser = creator.createUser(null, null, null, "Lab", "Off", creator.getUserRoleReference(DefaultUserRole.LAB_USER));
		labUser.setLaboratory(rdcf1.facility);
		getUserFacade().saveUser(labUser, false);

		nationalClinician =
			creator.createUser(null, null, null, null, "National", "Observer", creator.getUserRoleReference(DefaultUserRole.NATIONAL_CLINICIAN));

		loginWith(user2);
	}

	@Test
	public void testGetSampleWithCaseInJurisdiction() {
		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		SampleDto sample = createCaseSample(caze, user2);

		assertNotPseudonymized(getSampleFacade().getSampleByUuid(sample.getUuid()), user2.getUuid());
	}

	@Test
	public void testGetSampleWithCaseOutsideJurisdiction() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		SampleDto sample = createCaseSample(caze, user1);

		assertPseudonymized(getSampleFacade().getSampleByUuid(sample.getUuid()), "Lab");
	}

	@Test
	public void testPseudonymizedGetByUuidWithLimitedUser() throws InterruptedException {

		// deactivate AUTOMATIC_RESPONSIBILITY_ASSIGNMENT in order to assign the limited user to a case from outside jurisdiction
		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.CASE_SURVEILANCE);

		executeInTransaction(em -> {
			Query query = em.createQuery("select f from featureconfiguration f");
			FeatureConfiguration singleResult = (FeatureConfiguration) query.getSingleResult();
			HashMap<FeatureTypeProperty, Object> properties = new HashMap<>();
			properties.put(FeatureTypeProperty.AUTOMATIC_RESPONSIBILITY_ASSIGNMENT, false);
			singleResult.setProperties(properties);
			em.persist(singleResult);
		});

		// case and sample within limited user's jurisdiction
		CaseDataDto caze1 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		SampleDto sample1 = createCaseSample(caze1, user1);

		// case and sample outside limited user's jurisdiction
		CaseDataDto caze2 = creator.createCase(user2.toReference(), creator.createPerson("Max", "Mustermann").toReference(), rdcf2);
		SampleDto sample2 = createCaseSample(caze2, user2);

		loginWith(nationalAdmin);
		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf1);

		//case and sample created by limited user within limited user's  jurisdiction
		CaseDataDto caze3 = creator.createCase(
			surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference(),
			creator.createPerson("Max", "Mustermann").toReference(),
			rdcf2);
		SampleDto sample3 = createCaseSample(caze3, surveillanceOfficerWithRestrictedAccessToAssignedEntities);

		//case and sample created by limited user outside limited user's jurisdiction
		CaseDataDto caze4 = creator.createCase(
			surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference(),
			creator.createPerson("Max", "Mustermann").toReference(),
			rdcf2);
		SampleDto sample4 = createCaseSample(caze4, surveillanceOfficerWithRestrictedAccessToAssignedEntities);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertTrue(getCurrentUserService().isRestrictedToAssignedEntities());
		final SampleDto testSample1 = getSampleFacade().getSampleByUuid(sample1.getUuid());
		final SampleDto testSample2 = getSampleFacade().getSampleByUuid(sample2.getUuid());
		assertThat(testSample1.isPseudonymized(), is(true));
		assertThat(testSample1.getComment(), is(emptyString()));
		assertThat(testSample2.isPseudonymized(), is(true));
		assertThat(testSample2.getComment(), is(emptyString()));

		loginWith(nationalAdmin);
		final CaseDataDto testCase1 = getCaseFacade().getCaseDataByUuid(caze1.getUuid());
		testCase1.setSurveillanceOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getCaseFacade().save(testCase1);
		final CaseDataDto testCase2 = getCaseFacade().getCaseDataByUuid(caze2.getUuid());
		testCase2.setSurveillanceOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getCaseFacade().save(testCase2);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		final SampleDto returnedTestSample1 = getSampleFacade().getSampleByUuid(sample1.getUuid());
		assertThat(returnedTestSample1.isPseudonymized(), is(false));
		assertThat(returnedTestSample1.getComment(), is("Test comment"));

		final SampleDto returnedTestSample2 = getSampleFacade().getSampleByUuid(sample2.getUuid());
		assertThat(returnedTestSample2.isPseudonymized(), is(false));
		assertThat(returnedTestSample2.getComment(), is("Test comment"));

		final SampleDto returnedTestSample3 = getSampleFacade().getSampleByUuid(sample3.getUuid());
		assertThat(returnedTestSample3.isPseudonymized(), is(false));
		assertThat(returnedTestSample3.getComment(), is("Test comment"));

		final SampleDto returnedTestSample4 = getSampleFacade().getSampleByUuid(sample4.getUuid());
		assertThat(returnedTestSample4.isPseudonymized(), is(false));
		assertThat(returnedTestSample4.getComment(), is("Test comment"));
	}

	@Test
	public void testGetSampleWithLabUserOfSampleLab() {
		loginWith(user1);
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		SampleDto sample = createCaseSample(caze, user1, rdcf1.facility);
		loginWith(labUser);

		SampleDto sampleByUuid = getSampleFacade().getSampleByUuid(sample.getUuid());
		assertThat(sampleByUuid.getAssociatedCase().getFirstName(), is("John"));
		assertThat(sampleByUuid.getAssociatedCase().getLastName(), is("Smith"));
		assertNull(sampleByUuid.getReportingUser()); // user is not a lab level user and not in the same lab so the lab user does not see it - is this correct ?
		assertThat(sampleByUuid.getReportLat(), is(46.432));
		assertThat(sampleByUuid.getReportLon(), is(23.234));
		assertThat(sampleByUuid.getReportLatLonAccuracy(), is(10f));
		assertThat(sampleByUuid.getLab(), is(notNullValue()));
		assertThat(sampleByUuid.getLabDetails(), is("Test lab details"));
		assertThat(sampleByUuid.getShipmentDetails(), is("Test shipment details"));
		assertThat(sampleByUuid.getComment(), is("Test comment"));
	}

	@Test
	public void testGetSampleWithLabUserNotOfSampleLab() {
		loginWith(user2);
		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		SampleDto sample = createCaseSample(caze, user2, rdcf2.facility);
		loginWith(labUser);
		assertPseudonymized(getSampleFacade().getSampleByUuid(sample.getUuid()), rdcf2.facility.getCaption());
	}

	@Test
	public void testGetSampleWithLabUserCreatedBySameLabUser() {
		loginWith(user2);
		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		loginWith(labUser);
		SampleDto sample = createCaseSample(caze, labUser, rdcf1.facility);
		assertNotPseudonymized(getSampleFacade().getSampleByUuid(sample.getUuid()), labUser.getUuid());
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
		assertThat(savedSample.getAssociatedContact().getCaption(), containsString("James SMITH to case John Doe"));
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
		assertThat(index3.getAssociatedContact().getCaption(), containsString("John SMITH"));

		SampleIndexDto index4 = indexList.stream().filter(t -> t.getUuid().equals(sample4.getUuid())).findFirst().get();
		assertThat(index4.getAssociatedContact().getCaption(), is(DataHelper.getShortUuid(index4.getAssociatedContact().getUuid())));
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

		List<SampleExportDto> exportList = getSampleFacade().getExportList(new SampleCriteria(), Collections.emptySet(), 0, 100);
		SampleExportDto export1 = exportList.stream().filter(t -> t.getUuid().equals(sample1.getUuid())).findFirst().get();
		assertThat(export1.getSampleAssociatedCase().getFirstName(), is("John"));
		assertThat(export1.getSampleAssociatedCase().getLastName(), is("Smith"));
		assertThat(export1.getLab(), is("Lab - Test lab details"));
		assertThat(export1.getPathogenTestLab1(), is("Lab - Test lab details"));
		assertThat(export1.getPathogenTestType1(), is("Test type text"));

		SampleExportDto export2 = exportList.stream().filter(t -> t.getUuid().equals(sample2.getUuid())).findFirst().get();
		assertThat(export2.getSampleAssociatedCase().getFirstName(), is("Confidential"));
		assertThat(export2.getSampleAssociatedCase().getLastName(), is("Confidential"));
		assertThat(export2.getLab(), is("Lab - Test lab details"));
		assertThat(export2.getPathogenTestLab1(), is("Lab - Test lab details"));
		assertThat(export2.getPathogenTestType1(), is("Confidential"));
		assertThat(export2.getPathogenTestLab2(), is("Lab - Test lab details"));
		assertThat(export2.getPathogenTestType2(), is("Confidential"));
		assertThat(export2.getPathogenTestLab3(), is("Lab - Test lab details"));
		assertThat(export2.getPathogenTestType3(), is("Confidential"));
		assertThat(export2.getOtherPathogenTestsDetails(), is("2020-06-10 (Confidential, COVID-19, Pending)"));

		SampleExportDto export3 = exportList.stream().filter(t -> t.getUuid().equals(sample3.getUuid())).findFirst().get();
		assertThat(export3.getAssociatedContact().getContactName().getFirstName(), is("John"));
		assertThat(export3.getAssociatedContact().getContactName().getLastName(), is("Smith"));
		assertThat(export3.getLab(), is("Lab"));

		SampleExportDto export4 = exportList.stream().filter(t -> t.getUuid().equals(sample4.getUuid())).findFirst().get();
		assertThat(export4.getAssociatedContact().getContactName().getFirstName(), is("Confidential"));
		assertThat(export4.getAssociatedContact().getContactName().getLastName(), is("Confidential"));
		assertThat(export4.getLab(), is("Lab"));
	}

	@Test
	public void testGetSampleOfCaseWithSpecialAccess() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		creator.createSpecialCaseAccess(caze.toReference(), user1.toReference(), user2.toReference(), DateHelper.addDays(new Date(), 1));
		SampleDto sample = createCaseSample(caze, user1);

		SampleDto sampleByUuid = getSampleFacade().getSampleByUuid(sample.getUuid());
		assertNotPseudonymized(sampleByUuid, user1.getUuid());
		assertNotPseudonymized(getSampleFacade().getByUuids(Collections.singletonList(sample.getUuid())).get(0), user1.getUuid());
		assertThat(getSampleFacade().getIndexList(new SampleCriteria(), null, null, null).get(0).isPseudonymized(), is(false));
		assertThat(getSampleFacade().getExportList(new SampleCriteria(), null, 0, Integer.MAX_VALUE).get(0).getShipmentDetails(), is("Test shipment details"));
	}

	private void createPathogenTest(SampleDto sample, UserDto user) {
		Date testDateTime = new Date(1591747200000L);//2020-06-10
		creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.ISOLATION,
			Disease.CORONAVIRUS,
			testDateTime,
			sample.getLab(),
			user.toReference(),
			PathogenTestResultType.PENDING,
			"",
			true,
			t -> {
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
		assertNotPseudonymized(active1, user2.getUuid());

		SampleDto active2 = activeSamples.stream().filter(t -> t.getUuid().equals(sample2.getUuid())).findFirst().get();
		assertPseudonymized(active2, "Lab");

		// case samples not yet implemented
		Optional<SampleDto> active3 = activeSamples.stream().filter(t -> t.getUuid().equals(sample3.getUuid())).findFirst();
		assertThat(active3.isPresent(), is(true));

		Optional<SampleDto> active4 = activeSamples.stream().filter(t -> t.getUuid().equals(sample4.getUuid())).findFirst();
		assertThat(active4.isPresent(), is(true));
	}

	@Test
	public void testUpdatePseudonymizedSample() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		SampleDto sample = createCaseSample(caze, user1);

		loginWith(nationalClinician);

		sample.setReportLat(null);
		sample.setReportLon(null);
		sample.setReportLatLonAccuracy(20F);
		sample.setLab(rdcf1.facility);

		getSampleFacade().saveSample(sample);

		Sample updatedSample = getSampleService().getByUuid(sample.getUuid());

		assertThat(updatedSample.getReportLat(), is(46.432));
		assertThat(updatedSample.getReportLon(), is(23.234));
		assertThat(updatedSample.getReportLatLonAccuracy(), is(20F));
		assertThat(updatedSample.getLab().getUuid(), is(rdcf1.facility.getUuid()));
	}

	@Test
	public void testUpdateSampleInJurisdictionWithPseudonymizedDto() {
		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		SampleDto sample = createCaseSample(caze, user2);

		sample.setPseudonymized(true);
		sample.setReportLat(null);
		sample.setReportLon(null);
		sample.setReportLatLonAccuracy(20F);
		sample.setLab(rdcf2.facility);

		getSampleFacade().saveSample(sample);

		Sample updatedSample = getSampleService().getByUuid(sample.getUuid());

		assertThat(updatedSample.getReportLat(), is(46.432));
		assertThat(updatedSample.getReportLon(), is(23.234));
		assertThat(updatedSample.getReportLatLonAccuracy(), is(20F));
		assertThat(updatedSample.getLab().getUuid(), is(rdcf2.facility.getUuid()));
	}

	private SampleDto createCaseSample(CaseDataDto caze, UserDto reportingUser) {
		Facility lab = new Facility();
		lab.setName("Lab");
		getFacilityService().persist(lab);

		return creator.createSample(caze.toReference(), reportingUser.toReference(), lab, s -> {
			s.setReportLat(46.432);
			s.setReportLon(23.234);
			s.setReportLatLonAccuracy(10f);
			s.setLabDetails("Test lab details");
			s.setShipmentDetails("Test shipment details");
			s.setComment("Test comment");
		});
	}

	private SampleDto createCaseSample(CaseDataDto caze, UserDto reportingUser, FacilityReferenceDto lab) {
		return creator.createSample(caze.toReference(), reportingUser.toReference(), lab, s -> {
			s.setReportLat(46.432);
			s.setReportLon(23.234);
			s.setReportLatLonAccuracy(10f);
			s.setLabDetails("Test lab details");
			s.setShipmentDetails("Test shipment details");
			s.setComment("Test comment");
		});
	}

	private SampleDto createContactSample(ContactDto contactDto) {
		Facility lab = new Facility();
		lab.setName("Lab");
		getFacilityService().persist(lab);

		return creator.createSample(contactDto.toReference(), new Date(), new Date(), user1.toReference(), SampleMaterial.BLOOD, lab);
	}

	private void assertNotPseudonymized(SampleDto sample, String reportingUserUuid) {
		assertThat(sample.getAssociatedCase().getFirstName(), is("John"));
		assertThat(sample.getAssociatedCase().getLastName(), is("Smith"));

		//sensitive data
		assertThat(sample.getReportingUser().getUuid(), is(reportingUserUuid));
		assertThat(sample.getReportLat(), is(46.432));
		assertThat(sample.getReportLon(), is(23.234));
		assertThat(sample.getReportLatLonAccuracy(), is(10f));
		assertThat(sample.getLab(), is(notNullValue()));
		assertThat(sample.getLabDetails(), is("Test lab details"));
		assertThat(sample.getShipmentDetails(), is("Test shipment details"));
		assertThat(sample.getComment(), is("Test comment"));
	}

	private void assertPseudonymized(SampleDto sample, String labName) {
		assertThat(sample.getAssociatedCase().getFirstName(), isEmptyString());
		assertThat(sample.getAssociatedCase().getLastName(), isEmptyString());

		//sensitive data
		assertThat(sample.getReportingUser(), is(nullValue()));
		assertThat(sample.getReportLat(), is(nullValue()));
		assertThat(sample.getReportLon(), is(nullValue()));
		assertThat(sample.getReportLatLonAccuracy(), is(10F));
		assertThat(sample.getLab().getCaption(), is(labName));
		assertThat(sample.getLabDetails(), isEmptyString());
		assertThat(sample.getShipmentDetails(), isEmptyString());
		assertThat(sample.getComment(), isEmptyString());
	}

}
