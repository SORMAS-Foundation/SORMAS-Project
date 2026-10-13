package de.symeda.sormas.api.news.eios;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleDto extends EntityDto {

	public static final String I18N_PREFIX = "News";
	public static final String TITLE = "title";
	public static final String LINK = "link";
	public static final String DESCRIPTION = "description";
	public static final String PROCESSED_ON_DATE = "processedOnDate";
	public static final String RISK_LEVEL = "riskLevel";
	public static final String STATUS = "status";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	@JsonProperty("id")
	private String eiosId;
	private String title;
	private String eiosUrl;
	private String link;
	private String description;
	@JsonAlias({
		"processedOnDate",
		"pubDate" })
	private ZonedDateTime processedOnDate;
	private EiosArticleSourceDto source;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private RiskLevel riskLevel;

	private NewsStatus status;

	public String getEiosId() {
		return eiosId;
	}

	public void setEiosId(String eiosId) {
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

	public ZonedDateTime getProcessedOnDate() {
		return processedOnDate;
	}

	public void setProcessedOnDate(ZonedDateTime processedOnDate) {
		this.processedOnDate = processedOnDate;
	}

	public EiosArticleSourceDto getSource() {
		return source;
	}

	public void setSource(EiosArticleSourceDto source) {
		this.source = source;
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

	@Override
	public String toString() {
		return "EiosArticleDto{" + "eiosId=" + eiosId + ", title='" + title + '\'' + ", eiosUrl='" + eiosUrl + '\'' + ", link='" + link + '\''
			+ ", description='" + description + '\'' + ", processedOnDate=" + processedOnDate + ", region=" + region + ", district=" + district
			+ ", community=" + community + ", riskLevel=" + riskLevel + ", status=" + status + '}';
	}
}
