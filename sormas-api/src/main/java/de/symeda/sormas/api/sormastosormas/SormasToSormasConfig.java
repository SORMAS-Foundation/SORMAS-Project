package de.symeda.sormas.api.sormastosormas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SormasToSormasConfig implements Serializable {

	private static final long serialVersionUID = -7981351672462016280L;

	// We normally just send encrypted data DTOs between instances which already carry the org id of the sender, however,
	// this does not work for GET request. Therefore we include a query parameter in this case. This variable cannot
	// resort in the REST client, as it needs to be shared between REST client and sormas-rest.
	public static final String SENDER_SERVER_ID = "senderServerId";

	public static final String SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS = "sormas2sormas.ignoreProperty.additionalDetails";
	public static final String SORMAS2SORMAS_IGNORE_EXTERNAL_ID = "sormas2sormas.ignoreProperty.externalId";
	public static final String SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN = "sormas2sormas.ignoreProperty.externalToken";
	public static final String SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN = "sormas2sormas.ignoreProperty.internalToken";

	private String id;
	private String path;
	private String keystoreName;
	private String keystorePass;
	private String rootCaAlias;
	private String truststoreName;
	private String truststorePass;
	private Map<String, Boolean> ignoreProperties = new HashMap<>();
	{
		this.ignoreProperties.put(SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, true);
		this.ignoreProperties.put(SORMAS2SORMAS_IGNORE_EXTERNAL_ID, true);
		this.ignoreProperties.put(SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, true);
		this.ignoreProperties.put(SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, true);
	}

	private String oidcServer;
	private String oidcRealm;
	private String oidcClientId;
	private String oidcClientSecret;

	private String keyPrefix;

	private String districtExternalId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public Map<String, Boolean> getIgnoreProperties() {
		return ignoreProperties;
	}

	public void setIgnoreProperties(Map<String, Boolean> ignoreProperties) {
		this.ignoreProperties = ignoreProperties;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SormasToSormasConfig that = (SormasToSormasConfig) o;
		return Objects.equals(path, that.path)
			&& Objects.equals(keystorePass, that.keystorePass)
			&& Objects.equals(truststoreName, that.truststoreName)
			&& Objects.equals(truststorePass, that.truststorePass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, keystorePass, truststoreName, truststorePass);
	}

	@Override
	public SormasToSormasConfig clone() {
		try {
			return (SormasToSormasConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Clone failed", e);
		}
	}

	public String getOidcServer() {
		return oidcServer;
	}

	public void setOidcServer(String oidcServer) {
		this.oidcServer = oidcServer;
	}

	public String getOidcRealm() {
		return oidcRealm;
	}

	public void setOidcRealm(String oidcRealm) {
		this.oidcRealm = oidcRealm;
	}

	/**
	 * Computed property to get access to our realm URL.
	 *
	 * @return The complete URL to our S2S realm
	 */
	public String getOidcRealmUrl() {
		return getOidcServer() + "/auth/realms/" + getOidcRealm();
	}

	/**
	 * Computed property to get access to our realm's token URL.
	 *
	 * @return The complete URL to our S2S realm's token URL.
	 */
	public String getOidcRealmTokenEndpoint() {
		return getOidcRealmUrl() + "/protocol/openid-connect/token";
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

	/**
	 * Computed property to get access to our realm's certificate URL.
	 *
	 * @return The complete URL to our S2S realm's certificate URL.
	 */
	public String getOidcRealmCertEndpoint() {
		return getOidcRealmUrl() + "/protocol/openid-connect/certs";
	}

	/**
	 * Computed property to get access to the name of our own client scope.
	 *
	 * @return The complete URL to our S2S realm's certificate URL.
	 */
	public String getClientScope() {
		return "s2s-" + getOidcClientId();
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	/**
	 * Computed template for the key prefix
	 */
	public String getKeyPrefixTemplate() {
		return "/" + this.getKeyPrefix() + "/" + "%s";
	}

	public String getRootCaAlias() {
		return rootCaAlias;
	}

	public void setRootCaAlias(String rootCaAlias) {
		this.rootCaAlias = rootCaAlias;
	}

	public String getDistrictExternalId() {
		return districtExternalId;
	}

	public void setDistrictExternalId(String districtExternalId) {
		this.districtExternalId = districtExternalId;
	}
}
