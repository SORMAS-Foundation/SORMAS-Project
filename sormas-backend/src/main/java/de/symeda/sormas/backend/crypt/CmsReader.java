package de.symeda.sormas.backend.crypt;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JcaX509CertSelectorConverter;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;

/**
 * <a href="https://tools.ietf.org/html/rfc5652">CMS</a>-decodes the payload
 * for SORMAS to SORMAS communication
 */
public class CmsReader {

	private static final String EXPECTED_ENC_ALG_OID = SMIMECapability.aES256_CBC.getId();
	private static final String EXPECTED_KEY_ENC_ALG_OID = PKCSObjectIdentifiers.rsaEncryption.getId();
	private static final String EXPECTED_SIG_ALG_OID = PKCSObjectIdentifiers.sha256WithRSAEncryption.getId();

	private CmsReader() {
		//NOOP
	}

	public static byte[] decryptAndVerify(
		byte[] encryptedData,
		List<X509Certificate> expectedSignatureCerts,
		X509Certificate recipientCertificate,
		PrivateKey recipientPrivateKey) {
		byte[] decrypted = decrypt(encryptedData, recipientCertificate, recipientPrivateKey);
		return verifyAndExtractPayload(decrypted, expectedSignatureCerts);
	}

	static byte[] verifyAndExtractPayload(byte[] signedData, Collection<X509Certificate> expectedSignatureCerts) {

		//VERIFY

		CMSSignedData s;
		try {
			s = new CMSSignedData(signedData);
		} catch (CMSException e) {
			throw new RuntimeException(e);
		}

		verify(s, expectedSignatureCerts);

		//
		// extract the content
		//
		return (byte[]) s.getSignedContent().getContent();
	}

	static byte[] decrypt(byte[] encryptedData, X509Certificate recipientCertificate, PrivateKey recipientPrivateKey) {

		try {
			CMSEnvelopedData m = new CMSEnvelopedData(encryptedData);
			checkAlgOID("encryption", EXPECTED_ENC_ALG_OID, m.getEncryptionAlgOID());

			RecipientInformationStore recipients = m.getRecipientInfos();

			RecipientId recId = new JceKeyTransRecipientId(recipientCertificate);
			RecipientInformation recipientInfo = recipients.get(recId);
			checkAlgOID("key encryption", EXPECTED_KEY_ENC_ALG_OID, recipientInfo.getKeyEncryptionAlgOID());

			Recipient recipient = new JceKeyTransEnvelopedRecipient(recipientPrivateKey);

			return recipientInfo.getContent(recipient);

		} catch (CMSException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * verify the signature (assuming the cert is contained in the message)
	 * 
	 * @param expectedCerts
	 *            The Certificates of the expected Signer
	 * @throws Mail301Exception
	 */
	static void verify(CMSSignedData s, Collection<X509Certificate> expectedCerts) {

		CertStore certStore = createCertStore(expectedCerts);

		Iterator<SignerInformation> it = s.getSignerInfos().getSigners().iterator();

		if (!it.hasNext()) {
			throw new RuntimeException("Message has not been signed");
		}

		//
		// check each signer
		//
		while (it.hasNext()) {

			SignerInformation signer = it.next();
			checkAlgOID("signature", EXPECTED_SIG_ALG_OID, signer.getEncryptionAlgOID());

			SignerId sid = signer.getSID();

			Collection<X509Certificate> certCollection;
			try {
				certCollection = getCertificates(certStore, sid);
			} catch (CertStoreException e) {
				throw new RuntimeException(e);
			}

			Iterator<X509Certificate> certIt = certCollection.iterator();
			if (!certIt.hasNext()) {
				throw new RuntimeException("Unknown Signer Certificate: " + sid.getIssuer() + "#" + sid.getSerialNumber());
			}
			//Expectation: Exactly one match
			X509Certificate cert = certIt.next();

			BcDigestCalculatorProvider dcProvider = new BcDigestCalculatorProvider();
			JcaSignerInfoVerifierBuilder verifierBuilder = new JcaSignerInfoVerifierBuilder(dcProvider);
			SignerInformationVerifier verifier;
			try {
				verifier = verifierBuilder.build(cert);
			} catch (OperatorCreationException e) {
				throw new RuntimeException(e);
			}

			try {
				if (!signer.verify(verifier)) {
					throw new RuntimeException("Signature failed!");
				}
			} catch (CMSException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void checkAlgOID(String algTypeName, String expectedOid, String oid) {
		if (!expectedOid.equals(oid)) {
			throw new RuntimeException("Unexpected " + algTypeName + " algorithm " + oid + "; expected " + expectedOid);
		}
	}

	public static CertStore createCertStore(Collection<X509Certificate> allCerts) {
		CollectionCertStoreParameters params = new CollectionCertStoreParameters(allCerts);
		try {
			return CertStore.getInstance("Collection", params, CryptInit.getProvider());
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	static Collection<X509Certificate> getCertificates(CertStore certStore, SignerId sid) throws CertStoreException {

		X509CertSelector selector = new JcaX509CertSelectorConverter().getCertSelector(sid);

		return (Collection<X509Certificate>) certStore.getCertificates(selector);
	}
}
