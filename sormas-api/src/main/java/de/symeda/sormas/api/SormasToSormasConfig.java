package de.symeda.sormas.api;

import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.inject.Alternative;

@Alternative
public class SormasToSormasConfig implements Serializable, Cloneable {

	private static final long serialVersionUID = -7981351672462016280L;

	private String path;
	private String serverAccessDataFileName;
	private String keystoreName;
	private String keystorePass;
	private String truststoreName;
	private String truststorePass;
	private boolean retainExternalToken;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getServerAccessDataFileName() {
		return serverAccessDataFileName;
	}

	public void setServerAccessDataFileName(String serverAccessDataFileName) {
		this.serverAccessDataFileName = serverAccessDataFileName;
	}

	public String getKeystoreName() {
		return keystoreName;
	}

	public void setKeystoreName(String keystoreName) {
		this.keystoreName = keystoreName;
	}

	public String getKeystorePass() {
		return keystorePass;
	}

	public void setKeystorePass(String keystorePass) {
		this.keystorePass = keystorePass;
	}

	public String getTruststoreName() {
		return truststoreName;
	}

	public void setTruststoreName(String truststoreName) {
		this.truststoreName = truststoreName;
	}

	public String getTruststorePass() {
		return truststorePass;
	}

	public void setTruststorePass(String truststorePass) {
		this.truststorePass = truststorePass;
	}

	public boolean getRetainExternalToken() {
		return retainExternalToken;
	}

	public void setRetainExternalToken(boolean retainExternalToken) {
		this.retainExternalToken = retainExternalToken;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SormasToSormasConfig that = (SormasToSormasConfig) o;
		return Objects.equals(path, that.path)
			&& Objects.equals(serverAccessDataFileName, that.serverAccessDataFileName)
			&& Objects.equals(keystorePass, that.keystorePass)
			&& Objects.equals(truststoreName, that.truststoreName)
			&& Objects.equals(truststorePass, that.truststorePass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, serverAccessDataFileName, keystorePass, truststoreName, truststorePass);
	}

	@Override
	public SormasToSormasConfig clone() {
		try {
			return (SormasToSormasConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Clone failed", e);
		}
	}
}
