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
package de.symeda.sormas.api.user;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class UserRoleTest {

	@Test
	public void testUserRolesCombinationValidity() {

		assertValidRolesCombination(UserRole.ADMIN, UserRole.NATIONAL_USER);

		assertValidRolesCombination(
			UserRole.ADMIN,
			UserRole.NATIONAL_USER,
			UserRole.LAB_USER,
			UserRole.REST_USER,
			UserRole.REST_EXTERNAL_VISITS_USER,
			UserRole.IMPORT_USER);

		assertValidRolesCombination(
			UserRole.NATIONAL_USER,
			UserRole.NATIONAL_OBSERVER,
			UserRole.NATIONAL_CLINICIAN,
			UserRole.POE_NATIONAL_USER,
			UserRole.REST_EXTERNAL_VISITS_USER);

		assertValidRolesCombination(
			UserRole.NATIONAL_USER,
			UserRole.LAB_USER);

		assertValidRolesCombination(
			UserRole.ADMIN,
			UserRole.LAB_USER);

		assertInvalidRolesCombination(
			UserRole.LAB_USER,
			UserRole.EXTERNAL_LAB_USER);

		assertInvalidRolesCombination(
			UserRole.NATIONAL_USER,
			UserRole.EXTERNAL_LAB_USER);

		assertValidRolesCombination(
			UserRole.SURVEILLANCE_SUPERVISOR,
			UserRole.CASE_SUPERVISOR,
			UserRole.CONTACT_SUPERVISOR,
			UserRole.EVENT_OFFICER,
			UserRole.STATE_OBSERVER,
			UserRole.POE_SUPERVISOR);

		assertValidRolesCombination(
			UserRole.SURVEILLANCE_OFFICER,
			UserRole.CASE_OFFICER,
			UserRole.CONTACT_OFFICER,
			UserRole.DISTRICT_OBSERVER);
		
		assertValidRolesCombination(UserRole.HOSPITAL_INFORMANT);

		assertValidRolesCombination(UserRole.COMMUNITY_INFORMANT, UserRole.IMPORT_USER);

		assertInvalidRolesCombination(UserRole.COMMUNITY_INFORMANT, UserRole.HOSPITAL_INFORMANT);
		assertInvalidRolesCombination(UserRole.ADMIN, UserRole.NATIONAL_USER, UserRole.SURVEILLANCE_SUPERVISOR);
		assertInvalidRolesCombination(UserRole.NATIONAL_USER, UserRole.EVENT_OFFICER);
	}

	@Test
	public void testUserRolesJurisdiction(){

		assertJurisdictionForRoles(JurisdictionLevel.NONE, UserRole.ADMIN, UserRole.REST_USER);
		assertJurisdictionForRoles(JurisdictionLevel.NATION, UserRole.ADMIN, UserRole.REST_USER, UserRole.NATIONAL_CLINICIAN);
		assertJurisdictionForRoles(JurisdictionLevel.DISTRICT, UserRole.ADMIN, UserRole.REST_USER, UserRole.DISTRICT_OBSERVER);
		assertJurisdictionForRoles(JurisdictionLevel.NATION, UserRole.NATIONAL_USER, UserRole.LAB_USER);
		assertJurisdictionForRoles(JurisdictionLevel.REGION, UserRole.CASE_SUPERVISOR);
		assertJurisdictionForRoles(JurisdictionLevel.LABORATORY, UserRole.LAB_USER);
		assertJurisdictionForRoles(JurisdictionLevel.LABORATORY, UserRole.ADMIN, UserRole.LAB_USER);
	}


	private void assertJurisdictionForRoles(final JurisdictionLevel jurisdictionLevel, final UserRole... userRoles) {
		Assert.assertEquals(jurisdictionLevel, UserRole.getJurisdictionLevel(Arrays.asList(userRoles)));
	}

	private void assertValidRolesCombination(final UserRole... userRoles) {
		isValidRolesCombination(true, userRoles);
	}

	private void assertInvalidRolesCombination(final UserRole... userRoles) {
		isValidRolesCombination(false, userRoles);
	}

	private void isValidRolesCombination(final Boolean isValid, final UserRole... userRoles) {
		try {
			UserRole.validate(Arrays.asList(userRoles));
		} catch (UserRole.UserRoleValidationException e) {
			if (isValid) {
				Assert.fail(e.getMessage());
			}
		}
	}
}
