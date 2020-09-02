package de.symeda.sormas.api.therapy;

import java.io.Serializable;
import java.util.Date;

public class PrescriptionIndexDto implements Serializable {

	private static final long serialVersionUID = 9189039796173435070L;

	public static final String I18N_PREFIX = "Prescription";

	public static final String PRESCRIPTION_TYPE = "prescriptionType";
	public static final String PRESCRIPTION_DATE = "prescriptionDate";
	public static final String PRESCRIPTION_PERIOD = "prescriptionPeriod";
	public static final String FREQUENCY = "frequency";
	public static final String DOSE = "dose";
	public static final String ROUTE = "route";
	public static final String PRESCRIBING_CLINICIAN = "prescribingClinician";

	private String uuid;
	private String prescriptionType;
	private Date prescriptionDate;
	private PeriodDto prescriptionPeriod;
	private String frequency;
	private String dose;
	private String route;
	private String prescribingClinician;

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
		String prescribingClinician) {

		this.uuid = uuid;
		this.prescriptionType = TreatmentType.buildCaption(prescriptionType, prescriptionDetails, typeOfDrug);
		this.prescriptionDate = prescriptionDate;
		this.prescriptionPeriod = new PeriodDto(prescriptionStart, prescriptionEnd);
		this.frequency = frequency;
		this.dose = dose;
		this.route = TreatmentRoute.buildCaption(route, routeDetails);
		this.prescribingClinician = prescribingClinician;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPrescriptionType() {
		return prescriptionType;
	}

	public void setPrescriptionType(String prescriptionType) {
		this.prescriptionType = prescriptionType;
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

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getPrescribingClinician() {
		return prescribingClinician;
	}

	public void setPrescribingClinician(String prescribingClinician) {
		this.prescribingClinician = prescribingClinician;
	}
}
