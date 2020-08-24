package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

import java.util.Date;

public class CaseSimilarityCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = -941515738028452495L;

	private CaseCriteria caseCriteria;
	private String personUuid;
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
	public String getPersonUuid() {
		return personUuid;
	}

	public CaseSimilarityCriteria personUuid(String personUuid) {
		this.personUuid = personUuid;
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
