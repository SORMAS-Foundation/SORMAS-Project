package de.symeda.sormas.app.backend.caze;

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
import de.symeda.sormas.app.backend.user.User;

@Entity(name=Case.TABLE_NAME)
@DatabaseTable(tableName = Case.TABLE_NAME)
public class Case extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2697795184163562129L;

	public static final String TABLE_NAME = "cases";

	public static final String PERSON = "person";

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false)
	private Person person;

	@Column(length=512)
	private String description;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@Enumerated(EnumType.STRING)
	private CaseStatus caseStatus;

	@ManyToOne(cascade = {})
	private Facility healthFacility;

	@ManyToOne(cascade = {})
	private User reportingUser;
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date investigatedDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date suspectDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date confirmedDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date negativeDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date postiveDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date noCaseDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date recoveredDate;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Location illLocation;

	@ManyToOne(cascade = {})
	private User surveillanceOfficer;
	@ManyToOne(cascade = {})
	private User surveillanceSupervisor;
	@ManyToOne(cascade = {})
	private User caseOfficer;
	@ManyToOne(cascade = {})
	private User caseSupervisor;
	@ManyToOne(cascade = {})
	private User contactOfficer;
	@ManyToOne(cascade = {})
	private User contactSupervisor;
	
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

	public Date getSuspectDate() {
		return suspectDate;
	}
	public void setSuspectDate(Date suspectDate) {
		this.suspectDate = suspectDate;
	}

	public Date getConfirmedDate() {
		return confirmedDate;
	}
	public void setConfirmedDate(Date confirmedDate) {
		this.confirmedDate = confirmedDate;
	}

	public Date getNegativeDate() {
		return negativeDate;
	}
	public void setNegativeDate(Date negativeDate) {
		this.negativeDate = negativeDate;
	}

	public Date getPostiveDate() {
		return postiveDate;
	}
	public void setPostiveDate(Date postiveDate) {
		this.postiveDate = postiveDate;
	}

	public Date getNoCaseDate() {
		return noCaseDate;
	}
	public void setNoCaseDate(Date noCaseDate) {
		this.noCaseDate = noCaseDate;
	}

	public Date getRecoveredDate() {
		return recoveredDate;
	}
	public void setRecoveredDate(Date recoveredDate) {
		this.recoveredDate = recoveredDate;
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

	public User getSurveillanceOfficer() {
		return surveillanceOfficer;
	}
	public void setSurveillanceOfficer(User surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public User getSurveillanceSupervisor() {
		return surveillanceSupervisor;
	}
	public void setSurveillanceSupervisor(User surveillanceSupervisor) {
		this.surveillanceSupervisor = surveillanceSupervisor;
	}

	public User getCaseOfficer() {
		return caseOfficer;
	}
	public void setCaseOfficer(User caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public User getCaseSupervisor() {
		return caseSupervisor;
	}
	public void setCaseSupervisor(User caseSupervisor) {
		this.caseSupervisor = caseSupervisor;
	}

	public User getContactOfficer() {
		return contactOfficer;
	}
	public void setContactOfficer(User contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public User getContactSupervisor() {
		return contactSupervisor;
	}
	public void setContactSupervisor(User contactSupervisor) {
		this.contactSupervisor = contactSupervisor;
	}

}
