package de.symeda.sormas.api.news;

import java.util.Date;

import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class NewsCriteria extends BaseCriteria {

	public static final String I18N_PREFIX = "NewsCriteria";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public final static String RISK_LEVE = "riskLevel";
	public final static String STATUS = "status";
	public final static String START_DATE = "startDate";
	public final static String END_DATE = "endDate";
	public final static String IS_USER_LEVEL_FILER = "onlyInMyJurisdiction";
	public final static String NEWS_LIKE = "newsLike";
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private RiskLevel riskLevel;
	private NewsStatus status;
	private Date startDate;
	private Date endDate;
	private Boolean onlyInMyJurisdiction;
	private String newsLike;

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

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
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

	@IgnoreForUrl
	public String getNewsLike() {
		return newsLike;
	}

	public void setNewsLike(String newsLike) {
		this.newsLike = newsLike;
	}
}
