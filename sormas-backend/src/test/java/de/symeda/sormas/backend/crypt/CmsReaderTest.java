package de.symeda.sormas.backend.crypt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import org.bouncycastle.cms.jcajce.JcaSignerId;
import org.hamcrest.Matchers;
import org.junit.Test;

public class CmsReaderTest {

	@Test
	public void testCreateCertStore() throws Exception {

		X509Certificate c1 = (X509Certificate) X509CertBuilder.createTemporaryCert("cn1", "a1", "pass").getCertificate("a1");
		X509Certificate c2 = (X509Certificate) X509CertBuilder.createTemporaryCert("cn2", "a2", "pass").getCertificate("a2");

		CertStore certStore = CmsReader.createCertStore(Arrays.asList(c1, c2));

		Collection<? extends Certificate> certs = certStore.getCertificates(new X509CertSelector() {

			@Override
			public boolean match(Certificate cert) {
				return true;
			}
		});
		assertThat(certs, Matchers.containsInAnyOrder(c1, c2));

	}

	@Test
	public void testGetCertificates() throws Exception {
		X509Certificate c1 = (X509Certificate) X509CertBuilder.createTemporaryCert("cn1", "a1", "pass").getCertificate("a1");
		X509Certificate c2 = (X509Certificate) X509CertBuilder.createTemporaryCert("cn2", "a2", "pass").getCertificate("a2");

		CertStore certStore = CmsReader.createCertStore(Arrays.asList(c1, c2));

		assertThat(CmsReader.getCertificates(certStore, new JcaSignerId(c1)), Matchers.contains(c1));
		assertThat(CmsReader.getCertificates(certStore, new JcaSignerId(c2)), Matchers.contains(c2));
	}

	@Test
	public void testDecryptAndVerify() throws Exception {
		KeyStore alice = X509CertBuilder.createTemporaryCert("cn1", "alice", "pass");
		KeyStore bob = X509CertBuilder.createTemporaryCert("cn2", "bob", "pass");

		byte[] signedAndEncrypted = CmsCreator.signAndEncrypt(
			"Hello World!".getBytes(StandardCharsets.UTF_8),
			(X509Certificate) alice.getCertificate("alice"),
			(PrivateKey) alice.getKey("alice", "pass".toCharArray()),
			(X509Certificate) bob.getCertificate("bob"),
			true);

		byte[] plain = CmsReader.decryptAndVerify(
			signedAndEncrypted,
			Arrays.asList((X509Certificate) alice.getCertificate("alice")),
			(X509Certificate) bob.getCertificate("bob"),
			(PrivateKey) bob.getKey("bob", "pass".toCharArray()));

		assertEquals(new String(plain, StandardCharsets.UTF_8), "Hello World!");

	}

}
