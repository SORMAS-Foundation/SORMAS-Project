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

package de.symeda.sormas.backend.immunization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class ImmunizationFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

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

		loginWith(user2);
	}

	@Test
	public void testGetImmunizationOutsideJurisdiction() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson().toReference(), rdcf1);
		ImmunizationDto immunization = createImmunization(caze, user1, rdcf1);

		assertPseudonymized(getImmunizationFacade().getByUuid(immunization.getUuid()), rdcf1);
		assertPseudonymized(getImmunizationFacade().getByUuids(Collections.singletonList(immunization.getUuid())).get(0), rdcf1);
		assertThat(getImmunizationFacade().getAllAfter(new Date(0)), hasSize(0));
		assertThat(getImmunizationFacade().getIndexList(new ImmunizationCriteria(), null, null, null), hasSize(0));
	}

	@Test
	public void testGetReportOfCaseWithSpecialAccess() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson().toReference(), rdcf1);
		ImmunizationDto immunization = createImmunization(caze, user1, rdcf1);
		creator.createSpecialCaseAccess(caze.toReference(), user1.toReference(), user2.toReference(), DateHelper.addDays(new Date(), 1));

		assertNotPseudonymized(getImmunizationFacade().getByUuid(immunization.getUuid()), user1, rdcf1);
		assertNotPseudonymized(getImmunizationFacade().getByUuids(Collections.singletonList(immunization.getUuid())).get(0), user1, rdcf1);
		assertNotPseudonymized(getImmunizationFacade().getAllAfter(new Date(0)).get(0), user1, rdcf1);
		assertThat(getImmunizationFacade().getIndexList(new ImmunizationCriteria(), null, null, null).get(0).isPseudonymized(), is(false));
	}

	private void assertPseudonymized(ImmunizationDto immunization, TestDataCreator.RDCF rdcf) {
		assertThat(immunization.isPseudonymized(), is(true));
		assertThat(immunization.getReportingUser(), is(nullValue()));
		assertThat(immunization.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(immunization.getMeansOfImmunization(), is(MeansOfImmunization.OTHER));
		assertThat(immunization.getMeansOfImmunizationDetails(), is(""));
		assertThat(immunization.getResponsibleRegion(), is(rdcf.region));
		assertThat(immunization.getResponsibleDistrict(), is(rdcf.district));
		assertThat(immunization.getResponsibleCommunity(), is(nullValue()));
		assertThat(immunization.getHealthFacility(), is(nullValue()));
		assertThat(immunization.getHealthFacilityDetails(), is(""));
		assertThat(immunization.getAdditionalDetails(), is(""));
	}

	private void assertNotPseudonymized(ImmunizationDto immunization, UserDto user, TestDataCreator.RDCF rdcf) {
		assertThat(immunization.getReportingUser(), is(user.toReference()));
		assertThat(immunization.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(immunization.getMeansOfImmunization(), is(MeansOfImmunization.OTHER));
		assertThat(immunization.getMeansOfImmunizationDetails(), is("Test means of immunization details"));
		assertThat(immunization.getResponsibleRegion(), is(rdcf.region));
		assertThat(immunization.getResponsibleDistrict(), is(rdcf.district));
		assertThat(immunization.getResponsibleCommunity(), is(rdcf.community));
		assertThat(immunization.getHealthFacility(), is(rdcf.facility));
		assertThat(immunization.getHealthFacilityDetails(), is("Test facility details"));
		assertThat(immunization.getAdditionalDetails(), is("Test additional details"));
	}

	private ImmunizationDto createImmunization(CaseDataDto caze, UserDto user, TestDataCreator.RDCF rdcf) {
		ImmunizationDto immunization = ImmunizationDto.build(caze.getPerson());
		immunization.setRelatedCase(caze.toReference());
		immunization.setReportDate(new Date());
		immunization.setReportingUser(user.toReference());
		immunization.setDisease(Disease.CORONAVIRUS);
		immunization.setMeansOfImmunization(MeansOfImmunization.OTHER);
		immunization.setMeansOfImmunizationDetails("Test means of immunization details");
		immunization.setResponsibleRegion(rdcf.region);
		immunization.setResponsibleDistrict(rdcf.district);
		immunization.setResponsibleCommunity(rdcf.community);
		immunization.setFacilityType(FacilityType.HOSPITAL);
		immunization.setHealthFacility(rdcf.facility);
		immunization.setHealthFacilityDetails("Test facility details");
		immunization.setAdditionalDetails("Test additional details");

		return getImmunizationFacade().save(immunization);
	}
}
