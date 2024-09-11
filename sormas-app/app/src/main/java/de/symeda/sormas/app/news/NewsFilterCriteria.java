package de.symeda.sormas.app.news;

import java.util.Date;

import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

public class NewsFilterCriteria {

	private Region region;
	private District district;
	private Community community;
	private RiskLevel riskLevel;
	private NewsStatus status;
	private Date startDate;
	private Date endDate;
	private Boolean onlyInMyJurisdiction;

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	public NewsStatus getStatus() {
		return status;
	}

	public void setStatus(NewsStatus status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Boolean getOnlyInMyJurisdiction() {
		return onlyInMyJurisdiction;
	}

	public void setOnlyInMyJurisdiction(Boolean onlyInMyJurisdiction) {
		this.onlyInMyJurisdiction = onlyInMyJurisdiction;
	}
}
