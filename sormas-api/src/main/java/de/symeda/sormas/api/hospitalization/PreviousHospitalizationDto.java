package de.symeda.sormas.api.hospitalization;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class PreviousHospitalizationDto extends DataTransferObject {

	private static final long serialVersionUID = -7544440109802739018L;

	public static final String I18N_PREFIX = "CasePreviousHospitalization";
	
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String ISOLATED = "isolated";
	public static final String DESCRIPTION = "description";
	
	private Date admissionDate;
	private Date dischargeDate;
	private FacilityReferenceDto healthFacility;
	private YesNoUnknown isolated;
	private String description;
	
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getAdmissionDate() {
		return admissionDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}
	
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getDischargeDate() {
		return dischargeDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}
	
	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	public YesNoUnknown getIsolated() {
		return isolated;
	}
	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
