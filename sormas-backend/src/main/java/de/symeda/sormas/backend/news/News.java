package de.symeda.sormas.backend.news;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

@Entity(name = "news")
public class News extends AbstractDomainObject {

	public static final String TABLE_NAME = "News";
	public static final String EIOS_ID = "eiosId";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String URL = "url";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String NEWS_DATE = "newsDate";
	public static final String RISK_LEVEL = "riskLevel";
	public static final String STATUS = "status";
	public static final String DISEASE = "disease";

	private Long eiosId;
	private String comments;
	private String title;
	private String url;
	private String description;
	private String eiosUrl;
	private Date newsDate;
	private Boolean isContentRestricted;
	private Region region;
	private District district;
	private Community community;
	private RiskLevel riskLevel;
	private NewsStatus status;
	private Disease disease;

	public static News build() {
		News news = new News();
		news.setCreationDate(Timestamp.from(Instant.now()));
		return news;
	}

	public Long getEiosId() {
		return eiosId;
	}

	public void setEiosId(Long eiosId) {
		this.eiosId = eiosId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getTitle() {
		return title;
	}

	@Column(length = FieldConstraints.CHARACTER_LIMIT_DEFAULT)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(length = FieldConstraints.CHARACTER_LIMIT_BIG)
	public void setTitle(String title) {
		this.title = title;
	}

	@Column(length = FieldConstraints.CHARACTER_LIMIT_TEXT)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length = FieldConstraints.CHARACTER_LIMIT_DEFAULT)
	public String getEiosUrl() {
		return eiosUrl;
	}

	public void setEiosUrl(String eiosUrl) {
		this.eiosUrl = eiosUrl;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getNewsDate() {
		return newsDate;
	}

	public void setNewsDate(Date processedOnDate) {
		this.newsDate = processedOnDate;
	}

	public Boolean getContentRestricted() {
		return isContentRestricted;
	}

	public void setContentRestricted(Boolean contentRestricted) {
		isContentRestricted = contentRestricted;
	}

	@Enumerated(EnumType.STRING)
	public NewsStatus getStatus() {
		return status;
	}

	public void setStatus(NewsStatus status) {
		this.status = status;
	}

	@ManyToOne
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	@Enumerated(EnumType.STRING)
	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

}
