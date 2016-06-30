package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;

public class CaseDataDto extends DataTransferObject {

	private static final long serialVersionUID = 5007131477733638086L;
	
	public static final String I18N_PREFIX = "CaseData";
	
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
	
	private ReferenceDto person;
	private CaseStatus caseStatus;
	private Disease disease;
	private ReferenceDto healthFacility;
	private ReferenceDto reportingUser;
	private Date reportDate;
	private Date investigatedDate;

	private ReferenceDto surveillanceOfficer;
	private ReferenceDto surveillanceSupervisor;
	private ReferenceDto caseOfficer;
	private ReferenceDto caseSupervisor;
	private ReferenceDto contactOfficer;
	private ReferenceDto contactSupervisor;
	
	public CaseStatus getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(CaseStatus caseStatus) {
		this.caseStatus = caseStatus;
	}
	
	public ReferenceDto getPerson() {
		return person;
	}
	
	public void setPerson(ReferenceDto personDto) {
		this.person = personDto;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public ReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(ReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public ReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(ReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
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

	public ReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public void setSurveillanceOfficer(ReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public ReferenceDto getSurveillanceSupervisor() {
		return surveillanceSupervisor;
	}

	public void setSurveillanceSupervisor(ReferenceDto surveillanceSupervisor) {
		this.surveillanceSupervisor = surveillanceSupervisor;
	}

	public ReferenceDto getCaseOfficer() {
		return caseOfficer;
	}

	public void setCaseOfficer(ReferenceDto caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public ReferenceDto getCaseSupervisor() {
		return caseSupervisor;
	}

	public void setCaseSupervisor(ReferenceDto caseSupervisor) {
		this.caseSupervisor = caseSupervisor;
	}

	public ReferenceDto getContactOfficer() {
		return contactOfficer;
	}

	public void setContactOfficer(ReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public ReferenceDto getContactSupervisor() {
		return contactSupervisor;
	}

	public void setContactSupervisor(ReferenceDto contactSupervisor) {
		this.contactSupervisor = contactSupervisor;
	}
}
