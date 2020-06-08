package de.symeda.sormas.api.clinicalcourse;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.Order;

public class ClinicalVisitExportDto implements Serializable {

	private static final long serialVersionUID = -5724133522485897878L;

	public static final String I18N_PREFIX = "ClinicalVisitExport";

	private String caseUuid;
	private String caseName;
	private Disease disease;
	private Date visitDateTime;
	private String visitRemarks;
	private String visitingPerson;
	private long symptomsId;
	private SymptomsDto symptoms;

	public ClinicalVisitExportDto(
		String caseUuid,
		String caseFirstName,
		String caseLastName,
		Disease disease,
		Date visitDateTime,
		String visitRemarks,
		String visitingPerson,
		long symptomsId) {

		this.caseUuid = caseUuid;
		this.caseName = PersonDto.buildCaption(caseFirstName, caseLastName);
		this.disease = disease;
		this.visitDateTime = visitDateTime;
		this.visitRemarks = visitRemarks;
		this.visitingPerson = visitingPerson;
		this.symptomsId = symptomsId;
	}

	@Order(0)
	public String getCaseUuid() {
		return caseUuid;
	}

	@Order(1)
	public String getCaseName() {
		return caseName;
	}

	@Order(2)
	public Disease getDisease() {
		return disease;
	}

	@Order(3)
	public Date getVisitDateTime() {
		return visitDateTime;
	}

	@Order(4)
	public String getVisitRemarks() {
		return visitRemarks;
	}

	@Order(5)
	public String getVisitingPerson() {
		return visitingPerson;
	}

	@Order(6)
	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public long getSymptomsId() {
		return symptomsId;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public void setVisitingPerson(String visitingPerson) {
		this.visitingPerson = visitingPerson;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}

	public void setSymptomsId(long symptomsId) {
		this.symptomsId = symptomsId;
	}
}
