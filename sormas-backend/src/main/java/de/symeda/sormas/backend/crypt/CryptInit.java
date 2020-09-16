package de.symeda.sormas.backend.crypt;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptInit {

	private static final BouncyCastleProvider PROVIDER = new BouncyCastleProvider();

	private static Logger logger = LoggerFactory.getLogger(CryptInit.class);

	static {
		try {
			//Test for JCE Unlimited Strength Jurisdiction Policy Files
			if (Cipher.getMaxAllowedKeyLength("PBEWithSHAAnd3KeyTripleDES") < 256) {
				throw new RuntimeException("The JCE Unlimited Strength Jurisdiction Policy Files may have to be installed");
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private CryptInit() {
		//NOOP
	}

	public static BouncyCastleProvider getProvider() {
		return PROVIDER;
	}

}
