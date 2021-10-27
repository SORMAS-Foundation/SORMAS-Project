package de.symeda.sormas.backend.crypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;

import org.junit.Test;

public class X509CertBuilderTest {

	@Test
	public void testCreateTemporaryCert() throws Exception {

		KeyStore keyStore = X509CertBuilder.createTemporaryCert("commonName", "theAlias", "password");

		assertEquals(keyStore.getType(), "PKCS12");
		assertEquals(keyStore.size(), 1);

		assertTrue(keyStore.isKeyEntry("theAlias"));

		Key key = keyStore.getKey("theAlias", "password".toCharArray());
		assertEquals(key.getFormat(), "PKCS#8");
	}

	@Test
	public void testAddKeyPair() throws Exception {

		KeyStore keyStore = KeyStore.getInstance("jks");
		keyStore.load(null, "ksPass".toCharArray());

		X509CertBuilder.addKeyPair(keyStore, "key1", "keyPass1".toCharArray(), "k1", null, null, null, null, null, null);
		X509CertBuilder.addKeyPair(keyStore, "key2", "keyPass2".toCharArray(), "k2", null, null, null, null, null, null);

		assertEquals(keyStore.size(), 2);

		assertTrue(keyStore.isKeyEntry("key1"));

		Key key = keyStore.getKey("key1", "keyPass1".toCharArray());
		assertEquals(key.getFormat(), "PKCS#8");

		Certificate cert = keyStore.getCertificate("key1");
		assertEquals(cert.getType(), "X.509");

		X509Certificate x509Cert = (X509Certificate) cert;
		assertEquals(x509Cert.getSubjectDN().getName(), "CN=k1");

		assertEquals(cert.getPublicKey().getAlgorithm(), "RSA");
//		assertThat(x509Cert.getSigAlgName()).isEqualTo("RSASSA-PSS");
		assertEquals(x509Cert.getSigAlgName(), "SHA256withRSA");

		//2048 bit
		assertEquals(((RSAKey) cert.getPublicKey()).getModulus().bitLength(), 4096);

		//self-signed
		cert.verify(cert.getPublicKey());

	}

}
