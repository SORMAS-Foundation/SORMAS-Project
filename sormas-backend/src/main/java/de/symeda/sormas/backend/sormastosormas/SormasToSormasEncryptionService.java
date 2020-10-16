/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.symeda.sormas.api.SormasToSormasConfig;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.backend.crypt.CmsCreator;
import de.symeda.sormas.backend.crypt.CmsReader;

@Stateless
@LocalBean
public class SormasToSormasEncryptionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasEncryptionService.class);

	@Inject
	protected SormasToSormasConfig sormasToSormasConfig;
	@EJB
	protected ServerAccessDataService serverAccessDataService;

	public byte[] encrypt(byte[] data, String instanceID) throws SormasToSormasException {
		try {
			String filePath = sormasToSormasConfig.getPath();
			String keystoreName = sormasToSormasConfig.getKeystoreName();
			Path keystorePath = Paths.get(filePath, keystoreName);
			String keystorePass = sormasToSormasConfig.getKeystorePass();

			KeyStore keystore = getKeyStore(keystorePath, keystorePass);
			String organizationId = getOrganizationId();

			X509Certificate signerCertificate = (X509Certificate) keystore.getCertificate(organizationId);
			PrivateKey privateKey = (PrivateKey) keystore.getKey(organizationId, keystorePass.toCharArray());

			Path truststorePath = Paths.get(filePath, sormasToSormasConfig.getTruststoreName());
			KeyStore truststore = getKeyStore(truststorePath, sormasToSormasConfig.getTruststorePass());
			X509Certificate recipientCertificate = (X509Certificate) truststore.getCertificate(instanceID);

			return CmsCreator.signAndEncrypt(data, signerCertificate, privateKey, recipientCertificate, true);
		} catch (Exception e) {
			LOGGER.error("Couldn't encrypt data", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasEncrypt));
		}
	}

	public byte[] decrypt(byte[] data, String instanceID) throws SormasToSormasException {
		try {
			String filePath = sormasToSormasConfig.getPath();

			Path keystorePath = Paths.get(filePath, sormasToSormasConfig.getKeystoreName());
			String keystorePass = sormasToSormasConfig.getKeystorePass();
			KeyStore keystore = getKeyStore(keystorePath, keystorePass);

			String organizationId = getOrganizationId();
			X509Certificate recipientCertificate = (X509Certificate) keystore.getCertificate(organizationId);
			PrivateKey recipientPrivateKey = (PrivateKey) keystore.getKey(organizationId, keystorePass.toCharArray());

			Path truststorePath = Paths.get(filePath, sormasToSormasConfig.getTruststoreName());
			KeyStore truststore = getKeyStore(truststorePath, sormasToSormasConfig.getTruststorePass());
			X509Certificate singatureCert = (X509Certificate) truststore.getCertificate(instanceID);

			return CmsReader.decryptAndVerify(data, Lists.newArrayList(singatureCert), recipientCertificate, recipientPrivateKey);
		} catch (Exception e) {
			LOGGER.error("Couldn't decrypt data", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasDecrypt));
		}
	}

	private KeyStore getKeyStore(Path keystorePath, String keystorePass)
		throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		KeyStore keystore = KeyStore.getInstance("pkcs12");
		keystore.load(new FileInputStream(keystorePath.toFile()), keystorePass.toCharArray());
		return keystore;
	}

	private String getOrganizationId() throws SormasToSormasException {
		return serverAccessDataService.getServerAccessData()
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasCertNotGenerated)))
			.getId();
	}
}
