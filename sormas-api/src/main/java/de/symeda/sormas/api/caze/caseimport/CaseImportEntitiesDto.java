/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import javax.validation.Valid;

import de.symeda.sormas.api.audit.Auditable;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.vaccination.VaccinationDto;

public class CaseImportEntitiesDto implements Auditable, Serializable {

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

	public CaseImportEntitiesDto(UserReferenceDto reportingUser) {
		person = PersonDto.buildImportEntity();
		caze = createCase(person, reportingUser);

		samples = new ArrayList<>();
		pathogenTests = new ArrayList<>();
		vaccinations = new ArrayList<>();
	}

	public CaseImportEntitiesDto(PersonDto person, CaseDataDto caze) {
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

	@Override
	public String getAuditRepresentation() {
		StringBuilder auditRepresentation = new StringBuilder(getClass().getSimpleName());
		auditRepresentation.append(getClass().getSimpleName());

		auditRepresentation.append("(person=");
		auditRepresentation.append(person.getAuditRepresentation());

		auditRepresentation.append(", caze=");
		auditRepresentation.append(caze.getAuditRepresentation());


		auditRepresentation.append(", samples=[");
		for (SampleDto sample : samples) {
			auditRepresentation.append(sample.getAuditRepresentation());
			auditRepresentation.append(", ");
		}

		auditRepresentation.append("], pathogenTests=[");
		for (PathogenTestDto pathogenTest : pathogenTests) {
			auditRepresentation.append(pathogenTest.getAuditRepresentation());
			auditRepresentation.append(", ");
		}

		auditRepresentation.append("], vaccinations=[");
		for (VaccinationDto vaccination : vaccinations) {
			auditRepresentation.append(vaccination.getAuditRepresentation());
			auditRepresentation.append(", ");

		}
		auditRepresentation.append("])");
		return auditRepresentation.toString();
	}
}
