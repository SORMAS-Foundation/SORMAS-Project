package de.symeda.sormas.api.environment;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class EnvironmentCriteria extends BaseCriteria implements Serializable {

	public static final String FREE_TEXT = "freeText";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String REPORT_DATE_FROM = "reportDateFrom";
	public static final String REPORT_DATE_TO = "reportDateTo";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String ENVIRONMENT_MEDIA = "environmentMedia";
	public static final String RELEVANCE_STATUS = "relevanceStatus";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String GPS_LAT_FROM = "gpsLatFrom";
	public static final String GPS_LAT_TO = "gpsLatTo";
	public static final String GPS_LON_FROM = "gpsLonFrom";
	public static final String GPS_LON_TO = "gpsLonTo";
	private static final long serialVersionUID = -2947852193651003088L;

	private String freeText;
	private String externalId;
	private CountryReferenceDto country;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private Date reportDateFrom;
	private Date reportDateTo;

	private InvestigationStatus investigationStatus;
	private EnvironmentMedia environmentMedia;
	private EntityRelevanceStatus relevanceStatus;
	private UserReferenceDto responsibleUser;
	private Double gpsLatFrom;
	private Double gpsLatTo;
	private Double gpsLonFrom;
	private Double gpsLonTo;

	@IgnoreForUrl
	public String getFreeText() {
		return freeText;
	}

	public EnvironmentCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	@IgnoreForUrl
	public String getExternalId() {
		return externalId;
	}

	public EnvironmentCriteria externalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public CountryReferenceDto getCountry() {
		return country;
	}

	public EnvironmentCriteria country(CountryReferenceDto country) {
		this.country = country;
		return this;
	}

	public void setCountry(CountryReferenceDto country) {
		this.country = country;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public EnvironmentCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public EnvironmentCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public EnvironmentCriteria community(CommunityReferenceDto community) {
		this.community = community;
		return this;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public void setDateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
	}

	public Date getReportDateFrom() {
		return reportDateFrom;
	}

	public EnvironmentCriteria reportDateFrom(Date reportDateFrom) {
		this.reportDateFrom = reportDateFrom;
		return this;
	}

	public void setReportDateFrom(Date reportDateFrom) {
		this.reportDateFrom = reportDateFrom;
	}

	public Date getReportDateTo() {
		return reportDateTo;
	}

	public EnvironmentCriteria reportDateTo(Date reportDateTo) {
		this.reportDateTo = reportDateTo;
		return this;
	}

	public void setReportDateTo(Date reportDateTo) {
		this.reportDateTo = reportDateTo;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public EnvironmentCriteria investigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
		return this;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public EnvironmentMedia getEnvironmentMedia() {
		return environmentMedia;
	}

	public EnvironmentCriteria environmentMedia(EnvironmentMedia environmentMedia) {
		this.environmentMedia = environmentMedia;
		return this;
	}

	public void setEnvironmentMedia(EnvironmentMedia environmentMedia) {
		this.environmentMedia = environmentMedia;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public EnvironmentCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	public void setRelevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
	}

	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public EnvironmentCriteria responsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
		return this;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	@IgnoreForUrl
	public Double getGpsLatFrom() {
		return gpsLatFrom;
	}

	public EnvironmentCriteria gpsLatFrom(Double gpsLatFrom) {
		this.gpsLatFrom = gpsLatFrom;
		return this;
	}

	public void setGpsLatFrom(Double gpsLatFrom) {
		this.gpsLatFrom = gpsLatFrom;
	}

	@IgnoreForUrl
	public Double getGpsLatTo() {
		return gpsLatTo;
	}

	public EnvironmentCriteria gpsLatTo(Double gpsLatTo) {
		this.gpsLatTo = gpsLatTo;
		return this;
	}

	public void setGpsLatTo(Double gpsLatTo) {
		this.gpsLatTo = gpsLatTo;
	}

	@IgnoreForUrl
	public Double getGpsLonFrom() {
		return gpsLonFrom;
	}

	public EnvironmentCriteria gpsLonFrom(Double gpsLonFrom) {
		this.gpsLonFrom = gpsLonFrom;
		return this;
	}

	public void setGpsLonFrom(Double gpsLonFrom) {
		this.gpsLonFrom = gpsLonFrom;
	}

	@IgnoreForUrl
	public Double getGpsLonTo() {
		return gpsLonTo;
	}

	public EnvironmentCriteria gpsLonTo(Double gpsLonTo) {
		this.gpsLonTo = gpsLonTo;
		return this;
	}

	public void setGpsLonTo(Double gpsLonTo) {
		this.gpsLonTo = gpsLonTo;
	}

	public void reportDateBetween(Date reportDateFrom, Date reportDateTo, DateFilterOption dateFilterOption) {
		this.reportDateFrom = reportDateFrom;
		this.reportDateTo = reportDateTo;
		this.dateFilterOption = dateFilterOption;
	}
}
