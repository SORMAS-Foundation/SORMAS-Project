package de.symeda.sormas.api.news.eios;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EiosArticlesResponse {

	@JsonProperty("result")
	private List<ArticleDto> eiosArticlesDto;
	private Long count;
	private Boolean isCountAccurate;

	public List<ArticleDto> getArticles() {
		return eiosArticlesDto;
	}

	public void setArticles(List<ArticleDto> articleDtos) {
		this.eiosArticlesDto = articleDtos;
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

}
