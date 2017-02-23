package de.symeda.sormas.backend.caze;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.caze.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;

@Entity
public class Hospitalization extends AbstractDomainObject {

	private static final long serialVersionUID = -8576270649634034244L;

	public static final String HOSPITALIZED = "hospitalized";
	public static final String HEALTH_FACILIY = "healthFacility";
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String ISOLATED = "isolated";
	public static final String ISOLATION_DATE = "isolationDate";
	public static final String HOSPITALIZED_PREVIOUSLY = "hospitalizedPreviously";
	public static final String PREVIOUS_HOSPITALIZATIONS = "previousHospitalizations";
	
	private YesNoUnknown hospitalized;
	private Facility healthFacility;
	private Date admissionDate;
	private YesNoUnknown isolated;
	private Date isolationDate;
	private YesNoUnknown hospitalizedPreviously;
	
	private List<PreviousHospitalization> previousHospitalizations = new ArrayList<PreviousHospitalization>();
	
	
	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHospitalized() {
		return hospitalized;
	}
	public void setHospitalized(YesNoUnknown hospitalized) {
		this.hospitalized = hospitalized;
	}
	
	@ManyToOne(cascade = {})
	public Facility getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getAdmissionDate() {
		return admissionDate;
	}
	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
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
	
	@OneToMany(cascade = {}, mappedBy = PreviousHospitalization.HOSPITALIZATION)
	public List<PreviousHospitalization> getPreviousHospitalizations() {
		return previousHospitalizations;
	}
	public void setPreviousHospitalizations(List<PreviousHospitalization> previousHospitalizations) {
		this.previousHospitalizations = previousHospitalizations;
	}
	
}
