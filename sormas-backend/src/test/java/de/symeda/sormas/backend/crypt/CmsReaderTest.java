package de.symeda.sormas.backend.crypt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.cms.jcajce.JcaSignerId;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CmsReaderTest {

	private static final String PASSWORD = "pass";

	private static final String ALICE_ALIAS = "alice";
	private static final String BOB_ALIAS = "bob";
	private static final String CHARLIE_ALIAS = "charlie";

	private static KeyStore aliceKs;
	private static KeyStore bobKs;
	private static KeyStore chrKs;
	private static X509Certificate aliceCert;
	private static X509Certificate bobCert;
	private static X509Certificate charlieCert;

	// In this scenario alice is only talking to bob, and bob only to charlie

	CmsCertificateConfig aliceCertificateConfig = new CmsCertificateConfig() {

		@Override
		public String getOwnId() {
			return ALICE_ALIAS;
		}

		@Override
		public String getOtherId() {
			return BOB_ALIAS;
		}

		@Override
		public X509Certificate getOwnCertificate() {
			return aliceCert;
		}

		@Override
		public PrivateKey getOwnPrivateKey() {
			return getKeyChecked(aliceKs, ALICE_ALIAS);
		}

		@Override
		public X509Certificate getOtherCertificate() {
			return bobCert;
		}
	};

	CmsCertificateConfig bobCertificateConfig = new CmsCertificateConfig() {

		@Override
		public String getOwnId() {
			return BOB_ALIAS;
		}

		@Override
		public String getOtherId() {
			return ALICE_ALIAS;
		}

		@Override
		public X509Certificate getOwnCertificate() {
			return bobCert;
		}

		@Override
		public PrivateKey getOwnPrivateKey() {
			return getKeyChecked(bobKs, BOB_ALIAS);
		}

		@Override
		public X509Certificate getOtherCertificate() {
			return aliceCert;
		}
	};

	@BeforeClass
	public static void createCertificates() throws KeyStoreException {
		aliceKs = X509CertBuilder.createTemporaryCert("cn1", ALICE_ALIAS, PASSWORD);
		bobKs = X509CertBuilder.createTemporaryCert("cn2", BOB_ALIAS, PASSWORD);
		chrKs = X509CertBuilder.createTemporaryCert("cn3", CHARLIE_ALIAS, PASSWORD);
		aliceCert = (X509Certificate) aliceKs.getCertificate(ALICE_ALIAS);
		bobCert = (X509Certificate) bobKs.getCertificate(BOB_ALIAS);
		charlieCert = (X509Certificate) chrKs.getCertificate(CHARLIE_ALIAS);
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

	private PrivateKey getKeyChecked(KeyStore store, String id) {

		try {
			return (PrivateKey) store.getKey(id, PASSWORD.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			return null;
		}

	}

	@Test
	public void testDecryptAndVerify() throws Exception {

		final String helloWorld = "Hello World!";

		CmsPlaintext plaintext = new CmsPlaintext(ALICE_ALIAS, BOB_ALIAS, helloWorld);

		byte[] signedAndEncrypted = CmsCreator.signAndEncrypt(plaintext, aliceCertificateConfig, true);

		byte[] plain = CmsReader.decryptAndVerify(signedAndEncrypted, bobCertificateConfig);

		ObjectMapper mapper = new ObjectMapper();
		assertEquals(mapper.readValue(plain, String.class), helloWorld);

	}

	@Test
	public void testSurreptitiousForwarding() throws Exception {
		final String love = "I love you!";

		CmsPlaintext plaintext = new CmsPlaintext(ALICE_ALIAS, BOB_ALIAS, love);

		byte[] aliceMsg = CmsCreator.signAndEncrypt(plaintext, aliceCertificateConfig, true);

		// Alice -> Bob
		byte[] forward = CmsReader.decrypt(aliceMsg, bobCert, bobCertificateConfig.getOwnPrivateKey());

		byte[] forwardMsg = CmsCreator.encrypt(forward, charlieCert);

		// Charlie should recognize the forward from Bob.
		// Charlie receives the that seems to originate from Alice as it was signed correctly by her, however,
		// it is clear that Bob forwarded it to Charlie as Alice undoubtedly signed the intended recipient.
		CmsCertificateConfig charlieCertificateConfig = new CmsCertificateConfig() {

			@Override
			public String getOwnId() {
				return CHARLIE_ALIAS;
			}

			@Override
			public String getOtherId() {
				return ALICE_ALIAS;
			}

			@Override
			public X509Certificate getOwnCertificate() {
				return charlieCert;
			}

			@Override
			public PrivateKey getOwnPrivateKey() {
				return getKeyChecked(chrKs, CHARLIE_ALIAS);
			}

			@Override
			public X509Certificate getOtherCertificate() {
				return aliceCert;
			}
		};

		assertThrows(SecurityException.class, () -> CmsReader.decryptAndVerify(forwardMsg, charlieCertificateConfig));

	}

}
