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

package de.symeda.sormas.api.sormastosormas.entities.caze;

import javax.validation.Valid;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.entities.SormasToSormasEntityDto;

public class SormasToSormasCaseDto extends SormasToSormasEntityDto<CaseDataDto> {

	private static final long serialVersionUID = 1811907980150876134L;

	@Valid
	private PersonDto person;

	public SormasToSormasCaseDto() {
	}

	public SormasToSormasCaseDto(PersonDto person, CaseDataDto caze) {
		super(caze);
		this.person = person;
	}

	public PersonDto getPerson() {
		return person;
	}
}
