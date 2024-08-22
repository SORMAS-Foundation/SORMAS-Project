package de.symeda.sormas.api.news;

import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class NewsConfig {

	private String baseUrl;
	private String authToken;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
}
