package de.symeda.sormas.backend.hospitalization;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;

@Entity(name="previoushospitalization")
public class PreviousHospitalization extends AbstractDomainObject {

	private static final long serialVersionUID = 768263094433806267L;

	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String HEALTH_FACILIY = "healthFacility";
	public static final String ISOLATED = "isolated";
	public static final String DESCRIPTION = "description";
	public static final String HOSPITALIZATION = "hospitalization";
	
	private Date admissionDate;
	private Date dischargeDate;
	private Facility healthFacility;
	private YesNoUnknown isolated;
	private String description;
	private Hospitalization hospitalization;

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
	
	@ManyToOne(cascade = {})
	public Facility getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	@Enumerated(EnumType.STRING)
	public YesNoUnknown getIsolated() {
		return isolated;
	}
	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}
	
	@Column(length = 512)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public Hospitalization getHospitalization() {
		return hospitalization;
	}
	public void setHospitalization(Hospitalization hospitalization) {
		this.hospitalization = hospitalization;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PreviousHospitalization other = (PreviousHospitalization) obj;
		if (admissionDate == null) {
			if (other.admissionDate != null)
				return false;
		} else if (!admissionDate.equals(other.admissionDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (dischargeDate == null) {
			if (other.dischargeDate != null)
				return false;
		} else if (!dischargeDate.equals(other.dischargeDate))
			return false;
		if (healthFacility == null) {
			if (other.healthFacility != null)
				return false;
		} else if (!healthFacility.equals(other.healthFacility))
			return false;
		if (hospitalization == null) {
			if (other.hospitalization != null)
				return false;
		} else if (!hospitalization.equals(other.hospitalization))
			return false;
		if (isolated != other.isolated)
			return false;
		return true;
	}
	
}
