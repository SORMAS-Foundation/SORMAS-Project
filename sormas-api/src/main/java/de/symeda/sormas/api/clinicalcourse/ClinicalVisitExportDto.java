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

package de.symeda.sormas.api.clinicalcourse;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;

@AuditedClass
public class ClinicalVisitExportDto implements Serializable {

	private static final long serialVersionUID = -5724133522485897878L;

	public static final String I18N_PREFIX = "ClinicalVisitExport";
	@AuditInclude
	private String caseUuid;
	@PersonalData
	private String caseName;
	private Disease disease;
	private Date visitDateTime;
	@SensitiveData
	private String visitRemarks;
	@SensitiveData
	private String visitingPerson;
	private long symptomsId;
	private SymptomsDto symptoms;

	private Boolean isInJurisdiction;

	public ClinicalVisitExportDto(
		String caseUuid,
		String caseFirstName,
		String caseLastName,
		Disease disease,
		Date visitDateTime,
		String visitRemarks,
		String visitingPerson,
		long symptomsId,
		boolean isInJurisdiction) {

		this.caseUuid = caseUuid;
		this.caseName = PersonDto.buildCaption(caseFirstName, caseLastName);
		this.disease = disease;
		this.visitDateTime = visitDateTime;
		this.visitRemarks = visitRemarks;
		this.visitingPerson = visitingPerson;
		this.symptomsId = symptomsId;
		this.isInJurisdiction = isInJurisdiction;
	}

	@Order(0)
	public String getCaseUuid() {
		return caseUuid;
	}

	@Order(1)
	public String getCaseName() {
		return caseName;
	}

	@Order(2)
	public Disease getDisease() {
		return disease;
	}

	@Order(3)
	public Date getVisitDateTime() {
		return visitDateTime;
	}

	@Order(4)
	public String getVisitRemarks() {
		return visitRemarks;
	}

	@Order(5)
	public String getVisitingPerson() {
		return visitingPerson;
	}

	@Order(6)
	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public long getSymptomsId() {
		return symptomsId;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public void setVisitingPerson(String visitingPerson) {
		this.visitingPerson = visitingPerson;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}

	public void setSymptomsId(long symptomsId) {
		this.symptomsId = symptomsId;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}
}
