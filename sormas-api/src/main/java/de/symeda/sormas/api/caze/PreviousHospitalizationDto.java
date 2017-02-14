package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.facility.FacilityReferenceDto;

public class PreviousHospitalizationDto extends DataTransferObject {

	private static final long serialVersionUID = -7544440109802739018L;

	public static final String I18N_PREFIX = "CasePreviousHospitalization";
	
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String ISOLATION = "isolation";
	public static final String DESCRIPTION = "description";
	
	private Date admissionDate;
	private Date dischargeDate;
	private FacilityReferenceDto healthFacility;
	private Boolean isolation;
	private String description;
	
	public Date getAdmissionDate() {
		return admissionDate;
	}
	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}
	public Date getDischargeDate() {
		return dischargeDate;
	}
	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}
	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}
	public Boolean getIsolation() {
		return isolation;
	}
	public void setIsolation(Boolean isolation) {
		this.isolation = isolation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
