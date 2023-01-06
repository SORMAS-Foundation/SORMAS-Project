package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import io.swagger.v3.oas.annotations.media.Schema;

public class DashboardCriteria extends BaseCriteria implements Serializable {

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private CriteriaDateType newCaseDateType;
	@Schema(description = "Date that sets the lower boundary for the primary time period")
	private Date dateFrom;
	@Schema(description = "Date that sets the upper boundary for the primary time period")
	private Date dateTo;
	@Schema(description = "Date that sets the lower boundary for the secondary time period")
	private Date previousDateFrom;
	@Schema(description = "Date that sets the upper boundary for the secondary time period")
	private Date previousDateTo;
	private EpiCurveGrouping epiCurveGrouping;
	@Schema(description = "Whether to show at least a certain number of entries")
	private boolean showMinimumEntries;
	private CaseMeasure caseMeasure;

	@Schema(description = "Whether to include cases in the dashboard that have been confirmed to not be a case of the researched disease")
	private boolean includeNotACaseClassification;

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

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public DashboardCriteria dateBetween(Date dateFrom, Date dateTo) {
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		return this;
	}

	public boolean shouldIncludeNotACaseClassification() {
		return includeNotACaseClassification;
	}

	public DashboardCriteria includeNotACaseClassification(boolean includeNotACaseClassification) {
		this.includeNotACaseClassification = includeNotACaseClassification;
		return this;
	}

	public Date getPreviousDateFrom() {
		return previousDateFrom;
	}

	public Date getPreviousDateTo() {
		return previousDateTo;
	}

	public EpiCurveGrouping getEpiCurveGrouping() {
		return epiCurveGrouping;
	}

	public boolean isIncludeNotACaseClassification() {
		return includeNotACaseClassification;
	}

	public boolean isShowMinimumEntries() {
		return showMinimumEntries;
	}

	public CaseMeasure getCaseMeasure() {
		return caseMeasure;
	}
}
