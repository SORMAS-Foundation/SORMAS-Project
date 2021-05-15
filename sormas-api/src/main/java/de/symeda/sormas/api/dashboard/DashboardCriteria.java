package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;

public class DashboardCriteria extends BaseCriteria implements Serializable {

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	private CriteriaDateType newCaseDateType;
	private Date newCaseDateFrom;
	private Date newCaseDateTo;

	public RegionReferenceDto getRegion() {
		return region;
	}

	public DashboardCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public DashboardCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public DashboardCriteria disease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public CriteriaDateType getNewCaseDateType() {
		return newCaseDateType;
	}

	public DashboardCriteria newCaseDateType(CriteriaDateType newCaseDateType) {
		this.newCaseDateType = newCaseDateType;
		return this;
	}

	public Date getNewCaseDateFrom() {
		return newCaseDateFrom;
	}

	public Date getNewCaseDateTo() {
		return newCaseDateTo;
	}

	public DashboardCriteria newCaseDateBetween(Date newCaseDateFrom, Date newCaseDateTo) {
		this.newCaseDateFrom = newCaseDateFrom;
		this.newCaseDateTo = newCaseDateTo;
		return this;
	}
}
