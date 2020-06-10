package de.symeda.sormas.api.therapy;

import java.io.Serializable;
import java.util.Date;

public class TreatmentIndexDto implements Serializable {

	private static final long serialVersionUID = 8736174497617079947L;

	public static final String I18N_PREFIX = "Treatment";

	public static final String TREATMENT_TYPE = "treatmentType";
	public static final String TREATMENT_DATE_TIME = "treatmentDateTime";
	public static final String DOSE = "dose";
	public static final String ROUTE = "route";
	public static final String EXECUTING_CLINICIAN = "executingClinician";

	private String uuid;
	private String treatmentType;
	private Date treatmentDateTime;
	private String dose;
	private String route;
	private String executingClinician;

	public TreatmentIndexDto(
		String uuid,
		TreatmentType treatmentType,
		String treatmentDetails,
		TypeOfDrug typeOfDrug,
		Date treatmentDateTime,
		String dose,
		TreatmentRoute route,
		String routeDetails,
		String executingClinician) {

		this.uuid = uuid;
		this.treatmentType = TreatmentType.buildCaption(treatmentType, treatmentDetails, typeOfDrug);
		this.treatmentDateTime = treatmentDateTime;
		this.dose = dose;
		this.route = TreatmentRoute.buildCaption(route, routeDetails);
		this.executingClinician = executingClinician;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTreatmentType() {
		return treatmentType;
	}

	public void setTreatmentType(String treatmentType) {
		this.treatmentType = treatmentType;
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

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getExecutingClinician() {
		return executingClinician;
	}

	public void setExecutingClinician(String executingClinician) {
		this.executingClinician = executingClinician;
	}
}
