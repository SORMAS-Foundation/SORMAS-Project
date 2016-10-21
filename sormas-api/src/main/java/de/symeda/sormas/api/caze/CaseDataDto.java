package de.symeda.sormas.api.caze;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

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
	private UserReferenceDto reportingUser;
	private Date reportDate;
	private Date investigatedDate;
	
	private SymptomsDto symptoms;

	private UserReferenceDto surveillanceOfficer;
	private UserReferenceDto surveillanceSupervisor;
	private UserReferenceDto caseOfficer;
	private UserReferenceDto caseSupervisor;
	private UserReferenceDto contactOfficer;
	private UserReferenceDto contactSupervisor;
	
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

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public ReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(ReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getReportDate() {
		return reportDate;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getInvestigatedDate() {
		return investigatedDate;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setInvestigatedDate(Date investigatedDate) {
		this.investigatedDate = investigatedDate;
	}

	public UserReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public void setSurveillanceOfficer(UserReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public UserReferenceDto getSurveillanceSupervisor() {
		return surveillanceSupervisor;
	}

	public void setSurveillanceSupervisor(UserReferenceDto surveillanceSupervisor) {
		this.surveillanceSupervisor = surveillanceSupervisor;
	}

	public UserReferenceDto getCaseOfficer() {
		return caseOfficer;
	}

	public void setCaseOfficer(UserReferenceDto caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public UserReferenceDto getCaseSupervisor() {
		return caseSupervisor;
	}

	public void setCaseSupervisor(UserReferenceDto caseSupervisor) {
		this.caseSupervisor = caseSupervisor;
	}

	public UserReferenceDto getContactOfficer() {
		return contactOfficer;
	}

	public void setContactOfficer(UserReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public UserReferenceDto getContactSupervisor() {
		return contactSupervisor;
	}

	public void setContactSupervisor(UserReferenceDto contactSupervisor) {
		this.contactSupervisor = contactSupervisor;
	}

	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}
}
