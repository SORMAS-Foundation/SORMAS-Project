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

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

@RunWith(MockitoJUnitRunner.class)
public class CaseFacadeEjbUserFilterTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;

	private UserDto districtUser1;
	private UserDto districtUser2;
	private UserDto nationalUser;
	private UserDto regionUser;
	private UserDto communityUser;
	private UserDto facilityUser;

	@Override
	public void init() {

		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		districtUser1 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

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
	public void testGetCasesOnRegionevel() {
		loginWith(regionUser);

		CaseDataDto visibleCase = createCase(rdcf2, districtUser2);
		createCase(rdcf1, districtUser1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(visibleCase.getUuid()));
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
	public void testGetCasesWithResponsibleJurisdictionOnNationalLevel() {
		loginWith(nationalUser);

		createCase(rdcf1, districtUser1, rdcf2);
		createCase(rdcf1, districtUser1, rdcf1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(2));
	}

	@Test
	public void testGetCasesResponsibleJurisdictionOnRegionLevel() {
		loginWith(regionUser);

		CaseDataDto visibleCase = createCase(rdcf1, districtUser1, rdcf2);
		createCase(rdcf1, districtUser1, rdcf1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(visibleCase.getUuid()));
	}

	@Test
	public void testGetCasesResponsibleJurisdictionOnCommunityLevel() {
		loginWith(communityUser);

		CaseDataDto visibleCase = createCase(rdcf1, districtUser1, rdcf2);
		createCase(rdcf1, districtUser1, rdcf1);

		List<CaseIndexDto> indexList = getCaseFacade().getIndexList(new CaseCriteria(), 0, 100, null);
		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), is(visibleCase.getUuid()));
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

	private CaseDataDto createCase(TestDataCreator.RDCF rdcf, UserDto reportingUser, TestDataCreator.RDCF responsibleRdcf) {

		return creator.createCase(
			reportingUser.toReference(),
			creator.createPerson().toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			(c) -> {
				c.setResponsibleRegion(responsibleRdcf.region);
				c.setResponsibleDistrict(responsibleRdcf.district);
				c.setResponsibleCommunity(responsibleRdcf.community);
			});
	}
}
