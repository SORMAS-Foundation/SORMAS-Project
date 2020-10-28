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

package de.symeda.sormas.api.sormastosormas;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;

public class SormasToSormasContactDto implements Serializable {

	private static final long serialVersionUID = 7414547678077858460L;

	private PersonDto person;

	private ContactDto contact;

	private List<SormasToSormasSampleDto> samples;

	private SormasToSormasOriginInfoDto originInfo;

	public SormasToSormasContactDto() {
	}

	public SormasToSormasContactDto(PersonDto person, ContactDto contact, SormasToSormasOriginInfoDto originInfo) {
		this.person = person;
		this.contact = contact;
		this.originInfo = originInfo;
	}

	public PersonDto getPerson() {
		return person;
	}

	public ContactDto getContact() {
		return contact;
	}

	public List<SormasToSormasSampleDto> getSamples() {
		return samples;
	}

	public void setSamples(List<SormasToSormasSampleDto> samples) {
		this.samples = samples;
	}

	public SormasToSormasOriginInfoDto getOriginInfo() {
		return originInfo;
	}
}
