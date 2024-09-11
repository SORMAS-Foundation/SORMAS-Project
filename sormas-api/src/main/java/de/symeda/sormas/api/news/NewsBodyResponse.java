package de.symeda.sormas.api.news;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewsBodyResponse {

	@JsonProperty("data")
	private List<NewsIndexDto> newsList;
	@JsonProperty("meta")
	private NewsMetaResponseDto newsMetaResponse;

	public List<NewsIndexDto> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<NewsIndexDto> newsList) {
		this.newsList = newsList;
	}

	public NewsMetaResponseDto getNewsMetaResponse() {
		return newsMetaResponse;
	}

	public void setNewsMetaResponse(NewsMetaResponseDto newsMetaResponse) {
		this.newsMetaResponse = newsMetaResponse;
	}
}
