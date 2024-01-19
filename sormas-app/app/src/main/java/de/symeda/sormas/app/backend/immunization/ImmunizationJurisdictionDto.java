/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.immunization;

import java.io.Serializable;

import de.symeda.sormas.app.backend.caze.ResponsibleJurisdictionDto;
import de.symeda.sormas.app.backend.person.PersonJurisdictionDto;

public class ImmunizationJurisdictionDto implements Serializable {

    private String reportingUserUuid;
    private ResponsibleJurisdictionDto responsibleJurisdiction;

	private String personUuid;
	private PersonJurisdictionDto personJurisdiction;
    public ImmunizationJurisdictionDto() {
    }

    public ImmunizationJurisdictionDto(
            String reportingUserUuid,
            ResponsibleJurisdictionDto responsibleJurisdiction) {
        this.reportingUserUuid = reportingUserUuid;
        this.responsibleJurisdiction = responsibleJurisdiction;
    }

    public String getReportingUserUuid() {
        return reportingUserUuid;
    }

    public void setReportingUserUuid(String reportingUserUuid) {
        this.reportingUserUuid = reportingUserUuid;
    }

    public ResponsibleJurisdictionDto getResponsibleJurisdiction() {
        return responsibleJurisdiction;
    }

    public void setResponsibleJurisdiction(ResponsibleJurisdictionDto responsibleJurisdiction) {
        this.responsibleJurisdiction = responsibleJurisdiction;
    }
	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}

	public PersonJurisdictionDto getPersonJurisdiction() {
		return personJurisdiction;
	}

	public void setPersonJurisdiction(PersonJurisdictionDto personJurisdiction) {
		this.personJurisdiction = personJurisdiction;
	}
}
