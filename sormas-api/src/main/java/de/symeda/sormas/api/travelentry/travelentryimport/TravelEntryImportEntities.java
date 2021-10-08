/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.travelentry.travelentryimport;

import java.io.Serializable;

import javax.validation.Valid;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class TravelEntryImportEntities implements Serializable {

	private static final long serialVersionUID = -714807989630027827L;

	@Valid
	private final TravelEntryDto travelEntry;
	@Valid
	private final PersonDto person;

	public TravelEntryImportEntities(UserReferenceDto reportingUser) {
		person = PersonDto.buildImportEntity();
		travelEntry = TravelEntryDto.build(person.toReference());
		travelEntry.setReportingUser(reportingUser);
	}

	public TravelEntryImportEntities(UserReferenceDto reportingUser, PersonDto person) {
		this.person = person;
		travelEntry = TravelEntryDto.build(person.toReference());
		travelEntry.setReportingUser(reportingUser);
	}

	public TravelEntryDto getTravelEntry() {
		return travelEntry;
	}

	public PersonDto getPerson() {
		return person;
	}
}
