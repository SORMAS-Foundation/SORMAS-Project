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

	public ContactSimilarityCriteria setPerson(PersonReferenceDto person) {
		this.person = person;
		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public ContactSimilarityCriteria setCaze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public ContactSimilarityCriteria setDisease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public ContactSimilarityCriteria setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
		return this;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public ContactSimilarityCriteria setReportDate(Date reportDate) {
		this.reportDate = reportDate;
		return this;
	}

	public Date getRelevantDate() {
		return relevantDate;
	}

	public ContactSimilarityCriteria setRelevantDate(Date relevantDate) {
		this.relevantDate = relevantDate;
		return this;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public ContactSimilarityCriteria setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
		return this;
	}

	public Boolean getExcludePseudonymized() {
		return excludePseudonymized;
	}

	public ContactSimilarityCriteria setExcludePseudonymized(Boolean excludePseudonymized) {
		this.excludePseudonymized = excludePseudonymized;
		return this;
	}

	public Boolean getNoResultingCase() {
		return noResultingCase;
	}

	public ContactSimilarityCriteria setNoResultingCase(Boolean noResultingCase) {
		this.noResultingCase = noResultingCase;
		return this;
	}
}
