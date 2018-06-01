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

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Entity(name="previoushospitalization")
@Audited
public class PreviousHospitalization extends AbstractDomainObject {

	private static final long serialVersionUID = 768263094433806267L;

	public static final String TABLE_NAME = "previoushospitalization";
	
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILIY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String ISOLATED = "isolated";
	public static final String DESCRIPTION = "description";
	public static final String HOSPITALIZATION = "hospitalization";
	
	private Date admissionDate;
	private Date dischargeDate;
	private Region region;
	private District district;
	private Community community;
	private Facility healthFacility;
	private String healthFacilityDetails;
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
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	
	@ManyToOne(cascade = {})
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}
	
	@ManyToOne(cascade = {})
	public Community getCommunity() {
		return community;
	}
	public void setCommunity(Community community) {
		this.community = community;
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

	@Column(length = 512)
	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}
	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
	}
	
}
