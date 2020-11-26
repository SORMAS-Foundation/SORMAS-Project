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

package de.symeda.sormas.backend.sormastosormas.databuilder;

import java.util.List;

import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.sample.Sample;

public class CaseShareData {

	private final SormasToSormasCaseDto caseShareData;

	private final List<Contact> associatedContacts;

	private final List<Sample> samples;

	public CaseShareData(SormasToSormasCaseDto caseShareData, List<Contact> associatedContacts, List<Sample> samples) {
		this.caseShareData = caseShareData;
		this.associatedContacts = associatedContacts;
		this.samples = samples;
	}

	public SormasToSormasCaseDto getCaseShareData() {
		return caseShareData;
	}

	public List<Contact> getAssociatedContacts() {
		return associatedContacts;
	}

	public List<Sample> getSamples() {
		return samples;
	}
}
