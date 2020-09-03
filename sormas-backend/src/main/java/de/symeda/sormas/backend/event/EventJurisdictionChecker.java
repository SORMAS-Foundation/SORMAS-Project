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
package de.symeda.sormas.backend.event;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.EventJurisdictionHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless(name = "EventJurisdictionChecker")
@LocalBean
public class EventJurisdictionChecker {

	@EJB
	private UserService userService;

	public boolean isInJurisdiction(Event event) {

		return isInJurisdiction(JurisdictionHelper.createEventJurisdictionDto(event));
	}

	public boolean isInJurisdiction(EventJurisdictionDto eventJurisdiction) {

		User user = userService.getCurrentUser();

		return EventJurisdictionHelper
			.isInJurisdiction(user.getJurisdictionLevel(), JurisdictionHelper.createUserJurisdiction(user), eventJurisdiction);
	}
}
