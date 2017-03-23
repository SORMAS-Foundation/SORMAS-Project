package de.symeda.sormas.api.hospitalization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.utils.PreciseDateAdapter;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class HospitalizationDto extends DataTransferObject {

	private static final long serialVersionUID = 4846215199480684369L;
	
	public static final String I18N_PREFIX = "CaseHospitalization";
	
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String ISOLATED = "isolated";
	public static final String ISOLATION_DATE = "isolationDate";
	public static final String HOSPITALIZED_PREVIOUSLY = "hospitalizedPreviously";
	public static final String PREVIOUS_HOSPITALIZATIONS = "previousHospitalizations";
	
	private Date admissionDate;
	private Date dischargeDate;
	private YesNoUnknown isolated;
	private Date isolationDate;
	private YesNoUnknown hospitalizedPreviously;
	
	private List<PreviousHospitalizationDto> previousHospitalizations = new ArrayList<>();
	
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
	
	public List<PreviousHospitalizationDto> getPreviousHospitalizations() {
		return previousHospitalizations;
	}
	public void setPreviousHospitalizations(List<PreviousHospitalizationDto> previousHospitalizations) {
		this.previousHospitalizations = previousHospitalizations;
	}
}
