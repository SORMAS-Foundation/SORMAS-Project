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

public class TreatmentIndexDto extends AbstractUuidDto {

	private static final long serialVersionUID = 8736174497617079947L;

	public static final String I18N_PREFIX = "Treatment";

	public static final String TREATMENT_TYPE = "treatmentType";
	public static final String TREATMENT_DATE_TIME = "treatmentDateTime";
	public static final String DOSE = "dose";
	public static final String TREATMENT_ROUTE = "treatmentRoute";
	public static final String EXECUTING_CLINICIAN = "executingClinician";
	@EmbeddedSensitiveData
	private TreatmentIndexType treatmentIndexType;
	private Date treatmentDateTime;
	private String dose;
	@EmbeddedSensitiveData
	private TreatmentIndexRoute treatmentIndexRoute;
	@SensitiveData
	private String executingClinician;

	private Boolean isInJurisdiction;

	public TreatmentIndexDto(
		String uuid,
		TreatmentType treatmentType,
		String treatmentDetails,
		TypeOfDrug typeOfDrug,
		Date treatmentDateTime,
		String dose,
		TreatmentRoute route,
		String routeDetails,
		String executingClinician,
		boolean isInJurisdiction) {
		super(uuid);
		this.treatmentIndexType = new TreatmentIndexType(treatmentType, treatmentDetails, typeOfDrug);
		this.treatmentDateTime = treatmentDateTime;
		this.dose = dose;
		this.treatmentIndexRoute = new TreatmentIndexRoute(route, routeDetails);
		this.executingClinician = executingClinician;
		this.isInJurisdiction = isInJurisdiction;
	}

	public TreatmentIndexType getTreatmentIndexType() {
		return treatmentIndexType;
	}

	public String getTreatmentType() {
		return treatmentIndexType.formatString();
	}

	public Date getTreatmentDateTime() {
		return treatmentDateTime;
	}

	public void setTreatmentDateTime(Date treatmentDateTime) {
		this.treatmentDateTime = treatmentDateTime;
	}

	public String getDose() {
		return dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public TreatmentIndexRoute getTreatmentIndexRoute() {
		return treatmentIndexRoute;
	}

	public String getTreatmentRoute() {
		return treatmentIndexRoute.formatString();
	}

	public String getExecutingClinician() {
		return executingClinician;
	}

	public void setExecutingClinician(String executingClinician) {
		this.executingClinician = executingClinician;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}

	public static class TreatmentIndexType implements Serializable {

		private TreatmentType treatmentType;
		@SensitiveData
		private String treatmentDetails;
		private TypeOfDrug typeOfDrug;

		public TreatmentIndexType(TreatmentType treatmentType, String treatmentDetails, TypeOfDrug typeOfDrug) {
			this.treatmentType = treatmentType;
			this.treatmentDetails = treatmentDetails;
			this.typeOfDrug = typeOfDrug;
		}

		public String getTreatmentDetails() {
			return treatmentDetails;
		}

		public TypeOfDrug getTypeOfDrug() {
			return typeOfDrug;
		}

		public String formatString() {
			return TreatmentType.buildCaption(treatmentType, treatmentDetails, typeOfDrug);
		}
	}

	public static class TreatmentIndexRoute implements Serializable {

		private TreatmentRoute route;
		@SensitiveData
		private String routeDetails;

		public TreatmentIndexRoute(TreatmentRoute route, String routeDetails) {
			this.route = route;
			this.routeDetails = routeDetails;
		}

		public TreatmentRoute getRoute() {
			return route;
		}

		public String getRouteDetails() {
			return routeDetails;
		}

		public String formatString() {
			return TreatmentRoute.buildCaption(route, routeDetails);
		}
	}
}
