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

import de.symeda.sormas.api.vaccination.VaccinationDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class CaseImportEntities implements Serializable {

	private static final long serialVersionUID = -4565794925738392508L;

	@Valid
	private final PersonDto person;
	@Valid
	private final CaseDataDto caze;
	@Valid
	private final List<SampleDto> samples;
	@Valid
	private final List<PathogenTestDto> pathogenTests;
	@Valid
	private final List<VaccinationDto> vaccinations;

	public CaseImportEntities(UserReferenceDto reportingUser) {
		person = PersonDto.buildImportEntity();
		caze = createCase(person, reportingUser);

		samples = new ArrayList<>();
		pathogenTests = new ArrayList<>();
		vaccinations = new ArrayList<>();
	}

	public CaseImportEntities(PersonDto person, CaseDataDto caze) {
		this.person = person;
		this.caze = caze;

		samples = new ArrayList<>();
		pathogenTests = new ArrayList<>();
		vaccinations = new ArrayList<>();
	}

	public static CaseDataDto createCase(PersonDto person, UserReferenceDto reportingUser) {
		CaseDataDto caze = CaseDataDto.build(person.toReference(), null);
		caze.setReportingUser(reportingUser);

		return caze;
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

	public List<VaccinationDto> getVaccinations() {
		return vaccinations;
	}
}
