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

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class CaseJurisdictionHelper {

	public static Boolean isInJurisdiction(RoleCheck roleCheck, UserJurisdiction userJurisdiction, CaseJurisdictionDto caseJurisdictionDto) {

		if (roleCheck.hasAnyRole(Collections.singleton(UserRole.NATIONAL_USER)) ||
				caseJurisdictionDto.getDistrictUud() != null && DataHelper.equal(userJurisdiction.getUuid(), caseJurisdictionDto.getDistrictUud())) {
			return true;
		}

		if (roleCheck.hasAnyRole(UserRole.getSupervisorRoles())) {
			return DataHelper.equal(caseJurisdictionDto.getRegionUui(), userJurisdiction.getRegionUuid());
		}

		if (roleCheck.hasAnyRole(UserRole.getOfficerRoles())) {
			return DataHelper.equal(caseJurisdictionDto.getDistrictUud(), userJurisdiction.getDistrictUuid());
		}

		if ((roleCheck.hasAnyRole(Collections.singleton(UserRole.COMMUNITY_INFORMANT)))) {
			return DataHelper.equal(caseJurisdictionDto.getCommunityUuid(), userJurisdiction.getCommunityUuid());
		}

		if ((roleCheck.hasAnyRole(Collections.singleton(UserRole.HOSPITAL_INFORMANT)))) {
			return DataHelper.equal(caseJurisdictionDto.getHealthFacilityUuid(), userJurisdiction.getHealthFacilityUuid());
		}

		if ((roleCheck.hasAnyRole(Collections.singleton(UserRole.POE_INFORMANT)))) {
			return DataHelper.equal(caseJurisdictionDto.getPointOfEntryUuid(), userJurisdiction.getPointOfEntryUuid());
		}

		return false;
	}
}
