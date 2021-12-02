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

package de.symeda.sormas.api.sormastosormas.contact;

import javax.validation.Valid;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;

public class SormasToSormasContactDto extends SormasToSormasEntityDto<ContactDto> {

	private static final long serialVersionUID = 7414547678077858460L;

	@Valid
	private PersonDto person;

	public SormasToSormasContactDto() {
	}

	public SormasToSormasContactDto(PersonDto person, ContactDto contact) {
		super(contact);
		this.person = person;
	}

	public PersonDto getPerson() {
		return person;
	}
}
