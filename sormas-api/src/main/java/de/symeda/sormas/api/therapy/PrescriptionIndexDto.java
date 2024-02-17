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

package de.symeda.sormas.api.therapy;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class PrescriptionIndexDto extends AbstractUuidDto {

	private static final long serialVersionUID = 9189039796173435070L;

	public static final String I18N_PREFIX = "Prescription";

	public static final String PRESCRIPTION_TYPE = "prescriptionType";
	public static final String PRESCRIPTION_DATE = "prescriptionDate";
	public static final String PRESCRIPTION_PERIOD = "prescriptionPeriod";
	public static final String FREQUENCY = "frequency";
	public static final String DOSE = "dose";
	public static final String PRESCRIPTION_ROUTE = "prescriptionRoute";
	public static final String PRESCRIBING_CLINICIAN = "prescribingClinician";

	@EmbeddedSensitiveData
	private PrescriptionIndexType prescriptionIndexType;
	private Date prescriptionDate;
	private PeriodDto prescriptionPeriod;
	private String frequency;
	private String dose;
	@EmbeddedSensitiveData
	private PrescriptionIndexRoute prescriptionIndexRoute;
	@SensitiveData
	private String prescribingClinician;

	private Boolean isInJurisdiction;

	public PrescriptionIndexDto(
		String uuid,
		TreatmentType prescriptionType,
		String prescriptionDetails,
		TypeOfDrug typeOfDrug,
		Date prescriptionDate,
		Date prescriptionStart,
		Date prescriptionEnd,
		String frequency,
		String dose,
		TreatmentRoute route,
		String routeDetails,
		String prescribingClinician,
		boolean isInJurisdiction) {

		super(uuid);
		this.prescriptionIndexType = new PrescriptionIndexType(prescriptionType, prescriptionDetails, typeOfDrug);
		this.prescriptionDate = prescriptionDate;
		this.prescriptionPeriod = new PeriodDto(prescriptionStart, prescriptionEnd);
		this.frequency = frequency;
		this.dose = dose;
		this.prescriptionIndexRoute = new PrescriptionIndexRoute(route, routeDetails);
		this.prescribingClinician = prescribingClinician;
		this.isInJurisdiction = isInJurisdiction;
	}

	public String getPrescriptionType() {
		return prescriptionIndexType.formatString();
	}

	public PrescriptionIndexType getPrescriptionIndexType() {
		return prescriptionIndexType;
	}

	public Date getPrescriptionDate() {
		return prescriptionDate;
	}

	public void setPrescriptionDate(Date prescriptionDate) {
		this.prescriptionDate = prescriptionDate;
	}

	public PeriodDto getPrescriptionPeriod() {
		return prescriptionPeriod;
	}

	public void setPrescriptionPeriod(PeriodDto prescriptionPeriod) {
		this.prescriptionPeriod = prescriptionPeriod;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getDose() {
		return dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public String getPrescriptionRoute() {
		return prescriptionIndexRoute.formatString();
	}

	public PrescriptionIndexRoute getPrescriptionIndexRoute() {
		return prescriptionIndexRoute;
	}

	public String getPrescribingClinician() {
		return prescribingClinician;
	}

	public void setPrescribingClinician(String prescribingClinician) {
		this.prescribingClinician = prescribingClinician;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}

	public static class PrescriptionIndexType implements Serializable {

		private TreatmentType prescriptionType;
		@SensitiveData
		private String prescriptionDetails;
		private TypeOfDrug typeOfDrug;

		public PrescriptionIndexType(TreatmentType prescriptionType, String prescriptionDetails, TypeOfDrug typeOfDrug) {
			this.prescriptionType = prescriptionType;
			this.prescriptionDetails = prescriptionDetails;
			this.typeOfDrug = typeOfDrug;
		}

		public String getPrescriptionDetails() {
			return prescriptionDetails;
		}

		public TypeOfDrug getTypeOfDrug() {
			return typeOfDrug;
		}

		public String formatString() {
			return TreatmentType.buildCaption(prescriptionType, prescriptionDetails, typeOfDrug);
		}
	}

	public static class PrescriptionIndexRoute implements Serializable {

		private TreatmentRoute route;
		@SensitiveData
		private String routeDetails;

		public TreatmentRoute getRoute() {
			return route;
		}

		public String getRouteDetails() {
			return routeDetails;
		}

		public PrescriptionIndexRoute(TreatmentRoute route, String routeDetails) {
			this.route = route;
			this.routeDetails = routeDetails;
		}

		public String formatString() {
			return TreatmentRoute.buildCaption(route, routeDetails);
		}
	}
}
