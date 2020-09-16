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

package de.symeda.sormas.api.caze.caseimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class CaseImportEntities implements Serializable {

	private static final long serialVersionUID = -4565794925738392508L;

	private final PersonDto person;
	private final CaseDataDto caze;
	private final List<SampleDto> samples;
	private final List<PathogenTestDto> pathogenTests;

	public CaseImportEntities(UserReferenceDto reportingUser) {
		person = PersonDto.build();
		caze = createCase(person, reportingUser);

		samples = new ArrayList<>();
		pathogenTests = new ArrayList<>();
	}

	public static CaseDataDto createCase(PersonDto person, UserReferenceDto reportingUser) {
		CaseDataDto caze = CaseDataDto.build(person.toReference(), null);
		caze.setReportingUser(reportingUser);

		return caze;
	}

	public CaseImportEntities(PersonDto person, CaseDataDto caze) {
		this.person = person;
		this.caze = caze;

		samples = new ArrayList<>();
		pathogenTests = new ArrayList<>();
	}

	public PersonDto getPerson() {
		return person;
	}

	public CaseDataDto getCaze() {
		return caze;
	}

	public List<SampleDto> getSamples() {
		return samples;
	}

	public List<PathogenTestDto> getPathogenTests() {
		return pathogenTests;
	}
}
