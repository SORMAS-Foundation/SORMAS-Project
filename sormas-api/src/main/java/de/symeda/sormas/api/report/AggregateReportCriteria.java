package de.symeda.sormas.api.report;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

@SuppressWarnings("serial")
public class AggregateReportCriteria extends BaseCriteria implements Serializable {

	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String EPI_WEEK_FROM = "epiWeekFrom";
	public static final String EPI_WEEK_TO = "epiWeekTo";
	public static final String DISEASE = "disease";
	public static final String SHOW_ZERO_ROWS = "showZeroRows";
	public static final String SHOW_ONLY_DUPLICATES = "showOnlyDuplicates";
	public static final String REPORTING_USER = "reportingUser";

	private EpiWeek epiWeekFrom;
	private EpiWeek epiWeekTo;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private FacilityReferenceDto healthFacility;
	private PointOfEntryReferenceDto pointOfEntry;
	private Disease disease;
	private Boolean showZeroRows = Boolean.FALSE;
	private Boolean showOnlyDuplicates = Boolean.FALSE;
	private UserReferenceDto reportingUser;
	private Boolean considerNullJurisdictionCheck = false;

	private AggregateReportGroupingLevel aggregateReportGroupingLevel;

	public EpiWeek getEpiWeekFrom() {
		return epiWeekFrom;
	}

	public AggregateReportCriteria epiWeekFrom(EpiWeek epiWeekFrom) {
		this.epiWeekFrom = epiWeekFrom;
		return this;
	}

	public EpiWeek getEpiWeekTo() {
		return epiWeekTo;
	}

	public AggregateReportCriteria epiWeekTo(EpiWeek epiWeekTo) {
		this.epiWeekTo = epiWeekTo;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public AggregateReportCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public AggregateReportCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public AggregateReportCriteria healthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
		return this;
	}

	public PointOfEntryReferenceDto getPointOfEntry() {
		return pointOfEntry;
	}

	public AggregateReportCriteria pointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
		return this;
	}

	public void setEpiWeekFrom(EpiWeek epiWeekFrom) {
		this.epiWeekFrom = epiWeekFrom;
	}

	public void setEpiWeekTo(EpiWeek epiWeekTo) {
		this.epiWeekTo = epiWeekTo;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}

	public void setPointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public AggregateReportGroupingLevel getAggregateReportGroupingLevel() {
		return aggregateReportGroupingLevel;
	}

	public void setAggregateReportGroupingLevel(AggregateReportGroupingLevel aggregateReportGroupingLevel) {
		this.aggregateReportGroupingLevel = aggregateReportGroupingLevel;
	}

	public Boolean getShowZeroRows() {
		return showZeroRows;
	}

	public void setShowZeroRows(Boolean showZeroRows) {
		this.showZeroRows = showZeroRows;
	}

	public Boolean getShowOnlyDuplicates() {
		return showOnlyDuplicates;
	}

	public void setShowOnlyDuplicates(Boolean showOnlyDuplicates) {
		this.showOnlyDuplicates = showOnlyDuplicates;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	@IgnoreForUrl
	public Boolean isConsiderNullJurisdictionCheck() {
		return considerNullJurisdictionCheck;
	}

	public void setConsiderNullJurisdictionCheck(Boolean considerNullJurisdictionCheck) {
		this.considerNullJurisdictionCheck = considerNullJurisdictionCheck;
	}
}
