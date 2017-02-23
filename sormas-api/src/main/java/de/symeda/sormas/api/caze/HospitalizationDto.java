package de.symeda.sormas.api.caze;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class HospitalizationDto extends DataTransferObject {

	private static final long serialVersionUID = 4846215199480684369L;
	
	public static final String I18N_PREFIX = "CaseHospitalization";
	
	public static final String HOSPITALIZED = "hospitalized";
	public static final String HEALTH_FACILIY = "healthFacility";
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String ISOLATED = "isolated";
	public static final String ISOLATION_DATE = "isolationDate";
	public static final String HOSPITALIZED_PREVIOUSLY = "hospitalizedPreviously";
	
	private YesNoUnknown hospitalized;
	private FacilityReferenceDto healthFacility;
	private Date admissionDate;
	private YesNoUnknown isolated;
	private Date isolationDate;
	private YesNoUnknown hospitalizedPreviously;
	
	public YesNoUnknown getHospitalized() {
		return hospitalized;
	}
	public void setHospitalized(YesNoUnknown hospitalized) {
		this.hospitalized = hospitalized;
	}
	
	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getAdmissionDate() {
		return admissionDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}
	
	public YesNoUnknown getIsolated() {
		return isolated;
	}
	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}
	
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getIsolationDate() {
		return isolationDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}
	
	public YesNoUnknown getHospitalizedPreviously() {
		return hospitalizedPreviously;
	}
	public void setHospitalizedPreviously(YesNoUnknown hospitalizedPreviously) {
		this.hospitalizedPreviously = hospitalizedPreviously;
	}

}
