package de.symeda.sormas.api.therapy;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.utils.SensitiveData;

public class PrescriptionIndexDto implements Serializable {

	private static final long serialVersionUID = 9189039796173435070L;

	public static final String I18N_PREFIX = "Prescription";

	public static final String PRESCRIPTION_TYPE = "prescriptionType";
	public static final String PRESCRIPTION_DATE = "prescriptionDate";
	public static final String PRESCRIPTION_PERIOD = "prescriptionPeriod";
	public static final String FREQUENCY = "frequency";
	public static final String DOSE = "dose";
	public static final String PRESCRIPTION_ROUTE = "prescriptionRoute";
	public static final String PRESCRIBING_CLINICIAN = "prescribingClinician";

	private String uuid;
	private PrescriptionIndexType prescriptionIndexType;
	private Date prescriptionDate;
	private PeriodDto prescriptionPeriod;
	private String frequency;
	private String dose;
	private PrescriptionIndexRoute prescriptionIndexRoute;
	@SensitiveData
	private String prescribingClinician;

	private CaseJurisdictionDto caseJurisdiction;

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
		String caseReportingUserUuid,
		String caseRegionUuid,
		String caseDistrictUuid,
		String caseCommunityUuid,
		String caseHealthFacilityUuid,
		String casePointOfEntryUuid) {

		this.uuid = uuid;
		this.prescriptionIndexType = new PrescriptionIndexType(prescriptionType, prescriptionDetails, typeOfDrug);
		this.prescriptionDate = prescriptionDate;
		this.prescriptionPeriod = new PeriodDto(prescriptionStart, prescriptionEnd);
		this.frequency = frequency;
		this.dose = dose;
		this.prescriptionIndexRoute = new PrescriptionIndexRoute(route, routeDetails);
		this.prescribingClinician = prescribingClinician;

		this.caseJurisdiction = new CaseJurisdictionDto(
			caseReportingUserUuid,
			caseRegionUuid,
			caseDistrictUuid,
			caseCommunityUuid,
			caseHealthFacilityUuid,
			casePointOfEntryUuid);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public CaseJurisdictionDto getCaseJurisdiction() {
		return caseJurisdiction;
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

		public String formatString() {
			return TreatmentType.buildCaption(prescriptionType, prescriptionDetails, typeOfDrug);
		}
	}

	public static class PrescriptionIndexRoute implements Serializable {

		private TreatmentRoute route;
		@SensitiveData
		private String routeDetails;

		public PrescriptionIndexRoute(TreatmentRoute route, String routeDetails) {
			this.route = route;
			this.routeDetails = routeDetails;
		}

		public String formatString() {
			return TreatmentRoute.buildCaption(route, routeDetails);
		}
	}
}
