package de.symeda.sormas.api.caze;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class CaseDataDto extends CaseReferenceDto {

	private static final long serialVersionUID = 5007131477733638086L;
	
	public static final String I18N_PREFIX = "CaseData";
	
	public static final String CASE_STATUS = "caseStatus";
	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE = "reportDate";
	public static final String INVESTIGATED_DATE = "investigatedDate";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String CASE_OFFICER = "caseOfficer";
	public static final String CONTACT_OFFICER = "contactOfficer";
	
	private PersonReferenceDto person;
	private CaseStatus caseStatus;
	private Disease disease;
	private UserReferenceDto reportingUser;
	private Date reportDate;
	private Date investigatedDate;

	private ReferenceDto region;
	private ReferenceDto district;
	private ReferenceDto community;
	private ReferenceDto healthFacility;

	private SymptomsDto symptoms;

	private UserReferenceDto surveillanceOfficer;
	private UserReferenceDto caseOfficer;
	private UserReferenceDto contactOfficer;
	
	public CaseStatus getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(CaseStatus caseStatus) {
		this.caseStatus = caseStatus;
	}
	
	public PersonReferenceDto getPerson() {
		return person;
	}
	
	public void setPerson(PersonReferenceDto personDto) {
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

	public UserReferenceDto getCaseOfficer() {
		return caseOfficer;
	}

	public void setCaseOfficer(UserReferenceDto caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public UserReferenceDto getContactOfficer() {
		return contactOfficer;
	}

	public void setContactOfficer(UserReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}

	public ReferenceDto getRegion() {
		return region;
	}

	public void setRegion(ReferenceDto region) {
		this.region = region;
	}

	public ReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(ReferenceDto district) {
		this.district = district;
	}

	public ReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(ReferenceDto community) {
		this.community = community;
	}
}
