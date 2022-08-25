/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.user;

import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleCriteria;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class UserRoleServiceTest extends AbstractBeanTest {

	@Test
	public void testGetByLinkedDefaultUserRole() {

		creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER);

		Assert.assertEquals(6, getUserRoleFacade().count(new UserRoleCriteria().enabled(true).jurisdictionLevel(JurisdictionLevel.NATION)));

		DefaultUserRole defaultUserRole = DefaultUserRole.NATIONAL_USER;
		UserRoleDto userRoleDto =
			UserRoleDto.build(defaultUserRole.getDefaultUserRights().toArray(new UserRight[defaultUserRole.getDefaultUserRights().size()]));
		userRoleDto.setCaption(defaultUserRole.toString() + "2");
		userRoleDto.setEnabled(true);
		userRoleDto.setPortHealthUser(defaultUserRole.isPortHealthUser());
		userRoleDto.setLinkedDefaultUserRole(defaultUserRole);
		userRoleDto.setHasAssociatedDistrictUser(defaultUserRole.hasAssociatedDistrictUser());
		userRoleDto.setHasOptionalHealthFacility(defaultUserRole.hasOptionalHealthFacility());
		userRoleDto.setEmailNotificationTypes(defaultUserRole.getEmailNotificationTypes());
		userRoleDto.setSmsNotificationTypes(defaultUserRole.getSmsNotificationTypes());
		userRoleDto.setJurisdictionLevel(defaultUserRole.getJurisdictionLevel());
		getUserRoleFacade().saveUserRole(userRoleDto);

		Assert.assertEquals(7, getUserRoleFacade().count(new UserRoleCriteria().enabled(true).jurisdictionLevel(JurisdictionLevel.NATION)));

		UserRole byLinkedDefaultUserRole = getUserRoleService().getByLinkedDefaultUserRole(DefaultUserRole.NATIONAL_USER);
		Assert.assertNotNull(byLinkedDefaultUserRole);
		Assert.assertEquals(DefaultUserRole.NATIONAL_USER.toString(), byLinkedDefaultUserRole.getCaption());
	}
}
