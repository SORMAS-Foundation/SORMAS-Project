package de.symeda.sormas.api.news;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.news.eios.NewsStatus;

public class NewsIndexDto extends EntityDto {

	public static final String I18N_PREFIX = "News";
	public static final String NEWS_LINK = "newsLink";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String NEWS_DATE = "newsDate";
	public static final String COMMUNITY = "community";
	public static final String RISK_LEVEL = "riskLevel";
	public static final String STATUS = "status";
	public static final String DISEASE = "disease";

	long id;
	private Date newsDate;

	private String newsLink;

	private String title;

	private String description;

	private String categories;

	private String area;

	private String region;

	private String district;

	private String community;

	private String village;

	private String newsSource;

	private RiskLevel riskLevel;

	private NewsStatus status;
	private Disease disease;

	public NewsIndexDto() {
	}

	public NewsIndexDto(
		Long id,
		String uuid,
		String title,
		String link,
		String description,
		String region,
		String district,
		String community,
		Date newsDate,
		RiskLevel riskLevel,
		NewsStatus status,
		Disease disease) {
		this.id = id;
		setUuid(uuid);
		this.title = title;
		this.newsLink = link;
		this.description = description;
		this.region = region;
		this.district = district;
		this.community = community;
		this.newsDate = newsDate;
		this.riskLevel = riskLevel;
		this.status = status;
		this.disease = disease;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getNewsDate() {
		return newsDate;
	}

	public void setNewsDate(Date newsDate) {
		this.newsDate = newsDate;
	}

	public String getNewsLink() {
		return newsLink;
	}

	public void setNewsLink(String newsLink) {
		this.newsLink = newsLink;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
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

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public String getNewsSource() {
		return newsSource;
	}

	public void setNewsSource(String newsSource) {
		this.newsSource = newsSource;
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
}
