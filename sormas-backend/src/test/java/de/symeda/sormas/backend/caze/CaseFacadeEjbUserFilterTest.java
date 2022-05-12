/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.caze;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.RequestContextTO;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.feature.FeatureConfiguration;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.infrastructure.facility.Facility;

@RunWith(MockitoJUnitRunner.class)
public class CaseFacadeEjbUserFilterTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;

	private UserDto districtUser1;
	private UserDto districtUser11;
	private UserDto districtUser12;
	private UserDto districtUser2;
	private UserDto nationalUser;
	private UserDto regionUser;
	private UserDto communityUser;
	private UserDto facilityUser;
	private UserDto labUser;

	@Override
	public void init() {

		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		districtUser1 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);
		districtUser11 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off11", UserRole.SURVEILLANCE_OFFICER);
		districtUser12 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off12", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		districtUser2 = creator
			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);

		nationalUser = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Nat",
			"User2",
			UserRole.NATIONAL_USER);
		regionUser = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Sup2",
			UserRole.SURVEILLANCE_SUPERVISOR);
		communityUser = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Com",
			"Off2",
			UserRole.COMMUNITY_OFFICER);
		facilityUser = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.community.getUuid(),
			rdcf2.facility.getUuid(),
			"Hosp",
			"Inf2",
			UserRole.HOSPITAL_INFORMANT);

		labUser = creator.createUser(null, null, null, "Lab", "Off", UserRole.LAB_USER);
		labUser.setLaboratory(rdcf1.facility);
		getUserFacade().saveUser(labUser);

	}

	@Test
	public void testGetCasesWithLimitedSyncronization() {

		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.LIMITED_SYNCHRONIZATION);

		SessionImpl em = (SessionImpl) getEntityManager();
		QueryImplementor query = em.createQuery("select f from featureconfiguration f");
		FeatureConfiguration singleResult = (FeatureConfiguration) query.getSingleResult();
		HashMap<FeatureTypeProperty, Object> properties = new HashMap<>();
		properties.put(FeatureTypeProperty.EXCLUDE_NO_CASE_CLASSIFIED_CASES, true);
		singleResult.setProperties(properties);
		em.save(singleResult);

		RequestContextHolder.setRequestContext(new RequestContextTO(true)); // simulate mobile call

		loginWith(districtUser1);

		CaseDataDto case1 = createCase(rdcf1, districtUser1);
		case1.setCreationVersion("1.70");
		case1.setCaseClassification(CaseClassification.CONFIRMED);
		getCaseFacade().save(case1);

		CaseDataDto case2 = createCase(rdcf1, districtUser1);
		case2.setCreationVersion("1.70");
		case2.setCaseClassification(CaseClassification.NO_CASE);
		getCaseFacade().save(case2);
		CaseDataDto case3 = createCase(rdcf1, districtUser1);

		loginWith(districtUser11);
		CaseDataDto case11 = createCase(rdcf1, districtUser11);
		case11.setCaseClassification(CaseClassification.NO_CASE);
		getCaseFacade().save(case11);
		CaseDataDto case12 = createCase(rdcf1, districtUser11);
		case12.setCreationVersion("1.70");
		case12.setCaseClassification(CaseClassification.NO_CASE);
		getCaseFacade().save(case12);
		CaseDataDto case13 = createCase(rdcf1, districtUser11);

		loginWith(districtUser1);

		List<CaseDataDto> allActiveCasesAfter = getCaseFacade().getAllActiveCasesAfter(new DateTime(new Date()).minusDays(1).toDate());
		assertThat(allActiveCasesAfter, hasSize(4));
		Assert.assertFalse(allActiveCasesAfter.contains(case11));
		Assert.assertFalse(allActiveCasesAfter.contains(case12));

		loginWith(districtUser11);

		List<CaseDataDto> allActiveCasesAfter2 = getCaseFacade().getAllActiveCasesAfter(new DateTime(new Date()).minusDays(1).toDate());
		assertThat(allActiveCasesAfter2, hasSize(4));
		Assert.assertFalse(allActiveCasesAfter2.contains(case2));
		Assert.assertFalse(allActiveCasesAfter2.contains(case11));

		loginWith(districtUser12);

		List<CaseDataDto> allActiveCasesAfter3 = getCaseFacade().getAllActiveCasesAfter(new DateTime(new Date()).minusDays(1).toDate());
		assertThat(allActiveCasesAfter3, hasSize(3));
		Assert.assertFalse(allActiveCasesAfter3.contains(case2));
		Assert.assertFalse(allActiveCasesAfter3.contains(case11));
		Assert.assertFalse(allActiveCasesAfter3.contains(case12));
	}

	@Test
	public void testGetOwnedCases() {
		loginWith(districtUser2);

		CaseDataDto ownedCase = createCase(rdcf2, districtUser2);
		createCase(rdcf1, districtUser1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(ownedCase.getUuid()));
	}

	@Test
	public void testGetCasesOnNationalLevel() {
		loginWith(nationalUser);

		createCase(rdcf2, districtUser2);
		createCase(rdcf1, districtUser1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(2));
	}

	@Test
	public void testGetCasesOnRegionLevel() {
		loginWith(regionUser);

		CaseDataDto visibleCase = createCase(rdcf2, districtUser2);
		createCase(rdcf1, districtUser1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(visibleCase.getUuid()));
	}

	@Test
	public void testGetUsersHavingJurisdictionOverCase() {
		loginWith(nationalUser);

		CaseDataDto caze = createCase(rdcf1, districtUser1);

		UserDto inactiveUser = creator.createUser(rdcf1, UserRole.SURVEILLANCE_SUPERVISOR, user -> user.setActive(false));

		List<UserReferenceDto> usersHavingCaseInJurisdiction = getUserFacade().getUsersHavingCaseInJurisdiction(caze.toReference());
		Assert.assertNotNull(usersHavingCaseInJurisdiction);
		Assert.assertEquals(3, usersHavingCaseInJurisdiction.size()); // contains also admin as test admin user is also national user
		Assert.assertTrue(usersHavingCaseInJurisdiction.contains(nationalUser));
		Assert.assertTrue(usersHavingCaseInJurisdiction.contains(districtUser1));
		Assert.assertFalse(usersHavingCaseInJurisdiction.contains(inactiveUser));
	}

	@Test
	public void testGetCasesOnCommunityLevel() {
		loginWith(communityUser);

		CaseDataDto visibleCase = createCase(rdcf2, districtUser2);
		createCase(rdcf1, districtUser1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(visibleCase.getUuid()));
	}

	@Test
	public void testGetCasesOnFacilityLevel() {
		loginWith(facilityUser);

		CaseDataDto visibleCase = createCase(rdcf2, districtUser2);
		createCase(rdcf1, districtUser1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(visibleCase.getUuid()));
	}

	@Test
	public void testGetCasesWithPlaceOfStayOnNationalLevel() {
		loginWith(nationalUser);

		createCase(rdcf1, districtUser1, rdcf2);
		createCase(rdcf1, districtUser1, rdcf1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(2));
	}

	@Test
	public void testGetCasesOnLaboratoryLevel() {
		loginWith(nationalUser);

		CaseDataDto visibleCase = createCase(rdcf1, nationalUser);

		creator.createSample(visibleCase.toReference(), nationalUser.toReference(), rdcf1.facility, s -> {
			s.setReportLat(46.432);
			s.setReportLon(23.234);
			s.setReportLatLonAccuracy(10f);
			s.setLabDetails("Test lab details");
			s.setShipmentDetails("Test shipment details");
			s.setComment("Test comment");
		});

		CaseDataDto aCase = createCase(rdcf2, nationalUser);

		loginWith(labUser);

		CaseDataDto caseDataByUuid = getCaseFacade().getCaseDataByUuid(aCase.getUuid());

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(visibleCase.getUuid()));
	}

	@Test
	public void testGetCasesOnLaboratoryLevelWhenCasesDoNotHaveSamplesInUserLaboratory() {
		loginWith(nationalUser);

		CaseDataDto nonvisibleCase = createCase(rdcf1, nationalUser);

		Facility lab = new Facility();
		lab.setName("Lab");
		getFacilityService().persist(lab);

		creator.createSample(nonvisibleCase.toReference(), nationalUser.toReference(), lab, s -> {
			s.setReportLat(46.432);
			s.setReportLon(23.234);
			s.setReportLatLonAccuracy(10f);
			s.setLabDetails("Test lab details");
			s.setShipmentDetails("Test shipment details");
			s.setComment("Test comment");
		});

		createCase(rdcf2, nationalUser);

		loginWith(labUser);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(0));
	}

	@Test
	public void testGetCasesWithPlaceOfStayOnRegionLevel() {
		loginWith(regionUser);

		CaseDataDto visibleCase = createCase(rdcf1, districtUser1, rdcf2);
		createCase(rdcf1, districtUser1, rdcf1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(visibleCase.getUuid()));
	}

	@Test
	public void testGetCasesWithPlaceOfStayOnCommunityLevel() {
		loginWith(communityUser);

		CaseDataDto visibleCase = createCase(rdcf1, districtUser1, rdcf2);
		createCase(rdcf1, districtUser1, rdcf1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(visibleCase.getUuid()));
	}

	@Test
	public void testGetCaseUsersWithoutUsesLimitedToOthersDiseses() {
		loginWith(nationalUser);

		CaseDataDto caze = createCase(rdcf1, districtUser1);
		UserDto limitedCovidNationalUser =
			creator.createUser(rdcf1, "Limited Disease Covid", "National User", Disease.CORONAVIRUS, UserRole.NATIONAL_USER);
		UserDto limitedDengueNationalUser =
			creator.createUser(rdcf1, "Limited Disease Dengue", "National User", Disease.DENGUE, UserRole.NATIONAL_USER);

		List<UserReferenceDto> userReferenceDtos = getUserFacade().getUsersHavingCaseInJurisdiction(caze.toReference());
		Assert.assertNotNull(userReferenceDtos);
		Assert.assertTrue(userReferenceDtos.contains(nationalUser));
		Assert.assertTrue(userReferenceDtos.contains(districtUser1));
		Assert.assertTrue(userReferenceDtos.contains(limitedCovidNationalUser));
		Assert.assertFalse(userReferenceDtos.contains(limitedDengueNationalUser));
	}

	private CaseDataDto createCase(TestDataCreator.RDCF rdcf, UserDto reportingUser) {

		return creator.createCase(
			reportingUser.toReference(),
			creator.createPerson().toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			null);
	}

	private CaseDataDto createCase(TestDataCreator.RDCF rdcf, UserDto reportingUser, TestDataCreator.RDCF placeOfStayRdcf) {

		return creator.createCase(
			reportingUser.toReference(),
			creator.createPerson().toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			(c) -> {
				c.setRegion(placeOfStayRdcf.region);
				c.setDistrict(placeOfStayRdcf.district);
				c.setCommunity(placeOfStayRdcf.community);
				c.setHealthFacility(placeOfStayRdcf.facility);
			});
	}
}
