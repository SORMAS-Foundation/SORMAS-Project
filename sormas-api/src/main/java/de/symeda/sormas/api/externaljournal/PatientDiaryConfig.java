package de.symeda.sormas.api.externaljournal;

import java.io.Serializable;
import java.util.Objects;

public class PatientDiaryConfig implements Serializable, Cloneable {

	private static final long serialVersionUID = 9020437984638539380L;

	private String url;
	private String probandsUrl;
	private String authUrl;
	private String email;
	private String password;
	private UserConfig defaultUser;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProbandsUrl() {
		return probandsUrl;
	}

	public void setProbandsUrl(String probandsUrl) {
		this.probandsUrl = probandsUrl;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
		PatientDiaryConfig that = (PatientDiaryConfig) o;
		return Objects.equals(url, that.url)
			&& Objects.equals(probandsUrl, that.probandsUrl)
			&& Objects.equals(authUrl, that.authUrl)
			&& Objects.equals(email, that.email)
			&& Objects.equals(password, that.password)
			&& Objects.equals(defaultUser, that.defaultUser);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, probandsUrl, authUrl, email, password, defaultUser);
	}

	@Override
	public PatientDiaryConfig clone() {
		try {
			return (PatientDiaryConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Clone failed", e);
		}
	}
}
