package de.symeda.sormas.api.report;

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class AggregateReportGroupingDto implements Serializable, Cloneable {

	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String POINT_OF_ENTRY = "pointOfEntry";

	private String region;
	private String district;
	private String healthFacility;
	private String pointOfEntry;

	public AggregateReportGroupingDto(String region, String district, String healthFacility, String pointOfEntry) {
		this.region = region;
		this.district = district;
		this.healthFacility = healthFacility;
		this.pointOfEntry = pointOfEntry;
	}

	@AuditInclude
	public String getRegion() {
		return region;
	}

	@AuditInclude
	public String getDistrict() {
		return district;
	}

	@AuditInclude
	public String getHealthFacility() {
		return healthFacility;
	}

	@AuditInclude
	public String getPointOfEntry() {
		return pointOfEntry;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AggregateReportGroupingDto)) {
			return false;
		}
		AggregateReportGroupingDto aggregateReportGroupingDto = (AggregateReportGroupingDto) o;
		return this.region.equals(aggregateReportGroupingDto.getRegion())
			&& this.district.equals(aggregateReportGroupingDto.getDistrict())
			&& this.healthFacility.equals(aggregateReportGroupingDto.getHealthFacility())
			&& this.pointOfEntry.equals(aggregateReportGroupingDto.getPointOfEntry());
	}

}
