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

package de.symeda.sormas.api.survnet;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;

import java.util.List;

import javax.ejb.Remote;

/**
 * Gateway to interact with the local SurvNet instance of the health department
 */
@Remote
public interface SurvnetGatewayFacade {

	boolean isFeatureEnabled();

	/**
	 * Requests the cases to be sent to SurvNet
	 * 
	 * @param caseUuids
	 * @return http response code of the gateway
	 */
	int sendCases(List<String> caseUuids);

	int sendEvents(List<String> eventUuids);

    int deleteCases(List<CaseDataDto> cases);

	int deleteEvents(List<EventDto> events);
}
