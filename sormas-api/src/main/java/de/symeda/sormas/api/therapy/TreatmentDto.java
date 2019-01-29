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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.therapy;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class TreatmentDto extends EntityDto {

	private static final long serialVersionUID = 816932182306785932L;
	
	public static final String I18N_PREFIX = "Treatment";
	
	public static final String TREATMENT_DATE_TIME = "treatmentDateTime";
	public static final String EXECUTING_CLINICIAN = "executingClinician";
	public static final String TREATMENT_TYPE = "treatmentType";
	public static final String TREATMENT_DETAILS = "treatmentDetails";
	public static final String DOSE = "dose";
	public static final String ROUTE = "route";
	public static final String ROUTE_DETAILS = "routeDetails";
	public static final String ADDITIONAL_NOTES = "additionalNotes";
	
	private TherapyDto therapy;
	private Date treatmentDateTime;
	private String executingClinician;
	private TreatmentType treatmentType;
	private String treatmentDetails;
	private String dose;
	private TreatmentRoute route;
	private String routeDetails;
	private String additionalNotes;
	
	public static TreatmentDto buildTreatment(TherapyDto therapy) {
		TreatmentDto treatment = new TreatmentDto();
		treatment.setUuid(DataHelper.createUuid());
		treatment.setTherapy(therapy);
		treatment.setTreatmentDateTime(new Date());
		
		return treatment;
	}
	
	public static TreatmentDto buildTreatment(PrescriptionDto prescription) {
		TreatmentDto treatment = new TreatmentDto();
		treatment.setUuid(DataHelper.createUuid());
		treatment.setTherapy(prescription.getTherapy());
		treatment.setTreatmentDateTime(new Date());
		treatment.setTreatmentType(prescription.getPrescriptionType());
		treatment.setTreatmentDetails(prescription.getPrescriptionDetails());
		treatment.setDose(prescription.getDose());
		treatment.setRoute(prescription.getRoute());
		treatment.setRouteDetails(prescription.getRouteDetails());
		
		return treatment;
	}
	
	public TherapyDto getTherapy() {
		return therapy;
	}
	public void setTherapy(TherapyDto therapy) {
		this.therapy = therapy;
	}
	public Date getTreatmentDateTime() {
		return treatmentDateTime;
	}
	public void setTreatmentDateTime(Date treatmentDateTime) {
		this.treatmentDateTime = treatmentDateTime;
	}
	public String getExecutingClinician() {
		return executingClinician;
	}
	public void setExecutingClinician(String executingClinician) {
		this.executingClinician = executingClinician;
	}
	public TreatmentType getTreatmentType() {
		return treatmentType;
	}
	public void setTreatmentType(TreatmentType treatmentType) {
		this.treatmentType = treatmentType;
	}
	public String getTreatmentDetails() {
		return treatmentDetails;
	}
	public void setTreatmentDetails(String treatmentDetails) {
		this.treatmentDetails = treatmentDetails;
	}
	public String getDose() {
		return dose;
	}
	public void setDose(String dose) {
		this.dose = dose;
	}
	public TreatmentRoute getRoute() {
		return route;
	}
	public void setRoute(TreatmentRoute route) {
		this.route = route;
	}
	public String getRouteDetails() {
		return routeDetails;
	}
	public void setRouteDetails(String routeDetails) {
		this.routeDetails = routeDetails;
	}
	public String getAdditionalNotes() {
		return additionalNotes;
	}
	public void setAdditionalNotes(String additionalNotes) {
		this.additionalNotes = additionalNotes;
	}
	
}
