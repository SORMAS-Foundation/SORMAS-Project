package de.symeda.sormas.api.report;

import java.io.Serializable;

import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class WeeklyReportSummaryDto implements Serializable {

	private static final long serialVersionUID = -8776688104892901821L;

	public static final String I18N_PREFIX = "WeeklyReportSummary";
	
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String FACILITIES = "facilities";
	public static final String REPORTS = "reports";
	public static final String REPORTS_PERCENTAGE = "reportsPercentage";
	public static final String ZERO_REPORTS = "zeroReports";
	public static final String ZERO_REPORTS_PERCENTAGE = "zeroReportsPercentage";
	public static final String MISSING_REPORTS = "missingReports";
	public static final String MISSING_REPORTS_PERCENTAGE = "missingReportsPercentage";
	
	private RegionReferenceDto region;
	private DistrictReferenceDto district;		
	private int facilities;
	private int reports;
	private float reportsPercentage;
	private int zeroReports;
	private float zeroReportsPercentage;
	private int missingReports;
	private float missingReportsPercentage;

	public RegionReferenceDto getRegion() {
		return region;
	}
	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
	public int getFacilities() {
		return facilities;
	}
	public void setFacilities(int facilities) {
		this.facilities = facilities;
	}
	public int getReports() {
		return reports;
	}
	public void setReports(int reports) {
		this.reports = reports;
	}
	public float getReportsPercentage() {
		return reportsPercentage;
	}
	public void setReportsPercentage(float reportsPercentage) {
		this.reportsPercentage = reportsPercentage;
	}
	public int getZeroReports() {
		return zeroReports;
	}
	public void setZeroReports(int zeroReports) {
		this.zeroReports = zeroReports;
	}
	public float getZeroReportsPercentage() {
		return zeroReportsPercentage;
	}
	public void setZeroReportsPercentage(float zeroReportsPercentage) {
		this.zeroReportsPercentage = zeroReportsPercentage;
	}
	public int getMissingReports() {
		return missingReports;
	}
	public void setMissingReports(int missingReports) {
		this.missingReports = missingReports;
	}
	public float getMissingReportsPercentage() {
		return missingReportsPercentage;
	}
	public void setMissingReportsPercentage(float missingReportsPercentage) {
		this.missingReportsPercentage = missingReportsPercentage;
	}
	
}
