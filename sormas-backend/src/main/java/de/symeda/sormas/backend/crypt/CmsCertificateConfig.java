package de.symeda.sormas.backend.crypt;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public abstract class CmsCertificateConfig {

	protected  String ownId;
	protected  String otherId;

	protected X509Certificate ownCertificate;
	protected PrivateKey ownPrivateKey;
	protected X509Certificate otherCertificate;

	public String getOwnId() {
		return ownId;
	}

	public String getOtherId() {
		return otherId;
	}

	public X509Certificate getOwnCertificate() {
		return ownCertificate;
	}

	public PrivateKey getOwnPrivateKey() {
		return ownPrivateKey;
	}

	public X509Certificate getOtherCertificate() {
		return otherCertificate;
	}
	
}
