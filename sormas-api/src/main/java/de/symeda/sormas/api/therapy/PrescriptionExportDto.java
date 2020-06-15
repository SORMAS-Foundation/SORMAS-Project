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

public class PrescriptionExportDto implements Serializable {

	private static final long serialVersionUID = -8150207970598300997L;

	public static final String I18N_PREFIX = "PrescriptionExport";

	private String caseUuid;
	@SensitiveData
	private String caseName;
	private Date prescriptionDate;
	private Date prescriptionStart;
	private Date prescriptionEnd;
	@SensitiveData
	private String prescribingClinician;
	private TreatmentType prescriptionType;
	@SensitiveData
	private String prescriptionDetails;
	private TypeOfDrug typeOfDrug;
	private String frequency;
	private String dose;
	private TreatmentRoute route;
	@SensitiveData
	private String routeDetails;
	@SensitiveData
	private String additionalNotes;

	private CaseJurisdictionDto caseJurisdiction;

	public PrescriptionExportDto(
		String caseUuid,
		String caseFirstName,
		String caseLastName,
		Date prescriptionDate,
		Date prescriptionStart,
		Date prescriptionEnd,
		String prescribingClinician,
		TreatmentType prescriptionType,
		String prescriptionDetails,
		TypeOfDrug typeOfDrug,
		String frequency,
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
		this.prescriptionDate = prescriptionDate;
		this.prescriptionStart = prescriptionStart;
		this.prescriptionEnd = prescriptionEnd;
		this.prescribingClinician = prescribingClinician;
		this.prescriptionType = prescriptionType;
		this.prescriptionDetails = prescriptionDetails;
		this.typeOfDrug = typeOfDrug;
		this.frequency = frequency;
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
	public Date getPrescriptionDate() {
		return prescriptionDate;
	}

	@Order(3)
	public Date getPrescriptionStart() {
		return prescriptionStart;
	}

	@Order(4)
	public Date getPrescriptionEnd() {
		return prescriptionEnd;
	}

	@Order(10)
	public String getPrescribingClinician() {
		return prescribingClinician;
	}

	@Order(11)
	public TreatmentType getPrescriptionType() {
		return prescriptionType;
	}

	@Order(12)
	public String getPrescriptionDetails() {
		return prescriptionDetails;
	}

	@Order(13)
	public TypeOfDrug getTypeOfDrug() {
		return typeOfDrug;
	}

	@Order(14)
	public String getFrequency() {
		return frequency;
	}

	@Order(15)
	public String getDose() {
		return dose;
	}

	@Order(20)
	public TreatmentRoute getRoute() {
		return route;
	}

	@Order(21)
	public String getRouteDetails() {
		return routeDetails;
	}

	@Order(22)
	public String getAdditionalNotes() {
		return additionalNotes;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public void setPrescriptionDate(Date prescriptionDate) {
		this.prescriptionDate = prescriptionDate;
	}

	public void setPrescriptionStart(Date prescriptionStart) {
		this.prescriptionStart = prescriptionStart;
	}

	public void setPrescriptionEnd(Date prescriptionEnd) {
		this.prescriptionEnd = prescriptionEnd;
	}

	public void setPrescribingClinician(String prescribingClinician) {
		this.prescribingClinician = prescribingClinician;
	}

	public void setPrescriptionType(TreatmentType prescriptionType) {
		this.prescriptionType = prescriptionType;
	}

	public void setPrescriptionDetails(String prescriptionDetails) {
		this.prescriptionDetails = prescriptionDetails;
	}

	public void setTypeOfDrug(TypeOfDrug typeOfDrug) {
		this.typeOfDrug = typeOfDrug;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
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
