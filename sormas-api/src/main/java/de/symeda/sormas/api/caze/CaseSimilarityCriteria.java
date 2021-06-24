package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

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

	public static CaseSimilarityCriteria forCase(CaseDataDto caze, String personUuid) {
		CaseCriteria caseCriteria = new CaseCriteria().disease(caze.getDisease()).region(CaseLogic.getRegionWithFallback(caze));

		return new CaseSimilarityCriteria().personUuid(personUuid).caseCriteria(caseCriteria).reportDate(caze.getReportDate());
	}
}
