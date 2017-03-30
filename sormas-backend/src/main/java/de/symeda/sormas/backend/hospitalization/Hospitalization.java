package de.symeda.sormas.backend.hospitalization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
public class Hospitalization extends AbstractDomainObject {

	private static final long serialVersionUID = -8576270649634034244L;

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
	
	private List<PreviousHospitalization> previousHospitalizations = new ArrayList<PreviousHospitalization>();
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getAdmissionDate() {
		return admissionDate;
	}
	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDischargeDate() {
		return dischargeDate;
	}
	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}
	
	@Enumerated(EnumType.STRING)
	public YesNoUnknown getIsolated() {
		return isolated;
	}
	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getIsolationDate() {
		return isolationDate;
	}
	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}
	
	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHospitalizedPreviously() {
		return hospitalizedPreviously;
	}
	public void setHospitalizedPreviously(YesNoUnknown hospitalizedPreviously) {
		this.hospitalizedPreviously = hospitalizedPreviously;
	}
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = PreviousHospitalization.HOSPITALIZATION)
	public List<PreviousHospitalization> getPreviousHospitalizations() {
		return previousHospitalizations;
	}
	public void setPreviousHospitalizations(List<PreviousHospitalization> previousHospitalizations) {
		this.previousHospitalizations = previousHospitalizations;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hospitalization other = (Hospitalization) obj;
		if (admissionDate == null) {
			if (other.admissionDate != null)
				return false;
		} else if (!admissionDate.equals(other.admissionDate))
			return false;
		if (dischargeDate == null) {
			if (other.dischargeDate != null)
				return false;
		} else if (!dischargeDate.equals(other.dischargeDate))
			return false;
		if (hospitalizedPreviously != other.hospitalizedPreviously)
			return false;
		if (isolated != other.isolated)
			return false;
		if (isolationDate == null) {
			if (other.isolationDate != null)
				return false;
		} else if (!isolationDate.equals(other.isolationDate))
			return false;
		if (previousHospitalizations == null) {
			if (other.previousHospitalizations != null)
				return false;
		} else if (!previousHospitalizations.equals(other.previousHospitalizations))
			return false;
		return true;
	}
	
}
