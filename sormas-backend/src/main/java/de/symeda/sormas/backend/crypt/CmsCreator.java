package de.symeda.sormas.backend.crypt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://tools.ietf.org/html/rfc5652">CMS</a>-encodes the payload
 * for SORMAS to SORMAS communication
 */
public class CmsCreator {

	// @formatter:off
	/* 
	 * Algorithms from Anlage 16
	 * 
	 * 2.1.1 Einweg-Hashfunktionen
	 * SHA-256 2.16.840.1.101.3.4.2.1
	 * 
	 * 2.1.2 Signaturalgorithmen
	 * Sha256withRSAEncryption 1.2.840.113549.1.1.11
	 * 	PKCSObjectIdentifiers.sha256WithRSAEncryption
	 * id-RSAES-PSS 1.2.840.113549.1.1.10
	 * 	PKCSObjectIdentifiers.id_RSASSA_PSS
	 * 
	 * 2.1.2.1.2 Datenfeld MaskGenAlgorithm
	 * id-mgf 11.2.840.113549.1.1.8
	 * 
	 * 2.1.3 Verschlüsselungsalgorithmen für Daten
	 * id-aes256-CBC 2.16.840.1.101.3.4.1.42
	 * 
	 * 2.1.4 Verschlüsselungsalgorithmen für den Nachrichtenschlüssel
	 * rsaEncryption 1.2.840.113549.1.1.1
	 * 	PKCSObjectIdentifiers.rsaEncryption
	 * id-RSAES-OAEP 1.2.840.113549.1.1.7
	 * 	PKCSObjectIdentifiers.id_RSAES_OAEP
	 * 
	 * 2.1.4.1.2 Datenfeld MaskGenAlgorithm
	 * id-mgf 11.2.840.113549.1.1.8
	 * 
	 * 2.1.5 Algorithmen zur Nutzung des öffentlichen Schlüssels
	 * rsaEncryption 1.2.840.113549.1.1.1
	 * 	PKCSObjectIdentifiers.rsaEncryption
	 * 
	 * see https://www.gkv-datenaustausch.de/technische_standards_1/technische_standards.jsp Anlage 16, 2.1.2:
	 * see https://www.gkv-datenaustausch.d/media/dokumente/standards_und_normen/technische_spezifikationen/Anlage_16_-_Security-Schnittstelle.pdf
	 */
	// @formatter:on

	private static final ASN1ObjectIdentifier SYMMETRIC_CRYPT_ALG = SMIMECapability.aES256_CBC;

	private static final String SIG_ALG = "SHA256WITHRSA";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		//make sure BC is initialised
		CryptInit.getProvider();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		objectMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);
	}

	private static final Logger logger = LoggerFactory.getLogger(CmsCreator.class);

	private boolean validateSignature = false;

	private CmsCreator() {
		//NOOP
	}

	public static byte[] signAndEncrypt(CmsPlaintext plainData, CmsCertificateConfig certificateConfig, boolean validateSignature)
		throws CMSException, JsonProcessingException {

		byte[] signedData = sign(
			objectMapper.writeValueAsBytes(plainData),
			certificateConfig.getOwnCertificate(),
			certificateConfig.getOwnPrivateKey(),
			validateSignature);

		/* Create the encryptor */
		return encrypt(signedData, certificateConfig.getOtherCertificate());
	}

	static byte[] sign(byte[] plainData, X509Certificate signerCertificate, PrivateKey privateKey, boolean validateSignature) throws CMSException {

		BouncyCastleProvider provider = CryptInit.getProvider();

		/* Create the SMIMESignedGenerator */
		CMSSignedDataGenerator generator = createSigner(signerCertificate, privateKey, provider);

		CMSTypedData content = new CMSProcessableByteArray(plainData);

		CMSSignedData cmsSignedData = generator.generate(content, true);
		byte[] signedData;
		try {
			signedData = cmsSignedData.getEncoded();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		if (validateSignature) {
			CmsReader.verify(cmsSignedData, Collections.singletonList(signerCertificate));
			logger.info("Created signature is valid");
		}

		return signedData;
	}

	private static CMSSignedDataGenerator createSigner(X509Certificate signerCertificate, PrivateKey privateKey, BouncyCastleProvider provider)
		throws CMSException {

		ASN1EncodableVector attributes = createAttributes(signerCertificate);

		try {
			CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
			generator.addSignerInfoGenerator(
				new JcaSimpleSignerInfoGeneratorBuilder().setProvider(provider)
					.setSignedAttributeGenerator(new AttributeTable(attributes))
					.build(SIG_ALG, privateKey, signerCertificate));

			/* Add the list of certs to the generator */
			JcaCertStore certs = new JcaCertStore(Collections.singletonList(signerCertificate));
			generator.addCertificates(certs);

			return generator;

		} catch (CertificateEncodingException | OperatorCreationException e) {
			throw new RuntimeException(e);
		}
	}

	private static ASN1EncodableVector createAttributes(Certificate certificate) {
		ASN1EncodableVector attributes = new ASN1EncodableVector();
		attributes.add(
			new SMIMEEncryptionKeyPreferenceAttribute(
				new IssuerAndSerialNumber(
					new X500Name(((X509Certificate) certificate).getIssuerDN().getName()),
					((X509Certificate) certificate).getSerialNumber())));

		return attributes;
	}

	public static byte[] encrypt(byte[] signedData, X509Certificate recipientCertificate) {

		BouncyCastleProvider provider = CryptInit.getProvider();

		try {
			CMSEnvelopedDataGenerator encryptor = new CMSEnvelopedDataGenerator();
			encryptor.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(recipientCertificate).setProvider(provider));

			/* Encrypt the message */
			CMSTypedData content = new CMSProcessableByteArray(signedData);
			OutputEncryptor outputEncryptor = new JceCMSContentEncryptorBuilder(SYMMETRIC_CRYPT_ALG).setProvider(provider).build();
			CMSEnvelopedData ed = encryptor.generate(content, outputEncryptor);

			return ed.getEncoded();

		} catch (CertificateEncodingException | IllegalArgumentException | CMSException | IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public boolean isValidateSignature() {
		return validateSignature;
	}

	public void setValidateSignature(boolean validateSignature) {
		this.validateSignature = validateSignature;
	}
}
