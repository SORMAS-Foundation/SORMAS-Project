package de.symeda.sormas.api.caze;

import java.util.Collection;
import java.util.Date;

import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class CaseSimilarityCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = -941515738028452495L;

	private CaseCriteria caseCriteria;
	private String personUuid;
	private Collection<String> personUuids;
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
		return new CaseSimilarityCriteria().personUuid(personUuid).caseCriteria(buildCaseCriteria(caze)).reportDate(caze.getReportDate());
	}

	public static CaseSimilarityCriteria forCase(CaseDataDto caze, Collection<String> personUuids) {
		return new CaseSimilarityCriteria().personUuids(personUuids).caseCriteria(buildCaseCriteria(caze)).reportDate(caze.getReportDate());
	}

	private static CaseCriteria buildCaseCriteria(CaseDataDto caze) {
		return new CaseCriteria().disease(caze.getDisease()).region(CaseLogic.getRegionWithFallback(caze));
	}

	public CaseSimilarityCriteria personUuids(Collection<String> personUuids) {
		this.personUuids = personUuids;

		return this;
	}

	public Collection<String> getPersonUuids() {
		return personUuids;
	}
}
