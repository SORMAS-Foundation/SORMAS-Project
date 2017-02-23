package de.symeda.sormas.api.caze;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class PreviousHospitalizationDto extends DataTransferObject {

	private static final long serialVersionUID = -7544440109802739018L;

	public static final String I18N_PREFIX = "CasePreviousHospitalization";
	
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String ISOLATED = "isolated";
	public static final String HOSPITALIZATION = "hospitalization";
	
	private Date admissionDate;
	private Date dischargeDate;
	private FacilityReferenceDto healthFacility;
	private YesNoUnknown isolated;
	private HospitalizationDto hospitalization;
	
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
	
	public HospitalizationDto getHospitalization() {
		return hospitalization;
	}
	public void setHospitalization(HospitalizationDto hospitalization) {
		this.hospitalization = hospitalization;
	}

}
