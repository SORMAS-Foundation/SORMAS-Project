package de.symeda.sormas.api.news;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.api.utils.DataHelper;

@AuditedClass
public class NewsDto extends EntityDto {

	public static final String I18N_PREFIX = "News";
	public static final String TITLE = "title";
	public static final String LINK = "link";
	public static final String DESCRIPTION = "description";
	public static final String NEWS_DATE = "newsDate";
	public static final String RISK_LEVEL = "riskLevel";
	public static final String STATUS = "status";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String DISEASE = "disease";
	private Long eiosId;
	private String title;
	private String eiosUrl;
	private String link;
	private String description;
	private Date newsDate;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private RiskLevel riskLevel;

	private NewsStatus status;
	private Disease disease;
	private Disease communicableDisease;
	private String otherNewsCategory;

	public static NewsDto build() {
		NewsDto dto = new NewsDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setNewsDate(new Date());
		return dto;
	}

	public Long getEiosId() {
		return eiosId;
	}

	public void setEiosId(Long eiosId) {
		this.eiosId = eiosId;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEiosUrl() {
		return eiosUrl;
	}

	public void setEiosUrl(String eiosUrl) {
		this.eiosUrl = eiosUrl;
	}

	public Date getNewsDate() {
		return newsDate;
	}

	public void setNewsDate(Date newsDate) {
		this.newsDate = newsDate;
	}

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

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Disease getCommunicableDisease() {
		return communicableDisease;
	}

	public void setCommunicableDisease(Disease communicableDisease) {
		this.communicableDisease = communicableDisease;
	}

	public String getOtherNewsCategory() {
		return otherNewsCategory;
	}

	public void setOtherNewsCategory(String otherNewsCategory) {
		this.otherNewsCategory = otherNewsCategory;
	}

	@Override
	public String toString() {
		return "EiosArticleDto{" + "eiosId=" + eiosId + ", title='" + title + '\'' + ", eiosUrl='" + eiosUrl + '\'' + ", link='" + link + '\''
			+ ", description='" + description + '\'' + ", processedOnDate=" + newsDate + ", region=" + region + ", district=" + district
			+ ", community=" + community + ", riskLevel=" + riskLevel + ", status=" + status + '}';
	}
}
