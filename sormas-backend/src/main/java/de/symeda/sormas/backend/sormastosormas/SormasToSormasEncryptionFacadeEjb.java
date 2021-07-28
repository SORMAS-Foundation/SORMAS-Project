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
package de.symeda.sormas.backend.sormastosormas;

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

import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import org.bouncycastle.cms.CMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptionFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.backend.crypt.CmsCreator;
import de.symeda.sormas.backend.crypt.CmsReader;
import de.symeda.sormas.backend.sormastosormas.rest.SormasToSormasRestClient;

@Stateless(name = "SormasToSormasEncryptionFacade")
public class SormasToSormasEncryptionFacadeEjb implements SormasToSormasEncryptionFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasEncryptionFacadeEjb.class);

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;
	@Inject
	SormasToSormasRestClient restClient;
	private final ObjectMapper objectMapper;

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
	public X509Certificate getOwnCertificate()
		throws SormasToSormasException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
		String ownId = configFacadeEjb.getS2SConfig().getId();
		KeyStore keystore = getKeystore();
		return (X509Certificate) keystore.getCertificate(ownId);
	}

	private X509Certificate getOtherCertificate(String otherId)
		throws CertificateException, SormasToSormasException, KeyStoreException, IOException, NoSuchAlgorithmException {

		byte[] certBytes = restClient.get(otherId, SormasToSormasApiConstants.RESOURCE_PATH + SormasToSormasApiConstants.CERT_ENDPOINT, byte[].class);

		InputStream certStream = new ByteArrayInputStream(certBytes);
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		X509Certificate receivedCert = (X509Certificate) certificateFactory.generateCertificate(certStream);

		X509Certificate rootCA = (X509Certificate) getTruststore().getCertificate(configFacadeEjb.getS2SConfig().getRootCaAlias());

		try {
			receivedCert.verify(rootCA.getPublicKey());
		} catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
			LOGGER.error(MessageFormat.format("Verification of received certificate failed: {0}", e.toString()));
			throw new CertificateException(e);
		}

		return receivedCert;
	}

	enum Mode {
		ENCRYPTION,
		DECRYPTION
	}

	private byte[] cipher(Mode mode, byte[] data, String otherId)
		throws SormasToSormasException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException,
		CMSException {
		SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();
		String ownId = sormasToSormasConfig.getId();
		KeyStore keystore = getKeystore();
		X509Certificate ownCert = getOwnCertificate();

		PrivateKey ownKey = (PrivateKey) keystore.getKey(ownId, sormasToSormasConfig.getKeystorePass().toCharArray());
		X509Certificate otherCert = getOtherCertificate(otherId);

		if (otherCert == null) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasCertNotGenerated);
		}

		switch (mode) {
		case ENCRYPTION:
			return CmsCreator.signAndEncrypt(data, ownCert, ownKey, otherCert, true);
		case DECRYPTION:
			return CmsReader.decryptAndVerify(data, Lists.newArrayList(otherCert), ownCert, ownKey);
		default:
			throw new IllegalArgumentException("Unknown mode " + mode);
		}
	}

	@Override
	public SormasToSormasEncryptedDataDto signAndEncrypt(Object entities, String recipientId) throws SormasToSormasException {
		try {
			byte[] encryptedData = cipher(Mode.ENCRYPTION, objectMapper.writeValueAsBytes(entities), recipientId);
			return new SormasToSormasEncryptedDataDto(configFacadeEjb.getS2SConfig().getId(), encryptedData);
		} catch (Exception e) {
			LOGGER.error("Could not sign and encrypt data", e);
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasEncrypt);
		}
	}

	@Override
	public <T> T decryptAndVerify(SormasToSormasEncryptedDataDto encryptedData, Class<T> dataType) throws SormasToSormasException {
		try {
			byte[] decryptedData = cipher(Mode.DECRYPTION, encryptedData.getData(), encryptedData.getSenderId());
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
