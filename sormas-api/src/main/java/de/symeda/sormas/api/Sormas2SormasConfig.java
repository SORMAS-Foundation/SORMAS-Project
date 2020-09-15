package de.symeda.sormas.api;

import java.io.Serializable;
import java.util.Objects;

public class Sormas2SormasConfig implements Serializable, Cloneable {

	private static final long serialVersionUID = -7981351672462016280L;

	private String filePath;
	private String keyAlias;
	private String keystoreName;
	private String keystorePass;
	private String truststoreName;
	private String truststorePass;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getKeyAlias() {
		return keyAlias;
	}

	public void setKeyAlias(String keyAlias) {
		this.keyAlias = keyAlias;
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Sormas2SormasConfig that = (Sormas2SormasConfig) o;
		return Objects.equals(filePath, that.filePath)
			&& Objects.equals(keyAlias, that.keyAlias)
			&& Objects.equals(keystorePass, that.keystorePass)
			&& Objects.equals(truststoreName, that.truststoreName)
			&& Objects.equals(truststorePass, that.truststorePass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filePath, keyAlias, keystorePass, truststoreName, truststorePass);
	}

	@Override
	public Sormas2SormasConfig clone() {
		try {
			return (Sormas2SormasConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Clone failed", e);
		}
	}
}
