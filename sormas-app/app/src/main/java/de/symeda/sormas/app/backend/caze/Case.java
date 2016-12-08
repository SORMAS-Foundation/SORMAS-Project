package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.user.User;

@Entity(name=Case.TABLE_NAME)
@DatabaseTable(tableName = Case.TABLE_NAME)
public class Case extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2697795184163562129L;

	public static final String TABLE_NAME = "cases";
	public static final String CASE_STATUS = "caseStatus";

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false, unique = true)
	private Person person;

	@Column(length=512)
	private String description;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@Enumerated(EnumType.STRING)
	private CaseStatus caseStatus;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Region region;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private District district;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Community community;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility healthFacility;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Symptoms symptoms;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date reportDate;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date investigatedDate;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Location illLocation;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1)
	private User surveillanceOfficer;
	@ManyToOne(cascade = {})
	private User caseOfficer;
	@ManyToOne(cascade = {})
	private User contactOfficer;

	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public CaseStatus getCaseStatus() {
		return caseStatus;
	}
	public void setCaseStatus(CaseStatus caseStatus) {
		this.caseStatus = caseStatus;
	}

	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}

	public Community getCommunity() {
		return community;
	}
	public void setCommunity(Community community) {
		this.community = community;
	}
	
	public User getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}
	
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Date getInvestigatedDate() {
		return investigatedDate;
	}
	public void setInvestigatedDate(Date investigatedDate) {
		this.investigatedDate = investigatedDate;
	}
	
	public Facility getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}
	public Location getIllLocation() {
		return illLocation;
	}
	public void setIllLocation(Location illLocation) {
		this.illLocation = illLocation;
	}

	public Symptoms getSymptoms() {
		return symptoms;
	}
	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}

	public User getSurveillanceOfficer() {
		return surveillanceOfficer;
	}
	public void setSurveillanceOfficer(User surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public User getCaseOfficer() {
		return caseOfficer;
	}
	public void setCaseOfficer(User caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public User getContactOfficer() {
		return contactOfficer;
	}
	public void setContactOfficer(User contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

}
