package de.symeda.sormas.api.contact;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class ContactSimilarityCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = 6902101244020083789L;

	private PersonReferenceDto person;
	private CaseReferenceDto caze;
	private Disease disease;
	private Date lastContactDate;
	private Date reportDate;
	private Date relevantDate;
	private ContactClassification contactClassification;
	private Boolean excludePseudonymized;
	private Boolean noResultingCase;

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

	public ContactSimilarityCriteria withPerson(PersonReferenceDto person) {
		this.person = person;
		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public ContactSimilarityCriteria withCaze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public ContactSimilarityCriteria withDisease(Disease disease) {
		this.disease = disease;
		return this;
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

	public Date getRelevantDate() {
		return relevantDate;
	}

	public void setRelevantDate(Date relevantDate) {
		this.relevantDate = relevantDate;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public ContactSimilarityCriteria withContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
		return this;
	}

	public Boolean getExcludePseudonymized() {
		return excludePseudonymized;
	}

	public void setExcludePseudonymized(Boolean excludePseudonymized) {
		this.excludePseudonymized = excludePseudonymized;
	}

	public ContactSimilarityCriteria withExcludePseudonymized(Boolean excludePseudonymized) {
		this.excludePseudonymized = excludePseudonymized;
		return this;
	}

	public Boolean getNoResultingCase() {
		return noResultingCase;
	}

	public void setNoResultingCase(Boolean noResultingCase) {
		this.noResultingCase = noResultingCase;
	}

	public ContactSimilarityCriteria withNoResultingCase(Boolean noResultingCase) {
		this.noResultingCase = noResultingCase;
		return this;
	}
}
