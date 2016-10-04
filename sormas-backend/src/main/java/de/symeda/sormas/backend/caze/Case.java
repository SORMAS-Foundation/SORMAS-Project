package de.symeda.sormas.backend.caze;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;

@Entity(name="cases")
public class Case extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2697795184663562129L;

	public static final String CASE_STATUS = "caseStatus";
	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE = "reportDate";
	public static final String INVESTIGATED_DATE = "investigatedDate";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String SURVEILLANCE_SUPERVISOR = "surveillanceSupervisor";
	public static final String CASE_OFFICER = "caseOfficer";
	public static final String CASE_SUPERVISOR = "caseSupervisor";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String CONTACT_SUPERVISOR = "contactSupervisor";
	
	private Person person;
	private String description;
	private Disease disease;
	private CaseStatus caseStatus;
	private Facility healthFacility;
	
	private User reportingUser;
	private Date reportDate;
	private Date investigatedDate;
	private Date suspectDate;
	private Date confirmedDate;
	private Date negativeDate;
	private Date postiveDate;
	private Date noCaseDate;
	private Date recoveredDate;
	
	private Location illLocation;
	
	private User surveillanceOfficer;
	private User surveillanceSupervisor;
	private User caseOfficer;
	private User caseSupervisor;
	private User contactOfficer;
	private User contactSupervisor;
	
	private Symptoms symptoms;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable=false)
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	
	@Column(length=512)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	@Enumerated(EnumType.STRING)
	public CaseStatus getCaseStatus() {
		return caseStatus;
	}
	public void setCaseStatus(CaseStatus caseStatus) {
		this.caseStatus = caseStatus;
	}
	
	@ManyToOne(cascade = {})
	public User getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getInvestigatedDate() {
		return investigatedDate;
	}
	public void setInvestigatedDate(Date investigatedDate) {
		this.investigatedDate = investigatedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSuspectDate() {
		return suspectDate;
	}
	public void setSuspectDate(Date suspectDate) {
		this.suspectDate = suspectDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getConfirmedDate() {
		return confirmedDate;
	}
	public void setConfirmedDate(Date confirmedDate) {
		this.confirmedDate = confirmedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getNegativeDate() {
		return negativeDate;
	}
	public void setNegativeDate(Date negativeDate) {
		this.negativeDate = negativeDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPostiveDate() {
		return postiveDate;
	}
	public void setPostiveDate(Date postiveDate) {
		this.postiveDate = postiveDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getNoCaseDate() {
		return noCaseDate;
	}
	public void setNoCaseDate(Date noCaseDate) {
		this.noCaseDate = noCaseDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getRecoveredDate() {
		return recoveredDate;
	}
	public void setRecoveredDate(Date recoveredDate) {
		this.recoveredDate = recoveredDate;
	}
	
	@ManyToOne(cascade = {})
	public Facility getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	@ManyToOne(cascade = CascadeType.ALL)
	public Location getIllLocation() {
		if (illLocation == null) {
			illLocation = new Location();
		}
		return illLocation;
	}
	public void setIllLocation(Location illLocation) {
		this.illLocation = illLocation;
	}
	
	@ManyToOne(cascade = {})
	public User getSurveillanceOfficer() {
		return surveillanceOfficer;
	}
	public void setSurveillanceOfficer(User surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}
	
	@ManyToOne(cascade = {})
	public User getSurveillanceSupervisor() {
		return surveillanceSupervisor;
	}
	public void setSurveillanceSupervisor(User surveillanceSupervisor) {
		this.surveillanceSupervisor = surveillanceSupervisor;
	}

	@ManyToOne(cascade = {})
	public User getCaseOfficer() {
		return caseOfficer;
	}
	public void setCaseOfficer(User caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	@ManyToOne(cascade = {})
	public User getCaseSupervisor() {
		return caseSupervisor;
	}
	public void setCaseSupervisor(User caseSupervisor) {
		this.caseSupervisor = caseSupervisor;
	}

	@ManyToOne(cascade = {})
	public User getContactOfficer() {
		return contactOfficer;
	}
	public void setContactOfficer(User contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	@ManyToOne(cascade = {})
	public User getContactSupervisor() {
		return contactSupervisor;
	}
	public void setContactSupervisor(User contactSupervisor) {
		this.contactSupervisor = contactSupervisor;
	}
	
	@OneToOne(cascade = CascadeType.ALL)
	public Symptoms getSymptoms() {
		return symptoms;
	}
	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}
}
