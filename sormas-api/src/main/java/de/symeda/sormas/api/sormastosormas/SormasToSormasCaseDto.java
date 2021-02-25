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

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;

public class SormasToSormasCaseDto extends SormasToSormasDto<CaseDataDto> {

	private static final long serialVersionUID = 1811907980150876134L;

	private PersonDto person;

	private List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts;

	private List<SormasToSormasSampleDto> samples;

	public SormasToSormasCaseDto() {
	}

	public SormasToSormasCaseDto(PersonDto person, CaseDataDto caze, SormasToSormasOriginInfoDto originInfo) {
		super(caze, originInfo);
		this.person = person;
	}

	public PersonDto getPerson() {
		return person;
	}

	public List<SormasToSormasCaseDto.AssociatedContactDto> getAssociatedContacts() {
		return associatedContacts;
	}

	public void setAssociatedContacts(List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts) {
		this.associatedContacts = associatedContacts;
	}

	public List<SormasToSormasSampleDto> getSamples() {
		return samples;
	}

	public void setSamples(List<SormasToSormasSampleDto> samples) {
		this.samples = samples;
	}

	public static final class AssociatedContactDto implements Serializable {

		private static final long serialVersionUID = 1398270981748143566L;

		private PersonDto person;

		private ContactDto contact;

		public AssociatedContactDto() {
		}

		public AssociatedContactDto(PersonDto person, ContactDto contact) {
			this.person = person;
			this.contact = contact;
		}

		public PersonDto getPerson() {
			return person;
		}

		public void setPerson(PersonDto person) {
			this.person = person;
		}

		public ContactDto getContact() {
			return contact;
		}

		public void setContact(ContactDto contact) {
			this.contact = contact;
		}
	}
}
