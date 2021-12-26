/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.backend.sormastosormas.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptionFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.crypt.CmsCertificateConfig;
import de.symeda.sormas.backend.crypt.CmsCreator;
import de.symeda.sormas.backend.crypt.CmsPlaintext;
import de.symeda.sormas.backend.crypt.CmsReader;
import de.symeda.sormas.backend.sormastosormas.rest.SormasToSormasRestClient;

@Stateless(name = "SormasToSormasEncryptionFacade")
public class SormasToSormasEncryptionFacadeEjb implements SormasToSormasEncryptionFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasEncryptionFacadeEjb.class);
	private final ObjectMapper objectMapper;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;

	@Inject
	SormasToSormasRestClient restClient;

	public SormasToSormasEncryptionFacadeEjb() {
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		objectMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);
	}

	private KeyStore getKeystore() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
		SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();
		return loadStore(sormasToSormasConfig.getKeystoreName(), sormasToSormasConfig.getKeystorePass());
	}

	private KeyStore getTruststore() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
		SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();
		return loadStore(sormasToSormasConfig.getTruststoreName(), sormasToSormasConfig.getTruststorePass());
	}

	private KeyStore loadStore(String name, String password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		String filePath = configFacadeEjb.getS2SConfig().getPath();
		Path storePath = Paths.get(filePath, name);
		KeyStore store = KeyStore.getInstance("pkcs12");
		try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(storePath))) {
			store.load(in, password.toCharArray());
		}
		return store;
	}

	@Override
	public X509Certificate loadOwnCertificate()
		throws SormasToSormasException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
		String ownId = configFacadeEjb.getS2SConfig().getId();
		KeyStore keystore = getKeystore();
		X509Certificate cert = (X509Certificate) keystore.getCertificate(ownId);
		if (cert == null) {
			LOGGER.error("The own certificate is not contained in the provided keystore.");
			throw new CertificateException("Unable to load own certificate.");
		}
		LOGGER.info("Successfully loaded own certificate.");
		return cert;
	}

	private PrivateKey loadOwnPrivateKey()
		throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
		KeyStore keystore = getKeystore();
		SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();
		PrivateKey privKey = (PrivateKey) keystore.getKey(sormasToSormasConfig.getId(), sormasToSormasConfig.getKeystorePass().toCharArray());
		if (privKey == null) {
			LOGGER.error("Could not load private key.");
			throw new KeyStoreException("Unable to load private key.");
		}
		LOGGER.info("Successfully loaded private key.");
		return privKey;
	}

	private X509Certificate loadOtherCertificate(String otherId)
		throws CertificateException, SormasToSormasException, KeyStoreException, IOException, NoSuchAlgorithmException {

		byte[] certBytes = restClient.get(otherId, SormasToSormasApiConstants.RESOURCE_PATH + SormasToSormasApiConstants.CERT_ENDPOINT, byte[].class);

		InputStream certStream = new ByteArrayInputStream(certBytes);
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		X509Certificate receivedCert = (X509Certificate) certificateFactory.generateCertificate(certStream);

		if (receivedCert == null) {
			LOGGER.error("The received certificate from {} is invalid.", otherId);
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasCertNotGenerated);
		}

		final String rootCaAlias = configFacadeEjb.getS2SConfig().getRootCaAlias();
		X509Certificate rootCA = (X509Certificate) getTruststore().getCertificate(rootCaAlias);

		if (rootCA == null) {
			LOGGER.error("Unable to load CA root certificate for alias {}", rootCaAlias);
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasCertNotGenerated);
		}

		try {
			receivedCert.verify(rootCA.getPublicKey());
		} catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
			LOGGER.error(MessageFormat.format("Verification of received certificate failed: {}", e.toString()));
			throw new CertificateException(e);
		}
		LOGGER.info("The certificate for {} has been retrieved successfully", otherId);
		return receivedCert;
	}

	private class S2SCertificateConfig extends CmsCertificateConfig {

		private S2SCertificateConfig(String otherId) throws SormasToSormasException {
			SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();

			this.ownId = sormasToSormasConfig.getId();
			this.otherId = otherId;

			try {
				this.ownCertificate = loadOwnCertificate();
				this.ownPrivateKey = loadOwnPrivateKey();
				this.otherCertificate = loadOtherCertificate(otherId);
			} catch (SormasToSormasException
				| CertificateException
				| KeyStoreException
				| IOException
				| NoSuchAlgorithmException
				| UnrecoverableKeyException e) {
				LOGGER.error("Could not create the S2S certificate config for this instance: %s", e);
				throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasCertNotGenerated);
			}

		}
	}

	@Override
	public SormasToSormasEncryptedDataDto signAndEncrypt(Object entities, String recipientId) throws SormasToSormasException {
		LOGGER.info("Sign and encrypt data for {}", recipientId);
		try {
			final String ownId = configFacadeEjb.getS2SConfig().getId();
			CmsPlaintext plaintext = new CmsPlaintext(ownId, recipientId, entities);
			S2SCertificateConfig config = new S2SCertificateConfig(recipientId);
			byte[] encryptedData = CmsCreator.signAndEncrypt(plaintext, config, true);

			return new SormasToSormasEncryptedDataDto(ownId, encryptedData);
		} catch (Exception e) {
			LOGGER.error("Could not sign and encrypt data", e);
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasEncrypt);
		}
	}

	@Override
	public <T> T decryptAndVerify(SormasToSormasEncryptedDataDto encryptedData, Class<T> dataType) throws SormasToSormasException {
		LOGGER.info("Decrypt and verify data from {}", encryptedData.getSenderId());
		try {
			byte[] decryptedData = CmsReader.decryptAndVerify(encryptedData.getData(), new S2SCertificateConfig(encryptedData.getSenderId()));
			return objectMapper.readValue(decryptedData, dataType);
		} catch (Exception e) {
			LOGGER.error("Could not decrypt and verify data", e);
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasDecrypt);
		}
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasEncryptionFacadeEjbLocal extends SormasToSormasEncryptionFacadeEjb {

	}

}
