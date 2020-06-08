package de.symeda.sormas.api.contact;

import java.util.Date;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;

public class ContactSimilarityCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = 6902101244020083789L;

	private PersonReferenceDto person;
	private CaseReferenceDto caze;
	private Disease disease;
	private Date lastContactDate;
	private Date reportDate;

	public ContactSimilarityCriteria() {

	}

	public ContactSimilarityCriteria(PersonReferenceDto person, CaseReferenceDto caze, Disease disease, Date lastContactDate, Date reportDate) {

		this.person = person;
		this.caze = caze;
		this.disease = disease;
		this.lastContactDate = lastContactDate;
		this.reportDate = reportDate;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
}
