/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.therapy;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.SensitiveData;

public class TreatmentExportDto implements Serializable {

	private static final long serialVersionUID = 1165581587169924707L;

	public static final String I18N_PREFIX = "TreatmentExport";

	private String caseUuid;
	@SensitiveData
	private String caseName;
	private Date treatmentDateTime;
	@SensitiveData
	private String executingClinician;
	private TreatmentType treatmentType;
	@SensitiveData
	private String treatmentDetails;
	private TypeOfDrug typeOfDrug;
	private String dose;
	private TreatmentRoute route;
	@SensitiveData
	private String routeDetails;
	@SensitiveData
	private String additionalNotes;

	private CaseJurisdictionDto caseJurisdiction;

	public TreatmentExportDto(
		String caseUuid,
		String caseFirstName,
		String caseLastName,
		Date treatmentDateTime,
		String executingClinician,
		TreatmentType treatmentType,
		String treatmentDetails,
		TypeOfDrug typeOfDrug,
		String dose,
		TreatmentRoute route,
		String routeDetails,
		String additionalNotes,
		String caseReportingUserUuid,
		String caseRegionUuid,
		String caseDistrictUuid,
		String caseCommunityUuid,
		String caseHealthFacilityUuid,
		String casePointOfEntryUuid) {

		this.caseUuid = caseUuid;
		this.caseName = PersonDto.buildCaption(caseFirstName, caseLastName);
		this.treatmentDateTime = treatmentDateTime;
		this.executingClinician = executingClinician;
		this.treatmentType = treatmentType;
		this.treatmentDetails = treatmentDetails;
		this.typeOfDrug = typeOfDrug;
		this.dose = dose;
		this.route = route;
		this.routeDetails = routeDetails;
		this.additionalNotes = additionalNotes;

		this.caseJurisdiction = new CaseJurisdictionDto(
			caseReportingUserUuid,
			caseRegionUuid,
			caseDistrictUuid,
			caseCommunityUuid,
			caseHealthFacilityUuid,
			casePointOfEntryUuid);
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
	public Date getTreatmentDateTime() {
		return treatmentDateTime;
	}

	@Order(3)
	public String getExecutingClinician() {
		return executingClinician;
	}

	@Order(4)
	public TreatmentType getTreatmentType() {
		return treatmentType;
	}

	@Order(5)
	public String getTreatmentDetails() {
		return treatmentDetails;
	}

	@Order(10)
	public TypeOfDrug getTypeOfDrug() {
		return typeOfDrug;
	}

	@Order(11)
	public String getDose() {
		return dose;
	}

	@Order(12)
	public TreatmentRoute getRoute() {
		return route;
	}

	@Order(13)
	public String getRouteDetails() {
		return routeDetails;
	}

	@Order(14)
	public String getAdditionalNotes() {
		return additionalNotes;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public void setTreatmentDateTime(Date treatmentDateTime) {
		this.treatmentDateTime = treatmentDateTime;
	}

	public void setExecutingClinician(String executingClinician) {
		this.executingClinician = executingClinician;
	}

	public void setTreatmentType(TreatmentType treatmentType) {
		this.treatmentType = treatmentType;
	}

	public void setTreatmentDetails(String treatmentDetails) {
		this.treatmentDetails = treatmentDetails;
	}

	public void setTypeOfDrug(TypeOfDrug typeOfDrug) {
		this.typeOfDrug = typeOfDrug;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public void setRoute(TreatmentRoute route) {
		this.route = route;
	}

	public void setRouteDetails(String routeDetails) {
		this.routeDetails = routeDetails;
	}

	public void setAdditionalNotes(String additionalNotes) {
		this.additionalNotes = additionalNotes;
	}

	public CaseJurisdictionDto getCaseJurisdiction() {
		return caseJurisdiction;
	}
}
