package de.symeda.sormas.backend.crypt;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Generates simple X509 Certificates
 */
class X509CertBuilder {

	private static final String KEY_ALG = "RSA";
	private static final int KEY_SIZE = 4096;
	private static final String SIG_ALG = "SHA256withRSA";
	private static final int VALIDITY_YRS = 3;

	static {
		CryptInit.getProvider();
	}

	private X509CertBuilder() {
		//NOOP
	}

	public static KeyStore createTemporaryCert(String commonName, String alias, String password) {

		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(null, password.toCharArray());

			addKeyPair(keyStore, alias, password.toCharArray(), commonName, null, null, null, null, null, null);

			return keyStore;
		} catch (OperatorCreationException | GeneralSecurityException | IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void addKeyPair(
		KeyStore keystore,
		String alias,
		char[] privateKeyPassword,
		String commonName,
		String unit,
		String organization,
		String location,
		String state,
		String country,
		String emailAdress)
		throws GeneralSecurityException, OperatorCreationException {
		//generating random KeyPair
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALG);
		keyPairGenerator.initialize(KEY_SIZE);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		//generating certificate for KeyPair

		String x500Name = Stream
			.of(
				Pair.of("CN", commonName),
				Pair.of("OU", unit),
				Pair.of("O", organization),
				Pair.of("L", location),
				Pair.of("ST", state),
				Pair.of("C", country),
				Pair.of("EmailAddress", emailAdress))
			.filter(p -> p.getRight() != null)
			.map(p -> p.getLeft() + "=" + p.getRight())
			.collect(Collectors.joining(","));

		BigInteger serial = BigInteger.valueOf(new Random().nextLong());
		X500Name subject = new X500Name(x500Name);
		PublicKey pubKey = keyPair.getPublic();

		LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
		//generate certificate
		X509v3CertificateBuilder generator = new JcaX509v3CertificateBuilder(
			new X500Name(x500Name),
			serial,
			toDate(yesterday),
			toDate(yesterday.plusYears(VALIDITY_YRS)),
			subject,
			pubKey);

		ContentSigner sigGen = new JcaContentSignerBuilder(SIG_ALG).build(keyPair.getPrivate());
		X509CertificateHolder certHolder = generator.build(sigGen);
		X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

		//add certificate
		keystore.setKeyEntry(
			alias,
			keyPair.getPrivate(),
			privateKeyPassword,
			new Certificate[] {
				cert });
	}

	private static Date toDate(LocalDateTime yesterday) {
		return Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());
	}
}
