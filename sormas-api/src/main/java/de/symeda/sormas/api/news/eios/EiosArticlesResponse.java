package de.symeda.sormas.api.news.eios;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EiosArticlesResponse {

	@JsonProperty("result")
	private List<ArticleDto> articles;
	private Long count;
	private Boolean isCountAccurate;
	private Map<String, Object> aggregations;

	public List<ArticleDto> getArticles() {
		return articles;
	}

	public void setArticles(List<ArticleDto> articleDtos) {
		this.articles = articleDtos;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Boolean getIsCountAccurate() {
		return isCountAccurate;
	}

	public void setIsCountAccurate(Boolean isCountAccurate) {
		this.isCountAccurate = isCountAccurate;
	}

	public Map<String, Object> getAggregations() {
		return aggregations;
	}

	public void setAggregations(Map<String, Object> aggregations) {
		this.aggregations = aggregations;
	}

}
