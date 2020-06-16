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

import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;

public class ContactJurisdictionHelper {

	public static boolean isInJurisdiction(
		JurisdictionLevel jurisdictionLevel,
		UserJurisdiction userJurisdiction,
		ContactJurisdictionDto contactJurisdiction) {

		if (contactJurisdiction.getReportingUserUuid() != null
			&& DataHelper.equal(userJurisdiction.getUuid(), contactJurisdiction.getReportingUserUuid())) {
			return true;
		}

		if (contactJurisdiction.getCaseJurisdiction() != null) {
			return CaseJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, contactJurisdiction.getCaseJurisdiction());
		}

		switch (jurisdictionLevel) {
		case NONE:
			return false;
		case NATION:
			return true;
		case REGION:
			return contactJurisdiction.getRegionUuid() != null
				&& DataHelper.equal(contactJurisdiction.getRegionUuid(), userJurisdiction.getRegionUuid());
		case DISTRICT:
			return contactJurisdiction.getDistrictUuid() != null
				&& DataHelper.equal(contactJurisdiction.getDistrictUuid(), userJurisdiction.getDistrictUuid());
		case COMMUNITY:
			return false;
		case HEALTH_FACILITY:
			return false;
		case LABORATORY:
			return false;
		case EXTERNAL_LABORATORY:
			return false;
		case POINT_OF_ENTRY:
			return false;
		default:
			return false;
		}
	}
}
