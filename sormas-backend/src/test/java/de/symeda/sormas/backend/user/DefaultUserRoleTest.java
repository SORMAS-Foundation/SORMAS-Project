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
package de.symeda.sormas.backend.user;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;

public class DefaultUserRoleTest {

	@Test
	public void testUserRolesJurisdiction() {

		assertJurisdictionForRoles(JurisdictionLevel.NONE, DefaultUserRole.ADMIN);
		assertJurisdictionForRoles(JurisdictionLevel.NATION, DefaultUserRole.ADMIN, DefaultUserRole.REST_USER, DefaultUserRole.NATIONAL_CLINICIAN);
		assertJurisdictionForRoles(JurisdictionLevel.DISTRICT, DefaultUserRole.ADMIN, DefaultUserRole.DISTRICT_OBSERVER);
		assertJurisdictionForRoles(
			JurisdictionLevel.NATION,
			DefaultUserRole.NATIONAL_USER,
			DefaultUserRole.LAB_USER,
			DefaultUserRole.REST_USER,
			DefaultUserRole.ADMIN);
		assertJurisdictionForRoles(JurisdictionLevel.REGION, DefaultUserRole.CASE_SUPERVISOR);
		assertJurisdictionForRoles(JurisdictionLevel.LABORATORY, DefaultUserRole.LAB_USER);
		assertJurisdictionForRoles(JurisdictionLevel.LABORATORY, DefaultUserRole.ADMIN, DefaultUserRole.LAB_USER);
	}

	private void assertJurisdictionForRoles(final JurisdictionLevel jurisdictionLevel, final DefaultUserRole... userRoles) {
		Assert.assertEquals(jurisdictionLevel, DefaultUserRole.getJurisdictionLevel(Arrays.asList(userRoles)));
	}
}
