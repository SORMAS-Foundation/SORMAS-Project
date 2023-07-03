package de.symeda.sormas.api.environment;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class EnvironmentCriteria extends BaseCriteria implements Serializable {

	private String freeText;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
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
}
