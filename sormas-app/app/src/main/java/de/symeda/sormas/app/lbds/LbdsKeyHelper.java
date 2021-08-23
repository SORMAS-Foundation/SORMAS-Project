package de.symeda.sormas.app.lbds;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class LbdsKeyHelper {

	public static String getX509FromPublicKey(PublicKey publicKey) {
		return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
	}

	public static PublicKey getPublicKeyFromX509(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decode(publicKeyString, Base64.DEFAULT));
		return KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
	}

	public static String getPKCS8FromPrivateKey(PrivateKey privateKey) {
		return Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);
	}

	public static PrivateKey getPrivateKeyFromPKCS8(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKeyString, Base64.DEFAULT));
		return KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
	}

	public static String generateAESKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256);
		SecretKey secretKey = keyGen.generateKey();
		return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
	}

	public static String encryptAES(String toEncode, String aesKeyBase64) throws GeneralSecurityException {
		SecretKeySpec aesKey = new SecretKeySpec(Base64.decode(aesKeyBase64, Base64.DEFAULT), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		return Base64.encodeToString(cipher.doFinal(toEncode.getBytes(StandardCharsets.UTF_8)), Base64.DEFAULT);
	}

	public static String decryptAES(String encoded, String aesKeyBase64) throws GeneralSecurityException {
		SecretKeySpec aesKey = new SecretKeySpec(Base64.decode(aesKeyBase64, Base64.DEFAULT), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, aesKey);
		return new String(cipher.doFinal(Base64.decode(encoded, Base64.DEFAULT)), StandardCharsets.UTF_8);
	}
}
