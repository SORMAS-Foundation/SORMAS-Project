package de.symeda.sormas.backend.crypt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import org.bouncycastle.cms.jcajce.JcaSignerId;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CmsReaderTest {

	private static final String PASSWORD = "pass";
	private static final String ALICE_ALIAS = "alice";
	private static final String BOB_ALIAS = "bob";
	private static KeyStore aliceKs;
	private static KeyStore bobKs;
	private static X509Certificate aliceCert;
	private static X509Certificate bobCert;

	@BeforeClass
	public static void createCertificates() throws KeyStoreException {
		aliceKs = X509CertBuilder.createTemporaryCert("cn1", ALICE_ALIAS, PASSWORD);
		bobKs = X509CertBuilder.createTemporaryCert("cn2", BOB_ALIAS, PASSWORD);
		aliceCert = (X509Certificate) aliceKs.getCertificate(ALICE_ALIAS);
		bobCert = (X509Certificate) bobKs.getCertificate(BOB_ALIAS);
	}

	@AfterClass
	public static void cleanup() {
		aliceKs = null;
		bobKs = null;
		aliceCert = null;
		bobCert = null;
	}

	@Test
	public void testCreateCertStore() throws Exception {

		CertStore certStore = CmsReader.createCertStore(Arrays.asList(aliceCert, bobCert));

		Collection<? extends Certificate> certs = certStore.getCertificates(new X509CertSelector() {

			@Override
			public boolean match(Certificate cert) {
				return true;
			}
		});
		assertThat(certs, Matchers.containsInAnyOrder(aliceCert, bobCert));

	}

	@Test
	public void testGetCertificates() throws Exception {

		CertStore certStore = CmsReader.createCertStore(Arrays.asList(aliceCert, bobCert));

		assertThat(CmsReader.getCertificates(certStore, new JcaSignerId(aliceCert)), Matchers.contains(aliceCert));
		assertThat(CmsReader.getCertificates(certStore, new JcaSignerId(bobCert)), Matchers.contains(bobCert));
	}

	@Test
	public void testDecryptAndVerify() throws Exception {

		final String helloWorld = "Hello World!";

		byte[] signedAndEncrypted = CmsCreator.signAndEncrypt(
			helloWorld.getBytes(StandardCharsets.UTF_8),
			(X509Certificate) aliceKs.getCertificate(ALICE_ALIAS),
			(PrivateKey) aliceKs.getKey(ALICE_ALIAS, PASSWORD.toCharArray()),
			(X509Certificate) bobKs.getCertificate(BOB_ALIAS),
			true);

		byte[] plain = CmsReader.decryptAndVerify(
			signedAndEncrypted,
			Arrays.asList((X509Certificate) aliceKs.getCertificate(ALICE_ALIAS)),
			(X509Certificate) bobKs.getCertificate(BOB_ALIAS),
			(PrivateKey) bobKs.getKey(BOB_ALIAS, PASSWORD.toCharArray()));

		assertEquals(new String(plain, StandardCharsets.UTF_8), helloWorld);

	}

}
