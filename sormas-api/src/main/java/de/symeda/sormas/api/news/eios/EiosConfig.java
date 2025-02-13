package de.symeda.sormas.api.news.eios;

import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class EiosConfig {

	private String eiosUrl;
	private String oidUrl;
	private String oidcClientId;
	private String oidcClientSecret;
	private String oidScope;

	public String getEiosUrl() {
		return eiosUrl;
	}

	public void setEiosUrl(String eiosUrl) {
		this.eiosUrl = eiosUrl;
	}

	public String getOidUrl() {
		return oidUrl;
	}

	public void setOidUrl(String oidUrl) {
		this.oidUrl = oidUrl;
	}

	public String getOidcClientId() {
		return oidcClientId;
	}

	public void setOidcClientId(String oidcClientId) {
		this.oidcClientId = oidcClientId;
	}

	public String getOidcClientSecret() {
		return oidcClientSecret;
	}

	public void setOidcClientSecret(String oidcClientSecret) {
		this.oidcClientSecret = oidcClientSecret;
	}

	public String getOidScope() {
		return oidScope;
	}

	public void setOidScope(String oidScope) {
		this.oidScope = oidScope;
	}
}
