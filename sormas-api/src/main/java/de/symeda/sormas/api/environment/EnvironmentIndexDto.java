package de.symeda.sormas.api.environment;

import java.util.Date;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class EnvironmentIndexDto extends AbstractUuidDto {

	public static final String I18N_PREFIX = "Environment";

	public static final String EXTERNAL_ID = "externalId";
	public static final String ENVIRONMENT_NAME = "environmentName";
	public static final String ENVIRONMENT_MEDIA = "environmentMedia";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String GPS_LAT = "gpsLat";
	public static final String GPS_LON = "gpsLon";
	public static final String POSTAL_CODE = "postalCode";
	public static final String CITY = "city";
	public static final String REPORT_DATE = "reportDate";
	public static final String INVESTIGATION_STATUS = "investigationStatus";

	private String externalId;
	private String environmentName;
	private EnvironmentMedia environmentMedia;

	private String region;
	private String district;
	private String community;
	private Double gpsLat;
	private Double gpsLon;
	private String postalCode;
	private String city;
	private Date reportDate;
	private InvestigationStatus investigationStatus;

	public EnvironmentIndexDto(
		String uuid,
		String externalId,
		String environmentName,
		EnvironmentMedia environmentMedia,
		String region,
		String district,
		String community,
		Double gpsLat,
		Double gpsLon,
		String postalCode,
		String city,
		Date reportDate,
		InvestigationStatus investigationStatus) {

		super(uuid);
		this.externalId = externalId;
		this.environmentName = environmentName;
		this.environmentMedia = environmentMedia;
		this.region = region;
		this.district = district;
		this.community = community;
		this.gpsLat = gpsLat;
		this.gpsLon = gpsLon;
		this.postalCode = postalCode;
		this.city = city;
		this.reportDate = reportDate;
		this.investigationStatus = investigationStatus;
	}

	public EnvironmentIndexDto(String uuid) {
		super(uuid);
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public EnvironmentMedia getEnvironmentMedia() {
		return environmentMedia;
	}

	public void setEnvironmentMedia(EnvironmentMedia environmentMedia) {
		this.environmentMedia = environmentMedia;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public Double getGpsLat() {
		return gpsLat;
	}

	public void setGpsLat(Double gpsLat) {
		this.gpsLat = gpsLat;
	}

	public Double getGpsLon() {
		return gpsLon;
	}

	public void setGpsLon(Double gpsLon) {
		this.gpsLon = gpsLon;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}
}
