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

package de.symeda.sormas.backend.sormastosormas;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;

public class ProcessedCaseData implements Serializable {

	private static final long serialVersionUID = -437052876440284140L;

	private final PersonDto person;
	private final CaseDataDto caze;
	private final List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts;
	private final List<SormasToSormasSampleDto> samples;
	private final SormasToSormasOriginInfoDto originInfo;

	public ProcessedCaseData(
		PersonDto person,
		CaseDataDto caze,
		List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts,
		List<SormasToSormasSampleDto> samples,
		SormasToSormasOriginInfoDto originInfo) {
		this.person = person;
		this.caze = caze;
		this.associatedContacts = associatedContacts;
		this.samples = samples;
		this.originInfo = originInfo;
	}

	public PersonDto getPerson() {
		return person;
	}

	public CaseDataDto getCaze() {
		return caze;
	}

	public List<SormasToSormasCaseDto.AssociatedContactDto> getAssociatedContacts() {
		return associatedContacts;
	}

	public List<SormasToSormasSampleDto> getSamples() {
		return samples;
	}

	public SormasToSormasOriginInfoDto getOriginInfo() {
		return originInfo;
	}
}
