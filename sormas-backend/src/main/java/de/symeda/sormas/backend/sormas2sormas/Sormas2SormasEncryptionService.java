/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.sormas2sormas;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.google.common.collect.Lists;

import de.symeda.sormas.api.Sormas2SormasConfig;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.crypt.CmsCreator;
import de.symeda.sormas.backend.crypt.CmsReader;

@Stateless
@LocalBean
public class Sormas2SormasEncryptionService {

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	public String encrypt(String data, String instanceID) throws Exception {
		Sormas2SormasConfig config = configFacade.getSormas2SormasConfig();
		Path keystorePath = Paths.get(config.getFilePath(), config.getKeystoreName());
		KeyStore keystore = KeyStore.getInstance("pkcs12");
		keystore.load(new FileInputStream(keystorePath.toFile()), config.getKeystorePass().toCharArray());
		X509Certificate signerCertificate = (X509Certificate) keystore.getCertificate(config.getKeyAlias());
		PrivateKey privateKey = (PrivateKey) keystore.getKey(config.getKeyAlias(), config.getKeystorePass().toCharArray());

		Path truststorePath = Paths.get(config.getFilePath(), config.getTruststoreName());
		KeyStore truststore = KeyStore.getInstance("pkcs12");
		truststore.load(new FileInputStream(truststorePath.toFile()), config.getTruststorePass().toCharArray());
		X509Certificate recipientCertificate = (X509Certificate) truststore.getCertificate(instanceID);

		byte[] encryptedBytes =
			CmsCreator.signAndEncrypt(data.getBytes(StandardCharsets.UTF_8), signerCertificate, privateKey, recipientCertificate, true);
		return new String(encryptedBytes, StandardCharsets.UTF_8);
	}

	public String decrypt(String data, String instanceID) throws Exception {
		Sormas2SormasConfig config = configFacade.getSormas2SormasConfig();
		Path keystorePath = Paths.get(config.getFilePath(), config.getKeystoreName());
		KeyStore keystore = KeyStore.getInstance("pkcs12");
		keystore.load(new FileInputStream(keystorePath.toFile()), config.getKeystorePass().toCharArray());
		X509Certificate recipientCertificate = (X509Certificate) keystore.getCertificate(config.getKeyAlias());
		PrivateKey recipientPrivateKey = (PrivateKey) keystore.getKey(config.getKeyAlias(), config.getKeystorePass().toCharArray());

		Path truststorePath = Paths.get(config.getFilePath(), config.getTruststoreName());
		KeyStore truststore = KeyStore.getInstance("pkcs12");
		truststore.load(new FileInputStream(truststorePath.toFile()), config.getTruststorePass().toCharArray());
		X509Certificate singatureCert = (X509Certificate) truststore.getCertificate(instanceID);

		byte[] encryptedBytes =
			CmsReader.decryptAndVerify(data.getBytes(StandardCharsets.UTF_8), Lists.newArrayList(singatureCert), recipientCertificate, recipientPrivateKey);
		return new String(encryptedBytes, StandardCharsets.UTF_8);
	}
}
