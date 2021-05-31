package org.hzi.sormas.lbds.messaging;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import android.util.Base64;

public class LbdsKeyHelper {

	public static String getX509FromPublicKey(PublicKey publicKey) {
		return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
	}

	public static PublicKey getPublicKeyFromX509(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decode(publicKeyString, Base64.DEFAULT));
		return KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
	}

	public static String getPKCS8FromPrivetKey(PrivateKey privateKey) {
		return Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);
	}

	public static PrivateKey getPrivateKeyFromPKCS8(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKeyString, Base64.DEFAULT));
		return KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
	}
}
