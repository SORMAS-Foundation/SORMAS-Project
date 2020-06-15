package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.utils.SensitiveData;

import java.io.Serializable;
import java.util.Date;

public class TreatmentIndexDto implements Serializable {

	private static final long serialVersionUID = 8736174497617079947L;

	public static final String I18N_PREFIX = "Treatment";

	public static final String TREATMENT_TYPE = "treatmentType";
	public static final String TREATMENT_DATE_TIME = "treatmentDateTime";
	public static final String DOSE = "dose";
	public static final String TREATMENT_ROUTE = "treatmentRoute";
	public static final String EXECUTING_CLINICIAN = "executingClinician";

	private String uuid;
	private Type type;
	private Date treatmentDateTime;
	private String dose;
	private Route route;
	@SensitiveData
	private String executingClinician;

	private CaseJurisdictionDto caseJurisdiction;

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
		String caseReportingUserUuid,
		String caseRegionUuid,
		String caseDistrictUuid,
		String caseCommunityUuid,
		String caseHealthFacilityUuid,
		String casePointOfEntryUuid) {

		this.uuid = uuid;
		this.type = new Type(treatmentType, treatmentDetails, typeOfDrug);
		this.treatmentDateTime = treatmentDateTime;
		this.dose = dose;
		this.route = new Route(route, routeDetails);
		this.executingClinician = executingClinician;

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

	public Type getType() {
		return type;
	}

	public String getTreatmentType() {
		return type.stringFormat();
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

	public Route getRoute() {
		return route;
	}

	public String getTreatmentRoute() {
		return route.stringFormat();
	}

	public String getExecutingClinician() {
		return executingClinician;
	}

	public void setExecutingClinician(String executingClinician) {
		this.executingClinician = executingClinician;
	}

	public CaseJurisdictionDto getCaseJurisdiction() {
		return caseJurisdiction;
	}

	public static class Type implements Serializable {

		private TreatmentType treatmentType;
		@SensitiveData
		private String treatmentDetails;
		private TypeOfDrug typeOfDrug;

		public Type(TreatmentType treatmentType, String treatmentDetails, TypeOfDrug typeOfDrug) {
			this.treatmentType = treatmentType;
			this.treatmentDetails = treatmentDetails;
			this.typeOfDrug = typeOfDrug;
		}

		public String stringFormat() {
			return TreatmentType.buildCaption(treatmentType, treatmentDetails, typeOfDrug);
		}
	}

	public static class Route implements Serializable {

		private TreatmentRoute route;
		@SensitiveData
		private String routeDetails;

		public Route(TreatmentRoute route, String routeDetails) {
			this.route = route;
			this.routeDetails = routeDetails;
		}

		public String stringFormat() {
			return TreatmentRoute.buildCaption(route, routeDetails);
		}
	}
}
