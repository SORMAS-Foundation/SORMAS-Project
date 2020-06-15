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
package de.symeda.sormas.api.utils.jurisdiction;

import java.util.Set;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;

public class CaseJurisdictionHelper {

	public static Boolean isInJurisdiction(Set<UserRole> userRoles, UserJurisdiction userJurisdiction, CaseJurisdictionDto caseJurisdictionDto) {

		if (UserRole.getJurisdictionLevel(userRoles) == JurisdictionLevel.NATION
			|| caseJurisdictionDto.getReportingUserUuid() != null
				&& DataHelper.equal(userJurisdiction.getUuid(), caseJurisdictionDto.getReportingUserUuid())) {
			return true;
		}

		if (UserRole.getJurisdictionLevel(userRoles) == JurisdictionLevel.REGION) {
			return DataHelper.equal(caseJurisdictionDto.getRegionUuid(), userJurisdiction.getRegionUuid());
		}

		if (UserRole.getJurisdictionLevel(userRoles) == JurisdictionLevel.DISTRICT) {
			return DataHelper.equal(caseJurisdictionDto.getDistrictUuid(), userJurisdiction.getDistrictUuid());
		}

		if ((UserRole.getJurisdictionLevel(userRoles) == JurisdictionLevel.COMMUNITY)) {
			return DataHelper.equal(caseJurisdictionDto.getCommunityUuid(), userJurisdiction.getCommunityUuid());
		}

		if ((UserRole.getJurisdictionLevel(userRoles) == JurisdictionLevel.HEALTH_FACILITY)) {
			return DataHelper.equal(caseJurisdictionDto.getHealthFacilityUuid(), userJurisdiction.getHealthFacilityUuid());
		}

		if ((UserRole.getJurisdictionLevel(userRoles) == JurisdictionLevel.POINT_OF_ENTRY)) {
			return DataHelper.equal(caseJurisdictionDto.getPointOfEntryUuid(), userJurisdiction.getPointOfEntryUuid());
		}

		return false;
	}
}
