/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.utils.jurisdiction;

import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;

import java.util.Collections;

public class ContactJurisdictionHelper {
	public static boolean isInJurisdiction(RoleCheck roleCheck, UserJurisdiction userJurisdiction, ContactJurisdictionDto contactJurisdiction) {
		if (contactJurisdiction.getReportingUserUuid() != null && DataHelper.equal(userJurisdiction.getUuid(), contactJurisdiction.getReportingUserUuid())) {
			return true;
		}

		if (contactJurisdiction.getCaseJurisdiction() != null) {
			return CaseJurisdictionHelper.isInJurisdiction(roleCheck, userJurisdiction, contactJurisdiction.getCaseJurisdiction());
		}

		if (roleCheck.hasAnyRole(UserRole.getSupervisorRoles())) {
			return DataHelper.equal(contactJurisdiction.getRegionUuid(), userJurisdiction.getRegionUuid());
		}

		if (roleCheck.hasAnyRole(UserRole.getOfficerRoles())) {
			return DataHelper.equal(contactJurisdiction.getDistrictUuid(), userJurisdiction.getDistrictUuid());
		}

		return roleCheck.hasAnyRole(Collections.singleton(UserRole.NATIONAL_USER));
	}
}
