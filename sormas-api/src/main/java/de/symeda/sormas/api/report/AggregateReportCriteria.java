package de.symeda.sormas.api.report;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

@SuppressWarnings("serial")
public class AggregateReportCriteria extends BaseCriteria implements Cloneable {

	private Integer year;
	private Integer epiWeek;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private FacilityReferenceDto healthFacility;
	private PointOfEntryReferenceDto pointOfEntry;
	
	public Integer getYear() {
		return year;
	}
	
	public AggregateReportCriteria year(Integer year) {
		this.year = year;
		return this;
	}
	
	public Integer getEpiWeek() {
		return epiWeek;
	}
	
	public AggregateReportCriteria epiWeek(Integer epiWeek) {
		this.epiWeek = epiWeek;
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

}
