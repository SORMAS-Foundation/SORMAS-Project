package de.symeda.sormas.api.report;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.EpiWeek;

@SuppressWarnings("serial")
public class AggregateReportCriteria extends BaseCriteria implements Cloneable {

	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String EPI_WEEK_FROM = "epiWeekFrom";
	public static final String EPI_WEEK_TO = "epiWeekTo";

	private EpiWeek epiWeekFrom;
	private EpiWeek epiWeekTo;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private FacilityReferenceDto healthFacility;
	private PointOfEntryReferenceDto pointOfEntry;

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
}
