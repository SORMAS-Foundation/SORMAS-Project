package de.symeda.sormas.api.externaljournal;

import java.io.Serializable;
import java.util.Objects;

public class SymptomJournalConfig implements Serializable, Cloneable {

	private static final long serialVersionUID = -642391732124051183L;

	private String url;
	private String authUrl;
	private String clientId;
	private String secret;
	private UserConfig defaultUser;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public boolean isActive() {
		return url != null;
	}

	public UserConfig getDefaultUser() {
		return defaultUser;
	}

	public void setDefaultUser(UserConfig defaultUser) {
		this.defaultUser = defaultUser;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SymptomJournalConfig that = (SymptomJournalConfig) o;
		return Objects.equals(url, that.url)
			&& Objects.equals(authUrl, that.authUrl)
			&& Objects.equals(clientId, that.clientId)
			&& Objects.equals(secret, that.secret)
			&& Objects.equals(defaultUser, that.defaultUser);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, authUrl, clientId, secret, defaultUser);
	}

	@Override
	public SymptomJournalConfig clone() {
		try {
			return (SymptomJournalConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Clone failed", e);
		}
	}
}
