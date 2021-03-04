/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils.jurisdiction;

import de.symeda.sormas.api.event.EventParticipantJurisdictionDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;

public class EventParticipantJurisdictionHelper {

	public static boolean isInJurisdictionOrOwned(
		JurisdictionLevel jurisdictionLevel,
		UserJurisdiction userJurisdiction,
		EventParticipantJurisdictionDto eventParticipantJurisdiction) {

		if (isOwned(userJurisdiction, eventParticipantJurisdiction))
			return true;

		return isInJurisdiction(jurisdictionLevel, userJurisdiction, eventParticipantJurisdiction);
	}

	public static boolean isOwned(UserJurisdiction userJurisdiction, EventParticipantJurisdictionDto eventParticipantJurisdiction) {

		if (eventParticipantJurisdiction != null && eventParticipantJurisdiction.getReportingUserUuid() != null
			&& DataHelper.equal(userJurisdiction.getUuid(), eventParticipantJurisdiction.getReportingUserUuid())) {
			return true;
		}
		return false;
	}

	public static boolean isInJurisdiction(
		JurisdictionLevel jurisdictionLevel,
		UserJurisdiction userJurisdiction,
		EventParticipantJurisdictionDto eventParticipantJurisdiction) {

		switch (jurisdictionLevel) {
		case NONE:
			return false;
		case NATION:
			return true;
		case REGION:
			return eventParticipantJurisdiction.getRegionUuid() != null
				&& DataHelper.equal(eventParticipantJurisdiction.getRegionUuid(), userJurisdiction.getRegionUuid());
		case DISTRICT:
			return eventParticipantJurisdiction.getDistrictUuid() != null
				&& DataHelper.equal(eventParticipantJurisdiction.getDistrictUuid(), userJurisdiction.getDistrictUuid());
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
