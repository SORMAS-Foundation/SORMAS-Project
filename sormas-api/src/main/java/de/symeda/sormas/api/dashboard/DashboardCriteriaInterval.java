package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class DashboardCriteriaInterval extends BaseCriteria implements Serializable {

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Date dateFrom;
	private Date dateTo;
	private Date previousDateFrom;
	private Date previousDateTo;
	private NewCaseDateType newCaseDateType;

	public Date getPreviousDateFrom() {
		return previousDateFrom;
	}

	public Date getPreviousDateTo() {
		return previousDateTo;
	}

	public NewCaseDateType getNewCaseDateType() {
		return newCaseDateType;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}
}
