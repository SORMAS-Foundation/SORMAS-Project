package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.person.CasePersonDto;

public class CaseDataDto extends DataTransferObject {

	private static final long serialVersionUID = 5007131477733638086L;
	
	public static final String I18N_PREFIX = "CaseData";
	
	public static final String CASE_STATUS = "caseStatus";
	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String REPORTER = "reporter";
	public static final String REPORT_DATE = "reportDate";
	
	private CasePersonDto person;
	private CaseStatus caseStatus;
	private Disease disease;
	private ReferenceDto healthFacility;
	private ReferenceDto reporter;
	private Date reportDate;
	
	public CaseStatus getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(CaseStatus caseStatus) {
		this.caseStatus = caseStatus;
	}
	
	public CasePersonDto getPerson() {
		return person;
	}
	
	public void setPerson(CasePersonDto personDto) {
		this.person = personDto;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public ReferenceDto getReporter() {
		return reporter;
	}

	public void setReporter(ReferenceDto reporter) {
		this.reporter = reporter;
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
}
