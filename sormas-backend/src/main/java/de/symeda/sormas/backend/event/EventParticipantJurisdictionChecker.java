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

package de.symeda.sormas.backend.event;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventParticipantJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.EventJurisdictionHelper;
import de.symeda.sormas.api.utils.jurisdiction.EventParticipantJurisdictionHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless(name = "EventParticipantJurisdictionChecker")
@LocalBean
public class EventParticipantJurisdictionChecker {

	@EJB
	private UserService userService;
	@EJB
	private EventParticipantService eventParticipantService;

	private EventParticipantJurisdictionDto createEventParticipantJurisdictionDto(EventParticipant eventParticipant) {
		EventParticipantJurisdictionDto jurisdiction = new EventParticipantJurisdictionDto();

		jurisdiction.setEventParticipantUuid(eventParticipant.getUuid());

		if (eventParticipant.getReportingUser() != null) {
			jurisdiction.setReportingUserUuid(eventParticipant.getReportingUser().getUuid());
		}

		if (eventParticipant.getRegion() != null) {
			jurisdiction.setRegionUuid(eventParticipant.getRegion().getUuid());
		}

		if (eventParticipant.getDistrict() != null) {
			jurisdiction.setDistrictUuid(eventParticipant.getDistrict().getUuid());
		}

		if (eventParticipant.getEvent() != null) {
			jurisdiction.setEventUuid(eventParticipant.getEvent().getUuid());
		}

		return jurisdiction;
	}

	public boolean isInEventJurisdiction(EventParticipant eventParticipant) {
		User user = userService.getCurrentUser();
		Event event = eventParticipant.getEvent();

		return EventJurisdictionHelper.isInJurisdiction(
			user.getJurisdictionLevel(),
			JurisdictionHelper.createUserJurisdiction(user),
			JurisdictionHelper.createEventJurisdictionDto(event));
	}

	public boolean isOwned(EventParticipant eventParticipant) {
		User user = userService.getCurrentUser();

		return EventParticipantJurisdictionHelper
			.isOwned(JurisdictionHelper.createUserJurisdiction(user), createEventParticipantJurisdictionDto(eventParticipant));
	}

	public boolean isInJurisdiction(EventParticipant eventParticipant) {
		User user = userService.getCurrentUser();

		EventParticipantJurisdictionDto eventParticipantJurisdiction = createEventParticipantJurisdictionDto(eventParticipant);

		boolean isInEventParticipantJuristiction = EventParticipantJurisdictionHelper
			.isInJurisdiction(user.getJurisdictionLevel(), JurisdictionHelper.createUserJurisdiction(user), eventParticipantJurisdiction);

		boolean isInEventJurisdiction = isInEventJurisdiction(eventParticipant);

		if (eventParticipantJurisdiction.getRegionUuid() == null && eventParticipantJurisdiction.getDistrictUuid() == null) {
			return isInEventJurisdiction;
		} else {
			return isInEventParticipantJuristiction;
		}
	}

	public boolean isPseudonymized(EventParticipant eventParticipant) {
		boolean isOwned = isOwned(eventParticipant);
		boolean isInEventParticipantJurisdiction = isInJurisdiction(eventParticipant);
		boolean isInEventJurisdiction = isInEventJurisdiction(eventParticipant);

		// "true" means the pseudomyzatnion will not occur
		// "return" false means the sensitive data will be pseudonymized
		return isOwned || isInEventParticipantJurisdiction || isInEventJurisdiction;
	}

	public boolean isPseudonymized(String eventParticipantUuid) {
		EventParticipant eventParticipant = eventParticipantService.getByUuid(eventParticipantUuid);

		return isPseudonymized(eventParticipant);
	}
}
