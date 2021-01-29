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

package de.symeda.sormas.api.caze;

import java.io.Serializable;

import de.symeda.sormas.api.person.PersonDto;

public class CasePersonDto implements Serializable {

	private static final long serialVersionUID = 4238365446327936524L;

	private CaseDataDto caze;

	private PersonDto person;

	public CasePersonDto() {
	}

	public CasePersonDto(CaseDataDto caze, PersonDto person) {
		this.caze = caze;
		this.person = person;
	}

	public CaseDataDto getCaze() {
		return caze;
	}

	public void setCaze(CaseDataDto caze) {
		this.caze = caze;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}
}
