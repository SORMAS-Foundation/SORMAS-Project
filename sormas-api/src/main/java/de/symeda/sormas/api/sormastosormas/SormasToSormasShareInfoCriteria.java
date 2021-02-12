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

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;

public class SormasToSormasShareInfoCriteria extends BaseCriteria {

	private static final long serialVersionUID = 8659993748606564333L;

	private CaseReferenceDto caze;

	private ContactReferenceDto contact;

	private SampleReferenceDto sample;

	private EventReferenceDto event;

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public SormasToSormasShareInfoCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;

		return this;
	}

	public ContactReferenceDto getContact() {
		return contact;
	}

	public SormasToSormasShareInfoCriteria contact(ContactReferenceDto contact) {
		this.contact = contact;

		return this;
	}

	public SampleReferenceDto getSample() {
		return sample;
	}

	public SormasToSormasShareInfoCriteria sample(SampleReferenceDto sample) {
		this.sample = sample;

		return this;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public SormasToSormasShareInfoCriteria event(EventReferenceDto event) {
		this.event = event;

		return this;
	}
}
