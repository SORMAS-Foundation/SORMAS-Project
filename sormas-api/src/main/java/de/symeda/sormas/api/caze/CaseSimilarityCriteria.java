package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class CaseSimilarityCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = -941515738028452495L;

	private CaseCriteria caseCriteria;
	private String firstName;
	private String lastName;
	private Date reportDate;

	@Override
	public CaseCriteria clone() {

		try {
			return (CaseCriteria) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@IgnoreForUrl
	public String getFirstName() {
		return firstName;
	}

	public CaseSimilarityCriteria firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	@IgnoreForUrl
	public String getLastName() {
		return lastName;
	}

	public CaseSimilarityCriteria lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	@IgnoreForUrl
	public Date getReportDate() {
		return reportDate;
	}

	public CaseSimilarityCriteria reportDate(Date reportDate) {
		this.reportDate = reportDate;
		return this;
	}

	@IgnoreForUrl
	public CaseCriteria getCaseCriteria() {
		return caseCriteria;
	}

	public CaseSimilarityCriteria caseCriteria(CaseCriteria caseCriteria) {
		this.caseCriteria = caseCriteria;
		return this;
	}
}
