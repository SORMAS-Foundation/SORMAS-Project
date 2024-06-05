/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.selfreport.processing;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;

public class SelfReportProcessingResult {

	private EntitySelection<PersonDto> person;
	private EntitySelection<CaseDataDto> caze;
	private EntitySelection<ContactDto> contact;
	private boolean openEntityOnDone;

	public SelfReportProcessingResult withPerson(PersonDto person, boolean isNew) {
		this.person = new EntitySelection<>(person, isNew);

		return this;
	}

	public EntitySelection<PersonDto> getPerson() {
		return person;
	}

	public SelfReportProcessingResult withCase(CaseDataDto caze, boolean isNew) {
		this.caze = new EntitySelection<>(caze, isNew);
		return this;
	}

	public EntitySelection<CaseDataDto> getCaze() {
		return caze;
	}

	public SelfReportProcessingResult withContact(ContactDto contact, boolean isNew) {
		this.contact = new EntitySelection<>(contact, isNew);
		return this;
	}

	public EntitySelection<ContactDto> getContact() {
		return contact;
	}

	public boolean isOpenEntityOnDone() {
		return openEntityOnDone;
	}

	public SelfReportProcessingResult openEntityOnDone(boolean openEntityOnDone) {
		this.openEntityOnDone = openEntityOnDone;
		return this;
	}
}
